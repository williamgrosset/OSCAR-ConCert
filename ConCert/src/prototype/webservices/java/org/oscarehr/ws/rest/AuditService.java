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

import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtilsOld;
import org.oscarehr.ws.rest.to.AuditSystemResponse;
import org.oscarehr.ws.rest.to.AuditDatabaseResponse;
import org.oscarehr.ws.rest.to.AuditTomcatResponse;
import org.oscarehr.ws.rest.to.AuditOscarResponse;
import org.oscarehr.ws.rest.to.AuditDrugrefResponse;
import org.oscarehr.ws.rest.to.model.AuditSystemTo1;
import org.oscarehr.ws.rest.to.model.AuditDatabaseTo1;
import org.oscarehr.ws.rest.to.model.AuditTomcatTo1;
import org.oscarehr.ws.rest.to.model.AuditOscarTo1;
import org.oscarehr.ws.rest.to.model.AuditDrugrefTo1;
import org.oscarehr.managers.AuditManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.servlet.http.HttpServletRequest;

/*
*  Class that handles access to auditing information via the REST API.
*  
*  Author: github.com/williamgrosset
*/
@Path("/audit")
@Component("auditService")
public class AuditService extends AbstractServiceImpl {

    private static Logger logger = MiscUtilsOld.getLogger();

    @Autowired
    protected AuditManager auditManager;

    @Autowired
    protected SecurityInfoManager securityInfoManager;

    /*
    *  Performs an audit check using AuditManager to access the required
    *  system auditing information and returns a JSON object to the client. 
    *  
    *  @return response: An AuditSystemResponse object that represents the
    *                    JSON wrapper for the request.
    */
    @GET
    @Path("/systemInfo")
    @Produces("application/json")
    public AuditSystemResponse getAuditSystemInfo() {
        LoggedInInfo info = getLoggedInInfo();
        this.checkPrivileges(info); 

        AuditSystemResponse response = new AuditSystemResponse();
        AuditSystemTo1 model = new AuditSystemTo1();

        try {
            model = this.auditManager.auditSystem();

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved system auditing information.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve system auditing information.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }

    /*
    *  Performs an audit check using AuditManager to access the required
    *  database auditing information and returns a JSON object to the client. 
    *  
    *  @return response: An AuditDatabaseResponse object that represents the
    *                    JSON wrapper for the request.
    */
    @GET
    @Path("/databaseInfo")
    @Produces("application/json")
    public AuditDatabaseResponse getAuditDatabaseInfo() {
        LoggedInInfo info = getLoggedInInfo();
        this.checkPrivileges(info); 

        AuditDatabaseResponse response = new AuditDatabaseResponse();
        AuditDatabaseTo1 model = new AuditDatabaseTo1();

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

    /*
    *  Performs an audit check using AuditManager to access the required
    *  Tomcat auditing information and returns a JSON object to the client. 
    *  
    *  @return response: An AuditTomcatResponse object that represents the
    *                    JSON wrapper for the request.
    */
    @GET
    @Path("/tomcatInfo")
    @Produces("application/json")
    public AuditTomcatResponse getAuditTomcatInfo() {
        LoggedInInfo info = getLoggedInInfo();
        this.checkPrivileges(info); 

        AuditTomcatResponse response = new AuditTomcatResponse();
        AuditTomcatTo1 model = new AuditTomcatTo1();

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

    /*
    *  Performs an audit check using AuditManager to access the required
    *  OSCAR auditing information and returns a JSON object to the client. 
    *  
    *  @return response: An AuditOscarResponse object that represents the
    *                    JSON wrapper for the request.
    */
    @GET
    @Path("/oscarInfo")
    @Produces("application/json")
    public AuditOscarResponse getAuditOscarInfo() {
        LoggedInInfo info = getLoggedInInfo();
        this.checkPrivileges(info); 

        AuditOscarResponse response = new AuditOscarResponse();
        AuditOscarTo1 model = new AuditOscarTo1();

        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        try {
            model = this.auditManager.auditOscar(tomcatVersion, webAppName);

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

    /*
    *  Performs an audit check using AuditManager to access the required
    *  Drugref auditing information and returns a JSON object to the client. 
    *  
    *  @return response: An AuditDrugrefResponse object that represents the
    *                    JSON wrapper for the request.
    */
    @GET
    @Path("/drugrefInfo")
    @Produces("application/json")
    public AuditDrugrefResponse getAuditDrugrefInfo() {
        LoggedInInfo info = getLoggedInInfo();
        this.checkPrivileges(info); 

        AuditDrugrefResponse response = new AuditDrugrefResponse();
        AuditDrugrefTo1 model = new AuditDrugrefTo1();

        HttpServletRequest request = this.getHttpServletRequest();
        String tomcatVersion = request.getSession().getServletContext().getServerInfo();
        String webAppName = request.getSession().getServletContext().getContextPath().replace("/", "");

        try {
            model = this.auditManager.auditDrugref(tomcatVersion, webAppName);

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

    /*
    *  Check privileges for each API request using OSCAR's SecurityInfoManager
    *  class. Throw SecurityException error if user does not have privileges.
    *
    *  @param info: LoggedInInfo object from HTTP session.
    */
    private void checkPrivileges(LoggedInInfo info) {
        if (!securityInfoManager.hasPrivilege(info, "_admin", "r", null)) {
            throw new SecurityException("Missing required security object (_admin)");
        }
    }
}
