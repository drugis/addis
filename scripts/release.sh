#!/bin/bash

ANT_LIB=~/.ant/lib
if [ ! -e $ANT_LIB/maven-ant-tasks*.jar ]; then
	echo "Missing maven ant library, please install it to $ANT_LIB.";
	echo "Get it from https://drugis.org/files/maven-ant-tasks-2.1.3.jar";
	exit;
fi

echo '---- Getting version from Maven'
mvn compile

VERSION=`cat version`
rm version
README=README.md
DIR=addis-$VERSION
GFX=application/src/main/resources/org/drugis/addis/gfx

if [ "$VERSION" = '' ]; then
	echo '!!!! Error: could not get version';
	exit;
else
	echo "---- Version: $VERSION"
fi

if [[ "$VERSION" == *-SNAPSHOT ]]; then
	echo '!!!! Not packaging -SNAPSHOT';
	exit;
fi;

if grep -q $VERSION: $README
then
  echo "---- README up-to-date"
else 
  echo "!!!! Could not find version $VERSION in README.md, please update appropriately"
  exit
fi

# Create header.png for current version
echo '---- Generating header.png'
(cat graphics/header.scm; echo "(addis-version-header \"ADDIS v $VERSION\" \"graphics/header.xcf\" \"$GFX/header.png\")"; echo '(gimp-quit 0)') | gimp -i -b -
#create readme.html for pretty-printed display in installer
echo "<html><head><style type="text/css">h1{font-size:20pt;}</style> <title>README for ADDIS</title></head><body>" > installer/src/izpack/README.html
markdown README.md >> installer/src/izpack/README.html 
echo "</body></html>" >> installer/src/izpack/README.html 


# Package to be able to run CopyrightInfo
mvn package -Dmaven.test.skip=true -q|| exit

# Add license to all files
echo '---- Putting license on all sources'
ant license || exit

# Package ADDIS
echo '---- Building JAR'
mvn clean package -q|| exit

mkdir $DIR

cp application/target/addis-$VERSION-jar-with-dependencies.jar $DIR/addis-$VERSION.jar
chmod a+x $DIR/addis-$VERSION.jar
cp LICENSE.txt $DIR
cp README.md $DIR/README.txt

zip -r addis-$VERSION.zip $DIR
cp installer/target/addis-$VERSION-installer.jar .
chmod a+x ./addis-$VERSION-installer.jar
