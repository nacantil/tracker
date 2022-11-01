(ns tracker.components.modal
  (:require [reagent-modals.modals :as reagent-modals]))


;;; Make sure to create the modal-window element somewhere in the dom.
;;; Recommended: at the start of the document.
(defn modal-window []
  "Mount a modal window to dom"
  (reagent-modals/modal-window))

(defn modal!
  "Update and show the modal window. `reagent-content' is a normal
   reagent component. 'configs' is an optional map of advanced
   configurations:
   - :shown -> a function called once the modal is shown.
   - :hide -> a function called once the modal is asked to hide.
   - :hidden -> a function called once the modal is hidden.
   - :size -> Can be :lg (large) or :sm (small). Everything else defaults to medium.
   - :keyboard -> if true, `esc' key can dismiss the modal. Default to true.
   - :backdrop -> true (default): backdrop.
                  \"static\" : backdrop, but doesn't close the model when clicked upon.
                  false : no backdrop."
  ([reagent-content] (reagent-modals/modal! reagent-content))
  ([reagent-content configs] (reagent-modals/modal! reagent-content configs)))


(defn modal-header [{:keys [close-button]} & content]
  "Pre-configured modal header. 'content' is title of header.
   Optional map of configurations:
   - :close-button -> true : include pre-configured close button.
                      false (default) : no close button
   "
  [:div {:class "modal-header"}
   [:h5 {:class "modal-title"} content
    (when close-button
      [reagent-modals/close-button])]])

(defn modal-body [& content]
  "Pre-configured modal body. 'content' is normal reagent component"
  [:div {:class "modal-body"} content])
