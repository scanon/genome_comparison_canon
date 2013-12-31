#!/bin/bash
if (( $# != 2 ))
then
    echo "Usage: create_config <service_dir> <threadpool_size>"
    exit
fi
if [ ! -f $1/config.props ]
then
cat > $1/config.props <<EOF
thread.count=$2
temp.dir=$1
#blast.dir=/path/to/blast/bin/    #PATH is used when this parameter is not defined
EOF
fi
