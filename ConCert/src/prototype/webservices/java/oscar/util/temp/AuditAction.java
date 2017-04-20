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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
*  Action class that contains auditing information for the view (oscarAudit.jsp)
*  of the OSCAR Administration page.
*  
*  github.com/williamgrosset
*/
public class AuditAction extends Action {

    private SecurityInfoManager securityInfoManager;

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            Audit audit = new Audit();
            String tomcatVersion = servletRequest.getSession().getServletContext().getServerInfo();
            String webAppName = servletRequest.getSession().getServletContext().getContextPath().replace("/", "");
            securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

            if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(servletRequest), "_admin", "r", null)) {
                throw new SecurityException("Missing required security object (_admin)");
            }

            servletRequest.setAttribute("serverVersion", audit.systemInfo());
            servletRequest.setAttribute("databaseInfo", audit.databaseInfo());
            servletRequest.setAttribute("verifyTomcat", audit.verifyTomcat(tomcatVersion));
            servletRequest.setAttribute("verifyOscar", audit.verifyOscar(tomcatVersion, webAppName));
            servletRequest.setAttribute("verifyDrugref", audit.verifyDrugref(tomcatVersion));
            servletRequest.setAttribute("tomcatReinforcement", audit.tomcatReinforcement(tomcatVersion));
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + "," + (String)servletRequest.getAttribute("user");
        if (!roleName.contains("admin") || securityInfoManager == null) {
            return actionMapping.findForward("unauthorized");
        }

        return actionMapping.findForward("success");
    }

    private String displaySystemInfo() {
        try {
            if (lsbRelease == null || lsbRelease.getPath().equals(""))
                throw new FileNotFoundException();

            String line = "";
            ReversedLinesFileReader rf = new ReversedLinesFileReader(lsbRelease);
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternDIST_DESC = Pattern.compile("^(DISTRIB_DESCRIPTION\\s?=).*");

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (matcherComment.matches()) continue;
                Matcher matcherDIST_DESC = patternDIST_DESC.matcher(line);

                if (matcherDIST_DESC.matches()) {
                    this.systemVersion = line.substring(matcherDIST_DESC.group(1).length()).trim();
                    return "Version: " + this.systemVersion;
                }
            }
            return "Could not detect Linux server version.";
        } catch (Exception e) {
            return "Could not read \"lsb-release\" file to detect Linux server version.";
        }
    }

    private String displayDatabaseInfo() {
        try {
            connection = DbConnectionFilter.getThreadLocalDbConnection();
            if (connection == null) throw new NullPointerException();

            StringBuilder output = new StringBuilder();
            DatabaseMetaData metaData = connection.getMetaData();
            this.dbType = metaData.getDatabaseProductName().trim();
            this.dbVersion = metaData.getDatabaseProductVersion().trim();

            output.append("Type: " + this.dbType + "<br />");
            output.append("Version: " + this.dbVersion);
            return output.toString();
        } catch (Exception e) {
            return "Cannot determine database type and version.";
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    return "Cannot close connection to database.";
                }
            }
        }
    }

    private String displayTomcatInfo(String tomcatVersion) {
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (jvmVersion == null || jvmVersion.equals(""))
            return "Could not detect JVM version from system properties.";

        this.tomcatVersion = tomcatVersion;

        StringBuilder output = new StringBuilder();
        output.append("JVM Version: " + this.jvmVersion + "<br />");
        output.append("Tomcat version: " + this.tomcatVersion);
        return output.toString();
    }

    private String displayOscarInfo(String tomcatVersion, String webAppName) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("") 
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (webAppName == null || webAppName.equals(""))
            return "Could not detect the Oscar webapps directory name.";

        this.webAppName = webAppName;

        StringBuilder output = new StringBuilder();
        // Tomcat 7
        if (extractTomcatVersionNumber(tomcatVersion) == 7) {
            output.append("<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />");
            output.append(verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
            output.append("<br /><b>Currently checking \"" + webAppName + ".properties\" file in \"catalina.home\" directory..." + "</b><br />");
            output.append(verifyOscarProperties(catalinaHome.getPath() + "/" + webAppName + ".properties"));
            output.append("<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />");
        // Tomcat 8
        } else if (extractTomcatVersionNumber(tomcatVersion) == 8) {
            output.append("<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />");
            output.append(verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
            output.append("<br /><b>Currently checking \"" + webAppName + ".properties\" file in \"catalina.home\" directory..." + "</b><br />");
            output.append(verifyOscarProperties(System.getProperty("user.home") + "/" + webAppName + ".properties"));
            output.append("<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />");
        // No Tomcat version found
        } else {
            output.append("Could not detect Tomcat version number to determine audit check for Oscar properties.");
        }
        return output.toString();
    }
    
    private String displayDrugrefInfo(String tomcatVersion) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals(""))
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (drugrefUrl == null)
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";

        // Grab deployed Drugref folder name and use as the file name for the properties file
        Pattern patternDrugrefUrl = Pattern.compile(".*://.*/(drugref.*)/.*");
        Matcher matcherDrugrefUrl = patternDrugrefUrl.matcher(drugrefUrl);

        if (matcherDrugrefUrl.matches()) {
            StringBuilder output = new StringBuilder();
            // Tomcat 7
            if (extractTomcatVersionNumber(tomcatVersion) == 7) {
                output.append("<b>Currently checking \"" + matcherDrugrefUrl.group(1) + ".properties\" file..." + "</b><br />");
                output.append(verifyDrugrefProperties(catalinaHome.getPath() + "/" + matcherDrugrefUrl.group(1) + ".properties"));
            // Tomcat 8
            } else if (extractTomcatVersionNumber(tomcatVersion) == 8) {
                output.append("<b>Currently checking \"" + matcherDrugrefUrl.group(1) + ".properties\" file..." + "</b><br />");
                output.append(verifyDrugrefProperties(System.getProperty("user.home") + "/" + matcherDrugrefUrl.group(1) + ".properties"));
            // No Tomcat version found
            } else {
                output.append("Could not detect Tomcat version number to determine audit check for Drugref properties.");
            }
            return output.toString();
        } else {
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        }
    }
}
