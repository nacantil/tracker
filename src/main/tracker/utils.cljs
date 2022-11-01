(ns tracker.utils
	(:require 
		[reagent.core :as r]
	    [reagent.dom :as dom]
	    [clojure.string :as str]
        [tracker.components.modal :as m]
    )
 )

;; Needed a utility to generate random uuids ...
(defn generate-uuid
	[]
		(str (random-uuid))
)

(def day-of-week-names
	["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"]
)

(def month-names
	["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"]
)

(defn format-current-DTG
	[]
		(let
			[
				date (new js/Date)
				dayOfWeek (.getDay date)
				month (.getMonth date)
				dayOfMonth (.getDate date)
				year (.getFullYear date)
				timeZoneOffset (.getTimezoneOffset date)
			]
		
			;;(str "[" (.toLocaleTimeString date) "][" (.getUTCHours date) ":" (.getUTCMinutes date) ":" (.getUTCSeconds date) " GMT] [" (get day-of-week-names dayOfWeek) ", " (get month-names month) " " dayOfMonth ", " year "]")
			;;(str "[" (.toLocaleString date) "] [" (.toISOString date) "] [" (get day-of-week-names dayOfWeek) ", " (get month-names month) " " dayOfMonth ", " year "]")
			;;(str "[" (.toString date) "] [" (.toISOString date) "]")
			;;(.toISOString date)
			(.toString date)
		)
)

(defn show-msg
	[header message]	
		[m/modal-body
			[m/modal-header {:key "modal-header" :close-button true}
    			[:span {:key "" :style {:font-size "20px" :font-weight "bold"}} header]
    		]
    		[:span {:key "show-msg" :style {:font-size "20px" :font-weight "bold"}} message]
    	]
)

(defn drop-nth 
	[n coll]
  		(concat 
    		(take n coll)
    		(drop (inc n) coll)
    	)
)
