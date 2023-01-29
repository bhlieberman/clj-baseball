# `clj-baseball`

This is a Clojure library inspired by Python's `pybaseball`. It scrapes HTML and CSV data on baseball websites and makes it friendly to work with at the REPL. Baseball data is hugely plentiful and granular; I think it makes for an excellent match with a data-driven language like Clojure. My intention in building this library (besides having fun) is to offer a way for users to learn Clojure with abundant real world data. Even if the sport of baseball does not interest you, its ready-to-use data provides an excellent opportunity to explore the language ecosystem without hacking on your own web app or seeking out inferior APIs, second-rate "dummy data", or any other inadequate and frustrating option.

## Code organization

The library is divided into a few primary namespaces. At press time, the most complete functionality is available in `com/slothrop/statcast`, where the user will find the functions that query MLB's Statcast dataset for real-time, pitch- and swing-level data. Query validation is provided by Clojure `spec`. Data is returned in CSV format and converted into a Clojure map. 

In `com/slothrop/sabermetrics`, the user can find the existing functionality for creating their own maps of baseball stats (sabermetrics is the official term for baseball stats). These are a work in progress.

All other namespaces are stubs at this time.