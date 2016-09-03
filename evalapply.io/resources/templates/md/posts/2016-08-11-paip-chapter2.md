{:title "PAIP in Clojure Chapter 2"
:layout :post
:tags ["Clojure" "Lisp" "PAIP"]}

After some vacation and working on other things I finally got round to Chapter 2 of [Paradigms of Artificial Intelligence Programming: Case Studies in Common Lisp](http://amzn.to/29CsgBt) (PAIP).

## Summary for Chapter 2: A Simple Lisp Program

The chapter starts with a quote by the Italian royal historiographer Giovanni Battista Vico (1668-1744):

>*Certum quod factum.*

For those that do not speak Latin, this is the rough translation: "One is certain only of what one builds."

In this chapter we build a more elaborate Lisp program and therefore live a more certain life.
The program we will build generates random English sentences.
In fancy terms we call the program *generative syntax* for a *context-free phrase-structure grammar*.

The original Common Lisp code from the book can be viewed [here](http://norvig.com/paip/simple.lisp). 
First I translated this code to Clojure below.

```
(ns paip.simple
  (:gen-class))

; Clojure translation of the Common Lisp code in Chapter 2.
; Original: http://norvig.com/paip/simple.lisp

;;; Code translated from Paradigms of Artificial Intelligence Programming
;;; Copyright (c) 1991 Peter Norvig

(defn one-of
  "Pick one element of set, and make a list of it."
  [set]
  (-> set rand-nth list))

(def simple-grammar
  "A grammar for a trivial subset of English"
  {:sentence    [[:noun-phrase :verb-phrase]]
   :noun-phrase [[:article :noun]]
   :verb-phrase [[:verb :noun-phrase]]
   :article     #{"the" "a"}
   :noun        #{"man" "ball" "woman" "table"}
   :verb        #{"hit" "took" "saw" "liked"}})

(def bigger-grammar
  "A somewhat bigger grammar for a trivial subset of English"
  {:sentence    [[:noun-phrase :verb-phrase]]
   :noun-phrase [[:article :adj* :noun :pp*] [:name] [:pronoun]]
   :verb-phrase [[:verb :noun-phrase :pp*]]
   :pp*         [[] [:pp :pp*]]
   :adj*        [[] [:adj :adj*]]
   :pp          [[:prep :noun-phrase]]
   :prep        #{"to" "in" "by" "with" "on"}
   :adj         #{"big" "little" "blue" "green" "adiabatic"}
   :article     #{"the" "a"}
   :name        #{"Pat" "Kim" "Lee" "Terry" "Robin"}
   :noun        #{"man" "ball" "woman" "table"}
   :verb        #{"hit" "took" "saw" "liked"}
   :pronoun     #{"he" "she" "it" "these" "those" "that"}})

;;; ==============================

(def grammar
  "The grammar used by generate.  Initially, this is
  simple-grammar, but we can switch to other grammars."
  simple-grammar)

;;; ==============================

(defn rule-lhs
  "The left hand side of a rule."
  [rule]
  (->> rule keys (apply concat)))

(defn rule-rhs
  "The right hand side of a rule."
  [rule]
  (->> rule vals (apply concat)))

(defn rewrites
  "Return a list of the possible rewrites for this category."
  [category]
  (rule-rhs (select-keys grammar [category])))

;;; ==============================

(defn generate
  "Generate a random sentence or phrase"
  [phrase]
  (cond (vector? phrase)
        (mapcat generate phrase)
        (not (empty? (rewrites phrase)))
        (generate (rand-nth (rewrites phrase)))
        :else
        (list phrase)))

(generate :sentence)
; => (One example)
;    ("Kim" "saw" "Pat")

;;; ==============================

(defn generate-tree
  "Generate a random sentence or phrase,
  with a complete parse tree."
  [phrase]
  (cond (vector? phrase)
        (map generate-tree phrase)
        (not (empty? (rewrites phrase)))
        (cons phrase
              (generate-tree (rand-nth (rewrites phrase))))
        :else (list phrase)))

(generate-tree :sentence)
; => (one example)
;    (:sentence (:noun-phrase (:Article "the")
;                             (:Adj*)
;                             (:Noun "ball")
;                             (:PP*))
;               (:verb-phrase (:Verb "liked")
;                             (:noun-phrase
;                              (:Name "Robin"))
;                             (:PP*)))

;;;; ==============================

(defn combine-all
  "Return a list of lists formed by appending a y to an x.
  E.g., (combine-all '((a) (b)) '((1) (2)))
  -> ((A 1) (B 1) (A 2) (B 2))."
  [xlist ylist]
  (mapcat (fn [y]
            (map (fn [x] (concat x y)) xlist))
          ylist))

;; TODO: There is a bug in generate-all. For sentences that are constructed out of noun-phrase
;;       or verb-phrases the resulting sentences are displayed in characters.
(defn generate-all
  "Generate a list of all possible expansions of this phrase.
   Only works for non-recursive grammars."
  [phrase]
  (cond (vector? phrase)
        (combine-all (generate-all (first phrase))
                     (mapcat generate-all (rest phrase)))
        (not (empty? (rewrites phrase)))
        (mapcat generate-all (rewrites phrase))
        :else
        (list phrase)))
```

The translation between the different built-in methods and difference in behavior between Clojure and Common Lisp cost me quite some time, but it was worth it:
I know more about Clojure than I did before, and I have a working sentence generator.

The lessons from the chapter are:
- `data-driven` programming (where data drives what a program does next) makes it easy to extend functionality of a program just by adding new input data.
In the parser above `simple-grammar` and `bigger-grammar` both work without having the program to change).
- Code is also data, in what Norvig calls the one-data/multiple-program approach we can create slightly different versions of the same program which accomplish different tasks using the same data. `generate-tree` and `generate-all` programs are example of one-data/multiple-program: slight changes lead to different related functionality.

`generate-all` is not fully functional since somehow the words are cut down into characters when they are created by a `verb-phrase` or `noun-phrase`.
Perhaps that problem will somehow be solved in the exercises.

## Exercises

The source code with unit tests is available at [https://www.github.com/erooijak/paip](https://www.github.com/erooijak/paip).

### Exercise 2.1 [m]

*Write a version of `generate` that uses `cond` but avoids calling `rewrites` twice.*

Instead of checking it the rewrites are non-empty, in our way of defining the grammar (using the native Clojure map) we can perform the same check by checking if the phrase is a keyword:

```
(defn generate
  "Generate a random sentence or phrase"
  [phrase]
  (cond (vector? phrase) (mapcat generate phrase)
        (keyword? phrase) (generate (rand-nth (rewrites phrase)))
        :else (list phrase)))
```

### Exercise 2.2 [m]

*Write a version of `generate` that explicitly differentiates between terminal symbols (those with no rewrite rules) and non-terminal symbols.*

Just extract a method and provide it with a meaningful name.

```
(defn non-terminal?
  "Determines if a phrase is non terminal."
  [phrase]
  (keyword? phrase))

(defn generate
  "Generate a random sentence or phrase"
  [phrase]
  (cond (vector? phrase) (mapcat generate phrase)
        (non-terminal? phrase) (generate (rand-nth (rewrites phrase)))
        :else (list phrase)))

```

### Exercise 2.3 [m]

*Write a trivial grammer for some other language. This can be a natural language other than English, or perhaps a subset of a computer language.*

A subset of "Clojure":

```
(def simple-clj-grammar
  "A grammar for a trivial subset of Clojure"
  {:code                [[:definition-phrase :function-definition]]
   :definition-phrase   [[:definition :names :docstring]]
   :function-definition [[:library-definitions :parameter-types]]
   :definition          #{"defn"}
   :docstring           #{"wtf?" "TODO"}
   :names               #{"my-method" "awesome?" "change-it!"}
   :library-definitions #{"map" "mapcat" "inc" "dec"}
   :parameter-types     #{"string" "int" "long" "bool"}})
```

### Exercise 2.4 [m]

*One way of describing `combine-all` is that it calculates the cross-product of the function `append` on the argument lists. Write the higher-order function `cross-product`, and define `combine-all` in terms of it. The moral is to make your code as general as possible, because you never know what you may want to do with it next.*

It is possible to write cross-product using a [for-comprehension](https://clojuredocs.org/clojure.core/for):

```
(defn cross-product
  "Calculates the cross product."
  [f coll1 coll2]
  (if (empty? coll1)
    '(())
    (for [x coll1
          y coll2]
      (f x y))))

(defn combine-all
  [xlist ylist]
  (cross-product concat xlist ylist))
```

The cross-product function in use:

```
(cross-product + '(1 2 3) '(10 20 30))
; => '(11 21 31
;      12 22 32
;      13 23 33)))

(cross-product list '(a b) '(1 2))
; => '((a 1) (a 2) (b 1) (b 2))))))
```

That was a full day of work. I will start with Chapter 3 tomorrow.

