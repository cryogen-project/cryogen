{:title "Spacemacs Cheatsheet"
 :layout :page
 :page-index 3
 :navbar? true}

# Spacemacs cheatsheet

This is my Spacemacs cheatsheet.

## Cider

### , s [iI]
Start Clojure (i) or ClojureScript (I) cider-jack-in.

### :cider-restart <RET>
Restart Cider.-new

### C-c C-z 
Switch to REPL, in REPL switches to previous buffer.

### C-x C-e 
Evaluate expression before cursor.

### C-c M-e 
The same as `C-x C-e` except the result is sent to the REPL.

### C-c C-k
Evaluate buffer.

### C-c C-d d
Show the documentation as you would with (doc function-name).

### C-c M-n 
Switch to namespace of current Clojure buffer. 

## Testing and debugging

### SPC-m-t-*
Unit testing options: 
a all tests (as in file, looks for file with same name prefixed with tests).
r rerun last tests.
t run test under cursor.

### C-u C-M-x
This will take the current top-level form, place as many breakpoints inside 
it as possible (instrument it), and then evaluate it as normal.
Note: Needs to be in INSERT mode.

n next
i into
More: https://github.com/clojure-emacs/cider/blob/master/doc/debugging.md

## Navigation

### SPC w l
It is impossible to move out of Neotree with C-w Vim movements since the
Vim keybindings are disabled. To move to another buffer use `C-x o` or `SPC w l` to move to the left (so SPC is substituted for C here).

### SPC f e d
Show dot file.

### C-x b
List of open buffers (and switch to them).

## Editing

### SPC S c
Correct spelling of word under cursor

For Vim/Evil keybindings: 
- http://vim.rtorr.com/
- `:evil-tutor`

## Clojure mode

### C-A-\
Indent region

### SPC k s
Slurp forward

### SPC k S
Slurp backward

### SPC k b
Barf forward

### SPC k B
Barf backward

### C-c SPC
Align sexps
```
(def my-map
  {:a-key 1
   :other-key 2})
```
Leads to:
```
(def my-map
  {:a-key     1
   :other-key 2})
```
See https://github.com/clojure-emacs/clojure-mode

## Magit

### SPC g s
Display git status
- s Add untracked file to staging area
- i Add file to .gitignore

See http://daemianmack.com/magit-cheatsheet.html
