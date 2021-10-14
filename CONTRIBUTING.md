Contributing
============

The same [guidelines as in cryogen-core](https://github.com/cryogen-project/cryogen-core/blob/master/CONTRIBUTING.md) apply here.

Testing
-------

Clone the repo locally and switch to the branch you want to try. Then
(if using `clj-new`):
```sh
clojure -Sdeps '{:deps {c/c {:local/root "/path/to/cryogen"}}}' -Tclj-new create :template cryogen :name myname/myblog :force true
```
Or, if using `deps-new`:
```sh
clojure -Sdeps '{:deps {c/c {:local/root "/path/to/cryogen"}}}' -Tnew :template cryogen :name myname/myblog
```
