/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// Listens for "command:*" events and processes them, usually by firing off new events that are handled by the
// appropriate component.

var commandProcessor = (function () {

    var self = {};

    // ** Patch Mousetrap **
    // Install a custom stopCallback so that our keyboard shortcuts work in the codeMirror textareas.
    // This also lets us disable mousetrap processing when we show dialogs (this idea shamelessly stolen from the
    // Mousetrap 'pause' plugin).
    Mousetrap.enabled = true;
    Mousetrap.enable = function (enabled) {Mousetrap.enabled = enabled;};
    Mousetrap.stopCallback = function () {
        return !Mousetrap.enabled;
    };

    var addCommand = function (command) {
        eventBus.on(command.name, command.action);
        if (command.kb) Mousetrap.bind(command.kb, function () {
            eventBus.trigger(command.name);
            return false;
        });
    };

    self.installCommands = function (keymapOverrides) {
        if (keymapOverrides) {
            _.keys(keymapOverrides).forEach(function (k) {
                commandList.forEach(function (c) {
                    if (c.name === k) c.kb = keymapOverrides[k];
                })
            });
        }
        commandList.forEach(addCommand);
    };

    return self;

})();

// The list of commands. These could be located with the components they belong to if the list gets too unwieldy,
// but for now they're fine together here.

// On Windows and Linux "alt" is used as the command key, on Mac "ctrl"
var ck = /Win|Linux/.test(navigator.platform) ? "alt" : "ctrl";
// helper for two key combos
var combo = function (k1, k2) { return ck + "+" + k1 + " " + ck + "+" + k2};

commandList = [
    {
        name: "command:app:commands",
        desc: "Show the command list.",
        showInMenu: false,
        kb: combo('g', 'g'),
        action: function () {
            eventBus.trigger("app:commands");
        }
    },
    {
        name: "command:worksheet:leaveBack",
        desc: "Move to the previous segment.",
        showInMenu: false,
        action: function () {
            eventBus.trigger("worksheet:leaveBack");
        }
    },
    {
        name: "command:worksheet:leaveForward",
        desc: "Move to the next segment.",
        showInMenu: false,
        action: function () {
            eventBus.trigger("worksheet:leaveForward");
        }
    },
    {
        name: "command:evaluator:evaluate",
        desc: "Evaluate the highlighted segment.",
        showInMenu: true,
        kb: "shift+enter",
        action: function () {
            eventBus.trigger("worksheet:evaluate");
        }
    },
    {
        name: "command:evaluator:evaluate-all",
        desc: "Evaluate all segments.",
        showInMenu: true,
        kb: ck + "+shift+enter",
        action: function () {
            eventBus.trigger("worksheet:evaluate-all");
        }
    },
    {
        name: "command:worksheet:clear-output",
        desc: "Clear the output of the highlighted segment.",
        showInMenu: true,
        kb: combo('g', 'o'),
        action: function () {
            eventBus.trigger("worksheet:clear-output");
        }
    },
    {
        name: "command:worksheet:clear-all",
        desc: "Clear the output of all code segments.",
        showInMenu: true,
        kb: combo('g', 'z'),
        action: function () {
            eventBus.trigger("worksheet:clear-all-output");
        }
    },
    {
        name: "command:worksheet:delete",
        desc: "Delete the highlighted segment.",
        showInMenu: true,
        kb: combo('g', 'x'),
        action: function () {
            eventBus.trigger("worksheet:delete");
        }
    },
    {
        name: "command:worksheet:undelete",
        desc: "Undo the last segment delete.",
        showInMenu: true,
        kb: combo('g', '\\'),
        action: function () {
            eventBus.trigger("worksheet:undelete");
        }
    },
    {
        name: "command:worksheet:changeToFree",
        desc: "Convert the highlighted segment to a markdown segment.",
        showInMenu: true,
        kb: combo('g', 'm'),
        action: function () {
            eventBus.trigger("worksheet:changeToFree");
        }
    },
    {
        name: "command:worksheet:changeToCode",
        desc: "Convert the highlighted segment to a clojure segment.",
        showInMenu: true,
        kb: combo('g', 'j'),
        action: function () {
            eventBus.trigger("worksheet:changeToCode");
        }
    },
    {
        name: "command:app:open",
        desc: "Load a worksheet.",
        showInMenu: true,
        kb: combo('g', 'l'),
        action: function () {
            eventBus.trigger("app:load");
        }
    },
    {
        name: "command:app:save",
        desc: "Save the worksheet.",
        showInMenu: true,
        kb: combo('g', 's'),
        action: function () {
            eventBus.trigger("app:save");
        }
    },
    {
        name: "command:app:saveas",
        desc: "Save the worksheet to a new filename.",
        showInMenu: true,
        kb: combo('g', 'e'),
        action: function () {
            eventBus.trigger("app:saveas");
        }
    },
    {
        name: "command:worksheet:newBelow",
        desc: "Create a new segment below the highlighted segment.",
        showInMenu: true,
        kb: combo('g', 'n'),
        action: function () {
            eventBus.trigger("worksheet:newBelow");
        }
    },
    {
        name: "command:worksheet:newAbove",
        desc: "Create a new segment above the highlighted segment.",
        showInMenu: true,
        kb: combo('g', 'b'),
        action: function () {
            eventBus.trigger("worksheet:newAbove");
        }
    },
    {
        name: "command:worksheet:moveUp",
        desc: "Move the highlighted segment up the worksheet.",
        showInMenu: true,
        kb: combo('g', 'u'),
        action: function () {
            eventBus.trigger("worksheet:moveUp");
        }
    },
    {
        name: "command:worksheet:moveDown",
        desc: "Move the highlighted segment down the worksheet.",
        showInMenu: true,
        kb: combo('g', 'd'),
        action: function () {
            eventBus.trigger("worksheet:moveDown");
        }
    },
    {
        name: "command:docs:clojuredocs",
        desc: "Look up the symbol under the cursor in ClojureDocs.",
        showInMenu: true,
        kb: combo('g', 'c'),
        action: function () {
            eventBus.trigger("docs:clojuredocs");
        }
    },
    {
        name: "command:app:reset-worksheet",
        desc: "Reset the worksheet - a fresh start.",
        showInMenu: true,
        action: function () {
            eventBus.trigger("app:reset-worksheet");
        }
    },
    {
        name: "command:worksheet:completions",
        desc: "Show possible auto-completions.",
        showInMenu: true,
        // alternative provided to workaround Firefox's idiotic unstoppable binding of ctrl+space
        kb: ["ctrl+space", combo('g', 'a')],
        action: function () {
            eventBus.trigger("worksheet:completions");
        }
    }
];
