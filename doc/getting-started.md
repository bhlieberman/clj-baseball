# Getting Started

Download this library from Clojars or clone this repo to use the latest version. Use the functions in the `com/slothrop/statcast.batter` namespace to compose and send your query to Statcast. This will return a map of data. Currently, the implementation of `send-req!` is fairly naive; thus, large queries are discouraged unless you wish to wait. Moreover, the ability to compose queries of arbitrary granularity using Clojure maps should encourage you to keep it specific. An example query might look like this:

```
(def results (send-req! {:game-date-gt "2022-04-28" :game-date-lt "2022-05-01" :hfTeam "BAL"}))
```

This is looking for all game data for games between 2022-04-28 and 2022-05-01 where the Baltimore Orioles were playing. The data contained in `resources/public/query.edn` contains all the other available fields to narrow and broaden the scope of your query, as desired. When you pass a map like the one above to `send-req!`, your desired parameters are merged with the default values in the .edn file. Beware that the Statcast endpoint is a bit finicky: if it returns no data, you might have slipped up entering a parameter in a specific format. To the greatest extent possible I have attempted to mitigate this issue by using Clojure `spec` to validate queries; issues about this and other concerns with the library are welcome.