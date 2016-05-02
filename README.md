# EDI-NG_server
This is the server side for [SP7-Ritmare/EDI-NG_client](https://github.com/SP7-Ritmare/EDI-NG_client).

It's a [Spring](http://spring.io) Boot application configured to package as a WAR file, so as to allow deployment to, e.g., [Tomcat](http://tomcat.apache.org).
In order to deploy to Tomcat, you need to place the WAR file in Tomcat's webapps directory, or upload it via the Tomcat manager application.

[Binary WAR file](https://github.com/SP7-Ritmare/EDI-NG_server/releases/download/v1.0/edi.war) is available via the "releases" tab.
The WAR has been tested on Tomcat 7.

> You will probably not need the pre-packaged WAR, anyway, because it points to our own installation of the PostgreSQL database.
> You should edit your copy (https://github.com/SP7-Ritmare/EDI-NG_server/blob/master/EDI-T/src/main/resources/application.properties) file and change the spring.datasource.* entries to point to your own PostgreSQL installation.
> **Warning**: deploying the WAR file will create all necessary tables, but it WILL NOT create the database.

If you want to build the latest release from sources, you can user [Apache Maven](https://maven.apache.org/index.html) (Apache License v2.0). In the EDI-T directory, simply run:
```bash
mvn package
```
This will create your WAR file in the target directory, ready for deployment.

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
