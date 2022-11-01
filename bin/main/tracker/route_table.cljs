(ns tracker.route-table
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [tracker.state :as state]
            [tracker.orchestrator :as orchestrator]
            [tracker.components.modal :as m]
            ))
            
;; Storing the name of the FWC button that is currently selected
(def selected-fwc (r/atom "")) ;; This is my react "hook" that will render FWC buttons again when changed ...

;; Storing the name of the REGION button that is currently selected
(def selected-region (r/atom "")) ;; This is my react "hook" that will render Region buttons again when changed ...

(defn Forecast
  [forecast type]
  [:div
    [m/modal-header {:close-button true} type]
    [m/modal-body [:img {:key type
                         :src (.. js/window -URL (createObjectURL forecast))}]]])

(defn get-routes []
  (let [routes (orchestrator/get-routes {:handler (fn [x] (swap! state/routes (fn [_] x)))})]))

(defn generate-limits
   [limits]
   (str (get limits "ahead") "/" (get limits "astern") "/" (get limits "side")))

(defn Route
  [route]
  (let [ship (get route "ship")]
    [:tr
     [:td
       (get ship "name")]
     [:td
       (get route "region")]
     [:td
       (get route "forecast")]
     [:td
        (get route "start-dtg")]
     [:td
       (generate-limits (get route "limits"))]
     [:td
       (get route "limits-date")]
     [:td]
     [:td]
     [:td]
     [:td
      [:i {:class "fa-solid fa-ship"
           :title "get route recommendation"}]]
     [:td
      [:i {:class "fa-solid fa-umbrella"
           :title "get forecast"
           :on-click #(orchestrator/get-gweax (get route "uuid")
                                              {:handler (fn [x] (m/modal! [Forecast x (get route "forecast")]))
                                               :error-handler (fn [x] (js/alert x))})}]]
     [:td
      [:i {:class "fa-solid fa-umbrella-beach"
           :title "get psuedo forecast"
           :on-click #(orchestrator/get-psuedo-gweax (get route "uuid")
                                              {:handler (fn [x] (m/modal! [Forecast x (get route "forecast")]))
                                               :error-handler (fn [x] (js/alert x))})}]]]))

