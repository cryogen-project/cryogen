/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

var docViewer = function () {

    var self = {};

    self.shown = ko.observable(false);
    self.doc = ko.observable("");
    self.show = function () {
        // OK, this is really, really nasty. Position the doc viewer next to the CM autocomplete pop-up.
        var rect = $(".CodeMirror-hints")[0].getBoundingClientRect();
        var dv = $(".doc-viewer");
        dv.css({top: rect.top, left: rect.right});
        self.shown(true);
    };

    self.hide = function () {
        self.shown(false);
    };

    return self;
};