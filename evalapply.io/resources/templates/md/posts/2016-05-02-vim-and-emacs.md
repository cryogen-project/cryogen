{:title "Vim and Emacs"
:layout :post
:tags ["Emacs" "Vim" "Evil" "Lisp" "Clojure"]}

I'll show you why I love Vim and why I switched to Emacs and why you might too. 

## Vim
Vim is really really awesome. 
Inspired by a lot of great hackers who swear by Vim ([like Bram Moolenaar and Vim Diesel](https://www.reddit.com/r/vim/comments/34vffh/famous_vim_users/)) 
and by the greatest ode to a text editor ever (["Vim Creep"](https://www.norfolkwinters.com/vim-creep/)), 
eight months ago I decided to take the plunge. 

First I did `vimtutor` at home, then I 
installed VimEmu in Visual Studio (I programmed C\# at the time), 
I printed the [Vim cheat sheet](http://www.viemu.com/vi-vim-cheat-sheet.gif), 
and broke up with my mouse. 

Against the advice in the excellent video Learning Vim in a week (see below) I did start at work. 

<iframe src="https://www.youtube.com/embed/_NUO4JEtkDw" allowfullscreen="" frameborder="0" height="315" width="420"></iframe>

Work is the place where I edit the most text and I needed to do some boring work at the time.
I have no regrets learning Vim there.
Within a week my speed was back to my average before that.
I became slower in certain parts but faster in others.

More importantly, boring, repetitive tasks were no longer boring and repetitive. 
In Vim, writing boring code is seriously fun because you stretch your mind on creating efficient text manipulations. 
I stayed interested doing the work, and I learned Vim. In that period both my employer and myself have benefited from me learning Vim at work.

Eight months later I use Vim everywhere. Nowadays even a mundane task as updating my TODO list is exhilarating.

The following example of the awesomeness of Vim is based on a real life situation where I wanted to unroll a T-SQL loop.
Suppose you want to create four separate update statements from the four table names (First, Second, Third, Fourth) and columns 
above the EXECUTE block below:

```
INSERT INTO @TABLES_COLUMNS (TABLE_NAME, COLUMN_NAME)
    VALUES
    ('[dbo].[First]', '[Yadadada]'),
    ('[dbo].[Second]', '[Blabla]'),
    ('[dbo].[Third]', '[Abacadabra]'),
    ('[dbo].[Fourth]', '[Yeehah]'),
    (...)

    WHILE 1=1
    /* Set the variables of table name and column name (substitute with VALUES above) */
    EXECUTE 
    (
        'UPDATE ' + @TABLE_NAME + ' ' +
        'SET ' + @COLUMN_NAME + ' = temp.OLD
        'FROM ' + @TABLE_NAME + 'AS t ' +
        'INNER JOIN #temp AS temp ' +
        'ON t.' + @COLUMN_NAME + ' = temp.NEW'
    )
```

(Note that this code is a bit simplified: the setting of the loop variables and extra values are skipped.)

In the real life example this was based on there were about 15 of those substitutions instead of four.
This is what I would consider a boring task. 
Since the task is short enough that writing a script to automate it is slower than actually doing it,
in normal (non-Vim) life I would to do this by hand.

What needs to be done above is:
1. Cleanup the "+" and quotes from the EXECUTE statement.
2. Copy the cleaned up EXECUTE statement. 
3. Copy the first variable value and substitute it for @TABLE_NAME.
4. Copy the second variable value and substitute it for @COLUMN_NAME.
5. Repeat step 2 till 4 for the next entries in @TABLES_COLUMNS.

I would have to do this about 15 times. I think this would take me about 10 minutes.

With Vim it is different. 

I recorded the operations on the text once, 
and then I executed the operation I just did on the next two variable values. 
Then on the next, then on the next.
This is programming your text. And it is so easy.
You should probably see the recording and execution of the macro to understand:

<a href="http://i.imgur.com/KotHf82.gif">
<img src="http://i.imgur.com/KotHf82.gif" alt="Vim macro recorded and repeated" />
</a>

<sub>Note: after recording I see that showing these keypresses doesn't really help since a lot of them are missed and you cannot easily see the difference between lower and upper case. :) Apologies.</sub>

We see lots of missed efficiencies and some wrong keypresses. It is a dirty recording. But life in Vim is messy. 
Editing code with a mouse doesn't go right the first time either.

What we see in the gif above is that first I remove the no longer necessary EXECUTE statement after easily having jumped to the end of the file (G).
(Note that navigating in Vim goes with h, j, k and l -- you never have to leave your home row.)
I **v**isually select the statement with something like V5k (visually select five lines [V] upwards [k]).

In Vim it is as if you speak to your computer: you say a verb, you say how many times and you say what.

Then I **s**ubstitute the "'" and "+" from the selected region with a command (:) with nothing **g**lobally:

    :s/[+']//g

Next I **d**elete the selection and I yank the UPDATE statement plus one blank line in register u ("ud [delete into register u]).
Now I navigate to the top of the page (gg) and start a recording in register a (qa) 
(I pick register a for no other reason than that it is close to q).
I navigate down two lines (2j), **f**ind the first quote (f') of the first variable and **y**ank (copy) everything **i**nside the quotes (') into register **f** ("fyi').
Next I go to the end of the line ($) and copy the second variable into register s. (I use register f for first and s for second.) 
So now we have the table name we want to substitute stored in register f and the column name in register s.

Then I go to the end of the file (G), **p**aste everything inside register **u** ("up). This way the UPDATE-statement comes at the bottom of the page. i
Next I **v**isually select the **w**ord of the table name variable and paste the **f** register over it ("fp) (so: vw"fp). 
I also substitute the column name for the new value inside register s.

After having substituted all the column names and table names variables for the new values, I jump to the top of the file and go two lines down (gg2k). 
I remove the line (dd), jump back to the top (gg) and stop recording (q).

Next I execute that what I have just recorded three times (3@a)!
Since the recording was on a meta-level of navigation, selection and substitution this works perfectly fine for the other variable names.
Each time copying the statement, removing and substituting the values of the table and column name and ending on the top of the file.
The whole file is done. This would have worked just as fast for 100 rows by the way.

I find these possibilities so cool.

Note that the jumping around in Vim and selecting text is usually the biggest time saver.
As JangoSteve writes in the comments on Hacker News in response to the article Vim Creep:

>vim doesn't save you five minutes, twenty times a week; it saves you 0.75 seconds, eight thousand times a week. Same time savings, but much harder notice the pain before you've solved it.

And indeed the most time savings are just all the basic movements where you would have normally grabbed your mouse repeatedly.
But Vim is also absolutely wonderful for these large refactorings as above.

To be honest, when I recorded the macro for the first time (in the real life thing where the example was based on) 
it only really worked the tenth time I tried. 
(In my defense: it was during a late night cowboy deploy that went horribly wrong because of 
 a query that performed a tad bit worse on production than acceptance and there were people watching my actions via the remote which gave me 
 performance anxiety.)
But finally it did succeed and I learned from it and this time while recreating it I got it in one go.
In the excellent book 
<a  href="http://www.amazon.com/gp/product/059652983X/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=059652983X&linkCode=as2&tag=evalapply-20&linkId=UG223IAEZ2T3MQXI">Learning the vi and Vim Editors</a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=evalapply-20&l=as2&o=1&a=059652983X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" /> something like this is said about forcing yourself to learn efficiency gains: 
first some action or learning a regex will take you some time, but this pays back many times over the next time when 
hyper-efficient text-editing has become embedded in your muscle memory.

Note that one of the lessons of the Learning Vim in a Week video above is that "There is always a better way."
Typing this I see I could have better substituted the variable names in a similar manner as how I substituted the quotes and plus from the full statement. 
Also I used B or W instead of b and w to jump **B**ackwards or forwards a big **W**ord (including the @).
So there was indeed a better way with less keypresses. 
There surely is even a (much) better way.

Furthermore, the fact that I am now recreating this situation shows how much fun it is. 
I really don't mind.

I got a bit carried away already, but Vim is awesome.
Some of the things we have seen is easily navigating with the keyboard, executing commands over lines of text, storing stuff in multiple registers, editing selecting or deleting specific blocks of text, and recording a macro to re-execute a sequence of keypresses.

This is made possible by different modes that are available in Vim: normal mode for navigation, insert mode for inserting text, command mode for executing a command and visual mode for selection.

Some more awesomeness that is not shown in the example above are for example: 
- The .-command to repeat your last action.
- Editing the .vimrc file to easily bind new keys and add plugins. (Well this is shown, you see for example syntax highlighting and [relative line numbers](https://github.com/myusuf3/numbers.vim)).
- The awesome Vim plugin [surround.vim](https://github.com/tpope/vim-surround) to easily change surrounding quotes or braces into something else or add or delete them.
- Marking places in the text and jumping back (ma -- mark this position with a and 'a to jump to position a).
- Reselecting the region you just had selected with gv.
- Doing a block selection with CTRL-V and changing the selection size on the **o**ther side with o.
- Navigating undo trees (e.g., go to the state of your file five minutes back in time with :earlier 5m).
- Pair programming by working in the same file after sharing your buffer over SSH using tmux.
- Everything else other text-editors can do.

If you want to jump into Vim I can advice you to watch the video on Learning Vim in a Week above and do the `vimtutor`.

Stick with it. 
You will not regret it.

## Emacs
I love Lisp and it turns out that for Lisp editing it is somewhat more logical to use Emacs 
(although for example the expert Lisper Doug Hoyte swears by vi -- the non-improved version of Vim). 
The reason for that is it has tight integration with the language and you can use a version of Lisp called ELisp to configure and modify the editor. 
(Note that some people get really carried away with editing their editor instead of editing arguably more useful code).

But even before I used Emacs to learn the Lisp-dialect Clojure I had already switched to it. 
The reason for that was the video "Evil Mode: Or, How I Learned to Stop Worrying and Love Emacs":

<iframe src="https://www.youtube.com/embed/JWD1Fpdd4Pc" allowfullscreen="" frameborder="0" height="315" width="420"></iframe>

Emacs is a much better environment than Vim. 
Some people call it an operating system instead of a text-editor.
And it is true. 
Where Vim will sometimes hang when performing large operations, Emacs just runs on easily.

In the video some examples are shown how things that would block normal Vim can sometimes go on in Emacs on a background thread.
And for me Emacs with Evil mode just feels lighter than Vim. 
There are some other attempts to improve the performance of Vim (notably [NeoVim](https://neovim.io/)), but this would still not have the Lisp integration Emacs has.

Furthermore, things like searching and replacing are better. 
You see things changing or lighting up before you finished typing them in the command bar before you execute the command (an example is shown in the video at the end).

Because of the Lisp integration (firing up a REPL and never having to leave Emacs) and the better performance I use it.

For editing Clojure I just use the Emacs setup as described in [Clojure for the Brave and True](http://www.braveclojure.com/basic-emacs/) extended with
Evil mode.

Some of the cool features are mentioned in that book already, like CTRL-c CTRL-k to compile code and CTRL-c CTRL-d CTRL-d to view the library definition
(if I am not mistaken, fact of the matter is the keybindings for Emacs to me are much more unlogical than those of Vim).

Now I will visually show some of the other awesomeness:

Redoing things with the .-command work just as in Vim:
<a href="http://i.imgur.com/0UW5Jke.gif">
<img src="http://i.imgur.com/0UW5Jke.gif" alt="Redo command in Evil mode" />
</a>

Above we see the . command used to **c**hange text **i**nside the parentheses **"** (ci").

Next I have bound CTRL-b to display all available buffers and easily switch between them.
This is done in such a way you can use the familiar hjkl to navigate.

<a href="http://i.imgur.com/nQJip0a.gif">
<img src="http://i.imgur.com/nQJip0a.gif" alt="Easily switch between buffers in Evil-mode" />
</a>

And we can display multiple windows **v**ertically (CTRL-W v) or **s**plit them horizontally (CTRl-W s):

<a href="http://i.imgur.com/DXe5QtD.gif">
<img src="http://i.imgur.com/DXe5QtD.gif" alt="Multiple buffers editing" />
</a>

(Since I did not want to not use these buffers at all, note that we made a selection **u**pper case with U and it changed in all views of the buffer 
 simultaneously.)

Another really awesome thing which I believe comes out of the box is undolist, with it you can visually navigate the undo history. 
Yes, you can view the branches you decided to delete. 
Undo history can easily be viewed and navigated by typing the command:

    :undolist 

<a href="http://i.imgur.com/uCTvsRK.gif">
<img src="http://i.imgur.com/uCTvsRK.gif" alt="Evil mode has very nice undo history visualiation." />
</a>

How cool is that?

And of course we can add a very rich set of plugins. 
Of course we have all the plugins added for Clojure by the author of Clojure for the Brave and True.
Which provide syntax highlighting and tight integration with documentation, the REPL and executing code.

I'll show you two other plugins I use to give you an impression:

We can display the file system on the left if we want with F8 by using [NeoTree](https://github.com/jaypei/emacs-neotree):
<a href="http://i.imgur.com/B1RszIj.gif">
<img src="http://i.imgur.com/B1RszIj.gif" alt="Navigate with F8." />
</a>

Above we see me pressing F8 and navigating to an uninteresting folder on my file system.

Furthermore, look at this "slurping" of some elements from a string with > (SHIFT-.):
<a href="http://i.imgur.com/JaSZHP1.gif">
<img src="http://i.imgur.com/JaSZHP1.gif" alt="ParEdit for slurping" />
</a>

(Note that the five lines are combine with 5J [J is for **j**oining two lines] -- another feature I wanted to force into this).

Slurping on one end or the other or barfing things out on one side of the other is a feature of [ParEdit](https://www.emacswiki.org/emacs/ParEdit).
ParEdit is helpful for manipulating S-expressions (the parentheses in Lisp) but can also be used for other structures like the quotes above.

Is everything rose shine and moon light? No. I have identified the following shortcomings so far:
- Sometimes I want to quit an Emacs buffer (q), but then I cannot do so because I first have to enter insert mode. See my question about it on the Emacs Stack Exchange [here](http://emacs.stackexchange.com/questions/21628/use-default-keybindings-in-evil-in-non-text-editing-buffers).
- The window jumps halfways unwantedly when scrolling up and down (CTRL-E CTRL-Y btw). This can probably be fixed.
- Paredit can be annoying. You have to press CTRL-Q before inserting a parenthesis. But you can of course disable it.

That's all I can come up with and I have thought really hard.

## Closing remarks
Last but not least I don't want you to miss out on this great demonstration of Evil mode:

<iframe src="https://www.youtube.com/embed/Uz_0i27wYbg" allowfullscreen="" frameborder="0" height="315" width="420"></iframe>

You can download the Clojure for the Brave and True emac dotfiles which I extended with Evil mode as descibed above [by clicking here](https://github.com/erooijak/emacs). If you install Emacs and place the repository in the ~/.emacs.d directory everything will install automatically. Note that there are probably better dotfiles out there.
If you start using Vim do not forget to remap your Capslock to Escape. 
You will be pressing Escape a lot and on todays keyboards it is too faraway.

Thank you for reading. Comments are welcome.

