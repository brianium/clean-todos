# Clean Todos

An example of clean architecture in Clojure. The goal of this project is to demonstrate
the ever elusive [clean architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)
in Clojure, and also serve as a gentle introduction to `clojure.spec` (for me and hopefully others).

## Testing

This application leverages a mixture of traditional unit tests and generative testing via `clojure.test.check`. It's
pretty dern cool, so check out the test suite. Unit tests and generative tests can be run at once via:

```
$ lein test
```
