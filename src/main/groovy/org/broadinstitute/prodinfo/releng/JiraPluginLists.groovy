package org.broadinstitute.prodinfo.releng

/**
 * @author mhusby
 * Date: 10/17/17
 * Time: 2:49 PM
 *
 */
class JiraPluginLists {

    def usage() {
        println """
Generate a CSV file listing the plugins of 1 or more JIRA instances. 

Usage: JiraPluginLists --jira JiraName --db dbconfig.xml [--jira Jira2Name -db dbconfig2.xml]* --out outputFile

The purpose is to be able to see what plugins are in use and if there are different versions.


"""
    }

    public static void main(String[] args) {


        def cli = new CliBuilder(usage:"JiraPluginLists options",
            footer:"""
Generate a CSV file listing the plugins of 1 or more JIRA instances. 

The purpose is to be able to see what plugins are in use and if there are different versions.

Example:
    JiraPluginLists --jira LabOpsJira --db lojdbconfig.xml --jira LabOpsJiraTest --db lojtdbconfig.xml --out output.csv
    
""")
        cli.with {
            h(longOpt: 'help', 'print this message')
            j(longOpt: 'jira', args: 1, argName: 'jira', required: true, 'The name of the JIRA')
            d(longOpt: 'db', args: 1, argName: 'dbconfig', required: true, 'The dbconfig.xml file')
            o(longOpt: 'out', args: 1, argName: 'outputFile', required: true, 'The name of the output csv file')
        }

        def options = cli.parse(args )
        assert options

        if (options.h) {
            cli.usage()
        }

        def outputFileName = options.out

        def i = 0
        def jiras = []
        options.js.each { jira ->
            println "Gathering plugins for $jira"
            def js = new JiraSql(options.dbs[i])
            def plugins = js.plugins.grep{ !(it.pluginkey ==~ /com\.atlassian\..*|crowd.*|jira\..*|tac\..*/) }.sort{ a, b -> a.pluginname <=> b.pluginname }
            jiras += [name: jira, db: options.dbs[i], plugins:plugins]
            i++
        }

        jiras.each { jira ->

            println "Jira ${jira.name}, using db ${jira.db}, has ${jira.plugins.size} plugins"
        }

        // Generate the list that consists of:
        // Plugin name
        //   map of [ jiraname, pluginversion]
        def pluginRows = [:]

        jiras.eachWithIndex { jira, jj ->
            jira.plugins.each { plugin ->
                // if plugin name found in pluginRows then add this jira's name and plugin version to the row
                if ( ! pluginRows.containsKey(plugin.pluginname)) {
                    pluginRows[plugin.pluginname] = []
                }
                pluginRows[plugin.pluginname][jj] = [jira.name, plugin.pluginversion]

            }
        }

        println "Writing to $outputFileName"
        new File(outputFileName).withPrintWriter { out ->
            out.print "Row, Name "
            jiras.each {
                out.print ",${it.name} "
            }
            out.println " "
            pluginRows.eachWithIndex { key, value, jj ->
                out.print "$jj, ${key.replaceAll(',', ' ')} "
                value.each {
                    if (it ) {
                        out.print ", ${it[1]}"
                    } else {
                        out.print ", - "
                    }
                }
                out.println " "
            }
        }
        println "Done"
    }
}
