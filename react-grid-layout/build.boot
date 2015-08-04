(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/bootlaces   "0.1.11" :scope "test"]
                  [cljsjs/boot-cljsjs "0.5.0"  :scope "test"]
                  [cljsjs/react       "0.13.0-0"]])

(require '[adzerk.bootlaces :refer :all]
         '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +version+ "0.9.2-0")
(bootlaces! +version+)

(task-options!
 pom  {:project     'cljsjs/react-grid-layout
       :version     +version+
       :description "A draggable and resizable grid layout with responsive breakpoints, for React."
       :url         "https://github.com/rackt/react-router"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "http://opensource.org/licenses/MIT"}})

(deftask download-react-grid-layout []
  (download :url      "https://github.com/STRML/react-grid-layout/archive/0.9.2.zip"
            :checksum "0f2e25eb712dad64c01b3a05509d3e95"
            :unzip    true))

(deftask package []
  (comp
    (download-react-grid-layout)
    (sift :move {#"^react-.*/dist/react-grid-layout.min.js"
                 "cljsjs/react-grid-layout/production/react-grid-layout.inc.js"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-grid-layout"
               :requires ["cljsjs.react"])))
