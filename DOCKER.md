# Docker-compose

EDI-NG_server can be easily installed by using the included [docker-compose.yml](https://github.com/SP7-Ritmare/EDI-NG_server/blob/master/docker-compose.yml) file.

```yaml
version: '3.1'

services:

    pg_edi:
        image: postgres
        restart: always
        environment:
            - POSTGRES_PASSWORD=yourpassword
            - POSTGRES_DB=edi
        container_name: pg-edi
    edi-server:
        image: java:8-jdk-alpine
        restart: always
        depends_on:
            - pg_edi
        container_name: edi-server
        restart: always
        ports:
            - 8080:8080
        volumes:
            - ./dockerdemo:/app
        working_dir: /app/
        command: /app/edi.sh
volumes:
        db_data:

```
## Installation process
* ``` git clone ``` this repository to your server
* ``` cd ``` to the directory that has been created
* run ```docker-compose up -d ```
* check for errors by running ```docker-compose logs -f ```

## Customisation
The docker compose file works out of the box, but you might want to customise your setup.
### Postgres password
If you change your password in the docker-compose.yml file, you'll need to do the same for the *spring.datasource.password* setting in the [dockerdemo/application.properties](https://github.com/SP7-Ritmare/EDI-NG_server/blob/master/dockerdemo/application.properties) file.
### Different listening port
If you need to have EDI-NG_server listen on a port other than 8080, you can simply change 
```yaml
      ports:
            - 8080:8080 
```
to
```yaml
      ports:
            - <your port>:8080 
```
## Using EDI-NG_Client templates on your own server
You will need to update your templates so that the <metadataEndpoint> tags point to the public address and port of your EDI-NG_server installation (e.g.: http://my-server:8080/).
