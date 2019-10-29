package org.broadinstitute.prodinfo.releng

public class DBConfig {

    DBConfig ( String fileName) {
        jdc = new XmlParser().parse(new File(fileName))
    }

    def jdc

    def load() {
        return jdc."jdbc-datasource"
    }

    def getUrl() {
        return jdc."jdbc-datasource".url.text()
    }

    def getUsername() {
        return jdc."jdbc-datasource".username.text()
    }

    def getPassword() {
        return jdc."jdbc-datasource".password.text()
    }

    def getDriverClass() {
        return jdc."jdbc-datasource"."driver-class".text()
    }
}