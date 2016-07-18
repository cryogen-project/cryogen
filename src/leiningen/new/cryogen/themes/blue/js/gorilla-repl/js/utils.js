/*
 * This file is part of gorilla-repl. Copyright (C) 2014-, Jony Hudson.
 *
 * gorilla-repl is licenced to you under the MIT licence. See the file LICENCE.txt for full details.
 */

// takes a string and prefixes every line with ';; '
var makeClojureComment = function (code) {
    return code.split('\n').map(function (x) {
        return ";;; " + x;
    }).join("\n")
};

// the funny name indicates that it undoes what the above function does. It doesn't check whether the line is actually
// commented, so will break text that isn't in the format it expects.
var unmakeClojureComment = function (code) {
    if (code) {
        return code.split('\n').map(function (x) {
            return x.slice(4);
        }).join("\n");
    }
    else return null;
};


var makeHipNSName = function () {
    // The word lists are taken from Raymond Chan's MIT-licensed https://github.com/raycchan/bazaar
    var adj = ["affectionate", "amiable", "arrogant", "balmy", "barren", "benevolent", "billowing", "blessed", "breezy", "calm", "celestial", "charming", "combative", "composed", "condemned", "divine", "dry", "energized", "enigmatic", "exuberant", "flowing", "fluffy", "fluttering", "frightened", "fuscia", "gentle", "greasy", "grieving", "harmonious", "hollow", "homeless", "icy", "indigo", "inquisitive", "itchy", "joyful", "jubilant", "juicy", "khaki", "limitless", "lush", "mellow", "merciful", "merry", "mirthful", "moonlit", "mysterious", "natural", "outrageous", "pacific", "parched", "placid", "pleasant", "poised", "purring", "radioactive", "resilient", "scenic", "screeching", "sensitive", "serene", "snowy", "solitary", "spacial", "squealing", "stark", "stunning", "sunset", "talented", "tasteless", "teal", "thoughtless", "thriving", "tranquil", "tropical", "undisturbed", "unsightly", "unwavering", "uplifting", "voiceless", "wandering", "warm", "wealthy", "whispering", "withered", "wooden", "zealous"];
    var things = ["abyss", "atoll", "aurora", "autumn", "badlands", "beach", "briars", "brook", "canopy", "canyon", "cavern", "chasm", "cliff", "cove", "crater", "creek", "darkness", "dawn", "desert", "dew", "dove", "drylands", "dusk", "farm", "fern", "firefly", "flowers", "fog", "foliage", "forest", "galaxy", "garden", "geyser", "grove", "hurricane", "iceberg", "lagoon", "lake", "leaves", "marsh", "meadow", "mist", "moss", "mountain", "oasis", "ocean", "peak", "pebble", "pine", "plateau", "pond", "reef", "reserve", "resonance", "sanctuary", "sands", "shelter", "silence", "smokescreen", "snowflake", "spring", "storm", "stream", "summer", "summit", "sunrise", "sunset", "sunshine", "surf", "swamp", "temple", "thorns", "tsunami", "tundra", "valley", "volcano", "waterfall", "willow", "winds", "winter"];
    var adjI = Math.floor(Math.random() * adj.length);
    var thingsI = Math.floor(Math.random() * things.length);
    return adj[adjI] + "-" + things[thingsI];
};