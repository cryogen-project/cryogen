(ns cryogen.core
  (:require [cryogen-core.compiler :refer [compile-assets-timed]]))

(defn -main []
  (compile-assets-timed)
  (System/exit 0))
