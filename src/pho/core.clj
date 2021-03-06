(ns pho.core
  (:require [pho.handler :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn parse-port [port]
  (Integer/parseInt (or port (System/getenv "PORT") "8080")))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (run-jetty app {:port port :join? false})))
