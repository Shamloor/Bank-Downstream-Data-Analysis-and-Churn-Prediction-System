#/bin/bash
if (( $# == 0 ));then
    echo "Usage xRun.sh \"<COMMAND>\""
    exit 0
fi
for node in niit01 niit02 niit03
do
    echo "======== $node ========"
    echo "ssh $node $1"
    ssh $node "source /etc/profile;$1"
done
