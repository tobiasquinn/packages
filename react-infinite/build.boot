(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/bootlaces   "0.1.11" :scope "test"]
                  [cljsjs/boot-cljsjs "0.5.0"  :scope "test"]
                  [cljsjs/react       "0.13.0-0"]])

(require '[adzerk.bootlaces :refer :all]
         '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +version+ "0.4.1-0")
(bootlaces! +version+)

(task-options!
 pom  {:project     'cljsjs/react-infinite
       :version     +version+
       :description "A browser-ready efficient scrolling container based on UITableView, for React."
       :url         "https://github.com/seatgeek/react-infinite"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"BSD-2" "http://opensource.org/licenses/BSD-2-Clause"}})

(deftask download-react-infinite []
  (download :url      "https://github.com/seatgeek/react-infinite/archive/0.4.1.zip"
            :checksum "A4587ED0FF5089C38366B49C8361CC73"
            :unzip    true))

(deftask package []
  (comp
    (download-react-infinite)
    (sift :move {#"^react-.*/dist/react-infinite.js"
                 "cljsjs/react-infinite/development/react-infinite.inc.js"
                 #"^react-.*/dist/react-infinite.min.js"
                 "cljsjs/react-infinite/production/react-infinite.min.inc.js"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-infinite"
               :requires ["cljsjs.react"])))
