/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// Takes the REPL output and views it. The real work is handed over to the renderer, the outputViewer handles parsing
// the data, errors etc.

ko.bindingHandlers.outputViewer = {
    init: function (element, valueAccessor, allBindingsAccessor, viewModel) {
    },
    update: function (element, valueAccessor, allBindingsAccessor, viewModel) {

        // get the value to display
        var value = ko.utils.unwrapObservable(valueAccessor()());

        // to handle any errors, we need to know the ID of the segment that this output belongs to
        var segID = allBindingsAccessor.get('segmentID');
        // the errorHandler will route error messages to the segment's error div
        var errorHandler = function (msg) {
            eventBus.trigger("output:output-error", {segmentID: segID, error: msg});
        };

        if (value !== "") {
            try {
                var parsedValue = JSON.parse(value);
                // The renderer does all of the real work
                render(parsedValue, element, errorHandler);
            } catch (e) {
                // as a fallback, we display the value directly if we can't parse it as json. This also at least
                // allows worksheets that pre-date the new renderer to load, even if they look ugly!
                $(element).text(value);
            }
        }
        else $(element).html("");
    }
};