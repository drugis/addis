#!/bin/bash

VERSION=$1
DIR=addis-$VERSION
GFX=src/main/resources/org/drugis/addis/gfx
#DATA=$DIR/data

if [ "$VERSION" = '' ]; then
	echo 'Please specify the version on the command line';
	exit;
fi

# Create header.png for current version
echo '---- Generating header.png'
(cat graphics/header.scm; echo "(addis-version-header \"ADDIS v $VERSION\" \"graphics/header.xcf\" \"$GFX/header.png\")"; echo '(gimp-quit 0)') | gimp -i -b -

# Add license to all files
echo '---- Putting license on all sources'
ant license

# Package ADDIS
echo '---- Building JAR'
mvn package -Dmaven.test.skip

mkdir $DIR
#mkdir -p $DATA

cp target/addis-$VERSION-jar-with-dependencies.jar $DIR/addis-$VERSION.jar
chmod a+x $DIR/addis-$VERSION.jar
cp LICENSE.txt $DIR
cp README.txt $DIR
#cp hansen.xml $DATA
#cp hansen-analyses.xml $DATA 

zip -r addis-$VERSION.zip $DIR
