/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// a code segment contains code, and shows the results of running that code.
var codeSegment = function (contents, consoleText, output) {
    var self = {};
    self.renderTemplate = "code-segment-template";
    self.worksheet = worksheet;
    self.id = UUID.generate();
    self.type = "code";

    // Segment UI state
    self.active = ko.observable(false);
    // used for renderer errors
    self.errorText = ko.observable("");
    // used for Clojure errors
    self.stackTrace = ko.observable(null);
    if (consoleText) self.consoleText = ko.observable(consoleText);
    else self.consoleText = ko.observable("");
    if (output) self.output = ko.observable(output);
    else self.output = ko.observable("");
    self.runningIndicator = ko.observable(false);

    // The code
    // handle null contents
    if (contents === null) contents = "";
    self.content = codemirrorVM(
        self.id,
        contents,
        "text/x-clojure"
    );

    self.getTokenAtCursor = function () {
        return self.content.getTokenAtCursor();
    };

    self.getContents = function() {
        return self.content.contents();
    };

    self.clearErrorAndConsole = function () {
        self.errorText("");
        self.stackTrace(null);
        self.consoleText("");
    };

    self.clearOutput = function () {
        self.output("");
    };


    // activation and deactivation - these control whether the segment has the "cursor" outline, and focus
    // the content component.

    // activate the segment. fromTop will be true is the user's focus is coming from above (and so the cursor should
    // be placed at the top), false indicates the focus is coming from below.
    self.activate = function (fromTop) {
        self.active(true);
        if (fromTop) self.content.positionCursorAtContentStart();
        else self.content.positionCursorAtContentEnd();
    };

    self.deactivate = function () {
        self.content.blur();
        self.active(false);
    };

    // serialises the segment for saving. The result is valid clojure code, marked up with some magic comments.
    self.toClojure = function () {
        var startTag = ";; @@\n";
        var endTag = "\n;; @@\n";
        var outputStart = ";; =>\n";
        var outputEnd = "\n;; <=\n";
        var consoleStart = ";; ->\n";
        var consoleEnd = "\n;; <-\n";
        var cText = "";
        var oText = "";
        if (self.consoleText() !== "") cText = consoleStart + makeClojureComment(self.consoleText()) + consoleEnd;
        if (self.output() !== "") oText = outputStart + makeClojureComment(self.output()) + outputEnd;
        return startTag + self.getContents() + endTag + cText + oText;
    };

    return self;
};

// a free segment contains markdown
var freeSegment = function (contents) {
    var self = {};
    self.renderTemplate = "free-segment-template";
    self.id = UUID.generate();

    self.type = "free";

    // Segment UI state
    self.active = ko.observable(false);
    self.markupVisible = ko.observable(false);

    // The markup
    // handle null contents
    if (contents === null) contents = "";
    self.content = codemirrorVM(
        self.id,
        contents,
        "text/x-markdown"
    );

    self.getContents = function() {
        return self.content.contents();
    };

    self.renderedContent = ko.computed(function () {
        return marked(self.content.contents());
    }).extend({throttle: 250});

    self.handleClick = function () {
        eventBus.trigger("worksheet:segment-clicked", {id: self.id})
    };

    // activation and deactivation - these control whether the segment has the "cursor" outline, and focus
    // the content component.

    // activate the segment. fromTop will be true is the user's focus is coming from above (and so the cursor should
    // be placed at the top), false indicates the focus is coming from below.
    self.activate = function (fromTop) {
        self.markupVisible(true);
        self.content.reflow();
        self.active(true);
        if (fromTop) self.content.positionCursorAtContentStart();
        else self.content.positionCursorAtContentEnd();
    };

    self.deactivate = function () {
        self.content.blur();
        self.markupVisible(false);
        self.active(false);

    };

    // serialises the segment for saving. The result is valid clojure code, marked up with some magic comments.
    self.toClojure = function () {
        var tag = ";; **\n";
        return tag + makeClojureComment(self.getContents()) + "\n" + tag;
    };


    return self;
};