(ns tracker.forecast
	(:require 
	    [cljs.pprint :refer [pprint]]
		[reagent.core :as r]
	    [reagent.dom :as dom]
        [tracker.components.modal :as m]
        ["react-draft-wysiwyg" :refer (Editor)]
        [reagent-modals.modals :as reagent-modals] ;; I need to expose more "modal" functionality ...
        [tracker.utils :as utils]
    )
)

;; For the text editors within the Forecast Editor UI
(def text-content-1 (atom ""))
(def text-content-2 (atom ""))

(defn forecast-table 
	[data-source destination-url]
		[:forecast-table {:data-source data-source :destination-url destination-url}]
)

;; This is the "wrapper" for the REACT WYSIWYG Text Editor ...
(defn editor 
	[{:keys [editor-state toolbar-class-name wrapper-class-name editor-class-name on-editor-state-change on-content-editor-state-change on-change]}]
		(r/create-element
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

(defn onChangeTextEditor1
	[event]
		(let [text (aget (get event.blocks 0) "text")] 
			(js/console.log "onChangeTextEditor1")
			(reset! text-content-1 text)
			(js/console.log @text-content-1)
		)
)

(defn onChangeTextEditor2
	[event]
		(let [text (aget (get event.blocks 0) "text")] 
			(js/console.log "onChangeTextEditor2")
			(reset! text-content-2 text)
			(js/console.log @text-content-2)
		)
)

;; Experimenting with save HTML as a WORD document ...
(defn saveAsWORD 
	[event]
	(let
		[
			header "<html xmlns:o='urn:schemas-microsoft-com:office:office xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'> <head><meta charset='utf-8'><title>Export HTML to Word Document with JavaScript</title></head><body>"
    		footer "</body></html>"
    		body (js/document.getElementById "document-test")
    		sourceHTML (str header (str (str body.innerHTML (str (str "<p>" @text-content-1) (str "<p>" @text-content-2))) footer))
    		source (str "data:application/vnd.ms-word;charset=utf-8," (js/encodeURIComponent sourceHTML))
    		fileDownload (js/document.createElement "a")
		]

    	(js/document.body.appendChild fileDownload)
    	(set! fileDownload.href source)
    	(set! fileDownload.download "HTML2DOC.doc")
    	(.click fileDownload)
    	(js/document.body.removeChild fileDownload)
    )
)

;; Experimenting with save HTML as a PDF document ...
(defn saveAsPDF 
	[event]
		(let 
			[
				element (js/document.getElementById "document-test")
				;;doc (new js/jsPDF "p" "pt" "letter")
				parent (js/document.createElement "div")
				textElement1 (js/document.createElement "div")
				p (js/document.createElement "p")
				textElement2 (js/document.createElement "div")
			]

			(.append parent element)
			(set! textElement1.innerHTML @text-content-1)
			(.appendChild parent textElement1)

			(.appendChild parent p)

			(set! textElement2.innerHTML @text-content-2)
			(.appendChild parent textElement2)
			
			;;(.fromHTML doc
			;;	parent
			;;)
			;;(js/setTimeout (fn[event] (.save doc "HTML2PDF.pdf")) 2000)

		    (js/createPDF parent "HTML2PDF.pdf") ;; This function is implemented in index.html ...
	    )
)

(defn ForecastEditor 
	[route option]
		[m/modal-body 
      	[m/modal-header {:key "modal-header" :close-button true}
      	   [:span {:key "Stubbed Modal Window" :style {:font-size "20px" :font-weight "bold"}} option]
      	]
      	[:div {:id "document-test" :key "document-test"}
      		;;[:object {:key (utils/generate-uuid) :type "text/html" :data "http://localhost/tracker/webcomponents-main/forecast-table/index.html" :style {:width "500px" :height "450px"}}]
      		[:img {:id "image-1" :key (utils/generate-uuid) :src "https://picsum.photos/id/237/200/300"}]
      		[:img {:id "image-2" :key (utils/generate-uuid) :src "https://cdn.pixabay.com/photo/2019/07/22/10/11/darth-vader-4354735__340.jpg"}]
      	]
      	[:div {:key (utils/generate-uuid) :style {:width "1000px" :height "600px"}}
      		[:div {:key (utils/generate-uuid)}
      			[:div {:key (utils/generate-uuid) :style {:float "left" :height "200px" :width "50%" :overflow "auto" :border "1px solid black"}}
	  				[editor 
						{
							:on-change (fn[event] (onChangeTextEditor1 event)) ;; onChange
						}
	  				]
	  			]
      			[:div {:key (utils/generate-uuid) :style {:float "right" :height "200px" :width "50%" :overflow "auto" :border "1px solid black"}}
	  				[editor 
	  					{
							:on-change (fn[event] (onChangeTextEditor2 event)) ;; onChange
						}
	  				]
	  			]
	  		]
			[:forecast-table {:data-source "testData.json" :destination-url "http://localhost:5600/update"}] ;;Using Jonathan's web-component which I imported ...
	  		[:div {:key (utils/generate-uuid) :style {:float "right"}}
	  			[:button {:key (utils/generate-uuid) :on-click (fn[event](saveAsWORD event)(reagent-modals/close-modal!))} "Save To Word"]
	  			[:button {:key (utils/generate-uuid) :on-click (fn[event](saveAsPDF event)(reagent-modals/close-modal!))} "Save To PDF"]
	  			[:button {:key (utils/generate-uuid) :on-click #(reagent-modals/close-modal!)} "Cancel"]
	  		]
	  	]
   	]
)