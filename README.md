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

Dependencies
------------
* Gradle: available on AUR
* less: lessc package

Resources
----------

* Setup of a Spring project: http://spring.io/guides/gs/serving-web-content/
