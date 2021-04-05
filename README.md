# alma-connector

The Alma-connector is a web application offering a number of features to connect the Lib-Intel-Plattform to the CBMS Alma.
For the communication to the Alma API Feign clients are defined, the model classes are taken from the alma-shared package (https://git.uni-due.de/ub/alma-shared).

This program is designed to run as part of a microservice architecture. 

## Prerequisites as microservice

In this environment several conditions must be fulfilled to run the service:

* A Java jdk of version 11 or higher
* An eureka type service registry,
* A Redis key value store, accessible to all microservices
* A central config server
* A gateway server to handle authentication and user/role management
* An elasticsearch server to store the items, bib records, as well as loan and request events
* A database (usually a PostgreSQL database has been used), configured to be accessed via username/password authentication


## Configuration

The microservice tries to retrieve several configuration files from the central config server:
* the 'files' configuration, defining the mandatory `ub.statistics.data.dir`property holding the information about the central data directory to store the sap files.
* the 'client' configuration, holding several optional properties for the interaction within the Lib intel platform.
* the 'alma' configuration, holding the keys for the alma API. These should either be supplied in an encrypted properties file or by defining the environment variables. 
* the 'settings-datasource' configuration, defining the porperties for the database holding the bookbinder order data
* the 'sap' configuration, holding information such as the vat tax keys used to define the 'home' invoices 

The variables can also be set by environment variables (for further details please refer to https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config). This is particular usefull for senesitive information such as API keys.

## Installation

You can run this application by typing ```mvn spring-boot:run```. An executable jar can be generate using ```mvn clean install```.

Further documentation can be found in the wiki.