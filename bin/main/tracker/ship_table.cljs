(ns tracker.ship-table
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [tracker.state :as state]
            [tracker.components.modal :as m]))
            
;; Use to help sort table columns ...
(def app-state (r/atom {:sort-val :name :ascending true}))

;; Use to help sort table columns ...
(defn update-sort-value [new-val]
  (if (= new-val (:sort-val @app-state))
    (swap! app-state update-in [:ascending] not)
    (swap! app-state assoc :ascending true))
  (swap! app-state assoc :sort-val new-val)
)
  
;; Use to help sort table columns ...
(defn sorted-contents [ships]
  (let [sorted-contents (sort-by (:sort-val @app-state) ships)]
    (if (:ascending @app-state)
      sorted-contents
      (rseq sorted-contents))))
      
;; Use to help sort table columns ...
(defn add-new-attributes 
	[updated-ships updated-ship ship]
    	(reset! updated-ship (merge ship {:name (get ship "name")}))
    	(reset! updated-ship (merge @updated-ship {:hull (get ship "hull")}))
        (reset! updated-ships (conj @updated-ships @updated-ship))
)
      
;; Use to help sort table columns ...
(defn pre-process-ships 
    [ships]
    (let [updated-ships (atom []) updated-ship (atom {})]
	   (doall
          (for [[i s] (map-indexed (fn [i x] [i x]) ships)]
             ^{:key i} 
             (add-new-attributes updated-ships updated-ship s)
           )
       )
       ;;return the sorted routes ...
	   (sorted-contents @updated-ships)	
    )
)
      
(defn Ship
  [i ship]
  [:tr {:id i}
   [:td
    (get ship "name")]
   [:td
    (get ship "hull")]
   [:td]])

(defn ShipTable
  []
(cljs.pprint/pprint @state/ships)
  (let [ships @state/ships]
    [:div
     [m/modal-window]
    [:table {:id "ships"}
     [:colgroup
      [:col {:width "8%"}]
      [:col {:width "8%"}]
      [:col {:width "84%"}]]
     [:thead
      [:tr
       [:th {:on-click #(update-sort-value :name)} "Name"]
       [:th {:on-click #(update-sort-value :hull)} "Hull"]
       [:th]]]
     [:tbody
      (doall
        (for [[i s] (map-indexed (fn [i x] [i x]) (pre-process-ships ships))]
          ^{:key i} [Ship i s]))]]]))
