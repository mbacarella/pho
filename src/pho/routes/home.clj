(ns pho.routes.home
  (:require [pho.layout :as layout]
            [pho.tree :as tree]
            [pho.thumb :as thumb]
            [pho.cmd :as cmd]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            )
  (:use [taoensso.timbre :only [trace debug info warn error fatal]]))

(def public-base "resources/public")
(def photos-base (str public-base "/photos"))
;; on my box, thumbs-base is a symlink to /var/tmp/thumbs
(def thumbs-base (str public-base "/thumbs"))

(defrecord Breadcrumb [name url])

(defn breadcrumbs-of-setname [setname]
  (second (reduce (fn [[base-url acc] name]
                    (let [url (str base-url "/" name)]
                      [url (conj acc (Breadcrumb. name url))]))
                  ["" []]
                  (filter #(not (= "" %)) (clojure.string/split setname #"/")))))

(defn make-containing-dirs [path]
  (let [parts   (clojure.string/split path #"/")
        dirname (clojure.string/join "/" (take (- (count parts) 1) parts))]
    (cmd/exec ["/bin/mkdir" "-p" "--" dirname ])))

(defn abort-on-path-trickery [s]
  (if (.contains s "..")
    (throw (Exception. "path trickery detected"))))

(defn view-photoset [setname path]
  (abort-on-path-trickery setname)
  (abort-on-path-trickery path)
  (let [[photosets photos] (tree/get-sets-and-photos path)]
    (layout/render "home.html"
                   {:breadcrumbs (breadcrumbs-of-setname setname)
                    :photosets photosets
                    :photos photos})))

(defn home-page []
  (view-photoset "" photos-base))

(defn thumb-gen [path]
  (abort-on-path-trickery path)
  (let [ext        ".t.jpg"
        orig-path  (str photos-base "/" path)
        thumb-path (str thumbs-base "/" path ext)]
    (if (not (.exists (io/file orig-path)))
      (throw (Exception. "original photo does not exist")))
    ;; populate thumb cache if doesn't exist yet
    (if (not (.exists (io/file thumb-path)))
      (do (make-containing-dirs thumb-path)
          (thumb/convert-to-jpg-and-resize-shellout orig-path thumb-path 200)))
    (ring.util.response/redirect (str "/thumbs/" path ext))))

(defn set-page [setname]
  (abort-on-path-trickery setname)
  (view-photoset setname (str photos-base "/" setname)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET ["/set/:setname" :setname #".*"] [setname] (set-page setname))
  (GET ["/thumb-gen/:thumbpath" :thumbpath #".*"] [thumbpath] (thumb-gen thumbpath)))
