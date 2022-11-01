(ns tracker.header
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [ajax.core :refer [GET]]
            [tracker.state :as state]
            [tracker.orchestrator :as orchestrator]
          [tracker.components.modal :as m]))

(defn- get-version [] "v0.0.2")

(def buttons [{:key :tracker :display "ROUTES"}
              {:key :ship :display "SHIPS"}
              {:key :test :display "TEST-1"}
              ;;{:key :test2 :display "TEST-2"}])
              {:key :route-forecast-ui :display "ROUTE FORECAST UI"}])


(defn- get-buttons []
  buttons)

(defn- switch-page
  [page]
  (reset! state/page page))

;; Creates the row from the service JSON ...
(defn Service
  [i service]

  (let [status-color (atom "white")
        component (:component service)
        version (:version service)
        status (:status service)]

    ;;We can put logic to change styling here ...
    ;;For example ... everything 400 or over is an error code ...
    (cond
      (>= status 400) (reset! status-color "red")
      (>= status 200) (reset! status-color "green"))

    [:tr {:id i}
      [:td component]
      [:td version]
      [:td {:style {:color "white" :background-color @status-color}} status]]))


;; Creates a table for the popup widget when the "status" button clicked ...
(defn StatusTable []
  (let [services (flatten (vals @state/health))]
    [m/modal-body
     [m/modal-header {:key "modal-header" :close-button true}
      [:span {:key "System Health" :style {:font-size "20px" :font-weight "bold"}} "System Health"]]

     [:table {:key "status-table" :id "status-table"}
       [:thead
         [:tr
           [:th "Component"]
           [:th "Version"]
           [:th "Status"]]]

       [:tbody
         (doall
           (for [[i s] (map-indexed (fn [i x] [i x]) services)]
             ^{:key i} [Service i s]))]]]))

(defn- update-health [key info]
  (swap! state/health assoc key info))

(defn- display-health-check []
  (orchestrator/get-status {:handler (fn [x] (update-health :orchestrator (js->clj x)))
                            :error-handler (fn [x] (update-health :orchestrator [{:component "orchestrator" :status (:status x)}]))})
  (m/modal! [StatusTable @state/health] {:keyboard false :backdrop "static"}))

(defn Title []
  [:div
   {:class "title"}
   (str "MARSHAL " (get-version))
   " "
   [:i {:class "fa-solid fa-suitcase-medical"
        :title "status"
        :on-click #(display-health-check)}]])

(defn Button
  [{key :key display :display} active]
  [:span>button
    {:on-click #(switch-page key)
     :class (if (= key active) "active")}
    display])

(defn NavBar
  ([] [NavBar @state/page])
  ([active]
   [:div.topnav
    (doall
      (for [button (get-buttons)]
        [Button button active]))
    [Title]]))

(defn Header []
  [:div
   [NavBar]])
