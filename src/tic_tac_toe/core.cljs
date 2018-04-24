(ns tic-tac-toe.core
    (:require
      [reagent.core :as r]))

(def initial-app-state {:board        (into []
                                            (repeat 9 " "))
                        :current-user "X"})

(defonce app-state (r/atom initial-app-state))

;; in a board of 9 cells in the following configuration of indices
;; 0 1 2
;; 3 4 5
;; 6 7 8
;; , the vector of sets below represent all possible "three in a row" configurations.
;; Note that they individually only each account for one "three in a row" configuration,
;; of which there might be two on a traditional board at the end of a game.

(def three-in-a-row-win-conditions
  [#{0 1 2}
   #{3 4 5}
   #{6 7 8}
   #{0 3 6}
   #{1 4 7}
   #{2 5 8}
   #{0 4 8}
   #{6 4 2}])

(defn winner? [{:keys [board] :as app-state}]
  (let [indices-of       (fn [player board]
                           (->> board
                                (keep-indexed (fn [idx cell]
                                                (when (#{player} cell)
                                                  idx)))
                                set))
        xs               (indices-of "X" board)
        os               (indices-of "0" board)
        winning-indices? (fn [indices]
                           (->> three-in-a-row-win-conditions
                                (some (fn [win-condition]
                                        (clojure.set/subset? win-condition indices)))))
        x-won?           (winning-indices? xs)
        o-won?           (winning-indices? os)]
    (cond
      x-won? "X"
      o-won? "0"
      :else  nil)))

(defn valid-toggle? [idx {:keys [board] :as app-state}]
  (and (not (winner? app-state))
       (-> (get board idx)
           #{" "})))

(defn user-toggles [idx {:keys [current-user] :as app-state}]
  (if-not (valid-toggle? idx app-state)
    app-state
    (let [toggled-app-state (-> app-state
                                (update :board assoc idx current-user)
                                (update :current-user (fn [current-user]
                                                        (case current-user
                                                          "X" "0"
                                                          "0" "X"))))]
      toggled-app-state)))


(defn home-page []
  [:div {:style {:width           "100%"
                 :display         :flex
                 :justify-content :center}}
   [:div {:style {:display        :flex
                  :flex-direction :column}}
    [:div "Current player: " (-> @app-state :current-user)]
    [:div
     (->> @app-state
          :board
          (map-indexed (fn [idx cell]
                         (let [em-size (str 5 "em")]
                           ^{:key idx}
                           [:div {:on-click (fn [event]
                                              (swap! app-state (partial user-toggles idx)))
                                  :style    {:height              em-size
                                             :width               em-size
                                             :border              "1px solid black"
                                             :display             :flex
                                             :justify-content     :center
                                             :align-items         :center
                                             :-moz-user-select    :none
                                             :-webkit-user-select :none
                                             :-ms-user-select     :none
                                             :user-select         :none
                                             :color               (case cell
                                                                    " " :black
                                                                    "0" :red
                                                                    "X" :blue)}}
                            [:b cell]])))
          (partition 3)
          (map-indexed (fn [idx three-cells]
                         ^{:key idx}
                         [:div {:style {:display :flex}}
                          three-cells])))]
    (when-let [the-winner (-> @app-state winner?)]
      [:div
       [:div (str "The winner is " the-winner " ! Congratulations!")]
       [:button {:on-click (fn [event]
                             (reset! app-state initial-app-state))}
        "Play again?"]])]])

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
