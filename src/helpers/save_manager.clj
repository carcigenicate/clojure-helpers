(ns helpers.save-manager
  (:require [clojure.java.io :refer [as-file] :as io])
  (:import [java.io.File])
  (:refer-clojure :exclude [load]))

(defrecord Save-Manager [save-path extension])

(def ^:private default-save-path "./")
(def ^:private default-extension "")

(defn- full-save-path [manager name]
  (str (:save-path manager) name \. (:extension manager)))

(defn- create-save-path [path]
  (.mkdirs
    (as-file path)))

(defn- attach-end-slash [path]
  (let [end-char (last path)]
    (if (or (= end-char '\) (= end-char '/)))
      path
      (str path '/))))

(defn new-save-manager
  ([save-path extension]
   (let [path (attach-end-slash save-path)
         sm (->Save-Manager path extension)]
     (do
       (create-save-path path)
       sm)))

  ([extension]
   (new-save-manager default-save-path extension))

  ([]
   (new-save-manager default-extension)))

(defn save-exists? [manager name]
  (.isFile ; I don't know why it can't resolve this. It works.
    (as-file
      (full-save-path manager name))))
#_
(defn save [manager obj save-name] ; pr corrupts functions causing a Runtime Error
  (spit (full-save-path manager save-name) (with-out-str (pr obj))))

(defn stringify-object [object]
  (let [str-obj (with-out-str (pr object))
        str-obj' (if (= (first str-obj) \#)
                   (subs str-obj 1)
                   str-obj)]
    str-obj))

(defn save [manager obj save-name]
  (let [path (full-save-path manager save-name)]
    (clojure.java.io/make-parents path)

    (spit path (with-out-str (pr obj)))))


(defn load [manager save-name]
  (-> (full-save-path manager save-name) slurp read-string))

(defn load-with-default
  "If a save by the given name exists, it's loaded and returned.
  If the save doesn't exist, the provided default is saved, and returned."
  [manager save-name default]
  (if (save-exists? manager save-name)
    (load manager save-name)
    (do
      (save manager default save-name)
      default)))

