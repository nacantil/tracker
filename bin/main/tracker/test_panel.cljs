(ns tracker.test-panel
    (:require [reagent.core :as reagent]
              [reagent.dom :as rdom]
              ["react-select" :default Select]
              ["react-draft-wysiwyg" :refer (Editor)]
              ["draft-js" :refer (EditorState)]
			  [clojure.pprint :refer [pprint]]
	          [tracker.components.modal :as m]
	          [reagent-contextmenu.menu :as menu]
    )
)

(def app-state (reagent/atom {:sort-val :forecast :ascending true}))

(defn select 
	[{:keys [components on-change options is-multi is-searchable value]}]
		(reagent/create-element
    		Select
    			#js {:components components
    			     :onChange on-change
    			     :isMulti is-multi
    			     :value (clj->js value)
    			     :isSearchable is-searchable
    			     :options (clj->js options)
    			}
    	)
)
         
(defn editor 
	[{:keys [editor-state toolbar-class-name wrapper-class-name editor-class-name on-editor-state-change on-content-editor-state-change on-change]}]
   		(reagent/create-element
    		 Editor
     			#js {
     				;;:editorState editor-state
          			;;:toolbarClassName toolbar-class-name
          			;;:wrapperClassName wrapper-class-name
          			;;:editorClassName editor-class-name
          			:onChange on-change
          			:onEditorStateChange on-editor-state-change
          			:onContentStateChange on-content-editor-state-change
         		}
   		)
)

(def table-contents
	[
	  	{:id 1 :forecast "Forecast-1" :ship "Moolenaar"  :location "Vim" 	:start-dtg "111111" :limits "1/1/1" :limits-dtg "111111" :routing "Routing-1"}
	   	{:id 2 :forecast "Forecast-2" :ship "Stallman"   :location "GNU" 	:start-dtg "222222" :limits "2/2/2" :limits-dtg "222222" :routing "Routing-2"}
	   	{:id 3 :forecast "Forecast-3" :ship "Ritchie"    :location "C" 		:start-dtg "333333" :limits "3/3/3" :limits-dtg "333333" :routing "Routing-3"}
	   	{:id 4 :forecast "Forecast-4" :ship "Hickey"     :location "Clojure" :start-dtg "444444" :limits "4/4/4" :limits-dtg "444444" :routing "Routing-4"}
	   	{:id 5 :forecast "Forecast-5" :ship "Van Rossum" :location "Python" 	:start-dtg "555555" :limits "5/5/5" :limits-dtg "555555" :routing "Routing-5"}
	   	{:id 6 :forecast "Forecast-6" :ship "Torvalds"   :location "Linux" 	:start-dtg "666666" :limits "6/6/6" :limits-dtg "666666" :routing "Routing-6"}
	   	{:id 7 :forecast "Forecast-7" :ship "Katz"       :location "Ember" 	:start-dtg "777777" :limits "7/7/7" :limits-dtg "777777" :routing "Routing-7"}
	]
)
   
(def dimension-options
	[
		{:label "label-1" :value "value-1"}
	   	{:label "label-2" :value "value-2"}
	   	{:label "label-3" :value "value-3"}
	   	{:label "label-4" :value "value-4"}
	   	{:label "label-5" :value "value-5"}
	   	{:label "label-6" :value "value-6"}
	   	{:label "label-7" :value "value-7"}
	   	{:label "label-8" :value "value-8"}
	   	{:label "label-9" :value "value-9"}
   ]
)

(defn update-sort-value 
	[new-val]
  		(if (= new-val (:sort-val @app-state))
    		(swap! app-state update-in [:ascending] not)
    		(swap! app-state assoc :ascending true)
    	)
  		(swap! app-state assoc :sort-val new-val)
)

(defn sorted-contents 
	[]
  		(let [sorted-contents (sort-by (:sort-val @app-state) table-contents)]
    		(if (:ascending @app-state)
      			sorted-contents
      			(rseq sorted-contents)
      		)
      	)
)

