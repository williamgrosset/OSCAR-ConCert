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

package org.oscarehr.ws.rest;

import org.oscarehr.util.MiscUtilsOld;
import org.oscarehr.ws.rest.to.AuditResponse;
import org.oscarehr.ws.rest.to.model.AuditTo1;
import org.oscarehr.managers.AuditManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.servlet.http.HttpServletRequest;

/*
*  Class that handles access to auditing information via the REST API.
*  
*  github.com/williamgrosset
*/
@Path("/audit")
@Component("auditService")
public class AuditService extends AbstractServiceImpl {

    private static Logger logger = MiscUtilsOld.getLogger();

    @Autowired
    protected AuditManager auditManager;

    @GET
    @Path("/")
    @Produces("application/json")
    public AuditResponse getAuditInfo() {
        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        return this.getInfo(this.auditManager.audit(tomcatVersion, webAppName), "all");
    }

    @GET
    @Path("/serverInfo")
    @Produces("application/json")
    public AuditResponse getAuditServerInfo() {
        return this.getInfo(this.auditManager.auditServer(), "server");
    }

    @GET
    @Path("/databaseInfo")
    @Produces("application/json")
    public AuditResponse getAuditDatabaseInfo() {
        return this.getInfo(this.auditManager.auditDatabase(), "database");
    }

    @GET
    @Path("/tomcatInfo")
    @Produces("application/json")
    public AuditResponse getAuditTomcatInfo() {
        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();

        return this.getInfo(this.auditManager.auditTomcat(tomcatVersion), "Tomcat");
    }

    @GET
    @Path("/oscarInfo")
    @Produces("application/json")
    public AuditResponse getAuditOscarInfo() {
        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        return this.getInfo(this.auditManager.auditProperties(tomcatVersion, webAppName, "oscar"), "Oscar");
    }

    @GET
    @Path("/drugrefInfo")
    @Produces("application/json")
    public AuditResponse getAuditDrugrefInfo() {
        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        return this.getInfo(this.auditManager.auditProperties(tomcatVersion, webAppName, "drugref"), "Drugref");
    }

    private AuditResponse getInfo(AuditTo1 model, String message) {
        AuditResponse response = new AuditResponse();
        try {
            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved " + message + " auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve " + message + " auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }
}
