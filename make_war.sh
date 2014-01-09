#!/bin/bash

if (( $# != 2 ))
then
    echo "Usage: make_war <service_dir> <lib_jars_dir>"
    exit
fi

CONFIG_FILE=$1/config.props
DATE=$(date +"%Y%m%d%H%M")
WAR_DIR=./war_$DATE
WEB_INF_DIR=$WAR_DIR/WEB-INF
CURRENT_DIR="$(pwd)"
DIST_DIR=$CURRENT_DIR/dist
WAR_FILE=$DIST_DIR/service.war
SRC_DIR=./src
JARS=$2/kbase/auth/kbase-auth-1380919426-d35c17d.jar:$2/servlet/servlet-api-2.5.jar:$2/jackson/jackson-annotations-2.2.3.jar:$2/jackson/jackson-core-2.2.3.jar:$2/jackson/jackson-databind-2.2.3.jar:$2/jetty/jetty-all-7.0.0.jar:$2/syslog4j/syslog4j-0.9.46.jar:$2/ini4j/ini4j-0.5.2.jar
CLASSPATH="-classpath $JARS"
CLASSES_DIR=$WEB_INF_DIR/classes
LIB_DIR=$WEB_INF_DIR/lib

test -d "$DIST_DIR" || mkdir "$DIST_DIR"

mkdir $WAR_DIR
mkdir $WEB_INF_DIR
mkdir $CLASSES_DIR
mkdir $LIB_DIR

javac -sourcepath $SRC_DIR $CLASSPATH -d $CLASSES_DIR -g $SRC_DIR/us/kbase/genomecomparison/*.java

arr=$(echo $JARS | tr ":" "\n")
for x in $arr
do
    cp $x $LIB_DIR/
done

cat > $WEB_INF_DIR/web.xml <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<web-app>
    <servlet>
        <servlet-name>JsonRpcServlet</servlet-name>
        <servlet-class>us.kbase.genomecomparison.GenomeComparisonServer</servlet-class>
        <init-param>
            <param-name>config_file</param-name> 
            <param-value>$CONFIG_FILE</param-value> 
        </init-param>
    </servlet>
    <servlet>
        <servlet-name>ImageServlet</servlet-name>
        <servlet-class>us.kbase.genomecomparison.ComparisonImage</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>JsonRpcServlet</servlet-name>
        <url-pattern>/jsonrpc</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>ImageServlet</servlet-name>
        <url-pattern>/image</url-pattern>
    </servlet-mapping>
</web-app>
EOF

cp ./web/* $WAR_DIR

if [ -f $WAR_FILE ]
then
    rm $WAR_FILE
fi
cd $WAR_DIR
zip -r $WAR_FILE *
cd ..
rm -rf $WAR_DIR

#SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
#. $SCRIPT_DIR/glassfish_deploy_war.sh $CURRENT_DIR/$WAR_FILE $TARGET_PORT
