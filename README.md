# `clj-baseball`

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.slothrop/clj-baseball.svg)](https://clojars.org/org.clojars.slothrop/clj-baseball)

This is a Clojure library inspired by Python's `pybaseball`. It scrapes HTML and CSV data on baseball websites and makes it friendly to work with at the REPL. Baseball data is hugely plentiful and granular; I think it makes for an excellent match with a data-driven language like Clojure. My intention in building this library (besides having fun) is to offer a way for users to learn Clojure with abundant real world data. Even if the sport of baseball does not interest you, its ready-to-use data provides an excellent opportunity to explore the language ecosystem without hacking on your own web app or seeking out inferior APIs, second-rate "dummy data", or any other inadequate and frustrating option.

## Code organization

The public API is implemented in `com/slothrop/clj_baseball`. The core functions for interacting with the library are in `api.clj` in this directory. All folders outside of the aforementioned directory are still under development.

## Python interop!

This is an experimental feature, but for now basic DataFrames are available thanks to the excellent `libpython-clj` library. Two idiomatic Clojure wrappers around some DataFrame class methods (`DataFrame.from_dict` and `DataFrame.from_records`) are provided. To enable Python support, you must of course have Python installed on your machine, and use one of the `jdk-??` aliases specified in `deps.edn`, based on your version of Java. This includes modules required only by `libpython-clj` when the JVM starts. 

If you clone this repo, you can use these features. However, it is recommended to instead use the JVM-native functionality provided by `tech.ml.dataset` and `tablecloth`. It requires less dependencies for a far greater breadth of immediately available tabular operations. Moreover, the Python interop namespace will be removed in the `v0.4.0` release.