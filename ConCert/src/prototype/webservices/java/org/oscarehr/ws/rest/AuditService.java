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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.oscarehr.util.MiscUtilsOld;

// This will be replaced by an actual Response object
// (i.e. AuditResponse.class)
import org.oscarehr.ws.rest.to.GenericRESTResponse;

// This import won't exist here, will eventually be handled by
// AuditManager.class
import oscar.util.AuditAction;

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

    @GET
    @Path("/test")
    @Produces("application/json")
    public GenericRESTResponse getTestInfo() {
        GenericRESTResponse response = new GenericRESTResponse();
        HttpServletRequest request = this.getHttpServletRequest();
        AuditAction audit = new AuditAction();

        // Testing purposes
        String output = request.getSession().getServletContext().getContextPath().replace("/", "");
        try {
            response.setMessage("Test response successful :) " + output);
            response.setSuccess(true);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            response.setMessage("Test response failed");
            response.setSuccess(false);
        }
        return response;
    }
}
