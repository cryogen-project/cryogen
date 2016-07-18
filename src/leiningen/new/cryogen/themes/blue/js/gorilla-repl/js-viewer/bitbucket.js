/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

var getFromBitbucket = function (user, repo, path, revision, callback) {
    $.get("https://bitbucket.org/api/1.0/repositories/" + user + "/" + repo + "/raw/" + revision + "/" + path, callback);
};
