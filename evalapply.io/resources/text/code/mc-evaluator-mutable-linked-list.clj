(ns evalapply.evaluator)

;; Resources used:

;; Abelson, H., Sussmann, J., & Sussmann, G (1996)
;; "Structure and Interpretation of Computer Programs"
;; (official site: https://mitpress.mit.edu/sicp/), MIT Press. 
;; Code: https://mitpress.mit.edu/sicp/code/ch4-mceval.scm

;; Countryman, M. (2014) "Data Structures in Clojure: Singly-Linked Lists"
;; Accessed: April 12 2016. Source:
;; http://macromancy.com/2014/01/16/data-structures-clojure-singly-linked-list.html



;; Have to declare all defined stuff below on top (except eval and
;; apply.
(declare self-evaluating? variable? lookup-variable-value quoted?
         assignment? definition? if? lambda? make-procedure begin?
         cond? application? text-of-quotation eval-assignment
         eval-definition eval-if eval-sequence lambda-parameters
         lambda-body val-sequence cond->if list-of-values
         operator operands begin-actions no-operands? first-operand
         rest-operands primitive-procedure?
         apply-primitive-procedure compound-procedure?
         procedure-body extend-environment procedure-parameters
         procedure-environment last-exp? first-exp rest-exps
         set-variable-value! assignment-variable assignment-value
         define-variable! definition-variable definition-value
         if-predicate if-consequent if-alternative cond-predicate
         make-lambda make-begin expand-clauses
         primitive-procedure-names primitive-procedure-objects
         the-empty-environment prompt-for-input
         the-global-environment announce-output user-print
         boolean? error -cons car cdr cadr cdadr caddr caadr cadddr
         cddr cdddr -count -empty? -map -list? -list)

;; Store apply from Clojure, we need it to apply primitive
;; procedures. And then declare it as well, otherwise Clojure
;; mistakes apply defined in eval for the Clojure one.
(defn apply-from-underlying-lisp [f args] (apply f args))
(declare apply)

(defn eval [exp env]
  (println (str "EVAL with exp: " exp " and env: " env))
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

(defn apply [procedure arguments]
  (println (str "APPLY with proc: " procedure " and args: " arguments))
  (cond (primitive-procedure? procedure)
        (apply-primitive-procedure procedure arguments)
        (compound-procedure? procedure)
        (eval-sequence
         (procedure-body procedure)
         (extend-environment (procedure-parameters procedure)
                             arguments
                             (procedure-environment procedure)))
        :else (error "Unknown procedure type -- APPLY" procedure)))

(defn list-of-values [exps env]
  (cond (no-operands? exps) nil
        :else (-cons (eval (first-operand exps) env)
                    (list-of-values (rest-operands exps) env))))

(defn eval-sequence [exps env]
  (cond (last-exp? exps) (eval (first-exp exps) env)
        :else ((eval (first-exp exps) env)
               (eval-sequence (rest-exps exps) env))))

(defn eval-assignment [exp env]
  (set-variable-value! (assignment-variable exp)
                       (eval (assignment-value exp) exp)
                       env))

(defn eval-definition [exp env]
  (define-variable! (definition-variable exp)
    (eval (definition-value exp) env)
    env))

(defn eval-if [exp env]
  (if (eval (if-predicate exp) env)
    (eval (if--consequent exp) env)
    (eval (if-alternative exp) env)))

(defn tagged-list? [exp tag]
  (and (-list? exp) (= (car exp) tag)))

(defn error [msg & vars]
  (throw (Throwable. (str msg " " vars))))


;; Routines to detect expressions

(defn if? [exp] (tagged-list? exp 'if))
(defn lambda? [exp] (tagged-list? exp 'fn))
(defn application? [exp] (-list? exp))
(defn variable? [exp] (symbol? exp))

(defn self-evaluating? [exp]
  (cond (number? exp) true
        (string? exp) true
        (boolean? exp) true
        :else false))

(defn boolean? [exp] (or (= 'true exp) (= 'false exp))) 
(defn quoted? [exp] (tagged-list? exp 'quote))
(defn definition? [exp] (tagged-list? exp 'defn))
(defn assignment? [exp] (tagged-list? exp 'set!))
(defn begin? [exp] (tagged-list? exp 'begin))
(defn true? [x] (not (= x false)))
(defn false? [x] (= x false))
(defn cond? [exp] (tagged-list? exp 'cond))

(defn cond-else-clause? [clause]
  (= (cond-predicate clause) 'else))

(defn compound-procedure? [p]
  (tagged-list? p 'procedure))


;; Routines to get information out of expressions

(defn operator [exp] (car exp))
(defn operands [exp] (cdr exp))
(defn text-of-quotation [exp] (cadr exp))
(defn assignment-variable [exp] (cadr exp))
(defn assignment-value [exp] (caddr exp))

(defn definition-variable [exp]
  (if (symbol? (cadr exp))
    (cadr exp)
    (caadr exp)))

(defn definition-value [exp]
  (if (symbol? (cadr exp))
    (caddr exp)
    (make-lambda (cdadr exp)
                 (cddr exp))))

(defn lambda-parameters [exp] (cadr exp))
(defn lambda-body [exp] (cddr exp))

(defn if-predicate [exp] (cadr exp))
(defn if-consequent [exp] (caddr exp))

(defn if-alternative [exp]
  (if (not (-empty? (cdddr exp)))
    (cadddr exp)
    'false))

(defn begin-actions [exp] (cdr exp))

(defn last-exp? [seq] (-empty? (cdr seq)))
(defn first-exp [seq] (car seq))
(defn rest-exps [seq] (cdr seq))

(defn sequence->exp [seq]
  (cond (-empty? seq) seq
        (last-exp? seq) (first-exp seq)
        :else (make-begin seq)))

(defn cond-clauses [exp] (cdr exp))
(defn cond-predicate [clause] (car clause))
(defn cond-actions [clause] (cdr clause))

(defn procedure-parameters [p] (cadr p))
(defn procedure-body [p] (caddr p))
(defn procedure-environment [p] (cadddr p))


;; Routines to manipulate expressions

(defn no-operands? [args] (-empty? args))
(defn first-operand [args] (car args))
(defn rest-operands [args] (cdr args))

(defn make-lambda [parameters body]
  (-list 'fn parameters body))

(defn make-if [predicate -consequent alternative]
  (-list 'if predicate -consequent alternative))

(defn make-begin [seq] (-cons 'begin seq))

(defn cond->if [exp]
  (expand-clauses (cond-clauses exp)))

(defn expand-clauses [clauses]
  (if (-empty? clauses)
    'false                          ; no else clause
    (let [first (car clauses)
          rest (cdr clauses)]
      (if (cond-else-clause? first)
        (if (-empty? rest)
          (sequence->exp (cond-actions first))
          (error "ELSE clause isn't last -- COND->IF" clauses) )
        (make-if (cond-predicate first)
                 (sequence->exp (cond-actions first))
                 (expand-clauses rest))))))

(defn make-procedure [parameters body env]
  (println (-list 'procedure parameters body env))
  (-list 'procedure parameters body env))


;; Mutable -list implementation

(definterface INode
  (getCar [])
  (getCdr [])
  (setCar [x])
  (setCdr [x]))

(deftype Node [^:volatile-mutable car ^:volatile-mutable cdr]
  INode
  (getCar [this] car)
  (getCdr [this] cdr)
  (setCar [this x] (set! car x) this)
  (setCdr [this x] (set! cdr x) this))

(defn -cons [head tail]
  (Node. head tail))

(defn -list? [x]
  (= (instance? Node x)))

(defn set-car! [node new]
  (.setCar node new))

(defn set-cdr! [node new]
  (.setCdr node new))

(defn car [exp]
  (.getCar exp))

(defn caar [exp]
  (.getCar (.getCar exp)))

(defn cdr [exp]
  (.getCdr exp))

(defn cddr [exp]
  (.getCdr (.getCdr exp)))

(defn cadr [exp]
  (.getCar (.getCdr exp))) 

(defn cdadr [exp]
  (.getCdr (.getCar (.getCdr exp))))

(defn caddr [exp]
  (.getCar (.getCdr (.getCdr exp))))

(defn caadr [exp]
  (.getCar (.getCar (.getCdr exp))))

(defn cadddr [exp]
  (.getCar (.getCdr (.getCdr (.getCdr exp)))))

(defn cddr [exp]
  (.getCdr (.getCdr exp)))

(defn cdddr [exp]
  (.getCdr (.getCdr (.getCdr exp))))

(defn -empty? [list]
  (or
   (not (= (type list) Node))
   (= list nil)
   (= (car -list) nil)))

(defn -count [list]
  (loop [l list
         count 0]
    (if (-empty? list) count
        (recur (cdr list) (+ count 1)))))

(defn foldr [f init list] 
  (if (nil? list) 
      init 
      (f (car list) 
         (foldr f init (cdr list)))))

(defn -list [x & xs]
  (if (nil? x) '()
      (-cons x (foldr -cons '() xs))))

(-list 0 1 2 3 4 5 6 7 8 9 10) ;; => (Cons. 0 nil)
;; I GIVE UP!!!

(defn -map [f coll]
  (if (-empty? coll) nil
      (-cons (f (car coll)) (cdr coll))))

(defn -display [l]
  (-map println l))


;; Environment structure

(defn enclosing-environment [env]
  (cdr env))

(defn first-frame [env]
  (car env))

(def the-empty-environment '())

(defn make-frame [variables values]
  (-cons variables values))

(defn frame-variables [frame] (car frame))
(defn frame-values [frame] (cdr frame))

(defn add-binding-to-frame! [var val frame]
  (set-car! frame (-cons var (car frame)))
  (set-cdr! frame (-cons val (cdr frame))))

(defn extend-environment [vars vals base-env]
  (if (= (-count vars) (-count vals))
      (-cons (make-frame vars vals) base-env)
      (if (< (-count vars) (-count vals))
          (error "Too many arguments supplied" vars vals)
          (error "Too few arguments supplied" vars vals))))

(defn lookup-variable-value [var env]
  (defn env-loop [env]
    (defn scan [vars vals]
      (cond (-empty? vars) (env-loop (enclosing-environment env))
            (= var (car vars)) (car vals)
            :else (scan (cdr vars) (cdr vals))))
    (if (= env the-empty-environment)
      (error "Unbound variable -- LOOKUP" var)
      (let [frame (first-frame env)]
        (scan (frame-variables frame)
              (frame-values frame)))))
  (env-loop env))

(defn set-variable-value! [var val env]
  (defn env-loop [env]
    (defn scan [vars vals]
      (cond (-empty? vars) (env-loop (enclosing-environment env))
            (= var (car vars)) (set-car! vals val)
            :else (scan (cdr vars) (cdr vals))))
    (if (= env the-empty-environment)
      (error "Unbound variable -- SET!" var)
      (let [frame (first-frame env)]
        (scan (frame-variables frame)
              (frame-values frame)))))
  (env-loop env))

(defn define-variable! [var val env]
  (let [frame (first-frame env)]
    (defn scan [vars vals]
      (cond (-empty? vars) (add-binding-to-frame! var val frame))
      (= var (car vars) (set-car! vals val))
      :else (scan (cdr vars) (cdr vals)))
    (scan (frame-variables frame)
          (frame-values frame))))


;; Setup environment

(defn setup-environment []
  (let [initial-env
        (extend-environment primitive-procedure-names
                            primitive-procedure-objects
                            the-empty-environment)]
    (define-variable! 'true true initial-env)
    (define-variable! 'false false initial-env)
    initial-env))

(defn primitive-procedure? [proc]
  (tagged-list? proc 'primitive))

(defn primitive-implementation [proc] (cadr proc))

(-list (-list 'car car) 
       (-list 'cdr cdr)
       (-list 'cons -cons)
       (-list 'null? nil?)
       (-list 'list -list)
       (-list '+ +)
       (-list '- -)
       (-list '* *)
       (-list '/ /)
       ;; more primitives
       )

(def primitive-procedure-names 
  (-map car primitive-procedures))

;; -Map needs to be implemented.
(def primitive-procedure-objects 
  (-map (fn [proc] (-cons 'primitive (-cons (cadr proc) '())))
       primitive-procedures))

(defn apply-primitive-procedure [proc args]
  (apply-from-underlying-lisp
   (primitive-implementation proc) args))


;; Code to interact with the evaluator:

(def input-prompt ";;; Eval input:")
(def output-prompt ";;; Eval value:")

(defn driver-loop []
  (prompt-for-input input-prompt)
  (let [input (read)]
    (user-print input)
    (let [output (eval input the-global-environment)]
      (announce-output output-prompt)
      (user-print output)))
  (driver-loop))

(defn prompt-for-input [string]
  (newline) (newline) (println string) (newline))

(defn announce-output [string]
  (newline) (println string) (newline))

(defn user-print [object]
  (if (compound-procedure? object)
    (println (-list 'compound-procedure
                   (procedure-parameters object)
                   (procedure-body object)
                   '<procedure-env>))
    (println object)))

'METACIRCULAR-EVALUATOR-LOADED

(def the-global-environment (setup-environment))

(driver-loop)
