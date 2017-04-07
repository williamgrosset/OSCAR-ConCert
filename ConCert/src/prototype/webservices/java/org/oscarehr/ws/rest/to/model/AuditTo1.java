/**
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 */

package org.oscarehr.ws.rest.to.model;

import java.io.Serializable;

public class AuditTo1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serverVersion;
    private String databaseType;
    private String databaseVersion;
    private String jvmVersion;
    private String tomcatVersion;
    private String tomcatXmx;
    private String tomcatXms;
    private String oscarBuild;
    private String oscarBuildDate;
    private String oscarHL7TEXT_LABS;
    private String oscarSINGLE_PAGE_CHART;
    private String oscarTMP_DIR;
    private String oscarDrugref_url;
    private String drugrefDatabaseUser;
    private String drugrefDatabaseUrl;
    private String drugrefDatabaseDriver;

    public String getServerVersion() {
        return serverVersion;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public String getTomcatVersion() {
        return tomcatVersion;
    }

    public String getTomcatXmx() {
        return tomcatXmx;
    }

    public String getTomcatXms() {
        return tomcatXms;
    }

    public String getOscarBuild() {
        return oscarBuild;
    }

    public String getOscarBuildDate() {
        return oscarBuildDate;
    }

    public String getOscarHL7TEXT_LABS() {
        return oscarHL7TEXT_LABS;
    }

    public String getOscarSINGLE_PAGE_CHART() {
        return oscarSINGLE_PAGE_CHART;
    }

    public String getOscarTMP_DIR() {
        return oscarTMP_DIR;
    }
    
    public String getOscarDrugref_url() {
        return oscarDrugref_url;
    }

    public String getDrugrefDatabaseUser() {
        return drugrefDatabaseUser;
    }

    public String getDrugrefDatabaseUrl() {
        return drugrefDatabaseUrl; 
    }

    public String getDrugrefDatabaseDriver() {
        return drugrefDatabaseDriver;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public void setTomcatVersion(String tomcatVersion) {
        this.tomcatVersion = tomcatVersion;
    }

    public void setTomcatXmx(String tomcatXmx) {
        this.tomcatXmx = tomcatXmx;
    }

    public void setTomcatXms(String tomcatXms) {
        this.tomcatXms = tomcatXms;
    }

    public void setOscarBuild(String oscarBuild) {
        this.oscarBuild = oscarBuild;
    }

    public void setOscarBuildDate(String oscarBuildDate) {
        this.oscarBuildDate = oscarBuildDate;
    }

    public void setOscarHL7TEXT_LABS(String oscarHL7TEXT_LABS) {
        this.oscarHL7TEXT_LABS = oscarHL7TEXT_LABS;
    }

    public void setOscarSINGLE_PAGE_CHART(String oscarSINGLE_PAGE_CHART) {
        this.oscarSINGLE_PAGE_CHART = oscarSINGLE_PAGE_CHART;
    }

    public void setOscarTMP_DIR(String oscarTMP_DIR) {
        this.oscarTMP_DIR = oscarTMP_DIR;
    }

    public void setOscarDrugref_url(String oscarDrugref_url) {
        this.oscarDrugref_url = oscarDrugref_url;
    }

    public void setDrugrefDatabaseUser(String drugrefDatabaseUser) {
        this.drugrefDatabaseUser = drugrefDatabaseUser;
    }

    public void setDrugrefDatabaseUrl(String drugrefDatabaseUrl) {
        this.drugrefDatabaseUrl = drugrefDatabaseUrl;
    }

    public void setDrugrefDatabaseDriver(String drugrefDatabaseDriver) {
        this.drugrefDatabaseDriver = drugrefDatabaseDriver;
    }
}
