/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// The viewmodel for the 'palette' user interface component. This is used for a few things in Gorilla's UI.
// There is one palette viewmodel held by the app, corresponding to the palette div in the markup. It is reused each
// time it is shown. The only function that you should need to use is the `show` function, which brings up the palette.

var palette = function () {

    var self = {};

    self.shown = ko.observable(false);
    self.caption = ko.observable("Choose a command:");
    // These are the items that the palette was shown with - we keep these because when we filter ...
    self.allItems = [];
    // ... we will only show a subset of the items, held in the following observable array.
    self.items = ko.observableArray();
    self.highlight = ko.observable(1);
    // this is used to control/read the focus state of the text input. The text input is the only part of the palette
    // that will take the focus, and is focused when the palette appears.
    self.focused = ko.observable(false);
    // the text the user has put in the filter box
    self.filterText = ko.observable("");
    self.filterText.subscribe(function (nv) {self.updateFilter(nv)});

    // This function shows the palette with the given items. It's the only function on the palette that you should need
    // to call. The `items` should be an array of objects, with each object having a `desc` property, which is an HTML
    // string that will be shown to the user, a `text` property which is what the item will be filtered against (and so
    // had better match up with how the user reads the `desc` property, and an `action` property which a function that
    // will be called if that item is selected.
    self.show = function (caption, items) {
        self.caption(caption);
        self.allItems = items;
        self.updateItems(items);
        self.filterText("");
        self.shown(true);
        self.focused(true);
        self.scrollToNth(0,true);
    };

    self.hide = function () {
        self.shown(false);
    };

    // updates the list of _visible_ items.
    self.updateItems = function (newItems) {
        self.highlight(0);
        self.items.removeAll();
        self.items.push.apply(self.items, newItems);
    };

    // update the list of visible items, based on the filter text. Uses that matching that I don't really know the name
    // of that you get in IntelliJ/Sublime/Atom etc where as long as the chars in the filter text appear, in that order,
    // in the string, then it matches.
    self.updateFilter = function (filterText) {
        var filteredItems = self.allItems.filter(
            function (i) {
                // the idea to do it this way comes from http://stackoverflow.com/a/24570566
                var re = new RegExp(filterText.toLowerCase().split('').join('.*'));
                return i.text.toLowerCase().match(re);
            });
        self.updateItems(filteredItems);
    };

    // This method and the next move the selected item highlight
    self.moveSelectionDown = function () {
        var curPos = self.highlight();
        var newPos;
        if (curPos < (self.items().length - 1)) newPos = curPos + 1;
        else newPos = 0;
        self.highlight(newPos);
        self.scrollToNth(newPos, false);
    };

    self.moveSelectionUp = function () {
        var curPos = self.highlight();
        var newPos;
        if (curPos > 0) newPos = curPos - 1;
        else newPos = self.items().length - 1;
        self.highlight(newPos);
        self.scrollToNth(newPos, false);
    };

    self.scrollToNth = function (n, top) {
        var el = document.getElementById('palette-item-' + n);
        if (el) {
            // This isn't availble cross-browser, but it's much better when it is there
            if (el.scrollIntoViewIfNeeded) el.scrollIntoViewIfNeeded(top);
            // This is a bit janky at the moment, but it will do.
            else (el.scrollIntoView(top));
        }
    };

    self.handleItemClick = function (item) {
        self.hide();
        item.action();
    };

    // The overlay is a viewport sized div that sits behind the palette, but over everything else.
    self.handleOverlayClick = function () {
        self.hide();
    };

    // This is bound to keypresses on the text input.
    self.handleKeyPress = function (d, event) {
        // up
        if (event.keyCode === 38) {
            self.moveSelectionUp();
            return false;
        }
        // down
        if (event.keyCode === 40) {
            self.moveSelectionDown();
            return false;
        }
        // esc
        if (event.keyCode === 27) {
            self.hide();
            return false;
        }
        // enter
        if (event.keyCode === 13) {
            var item = self.items()[self.highlight()];
            self.hide();
            if (item) item.action();
            return false;
        }
        // Pass through keypresses to the default handler.
        return true;
    };

    return self;
};