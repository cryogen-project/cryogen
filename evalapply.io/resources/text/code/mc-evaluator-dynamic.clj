(ns evalapply.evaluator)

;; Resources used:

;; Abelson, H., Sussmann, J., & Sussmann, G (1996)
;; "Structure and Interpretation of Computer Programs"
;; (official site: https://mitpress.mit.edu/sicp/), MIT Press. 
;; Code: https://mitpress.mit.edu/sicp/code/ch4-mceval.scm

;; And the dynamic evaluator from:
;; https://github.com/matlux/metacircular-evaluator-clj


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
         second boolean? pair error)

;; Store apply from Clojure, we need it to apply primitive
;; procedures. And then declare it as well, otherwise Clojure
;; mistakes apply defined in eval for the Clojure one.
(defn apply-from-underlying-lisp [f args] (apply f args))
(declare apply)

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
                                  (list-of-values (operands exp) env)
                                  env)
        :else (error "Unknown expression type -- EVAL" exp)))

(defn apply [procedure arguments global-env]
  (cond (primitive-procedure? procedure)
        (apply-primitive-procedure procedure arguments)
        (compound-procedure? procedure)
        (eval-sequence
         (procedure-body procedure)
         (merge global-env (extend-environment
                            (procedure-parameters procedure)
                            arguments
                            (procedure-environment procedure))))
        :else (error "Unknown procedure type -- APPLY" procedure)))

(defn list-of-values [exps env]
  (cond (no-operands? exps) nil
        :else (cons (eval (first-operand exps) env)
                    (list-of-values (rest-operands exps) env))))

(defn eval-sequence [exps env]
  (if (last-exp? exps)
    (eval (first-exp exps) env)
    (do
      (eval (first-exp exps) env)
      (recur (rest-exps exps) env))))

(defn eval-assignment [exp env]
  (set-variable-value! (assignment-variable exp)
                       (eval (assignment-value exp) exp)
                       env))

(defn eval-definition [exp env]
  (list 'updated-env (define-variable! (definition-variable exp)
                       (eval (definition-value exp)
                             (dissoc env (definition-variable exp)))
     env)))

(defn eval-if [exp env]
  (if (eval (if-predicate exp) env)
    (eval (if-consequent exp) env)
    (eval (if-alternative exp) env)))

(defn tagged-list? [exp tag]
  (and (list? exp) (= (first exp) tag)))

(defn error [msg var]
  (throw (Throwable. (str msg " " var))))


;; Routines to detect expressions

