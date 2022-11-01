(ns tracker.app
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [ajax.core :refer [GET]]
            [tracker.state :as state]
            [tracker.header :as header]
            [tracker.route-table :as route]
            [tracker.ship-table :as ship]
            [tracker.orchestrator :as orchestrator]
            [tracker.test-panel :as test]
            [tracker.route-forecast-ui :as route-forecast-ui]))

(defn- load-page
  [page]
  (condp = page
    :tracker [route/RouteTable]
    :ship [ship/ShipTable]
    :test [test/TestPanel]
    :route-forecast-ui [route-forecast-ui/RoutePanel]
    [:div
     "Sorry Could Not Find That Page"]))

(defn Application []
  [:div
   [header/Header]
   (load-page @state/page)])

(dom/render [Application] (js/document.getElementById "app"))

; This is the :devtools {:before-load script
(defn stop []
  (js/console.log "Stopping..."))
; This is the :devtools {:after-load script
(defn start []
  (js/console.log "Starting..."))
; This is the `app.core.init()` that's triggered in the html
(defn ^:export init []
  (start)
  (orchestrator/get-ships)
  (orchestrator/get-routes))