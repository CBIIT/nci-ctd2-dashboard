#!/bin/bash

submissions_dir=$CTD2_DATA_HOME/submissions
cd_cmd="cd $submissions_dir"
echo $cd_cmd
$cd_cmd
for file in *; do
    if [[ -d $file ]]; then
        submission_dir=$file
        zip_file="${submission_dir}.zip"
        if [[ -f $zip_file ]]; then
            rm_cmd="rm -f $zip_file"
            echo $rm_cmd
            $rm_cmd
        fi
        zip_cmd="zip -rq $zip_file $submission_dir"
        echo $zip_cmd
        $zip_cmd
    fi
done