(defn table 
	[]
		[:table
			[:thead
				[:tr
	    			[:th {:width "200" :on-click #(update-sort-value :forecast)} "FORECAST"]
	    	    	[:th {:width "200" :on-click #(update-sort-value :ship)} "SHIP"]
				    [:th {:width "200" :on-click #(update-sort-value :location)} "LOCATION"]
	    			[:th {:width "500" :on-click #(update-sort-value :start-dtg)} "START-DTG"]
	 			    [:th {:width "200" :on-click #(update-sort-value :limits)} "LIMITS"]
	    			[:th {:width "500" :on-click #(update-sort-value :limits-dtg)} "LIMITS-DTG"]
	    	    	[:th {:width "200" :on-click #(update-sort-value :routing)} "ROUTING"]
				]
			]
	    	[:tbody
	    		(for [route (sorted-contents)]
	    	    	^{:key (:id route)} 
	    	    	[:tr 
	    	    		[:td (:forecast route)] 
	         			[:td (:ship route)] 
	         			[:td (:location route)]
	         			[:td (:start-dtg route)]
	         			[:td (:limits route)]
	         			[:td (:limits-dtg route)]
	         			[:td (:routing route)]
					]
				)
			]
		]
)
         
(def item-selections (reagent/atom ""))


(defn TestPanel 
	[]
		[:div 
		    [m/modal-window]
		    
		  	[menu/context-menu]
	
			[:div {:style {:fontSize 20 :fontWeight "bold" :text-align "center"}} "React Select Import Example"]	
			[select
				{
					:is-multi true
					:is-searchable true
					:value @item-selections
					:options dimension-options
					:on-change (fn [selected-items] (reset! item-selections selected-items) (js/console.log @item-selections))
				}
			]
		
		  	[:div {:style {:fontSize 20 :fontWeight "bold" :text-align "center"} :on-context-menu
	       		(fn [evt]
	        		(menu/context! 
	          			evt
	          			["Some title"             ; <---- string is a section title
	           				["my-fn" #(prn "my-fn")]
	           				[
	           					[:span "my-other-fn"] #(prn "my-other-fn")
	           				] ; <---- the name is a span
	           				:divider                    ; <--- keyword is a divider
	           				[
	           					[:span 
	             					[:span.cmd "Copy"] 
	             					[:span.kbd.text-muted "ctrl-c"]
	             				] ; <--- some classes to show a keyboard shortcut
	            				#(prn "Copy")
	            			]
	           				["Submenu" 
	            				["Submenu title" ["Submenu item 1" #(prn "Item 1")]]
	            			]
	            		]
	            	)
	            )}
            
	           	"Reagent-Contextmenu Example" 
	        ] ; <-- submenus are simply nested menus.
	    
			[:br]
			[:br]
		
			[:div {:style {:fontSize 20 :fontWeight "bold" :text-align "center"}} "Table Sort Example"]	
	  		[:div {:style {:margin "auto" :padding-top "30px" :width "600px"}}
	    		[table]
	    	]

			[:br]
			[:br]

			[:div {:style {:fontSize 20 :fontWeight "bold" :width "100%" :text-align "center"}} "React Draft WYSIWYG Import Example"]	
			[:div {:style {:border "1px solid black"}}
				[editor 
					{
						:on-change (fn[event] (js/console.log "on-change") (js/console.log event)) ;; onChange
						:on-editor-state-change (fn[event] (js/console.log "on-editor-state-change") (js/console.log event)) ;;onEditorStateChange
					}
				]
			]
	
			[:br]
			[:br]
	
			[:div {:style {:fontSize 20 :fontWeight "bold" :width "100%" :text-align "center"}} "Web Component Import Example"]	
			[:forecast-table {:data-source "testData.json" :destination-url "http://localhost:5600/update"}] ;;Using Jonathan's web-component which I imported ...
	    ]
)