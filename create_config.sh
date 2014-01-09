#!/bin/bash
if (( $# != 2 ))
then
    echo "Usage: create_config <service_dir> <threadpool_size>"
    exit
fi
if [ ! -d $1/blast ]
then
cp -r ./blast $1
fi
mkdir $1/temp
if [ ! -f $1/config.props ]
then
cat > $1/config.props <<EOF
thread.count=$2
temp.dir=$1/temp
blast.dir=$1/blast/linux    #PATH is used when this parameter is not defined
EOF
fi
