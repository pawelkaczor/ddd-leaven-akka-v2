ddd-leaven-akka-v2
==================


### Setup

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