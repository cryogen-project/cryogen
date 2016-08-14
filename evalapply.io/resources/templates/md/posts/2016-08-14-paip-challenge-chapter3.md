{:title "PAIP in Clojure Challenge Chapter 3"
:layout :post
:tags ["Clojure" "Lisp" "PAIP"]}

The Chapter of [Paradigms of Artificial Intelligence Programming: Case Studies in Common Lisp](http://amzn.to/29CsgBt) (PAIP) that dives into Common Lisp syntax.

## Summary for Chapter 3: Overview of Lisp

The chapter starts with a quote by Guy L. Steele, Jr.:

>*No doubt about it. Common Lisp is a *big* language*.

There is a lot to Common Lisp. In this chapter we will learn the subset of Lisp Norvig uses in his book.

There are six maxims every programmer should know about:
- Be specific. (Use `when` instead of `if` if there is only one clause.)
- Use abstractions. (Provide a `first-name` method when dealing with a `list names`, even if it is implemented as first.)
- Be concise.
- Use the provided tools.
- Don't be obscure.
- Be consistent. (Sometimes principles are in conflict with each other, then choose one and stick with it.)

### Exercise 3.1 [m]

```
(let [x 6
      y (* x x)]
  (+ x y)) ; => 42
```

*Show a `lambda` expression that is equivalent to the above `let*` expression. You may need more than one `lambda`.*

```
((fn [x]
   ((fn [y] (+ x y))
    (* x x)))
 6) ; => 42
```

### Exercise 3.2 [s]

*The function `cons` can be seen as a special case of one of the other functions listed previously. Which one?*

[`list*`](https://clojuredocs.org/clojure.core/list*). See:

```
(= (list* 1 '(2 3 4)) 
   (cons 1 '(2 3 4))) ; => true
```

### Exercise 3.3 [m] 

*Write a function that will print an expression in dotted pair notation. Use the built-in function `princ` to print each component of the expression.*

Note: princ prints suitable output without escape characters and binds some values to special Common Lisp parameters. I use `print`.

Dotted pair notation is used in Lisps to show we are dealing with an *improper* list: a linked list where the second element of a cell is not a list.
This is notated with a dotted pair. E.g., `(cons 1 2) ; => '(1 . 2)`.

Clojure has no dotted pair notation since it avoids the linked list data structure and uses the abstraction of the sequence. See [sequences](http: //clojure. org/reference/sequences):
>A seq is a logical list, and unlike most Lisps where the list is represented by a concrete, 2-slot structure, Clojure uses the ISeq interface to allow many data structures to provide access to their elements as sequences.

For a list (special type of sequence) we can introduce dotted pair notation.
To do so we need a method that can check for elements that have a single value (e.g., 1, :a). Elements which are therefore not a sequence.

I used the following method from an [answer on Stack Overflow](https://www.stackoverflow.com/questions/11782534/am-i-using-atom-wrong-or-there-is-something-else#11783268") to check if a method is single-valued (or an `atom` in other Lisps than Clojure).

```
(defn single-valued?
  "Checks if value is single-valued."
  [x]
  (not (or (nil? x)
           (.. x getClass isArray)
           (some #(instance? % x) [clojure.lang.Counted
                                   clojure.lang.IPersistentCollection
                                   java.util.Collection
                                   java.util.Map]))))
```

Next we can create a case analysis and recursively generate a dotted-pair representation of a sequence.

```
(declare create-dotted-pair)
(defn create-dotted-pair-rest
  "Creates dotted pair string representation for the rest of a list."
  [lst]
  (str " . " (create-dotted-pair lst)))

(defn create-dotted-pair
  "Creates dotted-pair string representation of list."
  [lst]
  (cond (single-valued? lst) (str lst)
        (empty? lst) (str "'()")
        (seq? lst) (str "("
                        (create-dotted-pair (first lst))
                        (create-dotted-pair-rest (rest lst))
                        ")")
        :else (str lst)))

(defn print-dotted-pair
  "Prints dotted-pair string representation of list."
  [lst]
  (print (create-dotted-pair lst)))
```

This works as follows for a list created by a chain of cons cells, the shorter notation for a list, and for a nested list:

```
(print-dotted-pair (cons 1 (cons 2 (cons 3 (cons 4 '())))))
; => (1 . (2 . (3 . (4 . '()))))
(print-dotted-pair '(1 2 3 4))
; => (1 . (2 . (3 . (4 . '()))))
(print-dotted-pair '(1 2 (3 4)))
; => (1 . (2 . ((3 . (4 . '())) . '())))
```

### Exercise 3.4 [m]
*Write a function that, like the regular `print` function, will print an expression in dotted pair notation when necessary but will use normal list notation when possible.*

Dotted pair notation is only necessary when the `rest` of a list is not a list. To implement this we have to make changes to the `create-dotted-pair-rest` method. Unfortunately it is impossible to create an improper list with native Clojure data structures.

```
(cons 2 3) ; => IllegalArgumentException (because 3 is not a sequence)
```

This makes it impossible to write a test without implementing a linked list data structure in Clojure (like Max Countryman [did](http://macromancy.com/2014/01/16/data-structures-clojure-singly-linked-list.html)). Therefore I'll skip this exercise.

Now the first hard exercise:

### Exercise 3.5 [h] 

*(Exercise in altering structure.) Write a program that will play the role of guesser in the game Twenty Questions. The user of the program will have in mind any type of thing. The program will ask questions of the user, which must be answered yes or no, or "it" when the program has guessed it. If the program runs out of guesses, it gives up and asks the user what "it" was. At first the program will not play well, but each time it plays, it will remember the user's replies and use them for subsequent guesses.*

The idea seems to be that the program has a tree of questions and remembers the answer for each node.

To solve it we need state. For mutable state we create a map which we hold in an [`atom`](http://clojure.org/reference/atoms).

I use the following map to build a tree of questions and answers:

```
(def questions-and-answers
  "Questions and answers database atom.
  Keywords are used to traverse the tree.
  An answer needs to be non-existent (nil) or in the form of a string."
  (atom
   {:is_it_a_programming_language?
    {:yes
     {:is_it_statically_typed?
      {:yes
       nil
       :no
       nil}}
     :no
     {:ca_va?
      {:yes
       nil
       :no
       nil}}}}))
```

The program could be made more interesting if it was possible to add new questions and answers. This way the database of questions and answers can keep growing.
For now, we just ask the questions and update if the user has an answer.

```
(defn get-remaining
  "Gets the remaining questions and answers from the questions-and-answers database.
  previous should be a vector of the previous questions and answers."
  [previous]
  (if (empty? previous) @questions-and-answers
      (get-in @questions-and-answers previous)))

(defn get-next-question
  "Gets the next question from questions-and-answers
  previous should be a vector of the previous questions and answers."
  [previous]
  (-> previous get-remaining ffirst))

(defn further-questions?
  "True if there is an answer for a question. False if not."
  [previous]
  (if (empty? previous) true
      (not (nil? (get-remaining previous)))))

(defn found-answer?
  "True if we have found the answer."
  [previous]
  (string? (get-remaining previous)))

(defn set-answer!
  "Sets the answer to the value provided.
  previous should be a vector of the previous questions and answers."
  [previous answer]
  (swap! questions-and-answers assoc-in previous answer))

(defn key->string
  "Create a string of the answer of question key."
  [key]
  (-> (name key) (clojure.string/replace "_" " ") clojure.string/capitalize))

(defn string->key
  "Create a key of the answer string."
  [answer]
  (-> answer (clojure.string/replace " " "_") clojure.string/lower-case keyword))

(defn driver-loop
  "Asks the questions, updates the answers.
  previous is vector containing the previous questions."
  ([] (driver-loop [])) ; Start with no previous provided answers.
  ([previous]
   (let [question (get-next-question previous)
         ask-it (println (key->string question)) ; ask-it is dummy variable to not create nested lets
         answer (read)
         print-answer (println answer) ; dummy, idem
         previous (conj previous question (string->key answer))]
     (cond (found-answer? previous)
           (let [found-answer (get-remaining previous)]
             (println (str "The answer is: " found-answer)))
           (not (further-questions? previous))
           (do (println "What was it?")
               (let [given-answer (str (read))]
                 (println given-answer)
                 (set-answer! previous given-answer)
                 (println "Thank you.")))
           :else (recur previous)))))

; (driver-loop)
```

Usage of the program looks like this:

<img src="http://i.imgur.com/1GniKoN.png" alt="Interacting with the program via the REPL."/>

The node in the tree is updated after an answer is given. The next time the same sequence of questions are answered the program responds with the answer the user added earlier. The representation is shown by dereferencing the atom.

*TODO:* Rest of the exercises.
