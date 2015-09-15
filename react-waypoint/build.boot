(set-env!
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/bootlaces   "0.1.11" :scope "test"]
                  [cljsjs/boot-cljsjs "0.5.0"  :scope "test"]
                  [cljsjs/react       "0.13.0-0"]])

(require '[adzerk.bootlaces :refer :all]
         '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +version+ "1.0.3-0")
(bootlaces! +version+)

(task-options!
 pom  {:project     'cljsjs/react-waypoint
       :version     +version+
       :description "A React component to execute a function whenever you scroll to an element."
       :url         "https://github.com/brigade/react-waypoint"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "https://opensource.org/licenses/MIT"}})

(deftask download-react-waypoint []
  (download :url      "https://github.com/tobiasquinn/react-waypoint/archive/v1.0.3-tq.zip"
            :checksum "EA6672FF72A420D70ED93CAFBBD651BD"
            :unzip    true))

(deftask package []
  (comp
    (download-react-waypoint)
    (sift :move {#"^react-.*/build/npm/waypoint.js"
                 "cljsjs/react-waypoint/development/react-waypoint.inc.js"})
;;                 #"^react-.*/dist/react-waypoint.min.js"
;;                 "cljsjs/react-waypoint/production/react-waypoint.min.inc.js"})
    (sift :include #{#"^cljsjs"})
    (deps-cljs :name "cljsjs.react-waypoint"
               :requires ["cljsjs.react"])))
