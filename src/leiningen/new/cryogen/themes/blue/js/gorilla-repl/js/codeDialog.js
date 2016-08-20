/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// The viewmodel for the code dialog user interface component, used for presenting code to the user (last-chance, value
// copy-and-paste).

var codeDialog = function () {

    var self = {};

    self.shown = ko.observable(false);
    self.caption = ko.observable("");
    self.message = ko.observable("");
    self.okButtonText = ko.observable("");
    // this is used to control/read the focus state of the text input. The text input is the only part of the palette
    // that will take the focus, and is focused when the dialog appears.
    self.focused = ko.observable(false);
    // the text the user has put in the box
    self.contents = ko.observable("");
    // will be called when the dialog is hidden (either by clicking the button, clicking the overlay,
    // or hitting esc).
    self.hideCallback = null;

    // Show the dialog
    self.show = function (options) {
        self.caption(options.caption);
        self.contents(options.contents);
        self.message(options.message);
        self.okButtonText(options.okButtonText);
        self.hideCallback = options.hideCallback;
        self.shown(true);
        self.focused(true);
    };

    self.hide = function () {
        self.shown(false);
        self.hideCallback();
    };

    // The overlay is a viewport sized div that sits behind the dialog, but over everything else.
    self.handleOverlayClick = function () {
        self.hide();
    };

    self.handleOKClick = function () {
        self.hide();
    };

    // This is bound to keypresses on the text input.
    self.handleKeyPress = function (d, event) {
        // esc
        if (event.keyCode === 27) {
            self.hide();
            return false;
        }
        // Pass through keypresses to the default handler.
        return true;
    };

    return self;
};