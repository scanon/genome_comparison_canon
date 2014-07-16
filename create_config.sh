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
if [ ! -f $1/deploy.cfg ]
then
cat > $1/deploy.cfg <<EOF
[genome_comparison]
thread.count=$2
temp.dir=$1/temp
blast.dir=$1/blast/linux
#ws.url=url_to_workspace_service
#ujs.url=url_to_user_job_status_service
EOF
fi
