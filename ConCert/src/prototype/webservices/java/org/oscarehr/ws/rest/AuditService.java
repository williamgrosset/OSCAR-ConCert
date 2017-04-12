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

// import autowire

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
    @Path("/test")
    @Produces("application/json")
    public AuditResponse getTestInfo() {
        AuditResponse response = new AuditResponse();
        AuditTo1 model;

        //HttpServletRequest request = this.getHttpServletRequest();

        // Testing purposes:
        // String output = request.getSession().getServletContext().getContextPath().replace("/", "");

        try {
            model = this.auditManager.auditServer();

            if (model != null) {
                response.setSuccess(true);
                response.setMessage("Successfuly retrieved server version.");
                response.setAudit(model);
            } else {
                response.setSuccess(false);
                response.setMessage("Failed to retrieve server version.");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            response.setSuccess(false);
            response.setMessage("An error has occured.");
        }
        return response;
    }
}
