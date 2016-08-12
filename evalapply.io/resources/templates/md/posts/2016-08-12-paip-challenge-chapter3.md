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

### Exercise 3.3 [m] Write a function that will print an expression in dotted pair notation. Use the built-in function `princ` to print each component of the expression.

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

### Exercise 3.4 [m] Write a function that, like the regular `print` function, will print an expression in dotted pair notation when necessary but will use normal list notation when possible.

Dotted pair notation is only necessary when the `rest` of a list is not a list. To implement this we have to make changes to the `create-dotted-pair-rest` method. Unfortunately it is impossible to create an improper list with native Clojure data structures.

```
(cons 2 3) ; => IllegalArgumentException (because 3 is not a sequence)
```

This makes it impossible to write a test without implementing a linked list data structure in Clojure (like Max Countryman [did](http://macromancy.com/2014/01/16/data-structures-clojure-singly-linked-list.html)). Therefore I'll skip this exercise.

### Exercise 3.5 ...
