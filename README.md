ddd-leaven-akka-v2
==================
Sample e-commerce application built on top of [Akka](akka.io) and [EventStore](geteventstore.com) following a [CQRS/DDDD](http://abdullin.com/post/dddd-cqrs-and-other-enterprise-development-buzz-words)-based approach. Makes use of [Akka DDD framework](https://github.com/pawelkaczor/akka-ddd). 

Currently only basic *Sales/Reservation* autonomous service is available. Autonomous service consist of 4 executable units ([Akka Microkernel](http://doc.akka.io/docs/akka/snapshot/scala/microkernel.html) bundles). 


### Sales service - executable units 

##### sales-write-back 
Business logic encapsulated inside Aggregate Roots. Starts as backend cluster node.
*Technologies:* Akka Persistence, Akka Cluster Sharding

##### sales-write-front 
Http server forwarding commands to backend cluster. 
*Technologies:* Akka-Http, Akka Cluster Client

##### sales-read-back
Service that consumes events from event store and updates view store (Postgresql database).
*Technologies:* [EventStore JVM Client](https://github.com/EventStore/EventStore.JVM), [Slick](http://slick.typesafe.com/)

##### sales-read-front
Http server providing rest endpoint for accessing view-store. 
*Technologies:* Akka-Http, Slick


### Prerequisites

##### Eventstore

~~~
docker run --name ecommerce-event-store -d -p 2113:2113 -p 1113:1113 jmkelly/eventstore
~~~
Go to http://127.0.0.1:2113/web/index.html#/projections and click `Enable All` button to enable system projections.

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

### Running Sales/Reservations service

For each sales-* bundle there is a corresponding run script: run-sales-{unit name}

### Manual testing of Sales/Reservation service using httpie

- Create reservation

~~~
http POST http://127.0.0.1:9100/ecommerce/sales Command-Type:ecommerce.sales.CreateReservation reservationId="reservation-200" clientId="client-1"
~~~

- Display all reservations

~~~
http GET localhost:9300/ecommerce/sales/reservation/all
~~~
