(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[cljsjs/boot-cljsjs "0.5.2"  :scope "test"]
                 [cljsjs/react-dom   "15.3.1-0"]
                 [cljsjs/react       "15.3.1-0"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all]
         '[boot.core :as boot]
         '[boot.tmpdir :as tmpd]
         '[clojure.java.io :as io]
         '[boot.util :refer [sh]])

(def +lib-version+ "2.0.2")
(def +version+ (str +lib-version+ "-0"))

(task-options!
 pom  {:project     'cljsjs/react-waypoint
       :version     +version+
       :description "A React component to execute a function whenever you scroll to an element."
       :url         "https://github.com/brigade/react-waypoint"
       :scm         {:url "https://github.com/cljsjs/packages"}
       :license     {"MIT" "https://opensource.org/licenses/MIT"}})

(deftask download-react-waypoint []
  (download :url      "https://github.com/brigade/react-waypoint/archive/v2.0.2.zip"
            :checksum "FB1629899B559F0134B4E11E418096CB"
            :unzip    true))

(deftask build-react-waypoint []
  (let [tmp (boot/tmp-dir!)]
    (with-pre-wrap
      fileset
      ;; Copy all files in fileset to temp directory
      (doseq [f (->> fileset boot/input-files)
              :let [target (io/file tmp (tmpd/path f))]]
        (io/make-parents target)
        (io/copy (tmpd/file f) target))
      (binding [boot.util/*sh-dir* (str (io/file tmp (format "react-waypoint-%s" +lib-version+)))]
        ((sh "npm" "install"))
        ;; patch to remove module.exports, require and esmodule export
        ((sh "patch" "build/npm/waypoint.js" "../waypoint.js.patch")))
      (-> fileset (boot/add-resource tmp) boot/commit!))))

(deftask package []
  (comp
   (download-react-waypoint)
   (build-react-waypoint)
   (sift :move {#"^.*waypoint\.js$"
                "cljsjs/react-waypoint/development/react-waypoint.inc.js"})
   (minify :in "cljsjs/react-waypoint/development/react-waypoint.inc.js"
           :out "cljsjs/react-waypoint/production/react-waypoint.min.inc.js")
   (sift :include #{#"^cljsjs"})
   (deps-cljs :name "cljsjs.react-waypoint"
     :requires ["cljsjs.react" "cljsjs.react.dom"])
   (pom)
   (jar)))
