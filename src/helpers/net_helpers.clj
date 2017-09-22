(ns helpers.net-helpers
  (:refer-clojure :exclude [read-line])

  (:require [clojure.java.io :as io]
            [clojure.core.async :refer [thread <! >!! go-loop]])

  (:import [java.io BufferedWriter BufferedReader IOException]
           [java.net Socket ServerSocket]))

(defn start-message-loop
  "Takes a channel, and starts a go-loop that prints each message sequentially.
  Stops when the channel is closed."
  [msg-chan]
  (go-loop []
    (let [[f-msg :as msgs] (<! msg-chan)]
      (when f-msg
        (apply println msgs)
        (recur)))))

(defn queue-message
  "Similar to println, except can be used across threads without causing interleaving.
  The msg-chan is expected to be a channel that was previously given to start-message-loop."
  [msg-chan & messages]
  (>!! msg-chan messages))

(defn write
  "Writes the message to the socket.
  Terminates the message with a newline, then flushes the stream."
  [^Socket sock ^String message]
  (let [^BufferedWriter w (io/writer sock)
        nl-terminated-ms (str message "\n")]

    (.write w nl-terminated-ms)
    (.flush w)))

(defn read-line
  "Reads a line from the socket."
  [^Socket sock]
  (let [^BufferedReader r (io/reader sock)]
    (.readLine r)))

; FIXME: Expect connect-f to close sock when done like with server, or use with-open?
(defn connect-to
  "Opens a socket for the given address and port, and passes it to connect-f.
   Closes the socket."
  [^String address ^long port connect-f]
  (with-open [server-sock (Socket. address port)]
    (connect-f server-sock)))

(defn pretty-address
  "Returns the host name of a socket."
  [^Socket sock]
  (.getHostAddress (.getInetAddress sock)))

(defn start-simple-server
  "Opens a single-threaded server on the given port, and accepts clients.
  Each client socket is handed to accept-f. Expects accept-f to close the given client.
  In the event of an error, error-handler will be given the server socket and exception.
  If the server socket is closed, the accept-loop will end."
  [port accept-f error-handler]
  (let [ss (ServerSocket. port)]
    (while (not (.isClosed ss))
      (try
        (let [^Socket client (.accept ss)]
          (accept-f client))

        (catch IOException e
          (error-handler ss e))))))

(defn start-async-server
  "Same as start-simple-server, but each accept-f is run in a thread pool."
  [port accept-f error-handler]
  (let [async-accept-handler #(thread (accept-f %))]
    (start-simple-server port async-accept-handler error-handler)))