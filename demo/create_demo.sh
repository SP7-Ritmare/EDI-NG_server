#!/bin/bash

cd ../EDI-T
mvn clean package && cp target/EDI-T-0.0.1-SNAPSHOT.jar ../demo/edi.jar && cd ../demo && ./edi.sh
