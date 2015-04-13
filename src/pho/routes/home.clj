(ns pho.routes.home
  (:require [pho.layout :as layout]
            [pho.tree :as tree]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [clojure.java.io :as io]))

(def public-base "resources/public/photos")

(defn view-photoset [path]
  (let [[photosets photos] (tree/get-sets-and-photos public-base)]
    (layout/render "home.html"
                   {:photosets photosets
                    :photos photos})))

(defn home-page []
  (view-photoset public-base))

(defn set-page [setname]
  (view-photoset (str public-base "/" setname)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET ["/set/:setname" :setname #".*"] [setname] (set-page setname)))
