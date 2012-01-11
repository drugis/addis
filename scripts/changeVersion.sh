#!/bin/bash

for i in pom.xml */pom.xml; do
	cat $i | sed "0,/<version>/s/<version>[^<]*<\/version>/<version>$1<\/version>/" >$i.tmp
	mv $i.tmp $i
done
