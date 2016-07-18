/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

var getFromGithub = function (user, repo, path, callback) {
    $.get("https://api.github.com/repos/" + user + "/" + repo + "/contents/" + path).success(function (data) {
        callback(decodeGitHubBase64(data.content));
    });
};

var getFromGist = function (id, filename, callback) {
    $.get("https://api.github.com/gists/" + id).success(function (data) {
        var file;
        // default to the only file
        if (_.size(data.files) == 1) file = data.files[Object.keys(data.files)[0]];
        else file = data.files[filename];
        console.log(file);
        callback(file.content);
    });
};

var decodeGitHubBase64 = function (data) {
    function b64_to_utf8(str) {
        return decodeURIComponent(escape(window.atob(str)));
    }
    return b64_to_utf8(data.replace(/\n/g, ""));
};
