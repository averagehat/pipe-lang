(require '[instaparse.core :as insta])
(require '[defun :refer [defun]])
(def ws-parse (insta/parser "WS : ' '+"))
(def parse (insta/parser (slurp "src/clj_make/make.grammar") :auto-whitespace ws-parse))
(def tree (parse (slurp "src/clj_make/working-example.mk")))
(def res (insta/transform {:filename str :extension str} tree ))
;;(defun transform 
;;  ([[contents]] contents)
;;  ([[kw1 [kw2 contents]]] (assoc {} kw1 (transform contents)))  )
;;  ([[kw & rest]] (assoc {} kw (transform contents)))  )
;;  ;([[kw & contents]] (assoc {} kw (transform contents))))
;;use networkx for top sort?
;;convert to pydoit
;;could template makefiles as well
;;maybedep should translate to a task_dep instead of file_dep?
;;:targets list
;;:file_dep list
;;:actions  list
;;:task_dep
;;;;(insta/transform {:filename str} ((insta/parser (slurp "src/clj_make/working.grammar") :auto-whitespace ws-parse) (slurp "src/clj_make/working-exaple.mk")))
;;
;;
;;bash_environ = {
;; "OUTS" : ' '.join(targets), ;; or export to environment
;; "INS"  : ' '.join(dependencies),
;; "OUT" : target[0], ;; or export to environment
;;  "IN"  : dependencies[0] }
;;os.environ.update(bash_environ)
;;
;;{ "$OUTS" : "%(targets)", ;; or export to environment
;;  "$INS"  : "%(dependencies)", 
;; " $OUT" : "%(targets)", ;; or export to environment
;;  "$IN"  : "%(dependencies)" }
;;'\n'.join
;;rule -> ruleHeader [target, dependency, orderDeps]
;;     -> codeBlock [codeline]
;;
;;[import] -> try import (inc. in python functions/scripts); add to top of python scripts
;;[requires] -> check path for executable
;;
;;  
;;[target] -> targets
;;[dependency] -> :file_dep
;;[orderDependencies] -> task_dep
;;bashBlcok  [codeline] ->  actions
;;pythonBlock [codeline] -> pythonfunc
;;
;;in bash, $dependencies is space-separated list, in python real list
;;could eval python
;;
;;
;;some settings object
;;$OPTS.BWA.THREADS
;;opts.bwa.threads
;;[python] block => def foo(dependencies, targets) etc.
;;inputs => dependencies, outputs => targets
;;bash block becomes %(dependencies or the like
;;
;;
;;top sort and then use task_dep? and ipnuts, outputs become those files
;;which match those regexes. 
;;top. sort using regex subsetting
;;
;;
;;
;;
;;def find(xs, k, k2=None):
;;    res = filter(_[0].name == k, t)
;;    if not k2:
;;        return res or find(res, k)
