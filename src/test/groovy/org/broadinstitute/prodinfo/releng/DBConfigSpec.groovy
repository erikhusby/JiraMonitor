package org.broadinstitute.prodinfo.releng

import spock.lang.Specification


/**
 * @author mhusby
 * Date: 4/7/15
 * Time: 3:38 PM
 *
 */
class DBConfigSpec extends Specification {
    def lojDBConfig

    def setup() {
        lojDBConfig = new DBConfig( "target/test-classes/dbconfigLOJ.xml").load()
    }

    def "Process DBConfig"() {
        expect:
            assert lojDBConfig.username.text() == "labopsjira7"
    }
}