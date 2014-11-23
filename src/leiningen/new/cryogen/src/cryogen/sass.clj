(ns cryogen.sass
  (:require [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io]))

(defn sass-installed?
  "Checks for the installation of Sass."
  []
  (= 0 (:exit (sh "sass" "--version"))))

(defn find-sass-files
  "Given a Diretory, gets files, filtered to those having scss or sass extention"
  [dir]
  (->> (.list (io/file dir))
       (seq)
       (filter   (comp not nil?  (partial re-find #"(?i:s[ca]ss$)")))))

(defn compile-sass-file!
  "Given a sass file which might be in src-sass directory,
  output the resulting css in dest-sass. All error handling is
    done by sh / launching the sass command."
  [sass-file
   src-sass
   dest-sass]
  (sh "sass"
      "--update"
      (str src-sass "/" sass-file)
      (str dest-sass "/" )))


(defn compile-sass->css!
  "Given a directory src-sass, looks for all sass files and compiles them into
dest-sass. Prompts you to install sass if he finds sass files and can't find
the command. Shows you any problems it comes across when compiling. "
  [src-sass
   dest-sass]

  (let [sass-files (find-sass-files src-sass)]
    (if (seq sass-files)
      ;; I found sass files,
      ;; If sass is installed
      (if (sass-installed?)
        ;; I compile all files
        (doseq [a-file sass-files]

          (println "Compiling Sass File:" a-file)
          (let [result   (compile-sass-file! a-file src-sass dest-sass)]
            (if (zero? (:exit result))
              ;; no problems in sass compilation
              (println "Successfully compiled:" a-file)
              ;; else I show the error
              (println (:err result)))))
        ;; Else I prompt to install Sass
        (println "Sass seems not to be installed, but you have scss / sass files in "
                 src-sass
                 " - You might want to install it here: sass-lang.com")))))
