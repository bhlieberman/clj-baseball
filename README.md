# `clj-baseball`

This is a Clojure library inspired by Python's `pybaseball`. It scrapes HTML and CSV data on baseball websites and makes it friendly to work with at the REPL. Baseball data is hugely plentiful and granular; I think it makes for an excellent match with a data-driven language like Clojure. My intention in building this library (besides having fun) is to offer a way for users to learn Clojure with abundant real world data. Even if the sport of baseball does not interest you, its ready-to-use data provides an excellent opportunity to explore the language ecosystem without hacking on your own web app or seeking out inferior APIs, second-rate "dummy data", or any other inadequate and frustrating option.

## Code organization

The library is divided into a few primary namespaces. At press time, the most complete functionality is available in `com/slothrop/statcast`, where the user will find the functions that query MLB's Statcast dataset for real-time, pitch- and swing-level data. Query validation is provided by Clojure `spec`. Data is returned in CSV format and converted into a Clojure map. 

In `com/slothrop/sabermetrics`, the user can find the existing functionality for creating their own maps of baseball stats (sabermetrics is the official term for baseball stats). These are a work in progress.

All other namespaces are stubs at this time.

## Python interop!

This is an experimental feature, but for now basic DataFrames are available thanks to the excellent `libpython-clj` library. Two idiomatic Clojure wrappers around some DataFrame class methods (`DataFrame.from_dict` and `DataFrame.from_records`) are provided. To enable Python support, you must of course have Python installed on your machine, and use one of the `jdk-??` aliases specified in `deps.edn`, based on your version of Java. This includes modules required only by `libpython-clj` when the JVM starts. 

## Portal

Pandas is a very powerful tool for tabular analysis, but its API can be somewhat difficult to manage. In the near future, I may investigate the `panthera` library for providing more idiomatic Clojure support for DFs. For now, however, I intend for users of this library to leverage Clojure's functional and data-driven approach to inspect the data provided by the Statcast API. One outstanding tool for this is [Portal](https://github.com/djblue/portal), which offers many different viewing options, including tables. I highly recommend it. 