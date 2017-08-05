# Clean Todos

An example of clean architecture in Clojure. The goal of this project is to demonstrate
the ever elusive [clean architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)
in Clojure, and also serve as a gentle introduction to `clojure.spec` (for me and hopefully others).

## Delivery (i.e presentation for the app)

The `todos.delivery` namespace contains different delivery mechanisms for clean todos - i.e cli, web, etc..

### todos.delivery.cli

This delivery provides a command line app for managing todos. Todos are persisted using sqlite.

#### Usage

```
Usage:
  todos command [options]

Options:
  -s, --status STATUS  all  Todo status to filter on
  -h, --help

Available Commands:
  create: Create a new todo
  create todo-name

  list: List todos
  list --status=STATUS <completed,active,all>

  toggle: Toggle todo status
  toggle todo-id

  delete: Permanently removes a todo
  delete todo-id
```

#### Building

This delivery is built to a single executble named `todos` using the [lein binplus](https://github.com/BrunoBonacci/lein-binplus) plugin. Lein can then be used to build the cli delivery using the `cli` profile:

```
$ lein with-profile +cli bin
```

## Testing

This application leverages a mixture of traditional unit tests and generative testing via `clojure.test.check`. It's
pretty dern cool, so check out the test suite. Unit tests and generative tests can be run at once via:

```
$ lein test
```

To run tests during development - i.e tests are re-run as changes are made - use `test-refresh`.

```
$ lein test-refresh
$ lein with-profile +cli test-refresh
```
