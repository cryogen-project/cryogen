/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// This viewmodel represents the worksheet document itself. Code to manage the "cursor" that is, the highlight on the
// active segment, and the position of the editor cursors, is in the worksheet, as it needs to know about the
// relationship between the segments.
var worksheet = function () {
    var self = {};

    // the content of the worksheet is a list of segments.
    self.segments = ko.observableArray();
    self.deletedSegment = null;

    // serialises the worksheet for saving. The result is valid clojure code, marked up with some magic comments.
    self.toClojure = function () {
        return ";; gorilla-repl.fileformat = 1\n\n" +
            self.segments().map(function (s) {
                return s.toClojure()
            }).join('\n');
    };

    // ** Segment management helpers **

    self.segmentIndexForID = function (id) {
        // so, this is not perhaps the most efficient way you could think of doing this, but for reasonable conditions
        // it will be fine.
        for (var i = 0; i < self.segments().length; i++) {
            if (self.segments()[i].id == id) return i;
        }
        // this had better never happen!
        return -1;
    };

    self.getSegmentForID = function (id) {
        var index = self.segmentIndexForID(id);
        if (index >= 0) return self.segments()[index];
        else return null;
    };

    self.activeSegmentIndex = null;

    self.getActiveSegment = function () {
        if (self.activeSegmentIndex != null) return self.segments()[self.activeSegmentIndex];
        else return null;
    };

    self.activateSegment = function (index, fromTop) {
        self.segments()[index].activate(fromTop);
        self.activeSegmentIndex = index;
    };

    self.deactivateSegment = function (index) {
        self.segments()[index].deactivate();
        self.activeSegmentIndex = null;
    };

    self.deleteSegment = function (index) {
        var seg = self.segments.splice(index, 1)[0];
        self.deletedSegment = {
            index: index,
            segment: seg
        };
    };

    self.undeleteSegment = function () {
        if (self.deletedSegment == null) return;
        self.deactivateSegment(self.activeSegmentIndex);
        self.segments.splice(self.deletedSegment.index, 0, self.deletedSegment.segment);
        self.activateSegment(self.deletedSegment.index, true);
        self.deletedSegment = null;
    };

    // an offset of 1 inserts below the active segment, 0 above.
    self.insertNewSegment = function (offset) {
        // do nothing if no segment is active
        if (self.activeSegmentIndex == null) return;
        var seg = codeSegment("");
        var currentIndex = self.activeSegmentIndex;
        self.deactivateSegment(currentIndex);
        self.segments.splice(currentIndex + offset, 0, seg);
        self.activateSegment(currentIndex + offset);
        self.deletedSegment = null;
    };

    self.moveSegment = function (up) {
        var index = self.activeSegmentIndex;
        if (index == null) return;
        if (up) {
            // can't move top segment up
            if (index == 0) return;
        } else {
            // or bottom segment down
            if (index == self.segments().length -1) return;
        }
        self.deactivateSegment(index);
        var offset = up ? -1 : 1;
        self.segments.splice(index + offset, 0, self.segments.splice(index, 1)[0]);
        self.activateSegment(index + offset);
        self.deletedSegment = null;
    };


    // ** Event handlers **
    // TODO: this is slightly nasty. The event handlers close over worksheet properties, so need to be removed and re-
    // TODO: added whenever the worksheet is changed. Maybe they shouldn't live here?

    // We store a list of added event types, by using this helper function to add events. This allows us to cleanly
    // deregister all the event handlers if the worksheet is to be replaced.
    var eventTypeList = [];
    var addEventHandler = function (event, callback) {
        eventTypeList.push(event);
        eventBus.on(event, callback);
    };
    // remove all worksheet event handlers from the bus - note that this will remove event handlers for _all_ worksheets
    // that exist, not just this one!
    self.removeEventHandlers = function () {
        eventTypeList.map(function (e) {eventBus.off(e);});
    };

    self.addEventHandlers = function () {
        // * Activation cursor / focus handling *

        // activation/deactivation and focusing of segments.
        addEventHandler("worksheet:leaveForward", function () {
            var leavingIndex = self.activeSegmentIndex;
            // can't leave the bottom segment forwards
            if (leavingIndex == self.segments().length - 1) return;
            self.deactivateSegment(leavingIndex);
            self.activateSegment(leavingIndex + 1, true);
        });

        addEventHandler("worksheet:leaveBack", function () {
            var leavingIndex = self.activeSegmentIndex;
            // can't leave the top segment upwards
            if (leavingIndex == 0) return;
            self.deactivateSegment(leavingIndex);
            self.activateSegment(leavingIndex - 1, false);
        });

        // the event for this action contains the segment id
        addEventHandler("worksheet:segment-clicked", function (e, d) {
            // don't do anything if this segment is already selected
            var focusIndex = self.segmentIndexForID(d.id);
            if (self.activeSegmentIndex == focusIndex) return;
            if (self.activeSegmentIndex != null) self.deactivateSegment(self.activeSegmentIndex);
            self.activateSegment(focusIndex, true);
        });

        // * Manipulating segments *

        // this is called if the delete command is issued ...
        addEventHandler("worksheet:delete", function () {
            // if there's only one segment, don't delete it
            if (self.segments().length == 1) return;
            var index = self.activeSegmentIndex;
            self.deleteSegment(index);
            // if we deleted the last segment, select the one above.
            if (index == self.segments().length) self.activateSegment(self.segments().length - 1, false);
            // otherwise, select the one below.
            else self.activateSegment(index, true);
        });

        addEventHandler("worksheet:undelete", function () {
            self.undeleteSegment();
        });

        // ... whereas this one is called if backspace is pressed in an empty segment.
        addEventHandler("worksheet:deleteBackspace", function () {
            var index = self.activeSegmentIndex;
            // backspace on empty does nothing to the first segment.
            if (index == 0) return;
            self.deleteSegment(index);
            // select the end of the previous segment
            self.activateSegment(index - 1, false);

        });

        addEventHandler("worksheet:newBelow", function () {
            self.insertNewSegment(1);
        });

        addEventHandler("worksheet:newAbove", function () {
            self.insertNewSegment(0);
        });

        addEventHandler("worksheet:moveUp", function () {
            self.moveSegment(true);
        });

        addEventHandler("worksheet:moveDown", function () {
            self.moveSegment(false);
        });

        // * Changing segment types *

        // a helper function that changes the type of the active segment
        var changeActiveSegmentType = function (newType, newSegmentConstructor) {
            var index = self.activeSegmentIndex;
            if (index == null) return;
            var seg = self.segments()[index];
            // if the segment is already the right type, do nothing.
            if (seg.type == newType) return;

            var contents = seg.getContents();
            var newSeg = newSegmentConstructor(contents);
            self.segments.splice(index, 1, newSeg);
            self.activateSegment(index, true);
        };

        addEventHandler("worksheet:changeToFree", function () {
            changeActiveSegmentType("free", freeSegment);
        });

        addEventHandler("worksheet:changeToCode", function () {
            changeActiveSegmentType("code", codeSegment);
        });


        // * Evaluation *

        var evaluateSegment = function (seg) {
            // only evaluate code segments
            if (seg.type == "code") {
                var code = seg.getContents();
                // clear the output
                seg.clearOutput();
                seg.clearErrorAndConsole();
                seg.runningIndicator(true);

                repl.beginEvaluation({code: code, segmentID: seg.id});
            }
        };

        // The evaluation command will fire this event. The worksheet will then send a message to the evaluator
        // to do the evaluation itself.
        addEventHandler("worksheet:evaluate", function () {
            // check that a segment is active
            var seg = self.getActiveSegment();
            if (seg == null) return;
            evaluateSegment(seg);
            // if this isn't the last segment, move to the next
            if (self.activeSegmentIndex != self.segments().length - 1) eventBus.trigger("command:worksheet:leaveForward");
            // if it is the last, create a new one at the end
            else eventBus.trigger("worksheet:newBelow")
        });

        addEventHandler("worksheet:evaluate-all", function () {
            self.segments().forEach(evaluateSegment);
        });

        // messages from the evaluator

        addEventHandler("evaluator:value-response", function (e, d) {
            var segID = d.segmentID;
            var seg = self.getSegmentForID(segID);
            try {
                // If you're paying attention, you'll notice that the value gets JSON.parse'd twice: once here, and again
                // in the output viewer. This is a workaround for a problem in the rendering nREPL middleware that results
                // in the string begin double escaped. This round of parsing should just unescape the string, leaving a
                // string that will JSON.parse to the object. This round of unescaping is done here in order that the
                // value associated with the segment (and hence saved in the worksheet) is not double escaped.
                var parsedValue = JSON.parse(d.value);
                seg.output(parsedValue);
            } catch (e) {
                // if anything goes wrong, fall back to displaying the raw response.
                seg.output(d.value);
            }
        });

        addEventHandler("evaluator:console-response", function (e, d) {
            var segID = d.segmentID;
            var seg = self.getSegmentForID(segID);
            var oldText = seg.consoleText();
            seg.consoleText(oldText + _.escape(d.out));
        });

        addEventHandler("evaluator:done-response", function (e, d) {
            var segID = d.segmentID;
            var seg = self.getSegmentForID(segID);
            seg.runningIndicator(false);
        });

        addEventHandler("output:output-error", function (e, d) {
            var segID = d.segmentID;
            var seg = self.getSegmentForID(segID);
            seg.errorText(d.error);
        });

        addEventHandler("evaluator:error-response", function (e, d) {
            var segID = d.segmentID;
            var seg = self.getSegmentForID(segID);
            seg.stackTrace(d);
        });



        // * Auto-completion *

        addEventHandler("worksheet:completions", function (e, d) {
            // check that a segment is active
            var seg = self.getActiveSegment();
            if (seg == null) return;
            if (seg.type == "code") {
                seg.content.complete(clojureCompleter);
            }
        });

        // * External documentation *

        addEventHandler("docs:clojuredocs", function () {
            var seg = self.getActiveSegment();
            if (seg == null) return;
            if (seg.type == "code") {
                var token = seg.getTokenAtCursor();
                if (token != " ") {
                    // we try and resolve the symbol's namespace to jump directly to the clojuredocs page.
                    // This is async, so we open the window now so as not to be stymied by the popup-blocker
                    var win = window.open('', '_blank')
                    repl.resolveSymbol(token, repl.currentNamespace, function (d) {
                        if (d.ns) {
                            win.location = "http://clojuredocs.org/clojure_core/" + d.ns + "/" + d.symbol;
                        } else {
                            // if we can't resolve the symbol, then we fall back to searching
                            win.location = "http://clojuredocs.org/search?q=" + token;
                        }
                    });
                }
            }
        });

        // * Clearing output *

        var clearSegment = function(seg) {
            if (seg == null) return;
            if (seg.type == "code") {
                seg.clearOutput();
                seg.clearErrorAndConsole();
            }
        };

        addEventHandler("worksheet:clear-output", function() {
            var seg = self.getActiveSegment();
            clearSegment(seg);
        });

        addEventHandler("worksheet:clear-all-output", function() {
            self.segments().forEach(clearSegment);
        });
    };

    return self;
};
