# EDI-NG_server
This is the server side for [SP7-Ritmare/EDI-NG_client](https://github.com/SP7-Ritmare/EDI-NG_client).

It's a [Spring](http://spring.io) Boot application configured to package as a WAR file, so as to allow deployment to, e.g., [Tomcat](http://tomcat.apache.org), relying on a [PostgreSQL](https://www.postgresql.org) database server.
In order to deploy to Tomcat, you need to place the WAR file in Tomcat's webapps directory, or upload it via the Tomcat manager application.

A [Binary WAR file](https://github.com/SP7-Ritmare/EDI-NG_server/releases/download/v1.0/edi.war) is available via the "releases" tab.
The WAR has been tested on Tomcat 7.

> The pre-packaged WAR file can be a good starting point to test the system in a reasonably "standalone" mode.

> It contains a built-in version of EDI-NG_client where the templates point to the EDI-NG_server packaged within, with the following pre-requisites:
> * Tomcat should run on port 8080
> * PostgreSQL must be installed on a host reachable from your Tomcat machine
> * An empty database must be created in the PostgreSQL server
> * Once deployed, you'll need to change the application.war file, present in your Tomcat host's $TOMCAT_HOME/webapps/edi/WEB-INF/classes/ directory, and then restart Tomcat
>

Following is an example of the configuration you need to edit in $TOMCAT_HOME/webapps/edi/WEB-INF/classes/application.properties 
```properties
spring.datasource.url=jdbc:postgresql://<your PostreSQL host>/<db name you picked>
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.datasource.driverClassName=org.postgresql.Driver
```

Once all prerequisites are met, you can test the EDI-NG_client and server by pointing your browser to http://localhost:8080/ on your Tomcat machine.

## Building your own WAR file
If you want to build the latest release from sources, you can user [Apache Maven](https://maven.apache.org/index.html) (Apache License v2.0). In the EDI-T directory, simply run:
```bash
mvn package
```
This will create your WAR file in the target directory, ready for deployment.

Prior to create the WAR file you might want to customise the application.properties file, mentioned above, so as to match your PostgreSQL location.

# Dependencies
All dependencies are listed in the *pom.xml* file.

# Local files
Third-party libraries are included in the distribution:
[Postgres JDBC driver](https://jdbc.postgresql.org/index.html), BSD License
[Saxon XSLT and XQuery Processor](http://saxon.sourceforge.net/#F9.7HE) Home Edition, Mozilla Public License version 1.0

# Copyright information

Copyright (C) 2013:

Anna Basoni - IREA CNR,
Mauro Bastianini - ISMAR CNR,
Cristiano Fugazza - IREA CNR,
Simone Lanucara - IREA CNR,
Stefano Menegon - ISMAR CNR,
Tiziano Minuzzo - ISMAR CNR,
Alessandro Oggioni - IREA CNR,
Fabio Pavesi - IREA CNR,
Monica Pepe - IREA CNR,
Alessandro Sarretta - ISMAR CNR,
Paolo Tagliolato - IREA CNR,
Andrea Vianello - ISMAR CNR,
Paola Carrara - IREA CNR

# Support contact
For support or suggestions you can use the GitHub Issue Tracker, or email fabio(at)adamassoft.it
