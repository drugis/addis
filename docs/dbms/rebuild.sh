#!/bin/bash
PORT=2345
HOST=localhost

PGPASSWORD=develop psql -h $HOST -p $PORT -U postgres -c  "SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'addis'";
PGPASSWORD=develop psql -h $HOST -p $PORT -U postgres -c "DROP DATABASE IF EXISTS addis"
psql -h $HOST -p $PORT -U addis -d postgres -c "CREATE DATABASE addis ENCODING 'utf-8' OWNER addis"
psql -h $HOST -p $PORT -U addis -f structure.sql

echo "Adding example files" 
./transform.sh "depressionExample" "Hansen 2005" "Depression dataset based on the Hansen et al. (2005) systematic review"
psql -h $HOST -p $PORT -U addis -f depressionExample.sql

./transform.sh "hypertensionExample" "Edarbi EPAR" "Hypertension dataset based on the Edarbi EPAR"
psql -h $HOST -p $PORT -U addis -f hypertensionExample.sql

#echo "Updating documentation" 
#ssh -f -n addis@$HOST -p $PORT "nohup postgresql_autodoc"
#scp -P $PORT addis@$HOST:~/addis.\* documentation/
#dot -Tpdf documentation/addis.dot -o documentation/addis.pdf
