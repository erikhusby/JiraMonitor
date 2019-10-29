//
// Generated from archetype; please customize.
//

package org.broadinstitute.prodinfo.releng

import groovy.sql.Sql

class JiraSql
{

    def dbconfig

    JiraSql( String fileName) {
        dbconfig = new DBConfig( fileName)
    }

    def getSql () {
        return Sql.newInstance(dbconfig.url, dbconfig.username, dbconfig.password, dbconfig.driverClass)
    }

    int getRank() {
        def row = sql.firstRow("SELECT FIELD_ID,  MAX(LENGTH('RANK')) AS max_rank_string_length FROM AO_60DB71_LEXORANK GROUP BY FIELD_ID")
        return row.max_rank_string_length
    }

    List getPlugins() {
        return sql.rows("SELECT * FROM PLUGINVERSION")
    }

    def checkRank( int upperBound ) {

        return rank < upperBound
    }

}
