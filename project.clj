(defproject paramorse-streamer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main paramorse-streamer.core
  :uberjar-name "paramorse-streamer.jar"
  :profiles {:uberjar {:aot :all}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.5.0"]
                 [cheshire/cheshire "5.5.0"]
                 [http-kit "2.1.18"]])
