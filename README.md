ddd-leaven-akka-v2
==================
Sample e-commerce application built on top of [Akka](akka.io) and [EventStore](geteventstore.com) following a [CQRS/DDDD](http://abdullin.com/post/dddd-cqrs-and-other-enterprise-development-buzz-words)-based approach. Makes use of [Akka DDD framework](https://github.com/pawelkaczor/akka-ddd). 

System currently consists of the following subsystems (aka. autonomous services):

* Sales/Reservation - accepting/confirming reservations (orders)
* Invoicing - handling payment process
* Shipping - handling shipping process

Each autonomous service consist of 4 executable units ([Akka Microkernel](http://doc.akka.io/docs/akka/snapshot/scala/microkernel.html) bundles).

##### write-back
Business logic encapsulated inside Aggregate Roots, Receptors and Process Managers (Sagas). Application starts as backend cluster node.

*Technologies:* Akka Persistence, Akka Cluster Sharding

##### write-front
Http server forwarding commands to backend cluster. 

*Technologies:* Akka-Http, Akka Cluster Client

##### read-back
Service that consumes events from event store and updates view store (Postgresql database).

*Technologies:* [EventStore JVM Client](https://github.com/EventStore/EventStore.JVM), [Slick](http://slick.typesafe.com/)

##### read-front
Http server providing rest endpoint for accessing view-store. 

*Technologies:* Akka-Http, Slick


### Prerequisites

##### Eventstore

~~~
docker run --name ecommerce-event-store -d -p 2113:2113 -p 1113:1113 newion/eventstore:3.0.1
~~~
Run [enable-projections](https://github.com/pawelkaczor/ddd-leaven-akka-v2/blob/master/enable-projections) script.

##### Postgresql
~~~
docker run --name sales-view-store -d -p 5432:5432 postgres
~~~

Postgres console: psql -h localhost -p 5432 -U postgres


##### Command line HTTP client

http://httpie.org/

### Building the project
~~~
sbt stage
~~~

### Running services
As there are multiply applications per service, running/monitoring the whole system is not straightforward.
You can use run scripts (located in [run-scripts](https://github.com/pawelkaczor/ddd-leaven-akka-v2/blob/master/run-scripts) directory)
to quickly start the system and execute sample [Reservation process](#manual-testing). But you'd better configure [supervisord](http://supervisord.org/)
to include [supervisord-configs](https://github.com/pawelkaczor/ddd-leaven-akka-v2/blob/master/supervisord-configs) dir and
manage (start/restart/stop) services using supervisorctrl tool.

### Manual testing of Reservation process using httpie (#manual-testing)

- Create reservation

~~~
http :9100/ecommerce/sales Command-Type:ecommerce.sales.CreateReservation reservationId="r1" customerId="customer-1"
~~~

- Confirm reservation

~~~
http :9100/ecommerce/sales Command-Type:ecommerce.sales.ConfirmReservation reservationId="r1"
~~~

- Display reservation

~~~
http :9110/ecommerce/sales/reservation/r1
~~~

- Display shipment status

~~~
http :9310/ecommerce/shipping/shipment/order/r1
~~~
