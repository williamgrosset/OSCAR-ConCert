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
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.TimeStamp;
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

    private static Logger logger = MiscUtils.getLogger();

    @Autowired
    protected SecurityInfoManager securityInfoManager;


    public AuditTo1 auditServer() {
        Date date = new Date();
        // String timestamp = new String(new TimeStap(date.getTime()).toString())
        Audit audit = new Audit();

        try {} catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }

        return null;
    }

    public AuditTo1 auditDatabase() {
        Date date = new Date();
        // String timestamp = new String(new TimeStap(date.getTime()).toString())
        Audit audit = new Audit();

        try {} catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }

        return null;
    }

    public AuditTo1 auditTomcat() {
        Date date = new Date();
        // String timestamp = new String(new TimeStap(date.getTime()).toString())
        Audit audit = new Audit();

        try {} catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }

        return null;
    }

    public AuditTo1 auditProperties(String name) {
        Date date = new Date();
        // String timestamp = new String(new TimeStap(date.getTime()).toString())
        Audit audit = new Audit();
        // call both verifyOscar and verifyDrugref

        try {
            if (name.equals("oscar")) {

            } else if (name.equals("drugref")) {


            } else {

            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }

        return null;
    }
}
