(ns helpers.point-helpers
  (:require [helpers.general-helpers :as g]))

(defn x [point]
  (first point))

(defn y [point]
  (second point))

(defn proper-point? [point]
  (= (count point) 2))

(defn map-pt [f point]
  (let [[x y] point]
    [(f x) (f y)]))

(defn map-pts [f p1 p2]
  (let [[x1 y1] p1
        [x2 y2] p2]
    [(f x1 x2) (f y1 y2)]))

(defn add-pts [p1 p2]
  (map-pts + p1 p2))

(defn sub-pts [p1 p2]
  (map-pts - p1 p2))

(defn mult-pts [p1 p2]
  (map-pts * p1 p2))

(defn div-pts [p1 p2]
  (map-pts / p1 p2))

(defn abs-pt [point]
  (map-pt g/abs point))

(defn signum-pt [point]
  (map-pt g/signum point))

(defn distance-between-pts [p1 p2]
  (g/distance-between-points p1 p2))

(defn random-point
  ([x-min x-max y-min y-max rand-gen]
   [(g/random-double x-min x-max rand-gen)
    (g/random-double y-min y-max rand-gen)])

  ([min max rand-gen]
   (random-point min max min max rand-gen)))

(defn angle-from-to
  "Gives the angle (in radians) from the source point to the target point
  UNTESTED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  [source-point target-point]
  (let [[sx sy] source-point
        [tx ty] target-point]
      (g/wrap (Math/atan2 (- ty sy) (- tx sx))
              0 (* 2 Math/PI))))

(defn deviate-point [point max-deviation rand-gen]
  (map-pt
    #(g/deviate % max-deviation rand-gen)
    point))