(defn if? [exp] (tagged-list? exp 'if))
(defn lambda? [exp] (tagged-list? exp 'fn))
(defn application? [exp] (list? exp))
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

(defn second [exp] (first (rest exp))) ; cadr (already exists)
(defn rest-of-second [exp] (rest (first (rest exp)))) ; cdadr
(defn third [exp] (first (rest (rest exp)))) ; caddr
(defn flat-third [exp] (first (first (rest exp)))) ; caadr
(defn fourth [exp] (first (rest (rest (rest exp))))) ; cadddr
(defn rest-after-second [exp] (rest (rest exp))) ; cddr
(defn rest-after-third [exp] (rest (rest (rest exp)))) ; cdddr

(defn operator [exp] (first exp))
(defn operands [exp] (rest exp))
(defn text-of-quotation [exp] (second exp))
(defn assignment-variable [exp] (second exp))
(defn assignment-value [exp] (third exp))

(defn definition-variable [exp]
  (if (symbol? (second exp))
    (second exp)
    (flat-third exp)))

(defn definition-value [exp]
  (if (symbol? (second exp))
    (third exp)
    (make-lambda (rest-of-second exp)
                 (rest-after-second exp))))

(defn lambda-parameters [exp] (second exp))
(defn lambda-body [exp] (rest-after-second exp))

(defn if-predicate [exp] (second exp))
(defn if-consequent [exp] (third exp))

(defn if-alternative [exp]
  (if (not (empty? (rest-after-third exp)))
    (fourth exp)
    'false))

(defn begin-actions [exp] (rest exp))

(defn last-exp? [seq] (empty? (rest seq)))
(defn first-exp [seq] (first seq))
(defn rest-exps [seq] (rest seq))

(defn sequence->exp [seq]
  (cond (empty? seq) seq
        (last-exp? seq) (first-exp seq)
        :else (make-begin seq)))

(defn cond-clauses [exp] (rest exp))
(defn cond-predicate [clause] (first clause))
(defn cond-actions [clause] (rest clause))

(defn procedure-parameters [p] (second p))
(defn procedure-body [p] (third p))
(defn procedure-environment [p] (fourth p))


;; Routines to manipulate expressions

(defn no-operands? [args] (empty? args))
(defn first-operand [args] (first args))
(defn rest-operands [args] (rest args))

(defn make-lambda [parameters body]
  (list 'fn parameters body))

(defn make-if [predicate consequent alternative]
  (list 'if predicate consequent alternative))

(defn make-begin [seq] (cons 'begin seq))

(defn cond->if [exp]
  (expand-clauses (cond-clauses exp)))

(defn expand-clauses [clauses]
  (if (empty? clauses)
    'false                          ; no else clause
    (let [first (first clauses)
          rest (rest clauses)]
      (if (cond-else-clause? first)
        (if (empty? rest)
          (sequence->exp (cond-actions first))
          (error "ELSE clause isn't last -- COND->IF" clauses))
        (make-if (cond-predicate first)
                 (sequence->exp (cond-actions first))
                 (expand-clauses rest))))))

(defn make-procedure [parameters body env]
  (list 'procedure parameters body env))


;; Environment structure

(defn extend-environment [procedure-parameters
                          args
                          procedure-environment]
  (merge procedure-environment (pair procedure-parameters args)))

(defn pair
  [xs ys]
  {:pre [(= (count xs) (count ys))]}
  (into {} (map vector xs ys)))

(defn define-variable! [var val env]
  (assoc env var val))

(defn lookup-variable-value [var env]
  (let [item (env var)]
    (cond (lambda? item) (eval item env)
          (nil? item) (error "Is not a valid symbol -- LOOKUP-VARIABLE-VALUE" item)
          :else item)))


;; Setup environment

(defn primitive-procedure? [proc]
  (tagged-list? proc 'primitive))

(defn primitive-implementation [proc] (second proc))
(def primitive-procedures
  {'cons cons
   'first first
   'rest rest
   'list list
   '= =
   '- -
   '+ +
   ;;      more primitives
   })

(defn primitive-procedure-names []
  (keys primitive-procedures))

(defn primitive-procedure-objects []
  (map (fn [proc] (list 'primitive (second proc)))
       primitive-procedures))

(defn apply-primitive-procedure [proc args]
  (apply-from-underlying-lisp
   (primitive-implementation proc) args))

(def setup-environment
  (extend-environment (primitive-procedure-names)
                      (primitive-procedure-objects)
                      (read-string "{}")))


;; Code to interact with the evaluator:

(def input-prompt ";;; Eval input:")
(def output-prompt ";;; Eval value:")

(defn repl-loop [env]
  (println input-prompt)
  (flush)
  (let [line (read-line)
        output (try
                 (eval (read-string line) env)
                 (catch Exception e
                   (str (.printStackTrace e) (.getMessage e)))
                 (catch StackOverflowError e
                   (str (.printStackTrace e) (.getMessage e))))]
    (prompt-for-input line)
    (println output-prompt)
    (announce-output output)
    (if (tagged-list? output 'updated-env)
      (recur (second output))
      (recur env))))

(defn prompt-for-input [string]
  (newline) (newline) (println string) (newline))

(defn announce-output [string]
  (newline) (println string) (newline))

(defn user-print [object]
  (if (compound-procedure? object)
    (println (list 'compound-procedure
                   (procedure-parameters object)
                   (procedure-body object)
                   '<procedure-env>))
    (println object)))

'METACIRCULAR-EVALUATOR-LOADED

(def initial-environment setup-environment)

(repl-loop initial-environment)
