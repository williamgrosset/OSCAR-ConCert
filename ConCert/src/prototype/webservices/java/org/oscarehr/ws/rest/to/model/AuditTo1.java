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

    private String server_version;
    private String db_type;
    private String db_version;
    private String jvm_version;
    private String tomcat_version;
    private String xmx;
    private String xms;
    private String build;
    private String build_date;
    private String HL7TEXT_LABS;
    private String SINGLE_PAGE_CHART;
    private String TMP_DIR;
    private String drugref_url;
    private String db_user;
    private String db_url;
    private String db_driver;

    public String getServerVersion() {
        return server_version;
    }

    public String getDbType() {
        return db_type;
    }

    public String getDbVersion() {
        return db_version;
    }

    public String getJvmVersion() {
        return jvm_version;
    }

    public String getTomcatVersion() {
        return tomcat_version;
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
        return build_date;
    }

    public String getHL7TEXT_LABS() {
        return HL7TEXT_LABS;
    }

    public String getSINGLE_PAGE_CHART() {
        return SINGLE_PAGE_CHART;
    }

    public String getTMP_DIR() {
        return TMP_DIR;
    }
    
    public String getDrugref_url() {
        return drugref_url;
    }

    public String getDbUser() {
        return db_user;
    }

    public String getDbUrl() {
        return db_url; 
    }

    public String getDbDriver() {
        return db_driver;
    }

    public void setServerVersion(String server_version) {
        this.server_version = server_version;
    }

    public void setDbType(String db_type) {
        this.db_type = db_type;
    }

    public void setDbVersion(String db_version) {
        this.db_version = db_version;
    }

    public void setJvmVersion(String jvm_version) {
        this.jvm_version = jvm_version;
    }

    public void setTomcatVersion(String tomcat_version) {
        this.tomcat_version = tomcat_version;
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

    public void setBuildDate(String build_date) {
        this.build_date = build_date;
    }

    public void setHL7TEXT_LABS(String HL7TEXT_LABS) {
        this.HL7TEXT_LABS = HL7TEXT_LABS;
    }

    public void setSINGLE_PAGE_CHART(String SINGLE_PAGE_CHART) {
        this.SINGLE_PAGE_CHART = SINGLE_PAGE_CHART;
    }

    public void setTMP_DIR(String TMP_DIR) {
        this.TMP_DIR = TMP_DIR;
    }

    public void setDrugref_url(String drugref_url) {
        this.drugref_url = drugref_url;
    }

    public void setDbUser(String db_user) {
        this.db_user = db_user;
    }

    public void setDbUrl(String db_url) {
        this.db_url = db_url;
    }

    public void setDbDriver(String db_driver) {
        this.db_driver = db_driver;
    }
}
