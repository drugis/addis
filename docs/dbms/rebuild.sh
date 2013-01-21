#!/bin/bash
PORT=2345
HOST=localhost

PGPASSWORD=develop psql -h $HOST -p $PORT -U postgres -c  "SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'addis'";
PGPASSWORD=develop psql -h $HOST -p $PORT -U postgres -c "DROP DATABASE IF EXISTS addis"
psql -h $HOST -p $PORT -U addis -d postgres -c "CREATE DATABASE addis ENCODING 'utf-8' OWNER addis"
psql -h $HOST -p $PORT -U addis -f structure.sql

./transform.sh "depressionExample" "Hansen 2005" "Depression dataset based on the Hansen et al. (2005) systematic review"
psql -h $HOST -p $PORT -U addis -f depressionExample.sql

./transform.sh "hypertensionExample" "Edarbi EPAR" "Hypertension dataset based on the Edarbi EPAR"
psql -h $HOST -p $PORT -U addis -f hypertensionExample.sql
