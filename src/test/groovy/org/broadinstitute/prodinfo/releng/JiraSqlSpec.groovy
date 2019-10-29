package org.broadinstitute.prodinfo.releng

import spock.lang.*

public class JiraSqlSpec extends Specification {

    def setup() {
        jiraSql = new JiraSql("target/test-classes/dbconfigLOJ.xml")
    }

    def jiraSql

    def "Fetch Rank" () {
        expect:
            assert  jiraSql.rank > 0
    }

    def "Check Rank against upperBound" () {
        expect:
            assert jiraSql.checkRank(upperBound) == result

        where:
            upperBound  | result
            0           | false
            9           | true
            20          | true

    }

    def "Get list of plugins"() {
        expect:
            assert jiraSql.plugins.size() > 0
    }
}