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
        AuditTo1 model = new AuditTo1();

        try {
            audit.serverVersion();
            model.setTimestamp(new String(new Timestamp(date.getTime()).toString()));
            model.setServerVersion(audit.getServerVersion());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    public AuditTo1 auditDatabase() {
        Date date = new Date();
        Audit audit = new Audit();
        AuditTo1 model = new AuditTo1();

        try {
            audit.databaseInfo();
            model.setTimestamp(new String(new Timestamp(date.getTime()).toString()));
            model.setDbType(audit.getDbType());
            model.setDbVersion(audit.getDbVersion());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    public AuditTo1 auditTomcat(String tomcatVersion) {
        Date date = new Date();
        Audit audit = new Audit();
        AuditTo1 model = new AuditTo1();

        try {
            audit.verifyTomcat(tomcatVersion);
            model.setTimestamp(new String(new Timestamp(date.getTime()).toString()));
            model.setJvmVersion(audit.getJvmVersion());
            model.setTomcatVersion(audit.getTomcatVersion());
            model.setXmx(audit.getXmx());
            model.setXms(audit.getXms());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    public AuditTo1 auditProperties(String tomcatVersion, String webAppName, String type) {
        Date date = new Date();
        Audit audit = new Audit();
        AuditTo1 model = new AuditTo1();

        try {
            audit.verifyOscar(tomcatVersion, webAppName);
            audit.verifyDrugref(tomcatVersion, webAppName);
            model.setTimestamp(new String(new Timestamp(date.getTime()).toString()));
            model.setTomcatVersion(audit.getTomcatVersion());
            model.setWebAppName(audit.getWebAppName());

            if (type.equals("oscar")) {
                model.setBuild(audit.getBuild());
                model.setBuildDate(audit.getBuildDate());
                model.setHl7TextLabs(audit.getHl7TextLabs());
                model.setSinglePageChart(audit.getSinglePageChart());
                model.setTmpDir(audit.getTmpDir());
                model.setDrugrefUrl(audit.getDrugrefUrl());
            } else if (type.equals("drugref")) {
                model.setDbUser(audit.getDbUser());
                model.setDbUrl(audit.getDbUrl());
                model.setDbDriver(audit.getDbDriver());
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }
}
