/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// This defines a very simple binding that displays its value, and on every update
// asks MathJax to reprocess the output.
ko.bindingHandlers.mathJaxViewer = {
    init: function (element, valueAccessor, allBindingsAccessor, viewModel) {
        // make sure the element has a unique ID
        $(element).attr('id', 'mathjax-viewer-' + UUID.generate());
    },
    update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
        var value = ko.utils.unwrapObservable(valueAccessor()());
        $(element).html(value);
        // MathJax might not be available.
        if ("MathJax" in window) MathJax.Hub.Queue(["Typeset", MathJax.Hub, $(element).attr('id')]);
    }
};

// configure the MathJax library - we always guard for the case where MathJax was not loaded as it non-essential, and
// not bundled with the Gorilla distribution.
if ("MathJax" in window) {
    MathJax.Hub.Config({
        messageStyle: "none",
        showProcessingMessages: false,
        skipStartupTypeset: true,
        tex2jax: {
            inlineMath: [
                ['@@', '@@']
            ]
        }
    });
    MathJax.Hub.Configured();
}
