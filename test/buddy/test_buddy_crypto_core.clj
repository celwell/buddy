(ns buddy.test_buddy_crypto_core
  (:require [clojure.test :refer :all]
            [buddy.crypto.core :as crypto]
            [buddy.crypto.signing :as signing]
            [buddy.crypto.keys :refer [make-secret-key]])
  (:import (java.util Arrays)))

(def secret (make-secret-key "test"))

(deftest core-utils-test
  (testing "Hex encode/decode 01"
    (let [some-bytes  (crypto/str->bytes "FooBar")
          encoded     (crypto/bytes->hex some-bytes)
          decoded     (crypto/hex->bytes encoded)
          some-str    (crypto/bytes->str decoded)]
      (is (Arrays/equals decoded, some-bytes))
      (is (= some-str "FooBar"))))
  (testing "Hex encode/decode 02"
    (let [mybytes (into-array Byte/TYPE (range 10))
          encoded (crypto/bytes->hex mybytes)
          decoded (crypto/hex->bytes encoded)]
      (is (Arrays/equals decoded mybytes)))))

(deftest sign-tests
  (testing "Signing/Unsigning with default keys"
    (let [signed (signing/sign "foo" secret)]
      (Thread/sleep 1000)
      (is (not= (signing/sign "foo" secret) signed))
      (is (= (signing/unsign signed secret) "foo"))))

  (testing "Signing/Unsigning timestamped"
    (let [signed (signing/sign "foo" secret)]
      (is (= "foo" (signing/unsign signed secret {:max-age 20})))
      (Thread/sleep 700)
      (is (nil? (signing/unsign signed secret {:max-age -1})))))

  (testing "Signing/Unsigning complex clojure data"
    (let [signed (signing/dumps {:foo 2 :bar 1} secret)]
      (is (= {:foo 2 :bar 1} (signing/loads signed secret))))))