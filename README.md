# `clj-baseball`

This is a Clojure library inspired by Python's `pybaseball`. It scrapes HTML and CSV data on baseball websites and makes it friendly to work with at the REPL. Baseball data is hugely plentiful and granular; I think it makes for an excellent match with a data-driven language like Clojure. My intention in building this library (besides having fun) is to offer a way for users to learn Clojure with abundant real world data. Even if the sport of baseball does not interest you, its ready-to-use data provides an excellent opportunity to explore the language ecosystem without hacking on your own web app or seeking out inferior APIs, second-rate "dummy data", or any other inadequate and frustrating option.

## Code organization

The library is divided into a few primary namespaces. At press time, the most complete functionality is available in `com/slothrop/statcast`, where the user will find the functions that query MLB's Statcast dataset for real-time, pitch- and swing-level data. Query validation is provided by Clojure `spec`. Data is returned in CSV format and converted into a Clojure map. 

In `com/slothrop/sabermetrics`, the user can find the existing functionality for creating their own maps of baseball stats (sabermetrics is the official term for baseball stats). These are a work in progress.

All other namespaces are stubs at this time.

## Getting started

Once you've downloaded this library (soon to be available on Clojars), use the functions in the `com/slothrop/statcast.batter` namespace to compose and send your query to Statcast. This will return a map of data. Currently, the implementation of `send-req!` is fairly naive; thus, large queries are discouraged unless you wish to wait. Moreover, the ability to compose queries of arbitrary granularity using Clojure maps should encourage you to keep it specific. An example query might look like this:

```
(def results (send-req! {:game-date-gt "2022-04-28" :game-date-lt "2022-05-01 :hfTeam "BAL%7C"}))
```

This is looking for all game data for games between 2022-04-28 and 2022-05-01 where the Baltimore Orioles were playing. The data contained in `resources/public/query.edn` contains all the other available fields to narrow and broaden the scope of your query, as desired. When you pass a map like the one above to `send-req!`, your desired parameters are merged with the default values in the .edn file. Beware that the Statcast endpoint is a bit finicky: if it returns no data, you might have slipped up entering a parameter in a specific format. To the greatest extent possible I have attempted to mitigate this issue by using Clojure `spec` to validate queries; issues about this and other concerns with the library are welcome. 