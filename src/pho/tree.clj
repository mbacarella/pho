(ns pho.tree
  (:require [clojure.java.io :as io])
  (:import java.io.File))

(defn path-to-url [path]
  ;; strip up to and including 'public'
  (clojure.string/replace path #"^.*public/photos/" ""))

(defrecord Photo [name url path])
(defrecord Photoset [name url path photos preview])

(defn is-photo [file]
  (and (.isFile file)
       (let [lfile (clojure.string/lower-case file)
             ew    (fn [suffix] (.endsWith lfile suffix))]
         (or (ew ".jpg")
             (ew ".jpeg")
             (ew ".gif")
             (ew ".png")))))

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
