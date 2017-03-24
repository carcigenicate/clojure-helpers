(ns helpers.key-manager)

(defn new-key-manager []
  #{})

(defn press-key [key-manager key]
  (conj key-manager key))

(defn release-key [key-manager key]
  (disj key-manager key))

(defn key-pressed?
  ([key-manager key]
   (boolean (key-manager key)))

  ([key-manager]
   (not-empty key-manager)))

(defn reduce-pressed-keys [key-manager f acc]
  (reduce f acc key-manager))