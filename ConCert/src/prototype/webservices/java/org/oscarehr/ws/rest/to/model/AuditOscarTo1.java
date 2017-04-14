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
*  Class that represents the JSON object fields for the /oscarInfo API request,
*  which will be returned by the AuditManager function call: auditOscar().
*
*  github.com/williamgrosset
*/
public class AuditOscarTo1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timestamp;
    private String webAppName;
    private String build;
    private String buildDate;
    private String hl7TextLabs;
    private String singlePageChart;
    private String tmpDir;
    private String drugrefUrl;

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getWebAppName() {
        return this.webAppName;
    }

    public String getBuild() {
        return this.build;
    }

    public String getBuildDate() {
        return this.buildDate;
    }

    public String getHl7TextLabs() {
        return this.hl7TextLabs;
    }

    public String getSinglePageChart() {
        return this.singlePageChart;
    }

    public String getTmpDir() {
        return this.tmpDir;
    }

    public String getDrugrefUrl() {
        return this.drugrefUrl;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setWebAppName(String webAppName) {
        this.webAppName = webAppName;
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
}
