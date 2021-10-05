Contributing
============

The same [guidelines as in cryogen-core](https://github.com/cryogen-project/cryogen-core/blob/master/CONTRIBUTING.md) apply here.

Testing
-------

Clone the repo locally and switch to the branch you want to try. Then:

    clojure -Sdeps '{:deps {c/c {:local/root "/path/to/cryogen"}}}' -Tclj-new create :template cryogen :name test1/myblog :force true
