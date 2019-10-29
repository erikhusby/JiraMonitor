package org.broadinstitute.prodinfo.releng

import java.text.DateFormat

public class JiraRankMonitor {

    static void usage() {
        println """

Usage: JiraRankMonitor dbconfigFile rankFile

Reports on the value of the Agile board RANK length.

Appends a record with the timestamp and the current rank length to the rankFile

"""
    }

    public static void main(String[] args) {
        if ( args.length != 2 ) {
            usage()

        } else {
            def jiraSql = new JiraSql( args[0])
            def rankFilename = args[1]

            def rankFile = new File(rankFilename)
            def rankLine = "${DateFormat.instance.format(Calendar.instance.time)}, ${jiraSql.rank}"
            rankFile.append("${System.properties['line.separator']}$rankLine")
        }
    }

}