(defn- get-frequencies
  [key]
  (frequencies (map #(get % key) @state/routes)))

(def get-fwc-route-counts (partial get-frequencies "fwc"))
(def get-region-route-counts (partial get-frequencies "region"))

(defn- set-fwc
  [fwc]
  (println "setting fwc " fwc)
  (reset! state/route-filter (-> @state/route-filter
                                 (dissoc "region")
                                 (assoc "fwc" (:name fwc)))))

;; on-click callback for Region buttons, primarily to support toggle capability for now ...
(defn onClickRegionButton
  [region]
    (reset! selected-region (:name region)) ;; this is my react "hook" variable that changes with button clicking ... 
)

(defn RegionButton
  [region]

  ;; default
  (let [class-name (atom "region-button-unselected")] ;; a "mutable" local variable ...

    ;; Restore the state of previously toggled region button, if any
    (if (= @selected-region (:name region))
      (reset! class-name "region-button-selected")
    )
  
    [:button
      {:id (:name region) :class @class-name :on-click (fn[_] (onClickRegionButton region) (reset! state/route-filter (assoc @state/route-filter "region" (:name region))))}
      (str (:name region) "-" (:route-count region))]
  )
)

(defn RegionButtons
  [fwc]
  [:div
   (doall
     (let [route-counts (get-region-route-counts)]
       (for [[i r] (map-indexed (fn [i x] [i x]) (get @state/regions fwc))]
         ^{:key i} [RegionButton {:name r :route-count (get route-counts r 0)}])))])

;; on-click callback for FWC buttons, primarily to support toggle capability for now ...
(defn onClickFWCButton
  [fwc]
    ;; these are my react "hook" variables that change when button clicking ...
    (reset! selected-region "")
    (reset! selected-fwc (:name fwc))
)

(defn FWCButton
  [fwc]

  ;; default
  (let [class-name (atom "fwc-button-unselected")] ;; a "mutable" local variable ...

    ;; Restore the state of previously toggled FWC button, if any
    (if (= @selected-fwc (:name fwc))
      (reset! class-name "fwc-button-selected")
    )

    [:button
      {:id (:name fwc) :class @class-name :on-click (fn[_](onClickFWCButton fwc)(set-fwc fwc))}
      (str (:name fwc) "-" (:route-count fwc))]
  )
)

(defn FWCButtons
  []
  [:div {:id "topnav"}
   (doall
     (let [route-counts (get-fwc-route-counts)]
       (for [[i f] (map-indexed (fn [i x] [i x]) (keys @state/regions))]
         ^{:key i} [FWCButton {:name f :route-count (get route-counts f 0)}])))
   [RegionButtons (get @state/route-filter "fwc")]])

(defn- compare-filter
  [route k v]
  (= (get route k) v))

;; can not make (filter pred coll) work, dunno why
(defn- compare-all-filters-keep
  [route filter]
  (if (every? (fn [[k v]]
                (compare-filter route k v))
              (seq filter))
    route
    nil))

(defn- compare-all-filters
  [route filter]
  (every? (fn [[k v]]
            (compare-filter route k v))
          (seq filter)))

(defn filter-routes
  [routes filter]
  ;(filter #(compare-all-filters % filter) routes)
  (keep #(compare-all-filters-keep % filter) routes))

;; Use to help sort table columns ...
(def app-state (r/atom {:sort-val :ship :ascending true}))

;; Use to help sort table columns ...
(defn update-sort-value [new-val]
  (if (= new-val (:sort-val @app-state))
    (swap! app-state update-in [:ascending] not)
    (swap! app-state assoc :ascending true))
  (swap! app-state assoc :sort-val new-val))

;; Use to help sort table columns ...
(defn add-new-attributes 
	[updated-routes updated-route route]
    	(reset! updated-route (merge route {:ship (get (get route "ship") "name")}))
    	(reset! updated-route (merge @updated-route {:region (get route "region")}))
    	(reset! updated-route (merge @updated-route {:forecast (get route "forecast")}))
    	(reset! updated-route (merge @updated-route {:start-dtg (get route "start-dtg")}))
    	(reset! updated-route (merge @updated-route {:limits (generate-limits (get route "limits"))}))
    	(reset! updated-route (merge @updated-route {:limits-date (get route "limits-date")}))
        (reset! updated-routes (conj @updated-routes @updated-route))
)

;; Use to help sort table columns ...
(defn sorted-contents [routes]
  (let [sorted-contents (sort-by (:sort-val @app-state) routes)]
    (if (:ascending @app-state)
      sorted-contents
      (rseq sorted-contents))))

;; Use to help sort table columns ...
(defn pre-process-routes 
    [routes]
    (let [updated-routes (atom []) updated-route (atom {})]
	   (doall
          (for [[i r] (map-indexed (fn [i x] [i x]) routes)]
             ^{:key i} 
             (add-new-attributes updated-routes updated-route r)
           )
       )
       ;;return the sorted routes ...
	   (sorted-contents @updated-routes)	
    )
)

(defn RouteTable
  []
  (let [filter @state/route-filter
        routes (filter-routes @state/routes filter)]
        
    [:div
     [m/modal-window]
     [:div {:id "filter"}
      [FWCButtons]]
     [:table {:id "ships"}
      [:colgroup
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "8%"}]
       [:col {:width "48%"}]
       [:col {:width "4%"}]
       [:col {:width "4%"}]
       [:col {:width "4%"}]]
      [:thead
       [:tr
        [:th {:on-click #(update-sort-value :ship)} "Ship"]
        [:th {:on-click #(update-sort-value :region)} "Region"]
        [:th {:on-click #(update-sort-value :forecast)} "Forecast"]
        [:th {:on-click #(update-sort-value :start-dtg)} "Start DTG"]
        [:th {:on-click #(update-sort-value :limits)} "Limits"]
        [:th {:on-click #(update-sort-value :limits-date)} "Limits DTG"]
        [:th]
        [:th]
        [:th]
        [:th]
        [:th]
        [:th]]]
      [:tbody

       (if (not (empty? routes))
       		(doall
        		(for [[i r] (map-indexed (fn [i x] [i x]) (pre-process-routes routes))]
          			^{:key i} [Route r]
          		)
       		)
       )

       ]]]))

(comment
  (defn Region
    [i {display :display minimize :minimize :as region} routes]
    [:tbody
     [:tr {:class "priority"}
      [:td {:class "button"}
       (if minimize
         [:i {:class "fa-solid fa-minimize"
              :on-click #(set-min-max-button i false)}]
         [:i {:class "fa-solid fa-maximize"
              :on-click #(set-min-max-button i true)}])]
      [:td {:col-span 3}
       display]]
     (if (and routes (not minimize))
      (doall
        (for [[i s] (map-indexed (fn [i x] [i x]) routes)]
          ^{:key i} [Route s])))]))
