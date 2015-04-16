(ns pho.thumb
  (:require [clojure.java.io :as io])
  (:import  [javax.imageio.ImageIO]
            [java.awt.image.BufferedImage]))

(defn convert-to-png-and-resize-native [file-in file-out new-width]
  (let [img         (javax.imageio.ImageIO/read (io/file file-in))
        orig-width  (.getWidth img)
        orig-height (.getHeight img)
        new-height  (* orig-height (/ new-width orig-width))
        imgtype     (java.awt.image.BufferedImage/TYPE_INT_ARGB)
        simg        (java.awt.image.BufferedImage. new-width new-height imgtype)
        g           (.createGraphics simg)]
    (.drawImage g img 0 0 new-width new-height nil)
    (.dispose g)
    (javax.imageio.ImageIO/write simg "png" (io/file file-out))))

(defn convert-to-jpg-and-resize-shellout [file-in file-out new-width]
  (clojure.java.shell/sh "/usr/bin/convert"
                         "-resize"
                         (str new-width)
                         "--"
                         file-in
                         file-out))