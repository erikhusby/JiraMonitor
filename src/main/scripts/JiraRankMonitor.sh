#!/usr/bin/env bash
#
# Run the JiraRankMonitor
#
#
usage() {
    cat <<EOF
Usage: $0 dbconfigFile rankFile

Reports on the value of the Agile board RANK length.

Appends a record with the timestamp and the current rank length to the rankFile
EOF

}

if [ "$#" -eq 2 ]
then

    java -cp `dirname $0`/${project.build.finalName}.jar org.broadinstitute.prodinfo.releng.JiraRankMonitor $@

else
    usage
    exit 1
fi