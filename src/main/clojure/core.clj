(ns core
  (:require [instaparse.core :as insta]))

(def ws-parse (insta/parser "WS : ' '+"))
(def parse (insta/parser (slurp "data/make.grammar") :auto-whitespace ws-parse))


(defn run-example []
  (def tree (parse (slurp "data/working-example.mk")))
  (def res (fix-tree tree))
  res)

(defn to-str [kw] (comp #(vector kw %) str))
 
(defn fix-tree [tree]
  (insta/transform {:filename   (to-str :filename)
                    :extension  (to-str :extension)
                    :glob       (to-str :glob)
                    :regex      (to-str :regex)
                    :orderDependency #([:orderDependency (second %)])
;                    :bashBlock #(vector :bashBlock  (str (map second %))) 
;                    :pythonBlock #(vector :pythonBlock (str (map second %)))}
                   }  tree)  )


(def full-parse (comp fix-tree parse slurp))
(def args ["data/make.grammar"])
(defn -main
  [& args]
  (let [tree (full-parse (first args))] 
    (if-not (second args) 
      (println tree) 
      (spit (second args) tree))))
