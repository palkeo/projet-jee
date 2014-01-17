projet-jee
==========

Running the website
-------------------
* Clone the repo
* Run `gradle build` in the root directory
* Run `java -jar src/frontend/build/libs/frontend.war`

Running a worker
----------------
* Clone the repo
* Run `gradle build` in the root directory
* Run `java -jar src/backend/build/libs/backend.jar -h` to see available options

Running a JMS server
--------------------
* Fetch Apache-ActiveMQ by running `wget http://apache.mirrors.multidist.eu/activemq/apache-activemq/5.9.0/apache-activemq-5.9.0-bin.tar.gz`
* Run `tar zxf apache-activemq-5.9.0-bin.tar.gz`
* Run `cd apache-activemq-5.9.0`
* Launch ActiveMQ server with `bin/activemq start`

In case of a problem with the table 'turn'
------------------------------------------
`psql -U aichallenge -h viod.eu`
`drop table turn;`
`create table turn (id serial not null, state character varying(255), turn integer not null, match bigint`
`alter table only turn add constraint fk_f188a422a39e4aaea90ed0c95c9 foreign key (match) references match(id);`

Dependencies
------------
* Gradle: available on AUR
* less: lessc package

Resources
----------

* Setup of a Spring project: http://spring.io/guides/gs/serving-web-content/
