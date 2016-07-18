/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// This is a viewmodel and KO binding for the codemirror code editor. You should apply the codemirror binding
// to a textarea, and bind it to a viewmodel made with makeCodeMirrorViewmodel.
//
// The viewmodel raises events when something that might warrant external action happens. For instance, if the focus
// is entering or leaving the editor, or if a segment should be deleted. The editor must be given an id, and it will
// include this id in the events it raises.

var codemirrorVM = function (id, initialContents, contentType) {
    var self = {};
    self.id = id;
    self.contentType = contentType;

    self.contents = ko.observable(initialContents);

    // ** Public methods for manipulating this editor **

    // asks the editor to redraw itself. Needed when its size has changed.
    self.reflow = function () {
        self.codeMirror.refresh();
    };

    self.blur = function () {
        // this doesn't seem to be built in to the editor
        self.codeMirror.getInputField().blur();
    };

    self.complete = function (completionFunc) {
        CodeMirror.showHint(self.codeMirror, completionFunc,
            {async: true, completeSingle: false, alignWithWord: false});
    };

    // These can be called to position the CodeMirror cursor appropriately. They are used when the cell is receiving
    // focus from another cell.
    self.positionCursorAtContentStart = function () {
        self.codeMirror.focus();
        self.codeMirror.setCursor(0, 0);
        self.codeMirror.focus();
    };

    self.positionCursorAtContentEnd = function () {
        self.codeMirror.focus();
        // TODO: Bit of a fudge doing this here!
        self.reflow();
        // position the cursor past the end of the content
        self.codeMirror.setCursor(self.codeMirror.lineCount(), 0);
        self.codeMirror.focus();
    };

    self.positionCursorAtContentStartOfLastLine = function () {
        self.codeMirror.focus();
        self.codeMirror.setCursor(self.codeMirror.lineCount() - 1, 0);
        self.codeMirror.focus();
    };

    self.getTokenAtCursor = function () {
        var token = self.codeMirror.getTokenAt(self.codeMirror.getCursor());
        if (token != null) return token.string;
        else return null;
    };

    // ** Internal methods - should only be called by our CodeMirror instance. **

    // These will be called by the CodeMirror component, and will notify the application that something of note has
    // happened. Cursor movement and segment deletion generate "command" events, which will be handled by the command
    // processor (which will then delegate to the worksheet). Segment clicks are not sent as commands as it doesn't
    // really make sense.
    self.notifyMoveCursorBack = function () {
        eventBus.trigger("command:worksheet:leaveBack")
    };

    self.notifyMoveCursorForward = function () {
        eventBus.trigger("command:worksheet:leaveForward")
    };

    self.notifyBackspaceOnEmpty = function () {
        eventBus.trigger("worksheet:deleteBackspace")
    };

    self.notifyClicked = function () {
        eventBus.trigger("worksheet:segment-clicked", {id: self.id})
    };

    return self;
};

ko.bindingHandlers.codemirror = {
    init: function (element, valueAccessor, allBindingsAccessor, viewModel) {
        // we define a custom CodeMirror keymap, which takes a few steps.
        // First we need to define a CodeMirror command that does nothing
        // (according to the CodeMirror docs, one should be able set a key-binding as 'false' and have it do nothing
        // but I can't seem to get that to work.
        CodeMirror.commands['doNothing'] = function () {};
        // then patch the Mac default keymap to get rid of the emacsy binding, which interfere with our shortcuts
        CodeMirror.keyMap['macDefault'].fallthrough = "basic";
        // and then create our custom map, which will fall through to the (patched) default. Shift+Enter and variants
        // are stopped from doing anything.
        CodeMirror.keyMap["gorilla"] = {
            'Shift-Enter': "doNothing",
            'Shift-Ctrl-Enter': "doNothing",
            'Shift-Alt-Enter': "doNothing",
            fallthrough: "default"};
        var cm = CodeMirror.fromTextArea(element,
            {
                lineNumbers: false,
                matchBrackets: true,
                autoCloseBrackets: '()[]{}""',
                lineWrapping: true,
                keyMap: 'gorilla',
                mode: valueAccessor().contentType
            });
        cm.on("keydown", function (editor, event) {
            // we stop() the cursor events, as we don't want them reaching the worksheet. We explicitly
            // generate events when the cursor should leave the segment.
            var curs;
            if (event.keyCode === 38 && !event.shiftKey) {
                // get the current cursor position
                curs = editor.getCursor();
                // check for first line
                // TODO: I'm not sure whether the completionActive state is part of the public API
                if ((curs.line === 0) && !editor.state.completionActive)
                    valueAccessor().notifyMoveCursorBack();
           //     event.preventDefault();
            }
            // left
            if (event.keyCode === 37 && !event.shiftKey) {
                // get the current cursor position
                curs = editor.getCursor();
                // check for first line, start position
                if (curs.line === 0 && curs.ch === 0) valueAccessor().notifyMoveCursorBack();
            //    event.preventDefault();
            }
            // down
            if (event.keyCode === 40 && !event.shiftKey) {
                // get the current cursor position
                curs = editor.getCursor();
                // check for last line
                if ((curs.line === (editor.lineCount() - 1)) && !editor.state.completionActive)
                    valueAccessor().notifyMoveCursorForward();
             //   event.preventDefault();
            }
            // right
            if (event.keyCode === 39 && !event.shiftKey) {
                // get the current cursor position
                curs = editor.getCursor();
                // check for last line, last position
                if (curs.line === (editor.lineCount() - 1)) {
                    if (curs.ch === editor.getLine(curs.line).length)
                        valueAccessor().notifyMoveCursorForward();
                }
            //    event.preventDefault();
            }
            // delete on an empty editor
            if (event.keyCode === 8) {
                if (editor.getValue() === "") valueAccessor().notifyBackspaceOnEmpty();
            }
        });

        // this function is called back by codemirror when ever the contents changes.
        // It keeps the model in sync with the code.
        cm.on('change', function (editor) {
            var value = valueAccessor();
            value.contents(editor.getValue());
        });
        cm.on('mousedown', function () {
            valueAccessor().notifyClicked();
        });
        // store the editor object on the viewmodel
        valueAccessor().codeMirror = cm;
        // set the initial content
        cm.setValue(ko.utils.unwrapObservable(valueAccessor().contents));
    },
    update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
        var cm = valueAccessor().codeMirror;
        var value = ko.utils.unwrapObservable(valueAccessor().contents);
        // KO will trigger this update function whenever the model changes, even if that change
        // is because the editor itself has just updated the model. This messes with the cursor
        // position, so we check here whether the value really has changed before we interfere
        // with the editor.
        if (value !== cm.getValue()) cm.setValue(value);
    }
};
