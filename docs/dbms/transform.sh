#!/bin/bash

./saxon-xslt -o $1.sql \
  -s:../../application/src/main/resources/org/drugis/addis/$1.addis \
  -xsl:transform.xsl \
  projectName="$2" \
  projectDescription="$3"