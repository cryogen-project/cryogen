/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// Stripped down segment viewmodels for the viewer.

// a code segment contains code, and shows the results of running that code.
var codeSegment = function (contents, consoleText, output) {
    var self = {};
    self.renderTemplate = "code-segment-template";
    self.worksheet = worksheet;
    self.id = UUID.generate();
    self.type = "code";


    self.errorText = ko.observable("");
    if (consoleText) self.consoleText = ko.observable(consoleText);
    else self.consoleText = ko.observable("");
    if (output) self.output = ko.observable(output);
    else self.output = ko.observable("");

    if (contents) self.contents = ko.observable(contents);
    else self.contents = ko.observable("");

    return self;
};

// a free segment contains markdown
var freeSegment = function (contents) {
    var self = {};
    self.renderTemplate = "free-segment-template";
    self.id = UUID.generate();

    self.type = "free";

    if (contents) self.contents = ko.observable(contents);
    else self.contents = ko.observable("");

    self.renderedContent = ko.computed(function () {
        return marked(self.contents());
    }).extend({throttle: 250});

    return self;
};