# alma-connector

The Alma-connector offers the basic features to connect the Lib-Intel-Plattform to the next CBMS Alma.
For the communication to the Alma API a number of Feign clients are defined, taking the model classes from the alma-shared package.

## Prerequisites

This program is designed to run as part of a microservice architecture. 
Therefore several conditions must be fulfilled to run the service:
* A eureka type service registry,
* A Redis key value store
* A central config server
* A geteway server to handle authentication and user/role management


## Configuration

The microservice tries to retrieve three configuration files from the central config server:
* the 'files' configuration, defining the mandatory `ub.statistics.data.dir`property holding the information about the central data directory to store the sap files.
* the 'client' configuration, holding several optional properties for the interaction within the Lib-Intel-platform.
* the 'alma' configuration, holding the keys for the alma API. These should either be supplied in an encrypted properties file or by defining the environment variables. 
The variables have the form `alma.api.XXX.key`, where the XXX are 'acq' for the acquisition API, 'user' for the user API 


## Endpoints

