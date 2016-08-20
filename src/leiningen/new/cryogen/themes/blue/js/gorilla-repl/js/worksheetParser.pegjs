/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// The parser takes a worksheet persisted as a marked up clojure file and returns a list of segments.

worksheet = worksheetHeader seg:segmentWithBlankLine* {return seg;}

lineEnd = "\n" / "\r\n"

worksheetHeader = ";; gorilla-repl.fileformat = 1" lineEnd lineEnd

segmentWithBlankLine = seg:segment lineEnd? {return seg;}

segment = freeSegment / codeSegment

freeSegment = freeSegmentOpenTag content:stringNoDelim? freeSegmentCloseTag
                {return freeSegment(unmakeClojureComment(content));}

freeSegmentOpenTag = ";; **" lineEnd

freeSegmentCloseTag = lineEnd ";; **" lineEnd

codeSegment = codeSegmentOpenTag content:stringNoDelim? codeSegmentCloseTag cs:consoleSection? out:outputSection?
                {return codeSegment(content, unmakeClojureComment(cs), unmakeClojureComment(out));}

codeSegmentOpenTag = ";; @@" lineEnd

codeSegmentCloseTag = lineEnd ";; @@" lineEnd

outputSection = outputOpenTag output:stringNoDelim outputCloseTag {return output;}

outputOpenTag = ";; =>" lineEnd

outputCloseTag = lineEnd ";; <=" lineEnd

consoleSection = consoleOpenTag cs:stringNoDelim consoleCloseTag {return cs;}

consoleOpenTag = ";; ->" lineEnd

consoleCloseTag = lineEnd ";; <-" lineEnd

stringNoDelim = cs:noDelimChar+ {return cs.join("");}

delimiter = freeSegmentOpenTag / freeSegmentCloseTag /codeSegmentOpenTag / codeSegmentCloseTag / outputOpenTag /
                outputCloseTag / consoleOpenTag / consoleCloseTag

noDelimChar = !delimiter c:. {return c;}