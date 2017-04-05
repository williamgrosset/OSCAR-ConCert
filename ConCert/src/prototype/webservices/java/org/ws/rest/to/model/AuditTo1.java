package org.oscarehr.ws.rest.to.model;

import java.io.Serializable;

public class AuditTo1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serverVersion;
    private String databaseInfo;
    private String jvmVersion;
    private String tomcatVersion;
    private String memoryAllocation;
    private String oscarBuild;
    private String oscarProperties;
    private String drugrefProperties;

    public String getServerVersion() {
        return serverVersion;
    }

    public String getDatabaseInfo() {
        return databaseInfo;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public String getTomcatVersion() {
        return tomcatVersion;
    }

    public String getMemoryAllocation() {
        return memoryAllocation;
    }

    public String getOscarBuild() {
        return oscarBuild;
    }

    public String getOscarProperties() {
        return oscarProperties;
    }

    public String getDrugrefProperties() {
        return drugrefProperties;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setDatabaseInfo(String databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public void setTomcatVersion(String tomcatVersion) {
        this.tomcatVersion = tomcatVersion;
    }

    public void setMemoryAllocation(String memoryAllocation) {
        this.memoryAllocation = memoryAllocation;
    }

    public void setOscarBuild(String oscarBuild) {
        this.oscarBuild = oscarBuild;
    }

    public void setOscarProperties(String oscarProperties) {
        this.oscarProperties = oscarProperties;
    }

    public void setDrugrefProperties(String drugrefProperties) {
        this.drugrefProperties = drugrefProperties;
    }
}
