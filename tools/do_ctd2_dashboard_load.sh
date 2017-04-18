#!/bin/bash

LOAD_OPTS=('t' 'am' 'cl' 'ts' 'cp' 'g' 'p' 'sh' 'si' 'cv' 'o' 'i' 'r')
for opt in "${LOAD_OPTS[@]}"
do
    CMD="time java $JAVA_OPTS -jar $CTD2_HOME/admin/target/dashboard-admin.jar -$opt"
    echo $CMD
    $CMD
    if [[ $? -ne 0 ]] ; then
        echo 'Failed'
        exit 1
    fi
done
exit 0
