ddd-leaven-akka-v2
==================

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

### Running services

For each service there is a run script: run-{project name}

### Manual testing of Sales/Reservation service using httpie

- Create reservation

~~~
http POST http://127.0.0.1:9100/ecommerce/sales Command-Type:ecommerce.sales.CreateReservation reservationId="reservation-200" clientId="client-1"
~~~

- Display all reservations

~~~
http GET localhost:9300/ecommerce/sales/reservation/all
~~~