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
$ lein with-profile cli bin
```

### todos.delivery.api

The api delivery provides a simple restful interface for managing todos.

The following routes are supported:

```
GET    /todos{?status=completed,active} - status defaults to all todos
POST   /todos
DELETE /todos{/id}
PATCH  /todos{/id}
```

`POST` expects a JSON document of the form `{"title": "string", "complete?": boolean}`

`PATCH` expects a similar document to `POST` with the difference that all keys are optional.

All inputs are validated via `clojure.spec.alpha/conform`.

#### Running the server

The server is powered via [lein-ring](https://github.com/weavejester/lein-ring). Just include the `api`
profile when running:

```
$ lein api
```

### todos.delivery.web

The web delivery is a re-frame app that leverages the api delivery.

#### Running the web application

The web app is run in a dev setting using [lein-figwheel](https://github.com/bhauman/lein-figwheel). The api
delivery needs to be running in order to use the web delivery.

I'm not a lein wizard so I wasn't able to figure out how to run both in a single command (at least in a sane way) - so in order to demo the web app first start the api server in a terminal session:

```
$ lein api
```

Then in another terminal session start the web app:

```
$ lein web
```

You should then we able to visit `http://localhost:3449` to see the re-frame app in action.

## Testing

This application leverages a mixture of traditional unit tests and generative testing via `clojure.test.check`. It's
pretty dern cool, so check out the test suite. Unit tests and generative tests can be run at once via:

```
$ lein test
```

