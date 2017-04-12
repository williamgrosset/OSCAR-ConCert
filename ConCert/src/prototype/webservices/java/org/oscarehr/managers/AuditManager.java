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

package org.oscarehr.managers;

import oscar.util.Audit;
import org.oscarehr.ws.rest.to.model.AuditTo1;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtilsOld;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
*  Class that handles direct retrieval of the auditing information from the Audit
*  object and returning serializable AuditTo1 objects for AuditWebService.
*
*  github.com/williamgrosset
*/
@Service
public class AuditManager {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    private static Logger logger = MiscUtilsOld.getLogger();

    public AuditTo1 auditServer() {
        Date date = new Date();
        Audit audit = new Audit();
        AuditTo1 model;

        try {
            String timestamp = new String(new Timestamp(date.getTime()).toString());
            model = new AuditTo1();
            audit.serverVersion();

            model.setTimestamp(timestamp);
            model.setServerVersion(audit.getServerVersion());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    /******************************************************
     *                                                    *
     *    CURRENTLY TESTING REST API W/ FUNCTION ABOVE    *
     *                                                    *
     ******************************************************/

    public AuditTo1 auditDatabase() {
        return new AuditTo1();
    }

    public AuditTo1 auditTomcat() {
        return new AuditTo1();
    }

    public AuditTo1 auditProperties(String name) {
        try {
            if (name.equals("oscar")) {

            } else if (name.equals("drugref")) {


            } else {

            }
            return new AuditTo1();
        } catch (Exception e) {
            return null;
        }
    }
}
