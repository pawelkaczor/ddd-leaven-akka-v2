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

* [Akka](akka.io) - actor-based, reactive middleware implemented in Scala,

* [Akka HTTP](http://typesafe.com/blog/akka-http-preview) - HTTP server build upon [Akka Stream]() (Akka's implementation of [Reactive Streams Specification](http://www.reactive-streams.org/)).

* [Akka Persistence](http://doc.akka.io/docs/akka/current/scala/persistence.html) - infrastructure for building durable (event sourced) actors, which has a pluggable journal.

* [Event Store](http://geteventstore.com) - scalable, highly available event store with akka-persistence journal implementation. Provides engine for running user-defined projections (javascript functions) over single or multiple  event streams. Projections allow the system to group or combine events into new event streams that can represent domain-level journals such as office journals (events grouped by emitter (`Aggregate Root`) class) or business process journals (events related to concrete business process). Domain journals are topic of interest for services such as:
  * view updaters - responsible for updating the read side of the system 
  * receptors - allow event-driven interaction between subsystems (event choreography), including long-running processes (sagas)

* [Akka-DDD](http://github.com/pawelkaczor/akka-ddd) - umbrella project containing glue-code and all building blocks.

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

Installation
------------------------

##### Install Event Store

~~~
docker run --name ecommerce-event-store -d -p 2113:2113 -p 1113:1113 newion/eventstore:3.0.1
~~~

##### Install PostgreSQL
~~~
docker run --name sales-view-store -d -p 5432:5432 postgres
~~~

:bulb: Postgresql console: `psql -h localhost -p 5432 -U postgres`


##### Checkout the project
~~~
git clone https://github.com/pawelkaczor/ddd-leaven-akka-v2.git
~~~

##### Register projections
Run [enable-projections](enable-projections) script.

##### Install Command line HTTP client

http://httpie.org/

Building the project
------------------------------

~~~
sbt stage
~~~

Running subsystems
------------------------------

As there are multiple applications per subsystem, running/monitoring the whole system is not straightforward.
You can use run scripts (located in [run-scripts](run-scripts) directory)
to quickly start the system and execute sample [Reservation process](#manual-testing). However, it s recommended you configure supervisord [supervisord](http://supervisord.org/)
to include [supervisord-configs](supervisord-configs) dir and
manage (start/restart/stop) services using supervisorctrl tool.

<a name="manual-testing"></a>Manual testing of Reservation process
----------------------------

1. Create reservation
  * `http :9100/ecommerce/sales Command-Type:ecommerce.sales.CreateReservation reservationId="r1" customerId="customer-1"`


2. Add product
  * `echo '{"reservationId": "r1", "product": { "snapshotId": { "aggregateId": "123456789", "sequenceNr": 0 }, "name": "DDDD For Dummies - 7th Edition", "productType": 1, "price": { "doubleValue": 10.0, "currencyCode": "USD"}}, "quantity": 1}' | http :9100/ecommerce/sales Command-Type:ecommerce.sales.ReserveProduct`

3. Confirm reservation
  ![](https://raw.githubusercontent.com/pawelkaczor/ddd-leaven-akka-v2/master/project/diagrams/OrderingSystem.png)
  * `http :9100/ecommerce/sales Command-Type:ecommerce.sales.ConfirmReservation reservationId="r1"`

4. Pay
  * `echo '{"invoiceId": "{{INVOICE_ID}}", "orderId": "r1", "amount": { "doubleValue": 10.0, "currencyCode": "USD"}, "paymentId": "230982342"}' | http :9200/ecommerce/invoicing Command-Type:ecommerce.invoicing.ReceivePayment`
  * :bulb: If you do not pay within ~ 3 minutes, reservation will be canceled

5. Check read side
  * display reservation: `http :9110/ecommerce/sales/reservation/r1`
  * display shipment status: `http :9310/ecommerce/shipping/shipment/order/r1`
