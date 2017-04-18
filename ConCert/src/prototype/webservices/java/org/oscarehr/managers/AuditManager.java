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
import org.oscarehr.ws.rest.to.model.AuditSystemTo1;
import org.oscarehr.ws.rest.to.model.AuditDatabaseTo1;
import org.oscarehr.ws.rest.to.model.AuditTomcatTo1;
import org.oscarehr.ws.rest.to.model.AuditOscarTo1;
import org.oscarehr.ws.rest.to.model.AuditDrugrefTo1;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtilsOld;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
*  Class that handles direct retrieval of the auditing information from the Audit
*  object and returning serializable Audit<System|Database|Tomcat|Oscar|Drugref>To1 
*  objects for AuditWebService.
*
*  github.com/williamgrosset
*/
@Service
public class AuditManager {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static Logger logger = MiscUtilsOld.getLogger();

    /*
    *  Retrieves system auditing information from an Audit object and returns
    *  a serializable model object to represent the data.
    *
    *  @return model: An AuditSystemTo1 object that represents the property 
    *                 tags and data for the request.
    */
    public AuditSystemTo1 auditSystem() {
        Date date = new Date();
        Audit audit = new Audit();
        AuditSystemTo1 model = new AuditSystemTo1();

        try {
            audit.systemInfo();

            model.setTimestamp(sdf.format(new Timestamp(date.getTime())));
            model.setSystemVersion(audit.getSystemVersion());
            model.setJvmVersion(audit.getJvmVersion());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    /*
    *  Retrieves database auditing information from an Audit object and returns
    *  a serializable model object to represent the data.
    *
    *  @return model: An AuditDatabaseTo1 object that represents the property 
    *                 tags and data for the request.
    */
    public AuditDatabaseTo1 auditDatabase() {
        Date date = new Date();
        Audit audit = new Audit();
        AuditDatabaseTo1 model = new AuditDatabaseTo1();

        try {
            audit.databaseInfo();

            model.setTimestamp(sdf.format(new Timestamp(date.getTime())));
            model.setDbType(audit.getDbType());
            model.setDbVersion(audit.getDbVersion());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    /*
    *  Retrieves Tomcat auditing information from an Audit object and returns
    *  a serializable model object to represent the data.
    *
    *  @param tomcatVersion: Tomcat version from servlet context.
    *
    *  @return model:        An AuditTomcatTo1 object that represents the
    *                        property tags and data for the request.
    */
    public AuditTomcatTo1 auditTomcat(String tomcatVersion) {
        Date date = new Date();
        Audit audit = new Audit();
        AuditTomcatTo1 model = new AuditTomcatTo1();

        try {
            audit.verifyTomcat(tomcatVersion);
            audit.tomcatReinforcement(tomcatVersion);

            model.setTimestamp(sdf.format(new Timestamp(date.getTime())));
            model.setTomcatVersion(audit.getTomcatVersion());
            model.setXmx(audit.getXmx());
            model.setXms(audit.getXms());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    /*
    *  Retrieves OSCAR auditing information from an Audit object and returns
    *  a serializable model object to represent the data.
    *
    *  @param tomcatVersion: Tomcat version from servlet context.
    *  @param webAppName:    OSCAR web application name from servlet context.
    *
    *  @return model:        An AuditOscarTo1 object that represents the
    *                        property tags and data for the request.
    */
    public AuditOscarTo1 auditOscar(String tomcatVersion, String webAppName) {
        Date date = new Date();
        Audit audit = new Audit();
        AuditOscarTo1 model = new AuditOscarTo1();

        try {
            audit.verifyOscar(tomcatVersion, webAppName);

            model.setTimestamp(sdf.format(new Timestamp(date.getTime())));
            model.setWebAppName(audit.getWebAppName());
            model.setBuild(audit.getBuild());
            model.setBuildDate(audit.getBuildDate());
            model.setHl7TextLabs(audit.getHl7TextLabs());
            model.setSinglePageChart(audit.getSinglePageChart());
            model.setTmpDir(audit.getTmpDir());
            model.setDrugrefUrl(audit.getDrugrefUrl());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }

    /*
    *  Retrieves Drugref auditing information from an Audit object and returns
    *  a serializable model object to represent the data.
    *
    *  @param tomcatVersion: Tomcat version from servlet context.
    *  @param webAppName:    OSCAR web application name from servlet context.
    *
    *  @return model:        An AuditDrugrefTo1 object that represents the
    *                        property tags and data for the request.
    */
    public AuditDrugrefTo1 auditDrugref(String tomcatVersion, String webAppName) {
        Date date = new Date();
        Audit audit = new Audit();
        AuditDrugrefTo1 model = new AuditDrugrefTo1();

        try {
            audit.verifyOscar(tomcatVersion, webAppName);
            audit.verifyDrugref(tomcatVersion);

            model.setTimestamp(sdf.format(new Timestamp(date.getTime())));
            model.setDbUser(audit.getDbUser());
            model.setDbUrl(audit.getDbUrl());
            model.setDbDriver(audit.getDbDriver());
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return null;
        }
        return model;
    }
}
