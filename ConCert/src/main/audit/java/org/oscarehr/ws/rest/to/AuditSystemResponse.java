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
package org.oscarehr.ws.rest.to;

import org.oscarehr.ws.rest.to.model.AuditSystemTo1;

import java.io.Serializable;

/*
*  Class that represents the wrapper for the JSON object, which will be returned 
*  by AuditWebService function calls.
*
*  Author: github.com/williamgrosset
*/
public class AuditSystemResponse extends GenericRESTResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private AuditSystemTo1 audit;

    public AuditSystemTo1 getAudit() {
        return this.audit;
    }

    public void setAudit(AuditSystemTo1 audit) {
        this.audit = audit;
    }
}
