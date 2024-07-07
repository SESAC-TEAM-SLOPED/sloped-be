FROM postgres:latest
RUN apt-get update && apt-get install -y postgis
#COPY initdb-postgis.sh /docker-entrypoint-initdb.d/
#RUN echo "CREATE EXTENSION IF NOT EXISTS postgis;" > /docker-entrypoint-initdb.d/init.sql