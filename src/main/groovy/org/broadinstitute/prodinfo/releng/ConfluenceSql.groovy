package org.broadinstitute.prodinfo.releng

import groovy.sql.Sql

public class ConfluenceSql {
    def dbconfig

    ConfluenceSql( String fileName) {
        dbconfig = new DBConfig( fileName)
    }

    def getSql () {
        return Sql.newInstance(dbconfig.url, dbconfig.username, dbconfig.password, dbconfig.driverClass)
    }

    List getPlugins() {
        return sql.rows("SELECT * FROM PLUGINDATA")
    }
}