(ns todos.delivery.web.views
  (:require [re-frame.core :as rf]
            [reagent.core :as ra]
            [todos.core.entity.todo :as t]
            [todos.core.entity :as e]))


(defn- reject-props
  "Returns a new prop map excluding the given set of props to exclude"
  [reject props]
  (into {} (filter #(nil? (reject (first %))) props)))


(defn todo-input
  [{:keys [on-save on-clear title]}]
  (let [val   (ra/atom title)
        clear #(do (reset! val "")
                   (when on-clear (on-clear)))
        save  #(let [v @val] (when (seq v)
                               (on-save v)
                               (clear)))]
    (fn [props]
      [:input
       (merge (reject-props #{:on-save :on-clear} props)
         {:value       @val
          :auto-focus  true
          :on-change   #(reset! val (.. % -target -value))
          :on-blur     save
          :on-key-down #(case (.-which %)
                          13 (save)
                          27 (clear)
                          nil)})])))


(defn todo-header
  []
  [:header.header
   [:h1 "todos"]
   [todo-input {:placeholder "What needs to be done?"
                :class  "new-todo"
                :on-save #(rf/dispatch [:create-todo %])}]])


(defn todo-item
  []
  (let [editing  (ra/atom false)]
    (fn [{:keys [::t/title ::t/complete? ::e/id] :as todo}]
      [:li {:class (str (when complete? "completed ")
                        (when @editing "editing"))}
       [:div.view
        [:input.toggle
         {:type      "checkbox"
          :checked   complete?
          :on-change #(rf/dispatch [:toggle-todo todo])}]
        [:label
         {:on-double-click #(reset! editing true)}
         title]
        [:button.destroy {:on-click #(rf/dispatch [:delete-todo id])}]]
       (when @editing
         [todo-input {:class    "edit"
                      :title    title
                      :on-save  #(rf/dispatch [:edit-title id %])
                      :on-clear #(reset! editing false)}])])))


(defn todo-list
  []
  (let [visible-todos @(rf/subscribe [:visible-todos])]
    [:ul.todo-list
     (for [[id todo] visible-todos]
       ^{:key id} [todo-item todo])]))


(defn todo-main
  []
  [:section.main
   [:input#toggle-all.toggle-all {:type "checkbox"}]
   [:label {:htmlFor "toggle-all"} "Mark all as complete"]
   [todo-list]])


(defn todo-footer
  []
  (let [[active complete] @(rf/subscribe [:todo-counts])]
    [:footer.footer
     [:span.todo-count
      [:strong active]
      (str " " (case active 1 "item" "items") " left")]
     [:ul.filters
      [:li [:a.selected {:href "#/"} "All"]]
      [:li [:a.selected {:href "#/active"} "Active"]]
      [:li [:a.selected {:href "#/completed"} "Completed"]]]
     (when (pos? complete)
       [:button
        {:class    "clear-completed"
         :on-click #(rf/dispatch [:clear-completed])}
        "Clear completed"])]))


(defn todo-app
  []
  [:section.todoapp
   [todo-header]
   [todo-main]
   [todo-footer]])
