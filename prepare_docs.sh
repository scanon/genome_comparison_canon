#!/bin/bash

CURRENT_DIR="$(pwd)"
DOCS_DIR=$CURRENT_DIR/docs
SRC_DIR=./src
JARS_DIR=../jars/lib/jars
JARS=$JARS_DIR/kbase/auth/kbase-auth-1380919426-d35c17d.jar:$JARS_DIR/servlet/servlet-api-2.5.jar:$JARS_DIR/jackson/jackson-annotations-2.2.3.jar:$JARS_DIR/jackson/jackson-core-2.2.3.jar:$JARS_DIR/jackson/jackson-databind-2.2.3.jar:$JARS_DIR/jetty/jetty-all-7.0.0.jar:$JARS_DIR/syslog4j/syslog4j-0.9.46.jar:$JARS_DIR/ini4j/ini4j-0.5.2.jar:$JARS_DIR/apache_commons/commons-io-2.4.jar:$JARS_DIR/apache_commons/commons-fileupload-1.2.2.jar:$JARS_DIR/kbase/common/kbase-common-0.0.3.jar

test -d "$DOCS_DIR" || mkdir "$DOCS_DIR"

javadoc -d $DOCS_DIR -sourcepath $SRC_DIR -classpath $JARS us.kbase.genomecomparison
