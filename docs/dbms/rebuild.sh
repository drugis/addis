#!/bin/bash
PORT=2345
HOST=localhost

PGPASSWORD=develop psql -h $HOST -p $PORT -U postgres -c  "SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'addis'";
PGPASSWORD=develop psql -h $HOST -p $PORT -U postgres -c "DROP DATABASE IF EXISTS addis"
psql -h $HOST -p $PORT -U addis -d postgres -c "CREATE DATABASE addis ENCODING 'utf-8' OWNER addis"
psql -h $HOST -p $PORT -U addis -f structure.sql
#psql -h $HOST -p $PORT -U addis -f diabetes.sql


