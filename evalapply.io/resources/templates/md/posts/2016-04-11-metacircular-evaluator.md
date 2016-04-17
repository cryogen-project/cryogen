{:title "The meta-circular evaluator"
:layout :post
:tags ["Clojure" "Life" "Lisp" "Scala" "SICP"]}

## Introduction

THIS FIRST POST IS A WORK IN PROGRESS :)

TODO: why is it called meta-circular

Circulair omdat het circualr is http://www.c2.com/cgi/wiki?MetaCircularEvaluator

In this post I attempt to explain what a meta-circular evaluator is and why I find it awesome. I am definitely not an expert on the subject matter. In fact I learned a lot of new things while writing this post and trying to get the code that accompanies it to run. 

I first learned of the concept of the meta-circular evaluator in the book [The Structure and Interpretation of Computer Programs](https://mitpress.mit.edu/sicp/) (SICP). This book is seriously the best book I have ever read in any subject and the evaluator is one central piece.

The idea itself was created by John McCarthy in his paper [Recursive Functions of Symbolic Expressions and Their Computation by Machine, Part I](http://www-formal.stanford.edu/jmc/recursive.html) (note: there has never been a Part 2). The paper where he invented Lisp (acronym for LIst Processing). It was originally never his idea that the language actually was implemented. Quie a shock for him when it was.

What is so special about Lisp? First of all it has barely no syntax. This makes it very easy to make programs that work with programs (programs are also data objects). 
This property of Lisp makes it possible to write Lisp in itself.

As Paul Graham writes:

>The unusual thing about Lisp-- in fact, the defining quality of Lisp-- is that it can be written in itself. ([Source](http://www.paulgraham.com/rootsoflisp.html))


Paul Graham also remarks that it is interesting to learn about this concept not just to learn about the past, but also learn about where languages are heading.

Alan Kay (famous computer scientist and creator of the language Smalltalk) describes his excitement when he realized what these meant:

>I finally understood that the half page of code on the bottom of page 13 of the Lisp 1.5 manual was Lisp in itself. These were "Maxwells Equations of Software!" This is the whole world of programming in a few lines that I can put my hand over. ([Source](http://queue.acm.org/detail.cfm?id=1039523))

<sup>Note: the [Maxwell equations](https://www.youtube.com/watch?v=DSRLvkP0vmg) are four extremely elegant equations from which everything there is to know about the electro-magnetic field can be derived.</sup>

In SICP it is stated: 

>It is no exaggeration to regard this as the most fundamental idea in programming:<br><br>
The evaluator, which determines the meaning of expressions in a programming language, is just another program. ([Source](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-25.html))

In this post I have translated the code from meta-circular evaluator from SICP (you can find the original code [here](https://mitpress.mit.edu/sicp/code/ch4-mceval.scm)) to Clojure. 
Unfortunately I did not really succeed. The three attempts I undertook are detailed below, after we first look at some syntactical prerequisites..

Art of the Interpreter. Lambda papers.
http://www.c2.com/cgi/wiki?TheArtOfTheInterpreter

## Background

This paragraph can be skipped if you have worked with Lisp, but is included for those for whom Lisp is new. 
The nice thing about Lisp is that below is pretty much all the syntax there is. The rules are simple (like chess), but the possibilities within these rules are huge (like chess).

The rest of Lisp can be implemented in these special forms. (Note: this is not completely true, there are some other special forms used in other Lisps for for example performance reasons).

It is roughly based on the aforementioned [article](http://www.paulgraham.com/rootsoflisp.html) of Paul Graham where he explains what McCarthy has discovered.

The meta-circular evaluator is only introduced in chapter 4 of 5 of SICP and lecture 7 of 12. Also in the original paper of McCarthy first some groundwork needs to be laid out. 
Here we go:

The fundamental data structure in Lisp is a list (in Clojure we have some more data structures, we will not worry about that now). 
The first part of a list is interpreted as the operator, and the others as the operands. 

So for example in:

```
(+ 1 2 3 4)
```

the operator is + and the operands are 1, 2, 3 and 4. Lisp *applies* the operator to the operands and this leads to the result of 10.

Furthermore, we need have seven fundamental forms:

`atom`

Everything in Lisp is either a list or an atom. 

`quote`

Quoting an expression means that the expression does not need to be evaluated. This way the operator can be used to blur the distinction between code and data. By quoting code you can pass it as data to another method which can then inpect and evaluate the expression. Since everything in Lisp is a list you can easily work with that data. 

`(eq a b)`

Eq returns true if the values of *a* and *b* are the same atom or the empty list, and () otherwise.

`(cons a l)`

A fundamental building block. Cons stands for constructor and is used to add one element *a* to a list *l*.

`(car l)`

Returns the first item in a list. In Clojure this form is called first.

`(cdr l)`

Returns everything but the first item in a list. In Clojure this form is called rest.

Car and cdr refer to the way cons cells were stored in early computers. This is no longer the case, but Lispers have decided to stay with it because you can easily combine them. E.g., the car of the cdr (the second element of a list) is the cadr. Clojure breaks this connection with the early roots, but has introduced other forms to make it easier to chain methods together (e.g., [->](https://clojuredocs.org/clojure.core/-%3E)) and has special utility methods like 'nth' to obtain the nth element from a list.

`(cond (p<sub>1</sub> e<sub>1</sub>) ... (p<sub>n</sub> e<sub>n</sub>)`

A conditional (conditionals were among other things first introduced in the paper of McCarthy!). If *p<sup>n</sup>* is true *e<sup>n</sup>* is evaluated. Also an else clause can be introduced if no predicates are true.

Besides these fundamental forms you can create functions with `fn` as follows. In a function you have a list of arguments and a list of expressions. The value of the arguments are assigned to the values that are passed during the function call (this becomes relevant later!). A function is created as follows:

```
fn (arg) (+ arg 1))
```

and you call it as follows:

```
(fn (arg) (+ arg 1)) 1) ;; => 2
```

You can define variables in with `defun`, or in Clojure with `defn` as follows:

```
(defn add1 []
     (fn [arg] (+ arg 1)))
```

Clojure introduces another syntax here to define the function:

```
(fn [arg] (+ arg 1) 1) ;; => 2
```

Clojure has more data types than the list. 
Clojure also contains maps, vectors and sets. 
This helps for readability, but the real reason is that parenthetical forms are always evaluated and arguments are not evaluated and are therefore put in a vector.
It is interesting to note that the implementations of this data structures all are immutable and have very good performance characteristics. 
This is a topic for another post.

To define functions and evaluate it often recursion is used. 
For example to define a procedure defined that is called `map`. 
`map` applies a given function *f* to every argument in the list and returns a list with the results:

```
(def map 
     (fn [f coll]
         (if (empty? coll) '()
           (cons (f (first coll)) (map f (rest coll))))))

(map add1 '(1 2 3 4)) ;; => (2 3 4 5)
```

This is done by `cdr`ing ("resting" in the case of Clojure) down a list and `cons`ing up the result.
It applies f to the first element of the list, and combines the result with f mapped to the rest of the list. 
The recursion breaks if the collection is empty.
Try to wrap your head around it if you didn't yet. I find these recursive definitions really beautiful. The evaluator has a lot of them as well.

# The meta-circular evaluator

The meta-circular evaluator consists of two methods, `eval` and `apply`. I decided to name this blog after it.

If no fancy names are used for the methods it fits on one page, or as is show in SICP lecture 7A: [on the blackboard](https://www.youtube.com/watch?v=0m6hoOelZH8&t=34m36s). 

Here a bit more descriptive names are used since it helps for clarity.

Unfortunately what I have created so far is not truly a meta-circular evaluator. 
A defining characteristic of a meta-circular evaluator is that it is written in the language it evaluates.
This is written in Clojure, but it cannot evaluate Clojure. 
What it can evaluate looks a lot like Scheme translated to Clojure, because in fact it is.
But it was an attempt at a meta-circular evaluator so I decided to keep the name.

## Eval
We first write `eval`. Eval is a procedure that takes two arguments: an expression and an environment. And [like every interesting procedure, it is a case analysis](https://www.youtube.com/watch?v=0m6hoOelZH8&t=5m31s):


```
(defn eval [exp env]
      (cond (self-evaluating? exp) exp
            (variable? exp) (lookup-variable-value exp env)
            (quoted? exp) (text-of-quotation exp)
            (assignment? exp) (eval-assignment exp env)
            (definition? exp) (eval-definition exp env)
            (if? exp) (eval-if exp env)
            (lambda? exp) (make-procedure (lambda-parameters exp) 
                                          (lambda-body exp)
                                          env)
            (begin? exp) (eval-sequence (begin-actions exp) env)
            (cond? exp) (eval (cond->if exp) env)
            (application? exp) (apply (eval (operator exp) env)
                                      (list-of-values (operands exp) env))
            :else (error "Unknown expression type -- EVAL" exp)))
```

When we call eval with a quoted expression to evaluate, it will look at what type of expression we are dealing with, and then dispatch to another procedure to handle it.

When an expression is self-evaluating (like a string ["hello"] or a number [1]) we just get the expression back.
If it is a variable (`a`), we look up the value of the variable in the environment (we will discuss how this works later).
It is is a definition we want to define the value in the environment
(If we define (defn a "hello") we want "a" stored in the environment to be "hello", and when we look up the variable a later we want to have "hello" returned.)
If it is an assignment (set! a "hi"), we will look up "a" in the environment, and set it to a new value.
If it is a lambda expression we create a procedure of this lambda expression (we create a list tagged with the flag 'procedure), where we store the parameters and the body of the procedure alongside with the current environment captured in it.
Besides that we have an if-statement and conditional. A conditional is transformed to a list of if-statements. Finally if the predicate is true the first clause is evaluated, and if false the second clause is evaluated.
The most interesting thing happens if the expression is an application. Then we apply the result of evaluating the operator of the expression in the environment, to the list of values that follow, each evaluated in the environment. List of values is implemented as follows:

```
(defn list-of-values [exps env]
  (if (no-operands? exps)
    '()
    (cons (eval (first-operand exps) env)
          (list-of-values (rest-operands exps) env))))
```
This is a recursive definition (like map above) where we evaluate construct a new list with as a first argument the first operand evaluated in the environment, and as a tail the rest of the operands evaluated in the environment. When we come to the empty list '() (the last element of a cons cell) the recursion breaks and we return an empty list '().

We will discuss the implementation of `apply` after we have looked at the implementation of `eval` a little bit closer.

## Routines to detect expressions.

For clarity, all these things above like `first-operand` and `rest-operands` are all implemented with `car`s and `cdr`s (`first` and `rest` in Clojure). E.g.,

```
(defn first-operand [args] (first args))
(defn rest-operands [args] (rest args))
(defn no-operands? [args] (empty? args))
```
The checking of the type of an expression is done by comparing the first element of the list via tagged-list:

```
(defn tagged-list? [exp tag]
  (and (list? exp) (= (first exp) tag)))
```
This checks if the expression is a list, and if the first expression is equal to the tag.

For example, to see if a variable is a definition or quoted we check if the first element is respectively the symbol `'quote` or `'defn`:
```
(defn quoted? [exp] (tagged-list? exp 'quote))
(defn lambda? [exp] (tagged-list? exp 'fn))
(defn definition? [exp] (tagged-list? exp 'defn))
(defn assignment? [exp] (tagged-list? exp 'set!))
(defn begin? [exp] (tagged-list? exp 'begin))
(defn true? [x] (not (= x false)))
(defn false? [x] (= x false))
(defn cond? [exp] (tagged-list? exp 'cond))
(defn compound-procedure? [p] (tagged-list? p 'procedure))
(...)
```
These are the routines to detect expressions. 

## Routines that get information out of expressions

Beside routines to detect expressions we have some procedures to get information out of expressions. 
We know how the syntax is defined and we know at which position certain elements should be located.
First we define some helper methods to get certain elements out of a list besides the rest and the first, so for example the second is:

```
(defn second [exp] (first (rest exp))) ; cadr (already exists)
(defn rest-after-second [exp] (rest (rest exp))) ; cddr
(...)
```

One of the reasons Common Lisp and Scheme kept calling the first and the rest `car` and `cdr` is that you can combine them. 
The `car` of the `cdr` would be the `cadr`. 

Anyway, these routines are implemented in Clojure as follows:

```
(defn operator [exp] (first exp))
(defn operands [exp] (rest exp))
(defn text-of-quotation [exp] (second exp))
(defn assignment-variable [exp] (second exp))
(defn lambda-parameters [exp] (second exp))
(defn lambda-body [exp] (rest-after-second exp))
(...)
```

here we see that for example lamba-body is the `rest-after-second`. Which is indeed correct. If we look at:

```
(fn [a b c d] (+ a b (* c d)))
```

The second element itself are indeed the parameters `(a b c d)`. 
We see that everything after the second element `(a b c d) is the body. 

All the other implementations of routines that get information out of expressions are defined in the same manner.

In Clojure the car and cdr are removed, but instead you can do `(nth [expression] 3)` to get the third elements. 
I don't know if you can also get something like the rest after the second element, which would be the `cddr`. 
By combining procedures together with `comp` and then dropping elements after a certain index we can also get all parts we want.

## Routines to manipulate expressions
As seen in the definition of `eval` above, when an expression is a lambda expression (as identified by `lambda?`), we make a procedure. This is implemented as follows:

```
(defn make-procedure [parameters body env]
  (list 'procedure parameters body env))
```

So we create a new list via the procedure `list` (which is a routine to create a linked list via a chain of `cons`'es). 
In a similar manner we can create an if-expresion, to which cond expressions can be converted to via a procedure named `expand-clauses`.
By recursively expanding.

## Apply

Finally we arrive at `apply`. `apply` is called in `eval` if the expression is not one of the other types, but is an application.
An expression is an application if it is not one of the special forms, but it is a `list`. Then we apply the procedure to the arguments.
This is the definition of apply:

```
(defn apply [procedure arguments]
  (cond (primitive-procedure? procedure)
        (apply-primitive-procedure procedure arguments)
        (compound-procedure? procedure)
        (eval-sequence
         (procedure-body procedure)
         (extend-environment (procedure-parameters procedure)
                             arguments
                             (procedure-environment procedure)))
        :else (error "Unknown procedure type -- APPLY" procedure)))
```
Here we first check if a procedure is a primitive procedure. 
A procedure is a primitive procedure if it is looked up in the environment and it was in the list of primitive procedures defined in the
environment (we come to this later). 
The primitive procedures are defined as follows, and are later added to the environment.

```
(def primitive-procedures
  (list (list 'first first)
        (list 'rest rest)
        (list 'cons cons)
        (list 'nil? nil?)
        (list 'list list)
        (list '+ +)
        (list '- -)
        (list '* *)
        (list '/ /)
        ;;      more primitives
        ))
```

If the procedure is a primitive procedure we apply the primitive procedure with the underlying apply of Clojure.

At the beginning of the evaluator we store the underlying `apply` to not confuse it with the newly defined `apply`:

```
(defn apply-from-underlying-lisp [f args] (apply f args))
```

How this apply that is built into Clojure works, is magic. A topic for a later blog post.

The application of a primitive procedure is defined as follows:

```
(defn apply-primitive-procedure [proc args]
  (apply-from-underlying-lisp
   (primitive-implementation proc) args))
```

Where primitive-implementation is a method to retrieve the procedure:

```
(defn primitive-implementation [proc] (second proc))
```
As can be seen above where the `primitives-procedures` are defined the primitive-implementation is a symbol like `first` or `+`. 
Again, these are evaluated by the underlying Clojure implementation.

If we are dealing with a compound procedure (a list tagged with `procedure` which can contain more than one inner application), we evaluate the sequence of elements with the routine `eval-sequence`:

```
(defn eval-sequence [exps env]
  (if (last-exp? exps)
    (eval (first-exp exps) env)
    (do
      (eval (first-exp exps) env)
      (eval-sequence (rest-exps exps) env))))
```
This is a recursive definition where, if we have only one expression, only evaluate this one expression in the environment, and if we have more than one expression, we evaluate the first expression and `eval-sequence` the rest of the expressions in the environment.

Here `last-exp`, `first-exp` and `rest-exp` are defined as follows:

```
(defn last-exp? [seq] (empty? (rest seq)))
(defn first-exp [seq] (first seq))
(defn rest-exps [seq] (rest seq))
```

Naturally the first expression is the first element of the sequence (of expressions), the rest of the expressions are the rest of the sequence, and we are dealing with the the last expression if the rest of the sequence after it is empty.

Important to note is that before we evaluate we have to extend the environment of the evaluation with the procedure parameters, arguments, and the procedure environment itself. 
This way the environment of the procedure (which was stored when it was created) are found earlier.

So what exactly is this extending of an environment, and in what way are symbols added to it? 


## The environment

An environment consists of frames stacked together in a list. A frame is empty or consists of two lists, one lists with variable values, and one list of variable values.
When a definition is looked up we look at one frame to see if the variable is defined there. If it is not, we look one frame further to see if it is defined there.

In an image this looks like this:

[![environment frames](https://farm2.staticflickr.com/1622/26214944100_c18bbb5421_m.jpg)](https://www.flickr.com/photos/141920076@N05/26214944100/in/dateposted-public/)

Each time a function is applied, a new frame is created.

It turns out that how to implement the environment made the implementation in Clojure very difficult.
Clojure has, unlike Scheme (the language where this evaluator was translated from), no mutable (changeable) values. 
Where in Scheme you can change the first element or last-element of a list respectively with the routines `set-car!` and `set-cdr!`,
this is not possible in Clojure. In Clojure there is however something like an atom.

My first attempt at implementing the environment uses the implementation of Greg Sexton ([source](https://github.com/gregsexton/SICP-Clojure/blob/master/src/sicp/ch4.clj)).
This environment structure:

```
(def the-empty-environment '())
(def first-frame first)

(defn make-frame [variables values]
  (atom (zipmap variables values)))

(def frame-variables keys)
(def frame-values vals)

(defn add-binding-to-frame! [var val frame]
  (swap! frame assoc var val))

(defn extend-environment [vars vals base-env]
  (when (= (count vars) (count vals))
    (cons (make-frame vars vals)
          base-env)))

(defn lookup-variable-value [var env]
  (some (comp #(get % var) deref) env))

(defn find-first-frame-containing [var env]
  (some #(when (contains? (deref %) var) %) env))

(defn set-variable-value! [var val env]
  (when-let [frame (find-first-frame-containing var env)]
    (add-binding-to-frame! var val frame)))

(defn define-variable! [var val env]
  (add-binding-to-frame! var val
                         (or (find-first-frame-containing var env)
                             (first-frame env))))
```


And at the beginning we setup the environment, where we 
```
(defn setup-environment []
  (let [initial-env
        (extend-environment primitive-procedure-names
                            primitive-procedure-objects
                            the-empty-environment)]
    (define-variable! 'true true initial-env)
    (define-variable! 'false false initial-env)
    initial-env))
```

The behaviour of this implementation will be described here:

And when we start running the meta-circular evaluator


## Interacting with the evaluator
Some extra code is added to read in the input via `read`


## Seeing the behaviour:

The reason for this all have to do with concurrent programming. Mutable values can create huge headaches if you are dealing with multiple threads that are
accessing the same changing variables. I had three attempts to implement the environment structure of this evaluator, and unfortunately none of them have been succesful.

Here I'll show you the attempts.

## Lexical scoping versus dynamic scoping
Lexical closure

Test for a dynamic language can be done by a "let over lambda".

You can test if a language is dynamic or static for example with the following idea. First you define a method as follows:
```
(defn test (fn () scope))
```
When you call this method 

```
(test)
```
this returns

>Is not a valid symbol -- LOOKUP-VARIABLE-VALUE scope

This is because scope is not available in the environment. It is not defined.

Next when you introduce a new binding with the same name (`scope`) wherein you call test:
```
(let ((scope 'dynamic)) (test))
```
a lexically scoped language will return 'scope is undefined', because the environment when the definition of `test` was created does not contain this binding. 
While a dynamic language will return 'dynamic. It does not care about what the value of scope was *when test was defined* and just looks up the nearest definition. In other words: the lexical environment was not captured.

((fn (scope) (test)) 'dynamic)

(This idea was obtained from [this Stack Overflow answer](http://stackoverflow.com/questions/32344615/program-to-check-if-the-scoping-is-lexical-or-dynamic#32347224), but slightly modified since our language does not contain `let` (`let` is nothing more than a lambda, it is not a fundamental form).

As expected from the above description, our dynamic evaluator has the following behaviour:

>;;; Eval input:<br><br>
(defn test (fn () scope))<br><br>
;;; Eval value:<br><br>
('updated-env [Environment]<br><br>
>;;; Eval input:<br><br>
(test)<br><br>
>;;; Eval output:<br><br>
Is not a valid symbol -- LOOKUP-VARIABLE-VALUE scope

And when we call the new binding in a new environment:
>;;; Eval input:<br><br>
(defn test (fn () scope))<br><br>
;;; Eval value:<br><br>
('updated-env [Environment]<br><br>
>;;; Eval input:<br><br>
((fn (scope) (test)) (quote dynamic))<br><br>
;;; Eval value:<br><br>
dynamic

## Closing remarks

WHAT IS THIS MAGIC THIS SUSSMAN CHARACTER IS TELLING ME?

Once you understand how the meta-circular evaluator works, it becomes easy to change the implementation and create interpreters for other languages. In SICP among other things the evaluator is extended and modified to become an evaluator for:
- [A evaluator with lazy evaluation](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-27.html)
- [A Logic Programming language](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-29.html#%_sec_4.4)

And it is even used to create a [compiler](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-35.html#%_sec_5.5.1)!

By following with what Gerald Jay Sussman calls one powerful method of synthesis - *wishful thinking* 
> "*Wishful thinking is essential to good engineering.*"<br>
-- Gerald Jay Sussman ([Source](https://www.youtube.com/watch?v=erHp3r6PbJk&t=50m35s))

I asked a question about it on Stack Overflow and I mailed the author's of other meta-circular evaluators, so far no response. 

I am honored that you have read this post. Thank you for that. If you happen to know what is wrong with the environment structure please let me know! Other remarks are welcome as well.


# .setCar .setCdr...
I have never been so angry with myself and yet so relieved as when I had to implement this. 
The idea of only changing stuff at the edges. (art of unux programming) The interface.

[Data Structures in Clojure: Singly-Linked List](http://macromancy.com/2014/01/16/data-structures-clojure-singly-linked-list.html)

Next is implementing Macro's.

Even more respect for this piece of absolute ingenuity that was designed by Gerald Jay Sussman and Guy Steele: SCHEME!

Clojure is not an atomic programming language. I'm too tired/old/lazy
to program with atoms. Clojure provides production implementations of
generic dispatch, associative maps, metadata, concurrency
infrastructure, persistent data structures, lazy seqs, polymorphic
libraries etc etc. Much better implementations of some of the things
you would be building by following along with SICP are in Clojure
already. 


So the value in SICP would be in helping you understand programming
concepts. If you already understand the concepts, Clojure lets you get
on with writing interesting and robust programs much more quickly,
IMO. And I don't think the core of Clojure is appreciably bigger than
Scheme's. What do Schemers think?

I think the Lisps prior to Clojure lead you towards a good path with
functional programming and lists, only to leave you high and dry when
it comes to the suite of data structures you need to write real
programs, such data structures, when provided, being mutable and
imperative. Prior Lisps were also designed before pervasive in-process
concurrency, and before the value of high-performance polymorphic
dispatch (e.g. virtual functions) as library infrastructure was well
understood. Their libraries have decidedly limited polymorphism.

Alas, there is no book on Clojure yet. But, to the extent Schemes go
beyond the standard to provide more complete functionality (as most
do), there are no books on that either. Just docs in both cases. 
https://groups.google.com/forum/#!topic/clojure/jyOuJFukpmE


In the video, Mr. Hickey says:

    The biggest problem we have is we've conflated two things. We've
              said the idea that I attach to this thing that lasts over time
              is the thing that lasts over time.


YOU CANNOT CALL IT CLOJURE IT HAS MUTABLE DATA STRUCTURES


I gave up.

What I tried:
1. Build with Clojure data structures and have mutable state.
2. Use dynamic environment
3. Create new data structures as per other blog posts. A Mutable Clojure if you will.

Sexpression
<a data-flickr-embed="true"  href="https://www.flickr.com/photos/141920076@N05/26211413010/in/dateposted-public/" title="sexpression"><img src="https://farm2.staticflickr.com/1673/26211413010_4aa4dcef11_z.jpg" width="640" height="317" alt="sexpression"></a><script async src="//embedr.flickr.com/assets/client-code.js" charset="utf-8"></script>
Eval-apply cycle
<a data-flickr-embed="true"  href="https://www.flickr.com/photos/141920076@N05/25881391543/in/dateposted-public/" title="evalapply cycle"><img src="https://farm2.staticflickr.com/1549/25881391543_79284b2231_m.jpg" width="240" height="196" alt="evalapply cycle"></a><script async src="//embedr.flickr.com/assets/client-code.js" charset="utf-8"></script>


    The difference between self-interpreters and meta-circular interpreters is that the latter restate language features in terms of the features themselves, instead of actually implementing them. (Circular definitions, in other words; hence the name). They depend on their host environment to give the features meaning.
    — Reginald Braithwaite, "The significance of the meta-circular interpreter". 2006-11-22. Retrieved 2011-01-22.

Meta-circular evaluation is discussed at length in section 4.1, titled The Metacircular Evaluator, of the MIT university textbook Structure and Interpretation of Computer Programs (SICP). The core idea they present is two functions:

SICP based on Lambda papers:
http://library.readscheme.org/page1.html
