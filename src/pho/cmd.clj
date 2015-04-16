(ns pho.cmd
  (:import [java.lang.Runtime]))

(defn exec [cmd-and-args]
  (. (Runtime/getRuntime) exec (into-array String cmd-and-args )))