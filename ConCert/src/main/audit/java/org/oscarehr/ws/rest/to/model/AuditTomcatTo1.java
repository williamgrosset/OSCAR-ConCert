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
*  Class that represents the JSON object fields for the /tomcatInfo API request,
*  which will be returned by the AuditManager function call: auditTomcat().
*
*  Author: github.com/williamgrosset
*/
public class AuditTomcatTo1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String timestamp;
    private String tomcatVersion;
    private String xmx;
    private String xms;

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getTomcatVersion() {
        return this.tomcatVersion;
    }

    public String getXmx() {
        return this.xmx;
    }

    public String getXms() {
        return this.xms;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
}
