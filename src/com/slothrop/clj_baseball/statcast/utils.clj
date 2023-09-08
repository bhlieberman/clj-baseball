(ns com.slothrop.clj-baseball.statcast.utils
  (:require [com.slothrop.clj-baseball.statcast.batter :refer [query-defaults]]
            [clojure.string :as string]
            [clojure.edn :as edn])
  (:import [java.time LocalDate Period]))

(defn transform-vals [m]
  (reduce-kv (fn [m k v] (cond-> m
                           (or (set? v) (vector? v)) (assoc k (string/join "|" v))
                           (string? v) (assoc k v))) #{} m))

(def test-query {:hfTeam ["BAL" "PIT" "CHC"]})

(defn transform-existing-query-vals [l r]
  (cond (vector? l) (conj l r)
        (and (string? l) (vector? r)) (conj r l)
        (nil? l) (identity r)
        (nil? r) (identity l)
        :else (throw (ex-info "cannot merge default and custom query maps." {:left-val l :right-val r}))))

(merge-with transform-existing-query-vals (assoc query-defaults :hfTeam "NYY") test-query)

(def first-season-map
  (-> "AB2: 1931, AB3: 1938, ABC: 1920, AC : 1923, AG : 1933, ALT: 1884, ANA: 1997, ARI: 1998,
    ATH: 1876, ATL: 1966, BAG: None, BAL: 1954, BBB: 1924, BBS: 1923, BCA: 1932, BE : 1935,
    BEG: 1938, BFB: 1890, BFL: None, BLA: 1901, BLN: 1892, BLO: 1882, BLT: 1914, BLU: 1884,
    BOS: 1901, BR2: 1923, BRA: 1872, BRD: 1884, BRG: 1890, BRO: 1884, BRS: 1890, BSN: 1876,
    BTT: 1914, BUF: 1879, BWW: 1890, CAG: 1920, CAL: 1965, CBB: 1933, CBE: 1943, CBK: 1883,
    CBL: 1870, CBN: 1924, CBR: 1939, CC : 1943, CCB: 1942, CCU: 1931, CEG: 1935, CEL: 1926,
    CEN: 1875, CG : 1933, CHC: 1876, CHH: 1914, CHP: 1890, CHT: 1927, CHW: 1901, CIN: 1876,
    CKK: 1891, CL2: 1932, CLE: 1901, CLI: 1890, CLS: 1889, CLV: 1887, CNR: 1876, CNS: 1880,
    COB: 1921, COG: 1920, COL: 1883, COR: 1884, COT: 1932, CPI: 1884, CRS: 1934, CS : 1921,
    CSE: 1923, CSW: 1920, CT : 1937, CTG: 1928, CTS: 1922, CUP: 1932, DET: 1901, DM : 1920,
    DS : 1920, DTN: 1881, DTS: 1937, DW : 1932, DYM: 1920, ECK: 1872, FLA: 1993, HAR: 1876,
    HBG: 1924, HG : 1929, HIL: 1923, HOU: 1962, IA : 1937, IAB: 1939, IBL: 1878, IC : 1946,
    ID : 1933, IHO: 1884, IND: 1887, JRC: 1938, KCA: 1955, KCC: 1888, KCM: 1920, KCN: 1886,
    KCP: 1914, KCR: 1969, KCU: 1884, KEK: 1871, LAA: 1961, LAD: 1958, LGR: 1876, LOU: 1882,
    LOW: 1931, LRG: 1932, LVB: 1930, MAN: 1872, MAR: 1873, MB : 1923, MGS: 1932, MIA: 2012,
    MIL: 1884, MIN: 1961, MLA: 1891, MLG: 1878, MLN: 1953, MLU: 1884, MON: 1969, MRM: 1932,
    MRS: 1924, NAT: 1872, NBY: 1936, ND : 1934, NE : 1936, NEG: 1930, NEW: 1915, NHV: 1875,
    NLG: 1923, NS : 1926, NWB: 1932, NYC: 1935, NYG: 1883, NYI: 1890, NYM: 1962, NYP: 1883,
    NYU: 1876, NYY: 1903, OAK: 1968, OLY: 1871, PBB: 1890, PBG: 1934, PBK: 1922, PBS: 1914,
    PC : 1933, PHA: 1882, PHI: 1873, PHK: 1884, PHQ: 1890, PIT: 1882, PK : 1922, PRO: 1878,
    PS : 1934, PTG: 1928, RES: 1873, RIC: 1884, ROC: 1890, ROK: 1871, SBS: 1876, SDP: 1969,
    SEA: 1977, SEN: 1938, SEP: 1969, SFG: 1958, SL2: 1937, SL3: 1939, SLB: 1902, SLG: 1920,
    SLI: 1914, SLM: 1884, SLR: 1875, SLS: 1922, SNH: 1938, SNS: 1940, STL: 1875, STP: 1884,
    SYR: 1879, SYS: 1890, TBD: 1998, TBR: 2008, TC : 1940, TC2: 1939, TEX: 1972, TLM: 1890,
    TOL: 1884, TOR: 1977, TRO: 1871, TRT: 1879, TT : 1923, WAP: 1932, WAS: 1884, WEG: 1936,
    WES: 1875, WHS: 1892, WIL: 1884, WMP: 1925, WNA: 1884, WNL: 1886, WOR: 1880, WP : 1924,
    WSA: 1961, WSH: 1901, WSN: 2005, WST: 1884,"
      (string/split #",")
      (->> (map (comp (fn [[name year]] [(string/trim name) year])
                      #(string/split % #":")
                      #(string/trim %)))
           (into {}))))

(def team-equivalents
  (-> " #{ANA, CAL, LAA},
    #{BSN, MLN, ATL},
    #{BLO, BLN, BLT, MLA, SLB, BAL},
    #{BRD, BRS, BOS},
    #{BRO, LAD},
    #{PHA, OAK},
    #{FLA, MIA},
    #{SEP, MIL},
    #{WSH, MIN},
    #{MON, WSN},
    #{NYG, SFG},
    #{TBD, TBR},
    #{BCA, IAB},
    #{AC , BAG},
    #{BR2, BRG},
    #{NEG, CEG, WEG, BEG},
    #{CNS, CIN},
    #{CCB, CBE},
    #{CLE, CLV},
    #{CS , CSW},
    #{AB2, ID },
    #{CC , IC },
    #{JRC, CBR},
    #{LVB, LOW},
    #{BE , NE },
    #{PC , TC , TC2},
    #{PBK, PK },
    #{SLG, SLS},
    #{AB3, SL3, SNS, HAR},
    #{WP , WMP},
    #{WHS, WNA},
    #{WAS, WST}"
      (string/trim-newline)
      (string/split #"," 1)
      (->> (map #(string/trim %))
           first
           (re-seq #"\#\{\w+,.*\}")
           (reduce (fn [acc s] (let [data (edn/read-string s)]
                                 (conj
                                  acc (into #{}
                                            (for [sym data]
                                              (keyword sym)))))) []))))

(defn get-first-season [team {include-equivalents? :include-equivalents?
                              :or {include-equivalents? true}}]
  (let [oldest (atom (or (get first-season-map team)
                         (.. LocalDate now getYear)))
        equivalents team-equivalents
        ret (cond
              (not include-equivalents?) (get first-season-map team)
              (not equivalents) @oldest
              :else (doseq [e (first equivalents)]
                      (let [fst (get first-season-map e)]
                        (when (and (some? fst) (< fst @oldest))
                          (reset! oldest fst)))))]
    (or ret @oldest)))

(def STATCAST-VALID-DATES {2008 [(LocalDate/of 2008 3 25) (LocalDate/of 2008 10 27)]
                           2009 [(LocalDate/of 2009 4 5) (LocalDate/of 2009 11 4)]
                           2010 [(LocalDate/of 2010 4 4) (LocalDate/of 2010 11 1)]
                           2011 [(LocalDate/of 2011 3 31) (LocalDate/of 2011 10 28)]
                           2012 [(LocalDate/of 2012 3 28) (LocalDate/of 2012 10 28)]
                           2013 [(LocalDate/of 2013 3 31) (LocalDate/of 2013 10 30)]
                           2014 [(LocalDate/of 2014 3 22) (LocalDate/of 2014 10 29)]
                           2015 [(LocalDate/of 2015 4 5) (LocalDate/of 2015 11 1)]
                           2016 [(LocalDate/of 2016 4 3) (LocalDate/of 2016 11 2)]
                           2017 [(LocalDate/of 2017 4 2) (LocalDate/of 2017 11 1)]
                           2018 [(LocalDate/of 2018 3 29) (LocalDate/of 2018 10 28)]
                           2019 [(LocalDate/of 2019 3 20) (LocalDate/of 2019 10 30)]
                           2020 [(LocalDate/of 2020 7 23) (LocalDate/of 2020 10 27)]})

(def pitch-codes  ["FF", "CU", "CH", "FC", "EP", "FO", "KN", "KC", "SC", "SI", "SL", "FS", "FT", "ST", "SV", "SIFT", "CUKC", "ALL"])

(def pitch-names (->> ["4-Seamer", "Curveball", "Changeup", "Cutter", "Eephus", "Forkball", "Knuckleball", "Knuckle-curve", "Screwball", "Sinker", "Slider", "Splitter", "2-Seamer", "Sweeper", "Slurve", "Sinker", "Curveball"]
                      (map #(.toUpperCase %))
                      (into [])))

(def names->codes (zipmap pitch-codes pitch-names))

;; TODO: add the concatenated name-code to another map repr

(def position-codes ["IF", "OF", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF", "ALL"])

(def position-names (->> ["Infield", "Outfield", "Catcher", "First Base", "Second Base", "Third Base", "Shortstop", "Left Field", "Center Field", "Right Field"]
                         (map #(.toUpperCase %))
                         (into [])))

(def pos-code->numbers (zipmap (subvec position-codes 2 10) (map str (range 2 10))))

(defn date-range []
  (let [low (LocalDate/of 2023 03 15)
        high (LocalDate/of 2023 11 15)]
    (remove (fn [^LocalDate d] (or (neg-int? (.compareTo d low))
                                   (pos-int? (.compareTo d high)))))))

(defn most-recent-season []
  (let [today (LocalDate/now)
        delta (Period/ofWeeks 52)
        start (.minusDays today (.getDays delta))
        dates (->> (.. start (datesUntil (LocalDate/now)) iterator)
                   iterator-seq
                   (into [] (date-range)))
        most-recent-date (nth dates (dec (count dates)))]
    (.getYear most-recent-date)))