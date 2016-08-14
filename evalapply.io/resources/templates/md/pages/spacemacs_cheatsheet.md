{:title "Spacemacs Cheatsheet"
 :layout :page
 :page-index 1
 :navbar? true}

This is my Spacemacs cheatsheet.

For the shortcuts C is CTRL and M (Meta) is ALT.

# Table of contents
1. [Cider](#cider)
2. [Testing and debugging](#testing_and_debugging)
3. [Navigation](#navigation)
4. [Editing](#editing)
5. [Clojure mode](#clojure_mode)
6. [Magit](#magit)

# Cider

### , s [iI]
Start Clojure (i) or ClojureScript (I) cider-jack-in.

### :cider-restart <RET>
Restart Cider.

### C-c C-z 
Switch to REPL, in REPL switches to previous buffer.

### C-x C-e 
Evaluate expression before cursor.

### C-x M-p
Send expression before cursor to REPL.

### C-c M-e 
The same as `C-x C-e` except the result is sent to the REPL.

### C-c C-k
Evaluate buffer.

### C-c C-b
Interrupt any pending evaluations.

### C-c C-d d or , h h
Show the documentation as you would with (doc function-name).

### C-c C-d r or , h g
Display documentation with examples via Grimoire (from Clojure site).

### , g g
Go to declaration

### C-c M-n 
Switch to namespace of current Clojure buffer. 

# Testing and debugging

### , t [art]
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

### C-c C-c
Remove breakpoints in method (if I am not mistaken).

# Navigation

### TAB
HELM completion at point.

### SPC a u
Display undo tree.

### SPC w l
It is impossible to move out of Neotree with C-w Vim movements since the
Vim keybindings are disabled. To move to another buffer use `C-x o` or `SPC w l` to move to the left (so SPC is substituted for C here).

### SPC f e d
Show dot file.

### C-x b
List of open buffers (and switch to them).

### SPC b [np]
p Previous useful buffer.
n Next useful buffer.

### SPC TAB
Previous buffer.

Also see some Cider commands

# Editing

### SPC S c
Correct spelling of word under cursor

For Vim/Evil keybindings: 
- http://vim.rtorr.com/
- `:evil-tutor`

# Clojure mode

### C-M-\
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

# Magit

### SPC g s
Display git status
- s Add untracked file to staging area
- i Add file to .gitignore

See http://daemianmack.com/magit-cheatsheet.html
