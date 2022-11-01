(ns tracker.ship-info
	(:require 
	    [cljs.pprint :refer [pprint]]
		[reagent.core :as r]
	    [reagent.dom :as dom]
        [tracker.utils :as utils]
	    [reagent-contextmenu.menu :as menu]
    )
)

(defn generate-limits
	[route]
		(let [limits (get route "limits")]	
   			(str (get limits "ahead") "/" (get limits "astern") "/" (get limits "side"))
   		)
)

(defn optionsHandler
	[option route parent event]
		(case option
			"Option-1" (do (set! parent.style.backgroundColor "black") (set! parent.style.color "white"))
			"Option-2" (do (set! parent.style.backgroundColor "orange") (set! parent.style.color "white"))
			"Option-3" (do (set! parent.style.backgroundColor "green") (set! parent.style.color "white"))
			"Option-4" (do (set! parent.style.backgroundColor "white") (set! parent.style.color "black"))
		)
)

(defn RoleWorkflowContextMenuHandler
		[route event]
			(let [uuid (get route "uuid")]
				;; You can get the role name from event.target.id ...
				(if (nil? uuid)
					(js/console.log "Route not selected.")
					(js/console.log (str event.target.id " [" uuid "]"))
				)
				(menu/context! 
					event
          			[event.target.id
           				["Option-1" (fn[event] (optionsHandler "Option-1" route event.target event))]
           				["Option-2" (fn[event] (optionsHandler "Option-2" route event.target event))]
           				["Option-3" (fn[event] (optionsHandler "Option-3" route event.target event))]
           				["Option-4" (fn[event] (optionsHandler "Option-4" route event.target event))]
            		]
            	)
            )
)

(defn get-ship-name
	[ship]
		(let [ship-name (get ship "name")]
			(if (nil? ship-name)
				"No route selected in table."
				ship-name	
			)
		)
)

(defn NonInfoPanel
	[]
		[:div {:style {:fontSize 20 :fontWeight "bold"}} 
			[:br]	
			"No route selected"
		]
)

(defn ShipInfoPanel
	[route]
		(let [ship (get route "ship")]
	
			[:div {:style {:padding "20px" :width "100%"}} 
			    [menu/context-menu]
			  	[:div {:style {:fontSize 20 :fontWeight "bold"}} (get-ship-name ship) ]
			  	[:div {:style {:fontSize 14 :fontWeight "normal" }} (get route "uuid")]
	  			[:div {:style {:fontSize 14 :fontWeight "normal" }} (str "Start DTG: " (get route "start-dtg"))]
	  			[:div {:style {:fontSize 14 :fontWeight "normal" }} "Text here ..."]
			  	[:span
					[:button {:id "FDO" :key "FDO" :on-click (fn[event] (RoleWorkflowContextMenuHandler route event)) :class "btn-workflow" :style {:backgroundColor "white"}} "FDO"]
				]
				[:span
					[:button {:id "SUP" :key "SUP" :on-click (fn[event] (RoleWorkflowContextMenuHandler route event)) :class "btn-workflow" :style {:backgroundColor "white"}} "SUP"]
				]
				[:span
					[:button {:id "SRO" :key "SRO" :on-click (fn[event] (RoleWorkflowContextMenuHandler route event)) :class "btn-workflow" :style {:backgroundColor "white"}} "SRO"]
				]
				[:span
					[:button {:id "CTW" :key "CTW" :on-click (fn[event] (RoleWorkflowContextMenuHandler route event)) :class "btn-workflow" :style {:backgroundColor "white"}} "CTW"]
				]
				[:span 
					[:button {:id "CDO" :key "CDO" :on-click (fn[event] (RoleWorkflowContextMenuHandler route event)) :class "btn-workflow" :style {:backgroundColor "white"}} "CDO"]
				]
			  	[:div {:style {}} "Text here ..."]
			  	[:div {:style {:height "160px" :fontSize 20 :fontWeight "bold"}} "Limits"
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} (generate-limits route)]
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} (get route "limits-date")]
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} "Text here ..."]
			  	]
			  	[:div {:style {:height "160px" :fontSize 20 :fontWeight "bold"}} "XSIT"
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} (str "FWC: " (get route "fwc"))]
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} (str "Region: " (get route "region"))]
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} "Waypoints:"]
			  		[:div {:style {:height "100px" :fontSize 14 :fontWeight "normal" :overflow "auto"}} (str "" (get route "waypoints"))]
			  		[:div {:style {:fontSize 14 :fontWeight "normal"}} "Text here ..."]
			  	]
			  	[:br]
			  	[:br]
			  	[:div {:style {:height "160px" :fontSize 20 :fontWeight "bold"}} "Contacts"
			  		[:div {:style {:fontSize 14 :fontWeight "normal" }} 
			  			[:a {:href "mailto:neil.p.acantilado.civ@us.navy.mil"} "neil.p.acantilado.civ@us.navy.mil"]
	  				  	[:br]
	 		 		  	"Text here ..."
	  				]
	  			]
			  	[:div {:style {:height "160px" :fontSize 20 :fontWeight "bold"}} "Notes & Messages"
			  		[:div {:style {:height "200px" :fontSize 14 :fontWeight "normal" :overflow "auto"}} (str "" route)]
	  			]
			]
		)
)

(defn GenerateShipInfoPanel
	[route]
		(if (nil? route)
			(NonInfoPanel)
			(ShipInfoPanel route)
		)
)