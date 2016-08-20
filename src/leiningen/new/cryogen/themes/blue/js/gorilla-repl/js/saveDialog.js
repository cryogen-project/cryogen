/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// The viewmodel for the save dialog user interface component.

var saveDialog = function (callback) {

    var self = {};

    self.shown = ko.observable(false);
    // this is used to control/read the focus state of the text input. The text input is the only part of the palette
    // that will take the focus, and is focused when the dialog appears.
    self.focused = ko.observable(false);
    // the text the user has put in the box
    self.filename = ko.observable("");

    // Show the dialog
    self.show = function ( existingFilename ) {
        existingFilename && (self.filename(existingFilename));        self.shown(true);
        self.focused(true);
    };

    self.hide = function () {
        self.shown(false);
    };

    // The overlay is a viewport sized div that sits behind the dialog, but over everything else.
    self.handleOverlayClick = function () {
        self.hide();
    };

    self.handleCancelClick = function () {
        self.hide();
    };

    self.handleOKClick = function () {
        self.hide();
        callback(self.filename());
    };

    // This is bound to keypresses on the text input.
    self.handleKeyPress = function (d, event) {
        // esc
        if (event.keyCode === 27) {
            self.hide();
            return false;
        }
        // enter
        if (event.keyCode === 13) {
            self.hide();
            callback(self.filename());
            return false;
        }
        // Pass through keypresses to the default handler.
        return true;
    };

    return self;
};