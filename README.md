
ddd-leaven-akka-v2[![Build Status](https://travis-ci.org/pawelkaczor/ddd-leaven-akka-v2.svg?branch=master)](https://travis-ci.org/pawelkaczor/ddd-leaven-akka-v2)
==================

[![Join Akka-DDD chat at https://gitter.im/pawelkaczor/akka-ddd](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/pawelkaczor/akka-ddd?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Sample e-commerce application built on top of [Akka](http://akka.io) and [EventStore](http://geteventstore.com) following a [CQRS/DDDD](http://abdullin.com/post/dddd-cqrs-and-other-enterprise-development-buzz-words)-based approach.

Overview
--------------------

This sample e-commerce system has unique set of properties. It is:

* responsive, resilient, elastic :clap:,
* incorporates a SOA, EDA, and Microservice architecture :ok_hand:,
* incorporates [CQRS/DDDD](http://abdullin.com/post/dddd-cqrs-and-other-enterprise-development-buzz-words) architectural patterns :+1:,
* supporting long-running business processes (eg. payment deadlines) :muscle:, and
* developer-friendly (implemented in Scala, ~1500 lines of code) :smile:.

All these capabilities are obviously supported by the underlying technology stack, which includes:

* [Akka](http://akka.io) - actor-based, reactive middleware implemented in Scala,

* [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/introduction.html) - HTTP server build upon [Akka Stream](http://doc.akka.io/docs/akka/2.4/scala/stream/stream-introduction.html) (Akka's implementation of [Reactive Streams Specification](http://www.reactive-streams.org/)),

* [Akka Persistence](http://doc.akka.io/docs/akka/current/scala/persistence.html) - infrastructure for building durable (event sourced) actors, which has a pluggable journal,

* [Event Store](http://geteventstore.com) - scalable, highly available event store with akka-persistence journal implementation. Provides engine for running user-defined projections (javascript functions) over single or multiple  event streams. Projections allow the system to group or combine events into new event streams that can represent domain-level journals such as office journals (events grouped by emitter (`Aggregate Root`) class) or business process journals (events related to concrete business process). Domain journals are topic of interest for services such as:
  * **view updaters** - responsible for updating the read side of the system 
  * **receptors** - allow event-driven interaction between subsystems (event choreography), including long-running processes (sagas), 
  
:exclamation: Both view uppdaters and receptors are operating within non-blocking **back-pressured** event processing pipeline :exclamation: 

* [Akka-DDD](http://github.com/pawelkaczor/akka-ddd) - framework containing glue-code and all building blocks

Subsystems
--------------------

The system currently consists of the following subsystems:

* Sales/Reservation - responsible for creating and confirming Reservations
* Invoicing - responsible for the invoicing
* Shipping - responsible for the goods delivery
* Headquarters - executes the Ordering Process (see below)
 
Ordering Process
--------------------
 
![Ordering Process](https://raw.githubusercontent.com/pawelkaczor/ddd-leaven-akka-v2/master/project/diagrams/OrderingSystem.png)

Subsystem components
--------------------

Each subsystem is divided into **write** and **read** side, each side containing **back-end** and **front-end** application: 
![](https://docs.google.com/drawings/d/12Lwwq3WROlu2pkXsIwICQvuWiPNKW5XQwc7bRtLaauI/pub?w=722&amp;h=620)

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
- [Stress testing](https://github.com/pawelkaczor/ddd-leaven-akka-v2/wiki/Stress-testing)
- [Monitoring](https://github.com/pawelkaczor/ddd-leaven-akka-v2/wiki/Monitoring)

