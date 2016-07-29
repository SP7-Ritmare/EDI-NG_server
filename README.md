# EDI-NG_server
This is the server side for [SP7-Ritmare/EDI-NG_client](https://github.com/SP7-Ritmare/EDI-NG_client).

It's a [Spring](http://spring.io) Boot application configured to package as a JAR file, relying on any [JPA](https://en.wikipedia.org/wiki/Java_Persistence_API) database connector, e.g. one to a [PostgreSQL](https://www.postgresql.org) database server.

A [demo JAR file](https://github.com/SP7-Ritmare/EDI-NG_server/releases/download/v1.2/edi.zip) is available in the [demo](https://github.com/SP7-Ritmare/EDI-NG_server/tree/master/demo) directory.

> The pre-packaged JAR file can be a good starting point to test the system in a "standalone" mode.

> It contains a built-in version of EDI-NG_client where the templates point to the EDI-NG_server packaged within.

> **WARNING!** Demo configuration uses the [H2 in-memory database](http://www.h2database.com/html/main.html) so as to simplify EDI's configuration. **As it is an in-memory database, data won't survive a restart of the application**.

All you need to do is open a terminal, go to the demo directory you downloaded, and run

```bash
./edi.sh
```

Then just point your browser to http://localhost:8080/

## Building your own JAR file
If you want to build the latest release from sources, you can user [Apache Maven](https://maven.apache.org/index.html) (Apache License v2.0). In the EDI-T directory, simply run:
```bash
mvn package
```
This will create your JAR file in the target directory, ready for deployment.

As an alternative, you can go to the "demo" directory and run:
```bash
./create_demo.sh
```

This will update the JAR in the demo directory and launch it with the local application.properties file as a configuration.

Prior to creating the JAR file you might want to customise the application.properties file, mentioned above, so as to match your database desired location.

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
