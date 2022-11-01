(ns tracker.state
  (:require [reagent.core :as r]))

(def page (r/atom :tracker))

(def route-filter (r/atom {"fwc" "SD"}))

(def message-viewing (r/atom {}))

(def routes (r/atom []))

(def ships (r/atom []))

(def messages (r/atom []))

(def regions (r/atom {"FWC-NORFOLK" ["SOUTH AMERICA" "CARIBBEAN" "NORTH ATLANTIC" "SOUTH ATLANTIC" "WESTERN EUROPE" "WEST AFRICAN COAST" "MEDITERRANEAN", "NORTH SEA"],
                      "FWC-SD" ["EPAC" "WPAC" "ARABIAN GULF" "ARABIAN SEA" "RED SEA" "MARITIME CONTINENT" "SPAC"]
                      }))

(def health (r/atom {:tracker [{:component "tracker" :version "1.2" :status 200}]
                     :orchestrator []}))
