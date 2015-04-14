(ns pho.routes.home
  (:require [pho.layout :as layout]
            [pho.tree :as tree]
            [pho.thumb :as thumb]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [clojure.java.io :as io]))

(def public-base "resources/public")
(def photos-base (str public-base "/photos"))
;; on my box, thumbs-base is a symlink to /var/tmp
(def thumbs-base (str thumbs-base "/thumbs"))

(defrecord Breadcrumb [name url])

(defn breadcrumbs-of-setname [setname]
  (second (reduce (fn [[base-url acc] name]
                    (let [url (str base-url "/" name)]
                      [url (conj acc (Breadcrumb. name url))]))
                  ["" []]
                  (filter (fn [x] (not (= "" x))) (clojure.string/split setname #"/")))))

(defn make-containing-dirs [path]
  (let [parts   (clojure.string/split path #"/")
        dirname (clojure.string/join "/" (take (- (count parts) 1) parts))]
    (clojure.java.shell/sh "/bin/mkdir" "-p" "--" dirname)))
    
;; XXX: validate setname and path don't have ".."
(defn view-photoset [setname path]
  (let [[photosets photos] (tree/get-sets-and-photos path)]
    (layout/render "home.html"
                   {:breadcrumbs (breadcrumbs-of-setname setname)
                    :photosets photosets
                    :photos photos})))

(defn home-page []
  (view-photoset "" photos-base))

;; XXX: validate path doesn't have ".."
(defn thumb-gen [path]
  (let [thumb-path (str thumbs-base "/" path)]
    ;; populate thumb cache if doesn't exist yet
    (if (not (.exists (java.io.File. thumb-path)))
      (let [orig-path (str photos-base "/" path)]
        (make-containing-dirs orig-path)
        (thumb/convert-to-png-and-resize (str photos-base "/" path) 300)))
    (ring.util.response/redirect (str "/thumbs" path))))

;; XXX: validate setname doesn't have ".."
(defn set-page [setname]
  (view-photoset setname (str photos-base "/" setname)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET ["/set/:setname" :setname #".*"] [setname] (set-page setname))
  (GET ["/thumb-gen/:thumbpath" :thumbpath #".*"] [thumbpath] (thumb-gen thumbpath)))
