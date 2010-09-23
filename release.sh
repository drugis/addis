#!/bin/bash

VERSION=$1
DIR=addis-$VERSION
#DATA=$DIR/data

mkdir $DIR
#mkdir -p $DATA

cp target/addis-$VERSION-jar-with-dependencies.jar $DIR/addis-$VERSION.jar
chmod a+x $DIR/addis-$VERSION.jar
cp LICENSE.txt $DIR
cp README.txt $DIR
#cp hansen.xml $DATA
#cp hansen-analyses.xml $DATA 

zip -r addis-$VERSION.zip $DIR
