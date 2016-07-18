/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */


var app = function () {
    var self = {};

    self.worksheet = ko.observable();
    self.filename = ko.observable("");
    self.title = ko.computed(function () {
        if (self.filename() === "") return "Gorilla REPL viewer";
        else return "Gorilla REPL viewer: " + self.filename();
    });
    self.sourceURL = ko.observable("");
    self.source = ko.observable("");
    self.host = ko.observable("");

    // The copyBox is a UI element that gives links to the source of the worksheet, and how to copy/edit it.
    self.copyBoxVisible = ko.observable(false);
    self.showCopyBox = function () {
        self.copyBoxVisible(true);
    };
    self.hideCopyBox = function () {
        self.copyBoxVisible(false);
    };

    self.start = function (worksheetData, sourceURL, worksheetName, source) {

        var ws = worksheet();
        ws.segments = ko.observableArray(worksheetParser.parse(worksheetData));
        self.worksheet(ws);
        self.sourceURL(sourceURL);
        self.filename(worksheetName);
        self.source(source);
        self.host((source.toLowerCase() === "bitbucket") ? "Bitbucket" : "GitHub");

        // wire up the UI
        ko.applyBindings(self, document.getElementById("document"));

        // we only use CodeMirror to syntax highlight the code in the viewer
        CodeMirror.colorize($("pre.static-code"), "text/x-clojure");

    };

    return self;
};

var getParameterByName = function (name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};

// The application entry point
/*
$(function () {
    var viewer = app();
    // how are we getting the worksheet data?
    var source = getParameterByName("source");
    switch (source) {
        case "github":
            var user = getParameterByName("user");
            var repo = getParameterByName("repo");
            var path = getParameterByName("path");
            getFromGithub(user, repo, path, function (data) {
                viewer.start(data, "https://github.com/" + user + "/" + repo, path, source);
            });
            return;
        case "gist":
            var id = getParameterByName("id");
            var filename = getParameterByName("filename");
            getFromGist(id, filename, function (data) {
                viewer.start(data,  "https://gist.github.com/" + id, filename, source);
            });
            return;
        case "bitbucket":
            var user = getParameterByName("user");
            var repo = getParameterByName("repo");
            var path = getParameterByName("path");
            var revision = getParameterByName("revision") || "HEAD";
            getFromBitbucket(user, repo, path, revision, function (data) {
                viewer.start(data, "https://bitbucket.org/" + user + "/" + repo, path, source);
            });
            return;
        case "test":
            // so you can test without exhausting the github API limit
            $.get('/test.clj').success(function (data) {
                viewer.start(data, "http://gorilla-repl.org/", "test.clj", source);
            });
    }
});

*/
