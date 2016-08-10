{:title "PAIP in Clojure Challenge Chapter 1"
:layout :post
:tags ["Clojure" "Lisp" "PAIP"]}

From my parents I received the book [*Paradigms of Artificial Intelligence Programming*](http://amzn.to/29CsgBt) by Peter Norvig for my 28th birthday. 

<a href="http://amzn.to/29CsgBt"><img src="https://images-na.ssl-images-amazon.com/images/I/516h5FydqNL._SX359_BO1,204,203,200_.jpg"></img></a>

PAIP is a classic with high standard Common Lisp code to solve problems in artificial intelligence. My goal is to have read this book and finished a selection of the exercises in Clojure within a year.

There are 25 chapters and there are 52 weeks till 13 july 2017. This means I have to finish a chapter every two weeks.

The PAIP Clojure Challenge is reading PAIP and doing as much exercises as you can handle within a year.

## Summary for Chapter 1: Introduction to Lisp
The chapter starts with a quote by Alan Perlis:

>You think you know when you learn, are more sure<br>
when you can write, even more when you can teach,<br>
but certain when you can program.

The rest of the chapter explain the fundamentals of programming in Lisp and why it is awesome.

## Exercises

I will do a selection of the exercises in this book. I assume that you as a reader have not read the book, so I'll explain the exercises if necessary. The source code with unit tests is available at [https://www.github.com/erooijak/paip](https://www.github.com/erooijak/paip).

### Exercise 1.1 [m]
*Define a version of `last-name` that handles "Rex Morgan Md," "Morton Downey, Jr.," and whatever other cases you can think of.*

In the book we had a function `first-name` to retrieve the first names from a list of names, where a name is a list of strings like `("Robert" "Downey" "Jr.")`. The `first-name` method first checked if the first element of the name existed in the parameter list `*titles*`. If it did `first-name` invoked itself recursively with the rest of the name. 

We have to do the same, but instead of looking for the first element, we have to look for the last. And instead of validating the element does not exist in `*titles*`, we look in a parameter called *suffixes*.

```
(def suffixes '("Jr." "Md"))

(defn last-name
  "Select the last name from a name represented as a list."
  [name]
  (if (some #(= (last name) %) suffixes)
    (last-name (drop-last name))
    (last name)))

```

The library functions `drop-last` and `last` are convenient.

### Exercise 1.2 [m] 
*Write a function to exponentiate, or raise a number to an integer power. For example: (power 3 2) = 3<sup>2</sup> = 9.*

With the use of an iterative loop:

```
(defn power
  "Exponentiate a base to a power"
  [base power]
  (loop [b base 
         n power 
         result 1]
    (if (= 0 n)                   
      result                    
      (recur b (dec n) (* b result)))))
```

### Exercise 1.3 [m] 
*Write a function that counts the number of atoms in an expression. For example: `(count-atoms '(a (b) c)) = 3`. Notice that there is something of an ambiguity in this: should (a nil c) count as three atoms, or as two, because it is equivalent to (a () c)?*

```
(defn count-atoms
  "Counts the number of elements in an expression. nil is considered an atom.
   Clojure does not have true atoms, so better to call this method count-elements"
  [expression]
  (->> expression
       flatten
       count))
```

### Exercise 1.4 [m]
*Write a function that counts the number of times an expression occurs anywhere within another expression. Example: `(count-anywhere 'a '(a ((a) b) a)) ; => 3`.*

```
(defn count-anywhere
  "Counts the number of times an expression occurs anywhere within another
   expression"
  [expression-to-search expression]
  (->> expression
       flatten
       (filter #{expression-to-search})
       count))
```

### Exercise 1.5 [m]
*Write a function to compute the dot product of two sequences of numbers, represented as lists. The dot product is computed by multiplying corresponding elements and then adding up the resulting products. Example:*

`(dot-product `(10 20) '(3 4)) = 10 x 3 + 20 x 4 = 110`

```
(defn dot-product
  "Calculates the dot product. Assuming vectors are of even length and consist of numbers."
  [coll1 coll2]
  (if (empty? coll1) 0
    (+ (* (first coll1) (first coll2))
       (dot-product (rest coll1) (rest coll2)))))
```
