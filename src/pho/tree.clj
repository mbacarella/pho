(ns pho.tree
  (:require [clojure.java.io :as io])
  (:import java.io.File))

(defn path-to-url [path]
  ;; strip up to and including 'public'
  (clojure.string/replace path #"^.*public" ""))

(defrecord Photo [name url path])
(defrecord Photoset [name url path photos preview])

;; XXX: filter things without a .jpg, .gif, .png extension. maybe
(defn is-photo [file]
  (.isFile file))

(defn ls [path]
  (.listFiles (File. path)))

(defn names [files]
  (map (fn [f] (.getName f)) files))

(defn get-photos [path]
  (map (fn [name]
         (let [ppath (str path "/" name)]
           (Photo. name (path-to-url ppath) ppath)))
       (names (sort (fn [a b] (> (.lastModified a) (.lastModified b)))
                    (filter is-photo
                            (ls path))))))

(defn get-set-names [path]
  (names (sort (filter (fn [f] (.isDirectory f))
                       (ls path)))))

(defn get-sets-and-photos [path]
  (let [set-names  (get-set-names path)
        photos     (get-photos path)]
    [(map (fn [set-name]
            (let [sub-path   (str path "/" set-name)
                  sub-photos (get-photos sub-path)]
              (Photoset. set-name
                         (path-to-url sub-path)
                         sub-path
                         sub-photos
                         (take 5 sub-photos))))
          set-names)
     photos]))
