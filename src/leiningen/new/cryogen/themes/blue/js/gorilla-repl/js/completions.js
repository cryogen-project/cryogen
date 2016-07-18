/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

var clojureCompleter = function (cm, callback, options) {
    // The gist of this is lifted from the auto-completion modes included with CodeMirror.
    var cur = cm.getCursor();
    var token = cm.getTokenAt(cur);
    var word = token.string;
    var start = token.start;
    var end = token.end;

    // we send the whole editor contents as context, with __prefix__ inserted instead of the token.
    var doc = cm.getDoc().copy(false);
    doc.replaceRange("__prefix__", {line: cur.line, ch: start}, {line: cur.line, ch: end});
    var context = doc.getValue();

    // we need to know what namespace the user is currently working in, which we get from the evaluator module
    var ns = repl.currentNamespace;

    // TODO: this is a workaround for https://github.com/alexander-yakushev/compliment/issues/15
    if (word[0] != "/") {
        repl.getCompletions(word, ns, context, function (compl) {
            var completions = {
                list: compl,
                from: CodeMirror.Pos(cur.line, start),
                to: CodeMirror.Pos(cur.line, end)
            };

            // We show docs for the selected completion
            CodeMirror.on(completions, "select", function (s) {
                // TODO: this is a workaround for https://github.com/alexander-yakushev/compliment/issues/15
                if (s != "/") {
                    repl.getCompletionDoc(s, ns, function (docs) {
                        if (docs != null && docs != "")
                            eventBus.trigger("app:show-doc", docs);
                        else eventBus.trigger("app:hide-doc");
                    });
                }
            });

            // When the autocomplete UI is dismissed, hide the docs
            CodeMirror.on(completions, "close", function () {
                eventBus.trigger("app:hide-doc");
            });

            // Show the UI
            callback(completions);
        });
    }
};
