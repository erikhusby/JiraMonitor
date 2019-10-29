package org.broadinstitute.prodinfo.releng

public class JiraPluginComparison {

    def usage() {
        println """
Compare the list of plugins in two different Jira databases.

Usage:  JiraPluginComparison.sh Jira1Name dbconfig1.xml Jira2Name dbconfig2.xml

Where the two dbconfig files provide the DB configuration information.

"""
    }

    public static void main(String[] args) {
        def jira1Name = args[0]
        def jira1 = new JiraSql(args[1])
        def jira2Name = args[2]
        def jira2 = new JiraSql(args[3])

        def jira1Plugins = jira1.plugins.sort{ a, b -> a.pluginname  <=> b.pluginname }
        def jira2Plugins = jira2.plugins.sort{ a, b -> a.pluginname  <=> b.pluginname }

        def samePlugins = []
        def mismatchedVersions = []
        def jira1Only = []
        def jira2Only = []

        // Exclude plugins from:
        // jira.*
        // com.atlassian.*
        // crowd*
        // tac.*
        def excludeRegex =  /com\.atlassian\..*|crowd.*|jira\..*|tac\..*/

        jira1Plugins.each { plugin1 ->

            if ( ! (plugin1.pluginkey ==~ excludeRegex) ) {
                def plugin2 = jira2Plugins.find { j2 -> plugin1.pluginname.equalsIgnoreCase(j2.pluginname) }
                if (plugin2) {
                    if (plugin1.pluginversion == plugin2.pluginversion) {
                        samePlugins << plugin1
                    } else {
                        mismatchedVersions << [plugin1, plugin2]
                    }
                } else {
                    jira1Only << plugin1
                }
            }
        }

        jira2Plugins.each { plugin2 ->
            if ( ! ( plugin2.pluginkey ==~ excludeRegex ) ) {
                def plugin1 = jira1Plugins.find { j1 -> j1.pluginname.equalsIgnoreCase(plugin2.pluginname) }
                if (plugin1 == null ) {
                    jira2Only << plugin2
                }
            }
        }

        println "\nMismatched versions: $jira1Name -- $jira2Name"
        mismatchedVersions.each { p ->
            println "\t${p[0].pluginname}, version ${p[0].pluginversion} -- version ${p[1].pluginversion}"
        }
        println "\n$jira1Name Only:"
        jira1Only.each { p ->
            printPlugin(p)
        }
        println "\n$jira2Name Only:"
        jira2Only.each { p ->
            printPlugin(p)
        }

        println "\n$jira1Name User Added Plugins:"
        jira1Plugins.each { p ->
            if ( ! (p.pluginkey ==~ excludeRegex)) {
                printPlugin(p)
            }
        }
        println "\n$jira1Name All plugins:"
        jira1Plugins.each { p ->
            printPlugin(p)
        }
        println "\n$jira2Name All plugins:"
        jira2Plugins.each { p ->
            printPlugin(p)
        }
    }

    private static printPlugin(p) {
        println "\t${p.pluginname}, version ${p.pluginversion}"
    }
}