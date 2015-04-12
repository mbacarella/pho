(ns pho.tree
  (:require [clojure.java.io :as io])
  (:import java.io.File))

;; XXX: filter things without a .jpg, .gif, .png extension. maybe
(defn is-photo [file]
  (.isFile file))

(defn ls [path]
  (.listFiles (File. path)))

(defn names [files]
  (map (fn [f] (.getName f)) files))

(defn get-photos [path]
  (names (sort (fn [a b] (> (.lastModified a) (.lastModified b)))
               (filter is-photo
                       (ls path)))))

(defn get-sets [path]
  (names (sort (filter (fn [f] (.isDirectory f))
                       (ls path)))))

(defn get-sets-and-photos [path]
  (let [sets   (get-sets path)
        photos (get-photos path)]
    [(map (fn [set] [set (take 10 (get-photos (str path "/" set)))]) sets)
     photos]))
