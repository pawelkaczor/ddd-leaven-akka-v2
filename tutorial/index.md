ddd-leaven-akka-v2
==================
*Reactive DDD with Akka*

Overview
--------------------

This sample e-commerce system has a set of properties that makes it unique among similar systems. It is:

* responsive, resilient, elastic - at least potentially ;-),
* incorporates a SOA, EDA, and Microservice architecture,
* incorporates [CQRS/DDDD](http://abdullin.com/post/dddd-cqrs-and-other-enterprise-development-buzz-words) architectural patterns,
* supporting long-running business processes (eg. payment deadlines), and
* developer-friendly (implemented in Scala, ~1500 lines of code).

All these capabilities are obviously supported by the underlying technology stack, which includes:

* [Akka](http://akka.io) - actor-based, reactive middleware implemented in Scala,

* [Akka HTTP](http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0/scala/http/introduction.html) - HTTP server build upon [Akka Stream]() (Akka's implementation of [Reactive Streams Specification](http://www.reactive-streams.org/)),

* [Akka Persistence](http://doc.akka.io/docs/akka/current/scala/persistence.html) - infrastructure for building durable (event sourced) actors, which has a pluggable journal,

* [Event Store](http://geteventstore.com) - scalable, highly available event store with akka-persistence journal implementation. Provides engine for running user-defined projections (javascript functions) over single or multiple  event streams. Projections allow the system to group or combine events into new event streams that can represent domain-level journals such as office journals (events grouped by emitter (`Aggregate Root`) class) or business process journals (events related to concrete business process). Domain journals are topic of interest for services such as:
  * view updaters - responsible for updating the read side of the system 
  * receptors - allow event-driven interaction between subsystems (event choreography), including long-running processes (sagas), and

* [Akka-DDD](http://github.com/pawelkaczor/akka-ddd) - framework containing glue-code and all building blocks

Subsystems
--------------------

The system currently consists of the following subsystems:

* Sales/Reservation - accepting/confirming reservations (orders)
* Invoicing - handling payment process
* Shipping - handling shipping process

Applications
--------------------

Each subsystem is divided into **write** and **read** side, each side containing **back-end** and **front-end** application: 

***
#### write-back
Backend cluster node hosting `Aggregate Roots`, `Receptors` and `Process Managers (Sagas)`.

#### write-front
HTTP server forwarding commands to backend cluster. 

***
#### read-back
View update service that consumes events from event store and updates view store (PostgreSQL database).

#### read-front
HTTP server providing rest endpoint for accessing view store. 

Running and testing the system
--------------------

- [Running the system on physical server](https://github.com/pawelkaczor/ddd-leaven-akka-v2/wiki/Running-the-system-on-physical-server)
- [Running virtualized system](https://github.com/pawelkaczor/ddd-leaven-akka-v2/wiki/Running-virtualized-system)
- [Manual testing of order process](https://github.com/pawelkaczor/ddd-leaven-akka-v2/wiki/Manual-testing-of-order-process)
- [End-to-end test of order process](https://github.com/pawelkaczor/ddd-leaven-akka-v2/wiki/End-to-end-test-of-order-process)
