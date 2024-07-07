#!/bin/bash
set -e
# Create PostGIS extension
psql --username "$DB_USER" --dbname "$DB_NAME" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS postgis;
    CREATE EXTENSION IF NOT EXISTS postgis_topology;
EOSQL