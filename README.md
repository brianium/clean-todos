# Clean Todos

An example of clean architecture in Clojure. The goal of this project is to demonstrate
the ever elusive [clean architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)
in Clojure, and also serve as a gentle introduction to `clojure.spec` (for me and hopefully others).

Running the various deliveries should be a snap once you get [leiningen](https://leiningen.org/) installed.

If you are interested - please see the [retrospect](https://github.com/brianium/clean-todos#retrospect). One can only learn so much from the classic "todo" app, but I feel this app has given me a better understanding of the Clojure(Script) landscape as a whole.

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

## Retrospect

I'm not sure if this approach is a purist approach to the clean architecture. Everything is very use case driven, and the architecture *should* make it clear what the intent of the application is. The concept of input and output ports is handled (cleanly in my opinion) by [core.async](https://github.com/clojure/core.async) channels. Dependency injection is handled (again cleanly in my opinion) by [mount](https://github.com/tolitius/mount).

I don't have much experience building applications in Clojure(Script) - but I have leveraged similar concepts in larger apps using different languages. While a todo app can only teach so much - I was just floored by the elegance of Clojure(Script) in building a use case centered app with different deliveries.

### Basic Architecture

The architecture lays out like so:

```
resources/ -- static assets leveraged by web delivery
src/
  todos/
    core/ -- contains use cases, entities, and some simple conventions for messaging
	  action/
	  entity/
	  use_case/
	    create_todo/
		delete_todo/
		list_todos/
		update_todo/
	delivery/
	  api/ -- a simple restful api
	  cli/ -- a command-line interface to the app
	  web/ -- a re-frame application
	storage/
	  todo/ -- protocol implementations for persisting todos
```

### On core.async

The concept of inputs and outputs in the clean architecture just seemed to make sense as core.async channels. While this made sense to me - I'm no expert and I'm not sure I fully understand the implications of this choice. I do know that having a use case depend on channels opens the door for tons of flexibility - i.e (one-to-many channels, controlling buffers, etc..). The api is also fairly easy to use.

The deliveries I created usually leveraged a put followed by a blocking take - mostly out of necessity (blocking in the cli for example). An async take would be pretty slick for streaming interfaces.

### On clojure.spec

clojure.spec is amazing. I'm likely not leveraging it to the best of it's ability. The cool things I saw were:

* Generative teting via test.check. My functions are pretty thoroughly tested by a variety of inputs.
* Validation. The api and web deliveries use specs to validate inputs and outputs. This is especially cool
in the re-frame app as it checks the shape of app state after every mutation. This turned up all kinds of errors
during development.
* Documentation. Specs do a great job of documenting intent

Some things that I struggled with a bit - but might have the beginning of understanding:

* Generators. Making custom generators took a bit of legwork to understand and employ
* Convention - still not sure if specs make sense in the same file or a different one from the code they are describing. I ultimately settled on different files. It creates a little bloat - but makes sense and they are easy to import when composing other specs.

### On ClojureScript

The web delivery leverages [re-frame](https://github.com/Day8/re-frame). This choice was largely due to my interest
in the framework. I spend a good chunk of my day-to-day writing React/Redux apps using ES6/Next.

I personally prefer ClojureScript over vanilla JavaScript. The tooling, the standard library, persistent collections out of the box.. You can just do more with less. [lein-figwheel](https://github.com/bhauman/lein-figwheel) solves a lot of the problems that webpack does, and in some ways I find it easier to use.

The web delivery may fall out of the clean architecture camp because it did not actually use any of the use cases. While it is structured by use cases - actually leveraging the use case namespaces felt awkward. Re-frame solves a lot of the input/output problems itself.

**What I did get to re-use:** was my specs and core application code - thanks to the wonder of reader conditionals and .cljc files. This was nearly effortless, and it just blows my mind. Writing Clojure that works on the server and the client. Wow.

### On code organization

Not sure where I landed on this. The current code base is one repo supporting all deliveries. That means it is a mixture of `.clj`, `.cljs`, and `.cljc` files. At the time of this writing - [documentation](https://github.com/clojure/clojurescript/wiki/Using-cljc#general-considerations) on the subject isn't terribly clear on convention. The points made in favor of separate `src/clj`, `src/cljs`, and `src/cljc` directories are pretty compelling, and I think I would opt for this approach in the next app I build.

The benefit of one repo with many deliveries is still unclear to me. It wasn't terribly difficult, but I didn't do any of the work of deploying all deliveries. I'm not sure on the nuances of `lein` when it comes to building multiple jars with different requirements. Something to learn still! Basically comes down to the question of one `project.clj` or multiple?

The [lein-parent](https://github.com/achin/lein-parent) plugin seems like it offers some conventions for managing dependencies and multiple projects in a mono repo context.


## Todos (snicker)

The problem space of todos provides a finite amount of interest for me. I left some things out, and there are some
other things that I think would be cool. I may or may not ever get to any of these.

* A re-natal delivery. It would be cool to see a delivery for a native mobile app
* Error handling in the web delivery. Like a good "RUSH TO MARKET APP" the web delivery just assumes all http requests will always succeed.
* Document more of this

## Feedback / Pull Requests

I would welcome any pull requests or general feedback on any approach taken here. I'm not sure I ever have or ever will fully grasp what a computer is, so I always welcome people telling me how to do things better :)
