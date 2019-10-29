package org.broadinstitute.prodinfo.releng

public class ConfluencePluginComparison {

    def usage() {
        println """
Compare the list of plugins in two different Confluence databases.

Usage:  ConfluencePluginComparison.sh Confluence1Name dbconfig1.xml Confluence2Name dbconfig2.xml

Where the two dbconfig files provide the DB configuration information.

"""
    }

    public static void main(String[] args) {
        def confluence1Name = args[0]
        def confluence1 = new ConfluenceSql(args[1])
        def confluence2Name = args[2]
        def confluence2 = new ConfluenceSql(args[3])

        def confluence1Plugins = confluence1.plugins.sort{ a, b -> a.pluginkey  <=> b.pluginkey }
        def confluence2Plugins = confluence2.plugins.sort{ a, b -> a.pluginkey  <=> b.pluginkey }

        def samePlugins = []
        def mismatchedVersions = []
        def confluence1Only = []
        def confluence2Only = []

        // Exclude plugins from:
        // jira.*
        // com.atlassian.*
        // crowd*
        // tac.*
        def excludeRegex =  /com\.atlassian\..*|crowd.*|confluence\..*|tac\..*/

        confluence1Plugins.each { plugin1 ->

            if ( ! (plugin1.pluginkey ==~ excludeRegex) ) {
                def plugin2 = confluence2Plugins.find { j2 -> plugin1.pluginkey == j2.pluginkey }
                if (plugin2) {
                    if (pluginVersion(plugin1.filename) == pluginVersion(plugin2.filename) ) {
                        samePlugins << plugin1
                    } else {
                        mismatchedVersions << [plugin1, plugin2]
                    }
                } else {
                    confluence1Only << plugin1
                }
            }
        }

        confluence2Plugins.each { plugin2 ->
            if ( ! ( plugin2.pluginkey ==~ excludeRegex ) ) {
                def plugin1 = confluence1Plugins.find { j1 -> j1.pluginkey == plugin2.pluginkey }
                if (plugin1 == null ) {
                    confluence2Only << plugin2
                }
            }
        }

        println "\nMismatched versions: $confluence1Name -- $confluence2Name"
        mismatchedVersions.each { p ->
            println "\t${p[0].pluginkey}, version ${pluginVersion(p[0].filename)} -- version ${pluginVersion(p[1].filename)}"
        }
        println "\n$confluence1Name Only:"
        confluence1Only.each { p ->
            println "\t${p.pluginkey}, version ${pluginVersion(p.filename)}"
        }
        println "\n$confluence2Name Only:"
        confluence2Only.each { p ->
            println "\t${p.pluginkey}, version ${pluginVersion(p.filename)}"
        }

        println "\n$confluence1Name All plugins:"
        confluence1Plugins.each { p ->
            println "\t${p.pluginkey}, version ${pluginVersion(p.filename)}"
        }
        println "\n$confluence2Name All plugins:"
        confluence2Plugins.each { p ->
            println "\t${p.pluginkey}, version ${pluginVersion(p.filename)}"
        }
    }

    static def pluginVersion( String filename) {
        def ep = /plugin\.\d+\.(.*)/
        def vp = /.*-(\d.*).jar/
        def version
        def matcher
        def jar
        if (filename ==~ ep) {
            matcher = filename =~ ep
            jar = matcher[0][1]
        } else {
            jar = filename
        }
        if ( jar ==~ vp ) {
            matcher = jar =~ vp
            version = matcher[0][1]
        } else {
            version = "unknown"
        }
        return version
    }
}