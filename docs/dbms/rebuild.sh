#!/bin/bash
DB_PORT=2345
SSH_PORT=2222
HOST=localhost

PGPASSWORD=develop psql -h $HOST -p $DB_PORT -U postgres -c  "SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'addis'";
PGPASSWORD=develop psql -h $HOST -p $DB_PORT -U postgres -c "DROP DATABASE IF EXISTS addis"
psql -h $HOST -p $DB_PORT -U addis -d postgres -c "CREATE DATABASE addis ENCODING 'utf-8' OWNER addis"
psql -h $HOST -p $DB_PORT -U addis -f structure.sql

echo "Updating documentation"
ssh addis@$HOST -p $SSH_PORT "postgresql_autodoc";
scp -P $SSH_PORT addis@$HOST:~/addis.\* documentation/;
dot -Tpdf documentation/addis.dot -o documentation/addis.pdf;

echo "Adding example files"
./transform.sh "depressionExample" "Hansen 2005" "Depression dataset based on the Hansen et al. (2005) systematic review"
psql -h $HOST -p $DB_PORT -U addis -f depressionExample.sql

./transform.sh "hypertensionExample" "Edarbi EPAR" "Hypertension dataset based on the Edarbi EPAR"
psql -h $HOST -p $DB_PORT -U addis -f hypertensionExample.sql


