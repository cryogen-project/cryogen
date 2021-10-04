# Change Log

All notable changes to this project will be documented in this
file. This change log follows the conventions of
[keepachangelog.com](http://keepachangelog.com/), with the addition of
sections for `Highlights`, `Breaking` and `Changes since x.y.z`.

## [Unreleased]

### Breaking
- Fast compilation command line changed from `*-fast` to `*:fast`.

## [0.6.3] - 2020-12-07

### Highlights
- Allow fast compilation of changed files only. Opt-in. Requires
  `cryogen-core` version `0.4.0`.
- Support the use of `tools.deps` (aka clojure CLI) in addition to `lein`.

### Changes since 0.6.2
- [#226](https://github.com/cryogen-project/cryogen/pull/226) Prepare for release 0.6.3 (bombaywalla)
- [#225](https://github.com/cryogen-project/cryogen/pull/225) git-ignore lotus' sass-generated files (devurandom)
- [#221](https://github.com/cryogen-project/cryogen/pull/221) Support for deps tools instead of lein (holyjak)
- [#220](https://github.com/cryogen-project/cryogen/pull/220) Support for incremental compilation (holyjak)

## [0.6.2] - 2020-10-16

### Highlights
- Update to new version of the Markup protocol that allows for
  multiple file extensions for a particular markup.

### Breaking
- Requires latest versions of `cryogen-asciidoc` (at least 0.3.2),
  `cryogen-flexmark` (at least 0.1.4), and `cryogen-markdown` (at
  least 0.1.13).

### Changes since 0.6.1
- [#217](https://github.com/cryogen-project/cryogen/pull/217) Update README.md to current year (hewrin)
- [#216](https://github.com/cryogen-project/cryogen/pull/216) Fix typo in QuickStart template content (daemianmack)
- [#213](https://github.com/cryogen-project/cryogen/pull/213) Use Markup protocol defined in cryogen-core.markup (darth10)

## [0.6.1] - 2020-03-10

### Highlights
- Update versions of `cryogen-core` and `cryogen-flexmark`.

### Changes since 0.6.0
- [#206](https://github.com/cryogen-project/cryogen/pull/206) README: cryogen-markdown => cryogen-flexmark (daemianmack)

## [0.6.0] - 2020-01-20

### Highlights
- Switch to `cryogen-flexmark`.
- Handle case where `blog-prefix` is blank.

[Unreleased]: https://github.com/cryogen-project/cryogen/compare/0.6.3...HEAD

[0.6.3]: https://github.com/cryogen-project/cryogen/compare/0.6.2...0.6.3
[0.6.2]: https://github.com/cryogen-project/cryogen/compare/0.6.1...0.6.2
[0.6.1]: https://github.com/cryogen-project/cryogen/compare/0.6.0...0.6.1
[0.6.0]: https://github.com/cryogen-project/cryogen/compare/0.1.0...0.6.0
