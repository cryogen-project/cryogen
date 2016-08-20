/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// The app keeps track of application level state, and handles UI and interaction that's not part of the worksheet
// itself (things like load/save, commands etc).
var app = function () {
    var self = {};

    // Most importantly, the application has a worksheet! This is exposed so that the UI can bind to it, but note that
    // you should never change the worksheet directly, as this will leave the event handlers in an inconsistent state.
    // Rather you should use the `setWorksheet` function below.
    self.worksheet = ko.observable();
    // the filename that the worksheet corresponds to. If the worksheet was not loaded, or has never been saved,
    // this will be the empty string.
    self.filename = ko.observable("");
    // whenever we change the filename, we update the URL to match
    self.filename.subscribe(function (filename) {
        if (filename !== "") history.pushState(null, null, "?filename=" + filename);
        else history.pushState(null, null, "/worksheet.html");
     });
    // shows the name of the Leiningen project that gorilla was launched from, makes it easier to manage multiple
    // tabs with multiple gorilla sessions.
    self.project = ko.observable("no project");

    // Use this to change the worksheet being edited. It takes care of hooking/unhooking event handlers as well as
    // changing the worksheet data structure itself.
    self.setWorksheet = function (newWorksheet, newFilename) {
        // disconnect the worksheet event handlers
        if (self.worksheet()) self.worksheet().removeEventHandlers();
        self.filename(newFilename);
        self.worksheet(newWorksheet);
        newWorksheet.addEventHandlers();
    };

    var createPath = function(suffix) {
        return window.location.pathname.replace(/[^/]+$/,suffix);
    };

    // This starts the application. First of all we ask the server for configuration information, and then prepare the
    // UI and, if appropriate, load an initial worksheet.
    self.start = function (initialFilename) {
        // get hold of configuration information from the backend
        $.get(createPath('config'))
            .done(function (data) {
                self.config = data;
                // If we've got the configuration, then start the app
                self.project(self.config.project);
                // Prepare an empty worksheet so the UI has something to bind to. We do this as if we are loading a
                // worksheet on startup, we do it asynchronously, so need to have something in place before starting
                // the UI. This is easier than having two paths that both have UI startup code on them. (Although, the
                // UX is slightly less slick this way).
                var ws = worksheet();
                self.setWorksheet(ws, "");

                // start the UI
                commandProcessor.installCommands(self.config.keymap);
                ko.applyBindings(self, document.getElementById("document"));

                if (initialFilename) loadFromFile(initialFilename);
                else setBlankWorksheet();
            })
            .fail(function () {
                // not a lot we can do here.
                alert("Unable to get app configuration. Restart server.");
            });
    };

    // A helper function to create a new, blank worksheet with some introductory messages in.
    var setBlankWorksheet = function () {
        var ws = worksheet();
        ws.segments().push(
            // Note that the variable ck here is defined in commandProcessor.js, and gives the appropriate
            // shortcut key (ctrl or alt) for the platform.
            freeSegment("# Gorilla REPL\n\nWelcome to gorilla :-)\n\nShift + enter evaluates code. " +
                "Hit " + ck + "+g twice in quick succession or click the menu icon (upper-right corner) " +
                "for more commands ...\n\nIt's a good habit to run each worksheet in its own namespace: feel " +
                "free to use the declaration we've provided below if you'd like.")
        );
        ws.segments().push(codeSegment("(ns " + makeHipNSName() + "\n  (:require [gorilla-plot.core :as plot]))"));
        self.setWorksheet(ws, "");
        // make it easier for the user to get started by highlighting the empty code segment
        eventBus.trigger("worksheet:segment-clicked", {id: self.worksheet().segments()[1].id});
    };


    // bound to the window's title
    self.title = ko.computed(function () {
        if (self.filename() === "") return "Gorilla REPL - " + self.project();
        else return self.project() + " : " + self.filename();
    });

    // status indicator - bound to a popover type element in the UI
    self.status = ko.observable("");
    // A message queue could be useful here, although I'm not sure it'll ever come up in practice.
    self.flashStatusMessage = function (message, displayMillis) {
        var millis = displayMillis ? displayMillis : 700;
        self.status(message);
        setTimeout(function () {self.status("");}, millis);
    };

    // The palette UI component. This single palette is reused each time it appears.
    self.palette = palette();
    // handler for the worksheet menu icon.
    self.handleMenuClick = function () {
        eventBus.trigger("command:app:commands");
    };

    // The save dialog UI component. See below for a summary of the app:save logic.
    self.saveDialog = saveDialog((function (f) {self.handleSaveDialogSuccess(f)}));
    // the callback that the save dialog will call
    self.handleSaveDialogSuccess = function (fname) {
        if (fname) {
            saveToFile(fname, function() {
                // if the save was successful, hold on to the filename.
                self.filename(fname);
            });
        }
    };

    // The code dialog component, which is used for last-chance worksheet data and value copy and paste.
    self.codeDialog = codeDialog();

    // The doc viewer component, which shows docs on autocomplete, and manages the ELDOC style status bar
    self.docViewer = docViewer();

    // Helpers for loading and saving the worksheet - called by the various command handlers
    var saveToFile = function (filename, successCallback) {
        $.post(createPath('save'), {
            "worksheet-filename": filename,
            "worksheet-data": self.worksheet().toClojure()
        }).done(function () {
            self.flashStatusMessage("Saved: " + filename);
            if (successCallback) successCallback();
        }).fail(function () {
            self.flashStatusMessage("Failed to save worksheet: " + filename, 2000);
        });
    };

    var loadFromFile = function (filename) {
        // ask the backend to load the data from disk
        $.get(createPath('load'), {"worksheet-filename": filename})
            .done(function (data) {
                if (data['worksheet-data']) {
                    // parse and construct the new worksheet
                    var segments = worksheetParser.parse(data["worksheet-data"]);
                    var ws = worksheet();
                    ws.segments = ko.observableArray(segments);
                    // show it in the editor
                    self.setWorksheet(ws, filename);
                    // highlight the first code segment if it exists
                    var codeSegments = _.filter(self.worksheet().segments(), function(s) {return s.type === 'code'});
                    if (codeSegments.length > 0)
                        eventBus.trigger("worksheet:segment-clicked", {id: codeSegments[0].id});
                }
            })
            .fail(function () {
                self.flashStatusMessage("Failed to load worksheet: " + filename, 2000);
            });
    };

    // ** Application event handlers

    // The user has summoned the palette with the list of commands
    eventBus.on("app:commands", function () {
        var visibleCommands = commandList.filter(function (x) {return x.showInMenu});
        var paletteCommands = visibleCommands.map(function (c) {
            // take care of commands with no shortcut.
            var kb = c.kb ? c.kb : "&nbsp";
            return {
                desc: '<div class="command">' + c.desc + '</div><div class="command-shortcut">' + kb + '</div>',
                text: c.desc,
                action: c.action
            }
        });
        self.palette.show("Choose a command:", paletteCommands);
    });

    eventBus.on("app:load", function () {
        self.palette.show("Scanning for files ...", []);
        $.ajax({
            type: "GET",
            url: createPath('gorilla-files'),
            success: function (data) {
                var paletteFiles = data.files.map(function (c) {
                    return {
                        desc: '<div class="command">' + c + '</div>',
                        text: c,
                        action: (function () {loadFromFile(c)})
                    }
                });
                self.palette.show("Choose a file to load:", paletteFiles);
            }
        });
    });

    // Save logic is a bit confusing. This event will be triggered if the user commands a save. If there's a filename
    // we save using the helper above, and are done. If not, we put up the save dialog. If the user completes the save
    // dialog then it fires a callback which does the actual saving and stores the filename (see
    // self.handleSaveDialogSuccess)
    eventBus.on("app:save", function () {
        var fname = self.filename();
        // if we already have a filename, save to it. Else, prompt for a name.
        if (fname !== "") {
            saveToFile(fname);
        } else self.saveDialog.show();
    });

    eventBus.on("app:saveas", function () {
        var fname = self.filename();
        self.saveDialog.show(fname);
    });

    eventBus.on("app:reset-worksheet", function () {
        setBlankWorksheet();
    });

    eventBus.on("app:connection-lost", function () {
        self.codeDialog.show({
            message: "<p>The connection to the server has been lost. This window is now dead! Hit the button to " +
                "reload the browser window once the server is running again.</p>" +
                "<p>In case you didn't manage to save the worksheet, " +
                "the contents are below for your convenience :-)</p>",
            caption: "Connection to server lost",
            contents: self.worksheet().toClojure(),
            okButtonText: "Reload",
            hideCallback: function () {
                location.reload();
            }
        });
    });

    eventBus.on("app:show-value", function (e, value) {
        self.codeDialog.show({
            message: "",
            caption: "Clojure value:",
            contents: value,
            okButtonText: "OK",
            hideCallback: function () {}
        });
    });


    eventBus.on("app:show-doc", function (e, d) {
        self.docViewer.doc(d.replace(/\n/g, "<br/>"));
        self.docViewer.show();
    });
    eventBus.on("app:hide-doc", function (e, d) {
        self.docViewer.hide();
    });

    return self;
};

var getParameterByName = function (name) {
    var match = new RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};

// The application entry point
$(function () {
    // start the REPL - the app is started in a callback from the repl connection that indicates we are
    // successfully connected.
    // TODO: a bit of historical weirdness that the REPL connection is made here, and not inside the app.start method
    repl.connect(
        function () {
            var gorilla = app();
            var initialFilename = getParameterByName("filename");
            gorilla.start(initialFilename);
            // for debugging. Let's hope nobody else has defined a global variable called gorilla!
            window.gorilla = gorilla;
        },
        // this function is called if we failed to make a REPL connection. We can't really go any further.
        function () {
            alert("Failed to make initial connection to nREPL server. Refreshing the page might help.");
        });
});
