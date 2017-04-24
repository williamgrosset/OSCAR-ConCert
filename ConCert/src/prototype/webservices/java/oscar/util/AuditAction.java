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

package oscar.util;

import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.managers.SecurityInfoManager;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
*  Action class that contains auditing information for the corresponding 
*  view (oscarAudit.jsp).
*  
*  Author: github.com/williamgrosset
*/
public class AuditAction extends Action {

    private Audit audit = new Audit();

    @Autowired
    private SecurityInfoManager securityInfoManager;

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            String tomcatVersion = servletRequest.getSession().getServletContext().getServerInfo();
            String webAppName = servletRequest.getSession().getServletContext().getContextPath().replace("/", "");
            securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

            if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(servletRequest), "_admin", "r", null)) {
                throw new SecurityException("Missing required security object (_admin)");
            }

            servletRequest.setAttribute("serverVersion", displaySystemInfo());
            servletRequest.setAttribute("databaseInfo", displayDatabaseInfo());
            servletRequest.setAttribute("verifyTomcat", displayTomcatInfo(tomcatVersion));
            servletRequest.setAttribute("verifyOscar", displayOscarInfo(tomcatVersion, webAppName));
            servletRequest.setAttribute("verifyDrugref", displayDrugrefInfo(tomcatVersion));
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + "," + (String)servletRequest.getAttribute("user");
        if (!roleName.contains("admin") || securityInfoManager == null) {
            return actionMapping.findForward("unauthorized");
        }

        return actionMapping.findForward("success");
    }

    /*
    *  Audit and display Linux distribution version to user.
    *
    *  @return String: Audit function call to retrieve Linux server version.
    */
    private String displaySystemInfo() {
        return audit.verifySystemInfo();
    }

    /*
    *  Audit and display database type and version to user.
    *
    *  @return String: Audit function call to retrieve database type and version.
    */
    private String displayDatabaseInfo() {
        return audit.verifyDatabaseInfo();
    }

    /*
    *  Audit and display Tomcat version, Xmx, and Xms values to user.
    *
    *  @param tomcatVersion: Tomcat version from servlet context.
    *
    *  @return String:       Audit function call to retrieve Tomcat version, Xmx,
    *                        and Xms values. 
    */
    private String displayTomcatInfo(String tomcatVersion) {
        return audit.verifyTomcatVersion(tomcatVersion) + "<br />" + audit.verifyTomcatReinforcement(tomcatVersion);
    }

    /*
    *  Audit and display OSCAR properties to user.
    *
    *  @param tomcatVersion: Tomcat version from servlet context.
    *  @param webAppName:    Oscar web application name from servlet context.
    *
    *  @return String:       Audit function call to retrieve auditing information
    *                        for OSCAR properties.
    */
    private String displayOscarInfo(String tomcatVersion, String webAppName) {
        StringBuilder output = new StringBuilder();
        output.append("<b>Verifying default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />");
        output.append(audit.verifyOscar(tomcatVersion, webAppName, true));
        output.append("<br /><b>Verifying custom \"" + webAppName + ".properties\" file..." + "</b><br />");
        output.append(audit.verifyOscar(tomcatVersion, webAppName, false));
        output.append("<br /><b>NOTE:</b> The custom properties file will overwrite the default properties file found in the deployed WAR.<br />");
        return output.toString();
    }
        
    /*
    *  Audit and display Drugref properties to user.
    *
    *  @param tomcatVersion: Tomcat version from servlet context.
    *
    *  @return String:       Audit function call to retrieve auditing information
    *                        for Drugref properties.
    */
    private String displayDrugrefInfo(String tomcatVersion) {
        StringBuilder output = new StringBuilder();
        output.append(audit.verifyDrugref(tomcatVersion));
        return output.toString();
    }
}
