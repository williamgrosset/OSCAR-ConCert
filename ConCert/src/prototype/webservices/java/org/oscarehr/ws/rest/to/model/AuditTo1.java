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
