(ns tracker.orchestrator
  (:require [ajax.core :refer [GET]]
            [ajax.protocols :refer [-body]]
            [tracker.state :as state])
  (:require-macros [adzerk.env :as env]))

(env/def MARSHAL_ORCHESTRATOR "/orchestrator" :required)
(env/def MARSHAL_ORCHESTRATOR_STATUS "/info" :required)
(env/def MARSHAL_ORCHESTRATOR_PSUEDO_GWEAX "/test/gweax" :required)
(env/def MARSHAL_ORCHESTRATOR_GWEAX "/forecast/gweax" :required)
(env/def MARSHAL_ORCHESTRATOR_ROUTEREC "/route/recommendation" :required)
(env/def MARSHAL_ORCHESTRATOR_ROUTES "/data/route" :required)
(env/def MARSHAL_ORCHESTRATOR_SHIPS "/data/ship" :required)

(defn get-orchestrator
  ([endpoint] (get-orchestrator endpoint {}))
  ([endpoint options] (get-orchestrator endpoint options {}))
  ([endpoint options params]
   (GET (str MARSHAL_ORCHESTRATOR endpoint) (merge options {:params params}))))

;; TODO: Psuedo call - remove after demo
(defn get-psuedo-gweax
  [uuid _]
  (get-orchestrator (str MARSHAL_ORCHESTRATOR_PSUEDO_GWEAX "/" uuid)
                    (merge {:response-format {:content-type "image/png" :description "PNG image" :read -body :type :blob}}
                           _)))

(defn get-gweax
  [uuid _]
  (get-orchestrator (str MARSHAL_ORCHESTRATOR_GWEAX "/" uuid)
                    (merge {:response-format {:content-type "image/png" :description "PNG image" :read -body :type :blob}}
                           _)))

(def get-route-rec (partial get-orchestrator MARSHAL_ORCHESTRATOR_ROUTEREC))
(def get-status (partial get-orchestrator MARSHAL_ORCHESTRATOR_STATUS))
(def get-ships (partial get-orchestrator MARSHAL_ORCHESTRATOR_SHIPS
                                         {:response-format :json
                                          :handler (fn [x]
                                                     (reset! state/ships (js->clj x)))}))
(def get-routes (partial get-orchestrator MARSHAL_ORCHESTRATOR_ROUTES
                                          {:response-format :json
                                           :handler (fn [x]
                                                      (reset! state/routes (js->clj x)))}))
