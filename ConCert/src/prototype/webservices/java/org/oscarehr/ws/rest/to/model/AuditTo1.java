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

/*
*  Class that represents the JSON object fields, which will be returned by 
*  AuditManager function calls.
*
*  github.com/williamgrosset
*/
public class AuditTo1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timestamp;
    private String serverVersion;
    private String dbType;
    private String dbVersion;
    private String jvmVersion;
    private String tomcatVersion;
    private String xmx;
    private String xms;
    private String build;
    private String buildDate;
    private String hl7TextLabs;
    private String singlePageChart;
    private String tmpDir;
    private String drugrefUrl;
    private String dbUser;
    private String dbUrl;
    private String dbDriver;

    public String getTimestamp() {
        return timestamp;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getDbType() {
        return dbType;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public String getTomcatVersion() {
        return tomcatVersion;
    }

    public String getXmx() {
        return xmx;
    }

    public String getXms() {
        return xms;
    }

    public String getBuild() {
        return build;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getHl7TextLabs() {
        return hl7TextLabs;
    }

    public String getSinglePageChart() {
        return singlePageChart;
    }

    public String getTmpDir() {
        return tmpDir;
    }
    
    public String getDrugrefUrl() {
        return drugrefUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbUrl() {
        return dbUrl; 
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public void setTomcatVersion(String tomcatVersion) {
        this.tomcatVersion = tomcatVersion;
    }

    public void setXmx(String xmx) {
        this.xmx = xmx;
    }

    public void setXms(String xms) {
        this.xms = xms;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public void setHl7TextLabs(String hl7TextLabs) {
        this.hl7TextLabs = hl7TextLabs;
    }

    public void setSinglePageChart(String singlePageChart) {
        this.singlePageChart = singlePageChart;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public void setDrugrefUrl(String drugrefUrl) {
        this.drugrefUrl = drugrefUrl;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }
}
