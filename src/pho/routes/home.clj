(ns pho.routes.home
  (:require [pho.layout :as layout]
            [pho.tree :as tree]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]))

(defn home-page []
  (let [[sets photos] (tree/get-sets-and-photos "/home/mbac/photos")]
    (layout/render "home.html"
                   {:sets sets
                    :photos photos})))

(defroutes home-routes
  (GET "/" [] (home-page)))
