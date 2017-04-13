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
*  Class that represents the JSON object fields for the /systemInfo API request,
*  which will be returned by the AuditManager function call: auditSystem().
*
*  github.com/williamgrosset
*/
public class AuditSystemTo1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timestamp;
    private String systemVersion;
    private String jvmVersion;

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getSystemVersion() {
        return this.systemVersion;
    }

    public String getJvmVersion() {
        return this.jvmVersion;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }
}
