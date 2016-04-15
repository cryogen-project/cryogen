{:title "The meta-circular evaluator"
:layout :post
:tags ["Clojure" "Life" "Lisp" "Scala" "SICP"]}

## Introduction

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

In this post I have translated the code from meta-circular evaluator from SICP (you can find the original code [here](https://mitpress.mit.edu/sicp/code/ch4-mceval.scm)) to Clojure. 
I will explain what there is to see.

## Background

This paragraph can be skipped if you have worked with Lisp, but is included for completeness and not deprave those who don't know of the awesomeness. 
I believe I will include all the syntax there is, the rest of Lisps can be implemented in these special forms. (Note: this is not completely true, there are some other special forms used in other Lisps for for example performance reasons).

It is roughly based on the aforementioned [article](http://www.paulgraham.com/rootsoflisp.html) of Paul Graham that explains what McCarthy has discovered.

The meta-circular evaluator is only introduced in chapter 4 of 5 of SICP and lecture 7 of 12. Also in the original paper of McCarthy first some groundwork needs to be laid out. 
Here we go:

The fun thing about Lisp is that the fundamental data structure is a list. The first part of a list is interpreted as the operator, and the others as the operands. 
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

You can define variables in with `defun`, or in Clojure with `def` as follows:

```
(def add1
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

If no fancy names are used for the methods it fits on one page, or as SICP show on the blackboard. 
Here a bit more descriptive names are used since it helps for clarity.

Unfortunately what I have created so far is not truly a meta-circular evaluator. 
A defining characteristic is that a meta-circular evaluator can evaluate Clojure into itself.
This is written in Clojure, but it cannot evaluate Clojure. 
What it can evaluate looks a lot like Scheme translated to Clojure, because in fact it is.

We first write eval. Eval is a procedure that takes two arguments: an expression and an environment. And [like every interesting procedure, it is a case analysis](https://www.youtube.com/watch?v=0m6hoOelZH8&t=5m31s):


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

In the original implementation or in the Lisp 1.5 manual, the

WHAT IS THIS MAGIC THIS SUSSMAN CHARACTER IS TELLING ME?

Apply
Fixed point.

## The environment

I will update this post once I found a solution for the environment problem.


## Lexical scoping versus dynamic scoping
Lexical closure

In SICP it is stated: 

>It is no exaggeration to regard this as the most fundamental idea in programming:<br><br>
The evaluator, which determines the meaning of expressions in a programming language, is just another program. ([Source](https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-25.html))

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

