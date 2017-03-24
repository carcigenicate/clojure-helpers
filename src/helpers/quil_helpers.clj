(ns helpers.quil-helpers
  (:require [helpers.general-helpers :as g]
            [quil.core :as q])

  (:import [processing.core PFont]))

(defn random-color [rand-gen]
  (mapv (fn [_] (g/random-int 0 256 rand-gen)) (range 3)))

(defn random-angle [rand-gen]
  (g/random-double 0 q/TWO-PI rand-gen))

(defn deviate-angle [angle max-deviation rand-gen]
  (g/wrap
    (g/deviate angle max-deviation rand-gen)
    0 q/TWO-PI))

(defn mouse-pos []
  [(q/mouse-x) (q/mouse-y)])

(defn draw-lines [[x y] messages ^PFont font color hor-spacing-factor]
  (q/text-font font)
  (apply q/fill color)

  (doseq [i (range (count messages))
          :let [y' (+ y (* (.getSize font) i hor-spacing-factor))]]
    (q/text (messages i) x y')))

(defn brighten-color
  "Brightens a color by the given ammount.
  Clamps each color-channel to inclusive [0 255].
  Forcing a color to brighten/darken when one color is already being clamped will cause the
   color to irreparibly darken the color towards the clamped channel.
  Colors can be darkened by suplying a negative brightness."
  [color brighten-by]
  (mapv #(g/clamp (+ % brighten-by) 0 255)
        color))

(defn invert-color [color]
  (let [color-max 255]
    (mapv #(g/clamp (- color-max %) 0 color-max) color)))

(defmacro with-weight [weight & body]
  `(let [old-weight# (.strokeWeight (q/current-graphics))]
     (do
       (q/stroke-weight ~weight)
       ~@body
       (q/stroke-weight old-weight#))))

(defmacro with-font [font & body]
  `(let [old-font# (.textFont (q/current-graphics))]
     (do
       (q/text-font ~font)
       ~@body
       (q/text-font old-font#))))

(def white [255 255 255])
(def black [0 0 0])