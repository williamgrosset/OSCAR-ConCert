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
* Class that handles access to auditing information via the REST API.
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
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        try {
            model = this.auditManager.audit(tomcatVersion, webAppName);

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved all auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve all auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }

    @GET
    @Path("/serverInfo")
    @Produces("application/json")
    public AuditResponse getAuditServerInfo() {
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        try {
            model = this.auditManager.auditServer();

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved server auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve server auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }

    @GET
    @Path("/databaseInfo")
    @Produces("application/json")
    public AuditResponse getAuditDatabaseInfo() {
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        try {
            model = this.auditManager.auditDatabase();

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved database auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve database auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }

    @GET
    @Path("/tomcatInfo")
    @Produces("application/json")
    public AuditResponse getAuditTomcatInfo() {
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();

        try {
            model = this.auditManager.auditTomcat(tomcatVersion);

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved Tomcat auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve Tomcat auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }

    @GET
    @Path("/oscarInfo")
    @Produces("application/json")
    public AuditResponse getAuditOscarInfo() {
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        try {
            model = this.auditManager.auditProperties(tomcatVersion, webAppName, "oscar");

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved OSCAR auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve OSCAR auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }

    @GET
    @Path("/drugrefInfo")
    @Produces("application/json")
    public AuditResponse getAuditDrugrefInfo() {
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        try {
            model = this.auditManager.auditProperties(tomcatVersion, webAppName, "drugref");

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved Drugref auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve Drugref auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }
}
