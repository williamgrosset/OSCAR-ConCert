/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package oscar.util;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import oscar.OscarProperties;

/*
*  github.com/williamgrosset
*/
public class MultiInstanceAudit extends Action {

    private File catalinaBase;
    private File catalinaHome;
    private File lsbRelease;
    private File tomcatSettings;
    private String jvmVersion;
    private String tomcatVersion;

    public MultiInstanceAudit() {
        catalinaBase = getCatalinaBase();
        catalinaHome = getCatalinaHome();
        lsbRelease = getLsbRelease();
        tomcatSettings = getTomcatSettings();
        jvmVersion = getJvmVersion();
        tomcatVersion = "";
    }

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            if (servletRequest.getSession().getAttribute("userrole") == null)
                servletResponse.sendRedirect("../logout.jsp");

            tomcatVersion = servletRequest.getSession().getServletContext().getServerInfo();
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + ","
                            + (String)servletRequest.getAttribute("user");
        if (!roleName.contains("admin")) {
            return actionMapping.findForward("unauthorized");
        }

        servletRequest.setAttribute("serverVersion", serverVersion());
        servletRequest.setAttribute("databaseInfo", databaseInfo());
        servletRequest.setAttribute("verifyTomcat", verifyTomcat());
        servletRequest.setAttribute("verifyOscar", verifyOscar(catalinaBase.getPath() + "/webapps/"));
        servletRequest.setAttribute("verifyDrugref", verifyDrugref(catalinaBase.getPath() + "/webapps/"));
        servletRequest.setAttribute("tomcatReinforcement", tomcatReinforcement());
        return actionMapping.findForward("success");
    }

    /*
    *  Retrieve catalina base directory from system properties. 
    *
    *  @return catalinaBase: File object for catalina base directory.
    */
    private File getCatalinaBase() {
        try {
            return new File(System.getProperty("catalina.base"));
        } catch (Exception e) {
            return new File("");
        }
    }

    /*
    *  Retrieve catalina home directory from system properties. 
    *
    *  @return catalinaHome: File object for catalina home directory.
    */
    private File getCatalinaHome() {
        try {
            return new File(System.getProperty("catalina.home"));
        } catch (Exception e) {
            return new File("");
        }
    }

    /*
    *  Retrieve "lsb-release" file from "/etc" directory. 
    *
    *  @return lsbRelease: File object for "/etc/lsb-release".
    */
    private File getLsbRelease() {
        try {
            return new File("/etc/lsb-release");
        } catch (Exception e) {
            return new File("");
        }
    }

    /*
    *  Retrieve Tomcat settings file from "/etc/default" directory. 
    *
    *  @return tomcatSettings: File object for "/etc/default/$tomcat" with
    *  $tomcat being the current Tomcat web container for this application.
    */
    private File getTomcatSettings() {
        try {
            if (catalinaBase == null || catalinaBase.getPath().equals(""))
                throw new FileNotFoundException();

            Pattern tomcatVersion = Pattern.compile(".*(tomcat[0-9]+)");
            Matcher tomcatMatch = tomcatVersion.matcher(catalinaBase.getPath());
            tomcatMatch.matches();
            return new File("/etc/default/" + tomcatMatch.group(1));
        } catch (Exception e) {
            return new File("");
        }
    }

    /*
    *  Retrieve JVM version from system properties. 
    *
    *  @return jvmVersion: String value for JVM version.
    */
    private String getJvmVersion() {
        try {
            return System.getProperty("java.runtime.version");
        } catch (Exception e) {
            return "";
        }
    }

    /*
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    *  NOTE: Majority of my methods require the ReversedLinesFilerReader class.
    *  Since I did not want to require another import for the default file reader,
    *  I went ahead and used ReversedLinesFileReader anyways.
    *
    *  @return output: Ubuntu server version.
    */
    protected String serverVersion() {
        try {
            if (lsbRelease == null || lsbRelease.getPath().equals(""))
                throw new FileNotFoundException();

            String line = "";
            ReversedLinesFileReader rf = new ReversedLinesFileReader(lsbRelease);
            boolean isMatch = false;

            while ((line = rf.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;

                isMatch = Pattern.matches("^(DISTRIB_DESCRIPTION=).*", line);
                if (isMatch) {
                    return line.substring(20);
                }
            }
            return "Could not detect Ubuntu server version.";
        } catch (Exception e) {
            return "Could not read \"lsb-release\" file to detect Ubuntu server version.";
        }
    }

    /*
    *  Retrieve url, username, and password information from Oscar properties
    *  to make a connection with our database. From our connection, we can 
    *  retrieve which database type we are connected to and the database version.
    *
    *  @return output: Database name and version.
    */
    protected String databaseInfo() {
        try {
            String dbType = OscarProperties.getInstance().getProperty("db_type");
            if (dbType == null || dbType.equals("")) {
                return "Cannot determine database type. \"db_type\" tag is not configured properly.";
            }

            String dbUri = OscarProperties.getInstance().getProperty("db_uri");
            String dbUserName = OscarProperties.getInstance().getProperty("db_username");
            String dbPassWord = OscarProperties.getInstance().getProperty("db_password");

            Connection connection = DriverManager.getConnection(dbUri, dbUserName, dbPassWord); 
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductName() + ": " + metaData.getDatabaseProductVersion();
        } catch (Exception e) {
            return "Cannot determine database name and version.";
        }
    }
    

    /*
    *  Extract JVM version from system properties and server information 
    *  from servlet.
    *
    *  @return output: JVM and Tomcat version information.
    */
    protected String verifyTomcat() {
        if (jvmVersion == null || tomcatVersion == null || jvmVersion.equals("")
                || tomcatVersion.equals(""))
            return "Please verify that Tomcat is setup correctly.";

        String output = "";
        output += "JVM Version: " + jvmVersion + "<br />";
        output += "Tomcat version: " + tomcatVersion + "<br />";
        return output;
    }

    /*
    *  Verify all possible Oscar deployments.
    *  Grab all possible Oscar deployed folder names in root directory 
    *  and push onto stack. Pop names off of the stack and verify 
    *  each properties file that exists in "catalinaHome" directory.
    *
    *  @param webAppsPath: Directory path to locate Oscar deployments.
    *  @return output: Combined output of Oscar build and properties information
    *  for each properties file that exists.
    */
    protected String verifyOscar(String webAppsPath) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }

        String output = "";
        File webApps = new File(webAppsPath);
        Stack<String> files = grabFiles(webApps, "^(oscar[0-9]*\\w*)$");

        if (files.empty()) {
            return "Could not find any properties files for Oscar." + "<br />";
        }
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            output += "<b>Currently checking \"oscar_mcmaster.properties\" file for \"" + file + "\"..." + "</b><br />";
            output += oscarBuild(webAppsPath + file + "/WEB-INF/classes/oscar_mcmaster.properties");
            output += verifyOscarProperties(webAppsPath + file + "/WEB-INF/classes/oscar_mcmaster.properties");
            output += "<b>Currently checking \"" + file + ".properties\" file..." + "</b><br />";
            output += oscarBuild(catalinaHome.getPath() + "/" + file + ".properties");
            output += verifyOscarProperties(catalinaHome.getPath() + "/" + file + ".properties");
        }
        return output;
    }

    /*
    *  Read Oscar buildtag of properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Current build and version of Oscar.
    */
    protected String oscarBuild(String fileName) {
        try {
            String line = "";
            File oscar = new File(fileName);
            ReversedLinesFileReader rf = new ReversedLinesFileReader(oscar);
            boolean isMatch = false;

            while ((line = rf.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;
                isMatch = Pattern.matches("^(buildtag=).*", line);
                if (isMatch) {
                    return "Oscar build and version: " + line.substring(9) + "<br />";
                }
            }
            return "Oscar build/version cannot be found." + "<br />";
        } catch (Exception e) {
            return "Could not read properties file to detect Oscar build.<br />";
        }
    }

    /*
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and
    *  "drugref_url" tags of Oscar properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Output of the required tags in the Oscar properties 
    *  file.
    */
    protected String verifyOscarProperties(String fileName) {
        try {
            String output = "";
            String line = "";
            File oscar = new File(fileName);
            ReversedLinesFileReader rf = new ReversedLinesFileReader(oscar);
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean isMatch3 = false;
            boolean isMatch4 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;

            while ((line = rf.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;
                isMatch1 = Pattern.matches("^(HL7TEXT_LABS=).*", line);
                isMatch2 = Pattern.matches("^(SINGLE_PAGE_CHART=).*", line);
                isMatch3 = Pattern.matches("^(TMP_DIR(=|:)).*", line);
                isMatch4 = Pattern.matches("^(drugref_url=).*", line);
                if (isMatch1) { // HL7TEXT_LABS=
                    flag1 = true;
                    output += "\"HL7TEXT_LABS\" tag is configured as: " + line.substring(13) + "<br />";
                }
                if (isMatch2) { // SINGLE_PAGE_CHART=
                    flag2 = true;
                    output += "\"SINGLE_PAGE_CHART\" tag is configured as: " + line.substring(18) + "<br />";
                }
                if (isMatch3) { // TMP_DIR=
                    flag3 = true;
                    output += "\"TMP_DIR\" tag is configured as: " + line.substring(8) + "<br />";
                }
                if (isMatch4) { // drugref_url=
                    flag4 = true;
                    output += "\"drugref_url\" tag is configured as: " + line.substring(12) + "<br />";
                }
                if (flag1 && flag2 && flag3 && flag4)
                    break;
            }
            
            if (!flag1)
                output += "\"HL7TEXT_LABS\" tag is not configured properly." + "<br />";
            if (!flag2)
                output += "\"SINGLE_PAGE_CHART\" tag is not configured properly." + "<br />";
            if (!flag3)
                output += "\"TMP_DIR\" tag is not configured properly." + "<br />";
            if (!flag4)
                output += "\"drugref_url\" tag is not configured properly." + "<br />";
            return output;
        } catch (Exception e) {
            return "Could not read properties file to verify Oscar tags.";
        }
    }

    /*
    *  Verify all Drugref deployments.
    *  Grab all possible Drugref deployed folder names in root directory 
    *  and push onto stack. Pop names off of the stack and verify 
    *  each properties file that exists in "catalinaHome" directory.
    *
    *  @param webAppsPath: Directory path to locate Drugref deployments.
    *  @return output: Combined output of Drugref properties information
    *  for each properties file that exists.
    */
    protected String verifyDrugref(String webAppsPath) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }

        String output = "";
        File webApps = new File(webAppsPath);
        Stack<String> files = grabFiles(webApps, "^(drugref[0-9]*\\w*)$");

        if (files.empty()) {
            return "Could not find any properties files for Drugref.";
        }
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            output += "<b>Currently checking \"" + file + ".properties\" file..." + "</b><br />";
            output += verifyDrugrefProperties(catalinaHome.getPath() + "/" + file + ".properties");
        }
        return output;
    }

    /*
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref 
    *  properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Output of the required tags in the Drugref properties 
    *  file.
    */
    protected String verifyDrugrefProperties(String fileName) {
        try {
            String output = "";
            String line = "";
            File drugref = new File(fileName);
            ReversedLinesFileReader rf = new ReversedLinesFileReader(drugref);
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean isMatch3 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;

            while ((line = rf.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;
                isMatch1 = Pattern.matches("^(db_user=).*", line);
                isMatch2 = Pattern.matches("^(db_url=).*", line);
                isMatch3 = Pattern.matches("^(db_driver=).*", line);
                if (isMatch1) { // db_user=
                    flag1 = true;
                    output += "\"db_user\" tag is configured as: " + line.substring(8) + "<br />";
                }
                if (isMatch2) { // db_url=
                    flag2 = true;
                    output += "\"db_url\" tag is configured as: " + line.substring(7) + "<br />";
                }
                if (isMatch3) { // db_driver=
                    flag3 = true;
                    output += "\"db_driver\" tag is configured as: " + line.substring(10) + "<br />";
                }
                if (flag1 && flag2 && flag3)
                    break;
            }

            if (!flag1)
                output += "\"db_user\" tag is not configured properly." + "<br />";
            if (!flag2)
                output += "\"db_url\" tag is not configured properly." + "<br />";
            if (!flag3)
                output += "\"db_driver\" tag is not configured properly." + "<br />";
            return output;
        } catch (Exception e) {
            return "Could not read properties file to verify Drugref tags.";
        }
    }

    /*
    *  Read through the Tomcat settings file and output the Xmx and Xms values
    *  to the user.
    *
    *  @return output: Xmx (maximum memory allocation) value followed by Xms 
    *  (minimum memory allocation) value.
    */
    protected String tomcatReinforcement() {
        if (catalinaBase == null || tomcatSettings == null || catalinaBase.getPath().equals("")
                || tomcatSettings.getPath().equals(""))
            return "Please verify that your \"catalina.base\" directory is setup correctly.";
        try {
            String output = "";
            String line = "";
            String xmx = "";
            String xms = "";
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            Pattern xmxPattern = Pattern.compile(".*(Xmx[0-9]+m).*");
            Pattern xmsPattern = Pattern.compile(".*(Xms[0-9]+m).*");
            ReversedLinesFileReader rf = new ReversedLinesFileReader(tomcatSettings);

            while ((line = rf.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;

                Matcher xmxMatch = xmxPattern.matcher(line);
                isMatch1 = xmxMatch.matches();
                Matcher xmsMatch = xmsPattern.matcher(line);
                isMatch2 = xmsMatch.matches();

                if (isMatch1) {
                    xmx = xmxMatch.group(1);
                    String[] xmxString = xmx.toString().split("x");
                    flag1 = true;
                    output += "Xmx value: " + xmxString[1] + "<br />";
                }
                if (isMatch2) {
                    xms = xmsMatch.group(1);
                    String[] xmsString = xms.toString().split("s");
                    flag2 = true;
                    output += "Xms value: " + xmsString[1] + "<br />";
                }
                if (flag1 && flag2)
                    break;
            }

            if (!flag1) {
                output += "Could not detect Xmx value." + "<br />";
            }
            if (!flag2) {
                output += "Could not detect Xms value." + "<br />";
            }
            return output;
        } catch (Exception e) {
            return "Could not detect Tomcat memory allocation in Tomcat settings file.";
        }
    }

    /////////////////////////////////
    //////// HELPER METHOD(S) ///////
    /////////////////////////////////

    /*
    *  Loop through folders/files in directory and push all possible files 
    *  (using pattern matching) onto the Stack.
    *
    *  @param directory: Webapps directory of Tomcat.
    *  @param regex: Used for pattern matching on finding Oscar and Drugref 
    *  deployed folders.
    *  @return files: Stack of properties files to be verified individually.
    */
    protected Stack<String> grabFiles(File directory, String regex) {
        Stack<String> files = new Stack<String>();
        String[] fileList = directory.list();

        // We did not find a file
        if (fileList == null || fileList.length == 0) {
            return files;
        }

        Arrays.sort(fileList);
        for (int i = 0; i < fileList.length; i++) {
            if (Pattern.matches(regex, fileList[i])) {
                files.push(fileList[i]);
            }
        }
        return files;
    }
}