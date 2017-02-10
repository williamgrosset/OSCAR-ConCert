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

/*
*  github.com/williamgrosset
*/
public class Audit extends Action {

    private File catalinaBase;
    private File catalinaHome;
    private File lsbRelease;
    private File tomcatSettings;
    private String jvmVersion;
    private String tomcatVersion;
    private String webAppName;
    private String drugrefUrl;

    public Audit() {
        catalinaBase = getCatalinaBase();
        catalinaHome = getCatalinaHome();
        lsbRelease = getLsbRelease();
        tomcatSettings = getTomcatSettings();
        jvmVersion = getJvmVersion();
        tomcatVersion = "";
        webAppName = "";
        drugrefUrl = "";
    }

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            if (servletRequest.getSession().getAttribute("userrole") == null)
                servletResponse.sendRedirect("../logout.jsp");

            tomcatVersion = servletRequest.getSession().getServletContext().getServerInfo();
            webAppName = servletRequest.getSession().getServletContext().getContextPath().replace("/", "");
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
        servletRequest.setAttribute("verifyOscar", verifyOscar());
        servletRequest.setAttribute("verifyDrugref", verifyDrugref());
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
        return "This is temporary.";
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
    *  Verify the current Oscar instance. Check build, version, and installation
    *  properties of the default properties file in the WAR and properties file
    *  found in Tomcat's catalinaHome directory.
    *
    *  @return output: Combined output of Oscar build and properties information.
    */
    protected String verifyOscar() {
        if (catalinaBase == null || catalinaHome == null || webAppName == null 
                || catalinaBase.getPath().equals("") || webAppName.equals("")
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }

        String output = "";
        output += "<b>Currently checking \"oscar_mcmaster.properties\" file for \"" + webAppName + "\"..." + "</b><br />";
        output += oscarBuild(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties");
        output += verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties");
        output += "<b>Currently checking \"" + webAppName + ".properties\" file..." + "</b><br />";
        output += oscarBuild(catalinaHome.getPath() + "/" + webAppName + ".properties");
        output += verifyOscarProperties(catalinaHome.getPath() + "/" + webAppName + ".properties");

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
                    drugrefUrl = line.substring(12);
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
    *  Verify the current Drugref instance. Check installation properties of 
    *  the properties file found in Tomcat's catalinaHome directory.
    *
    *  @return output: Output of Drugref properties information.
    */
    protected String verifyDrugref() {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }
        if (drugrefUrl.equals("")) {
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        }

        // Grab deployed Drugref folder name and use as file name for the properties file
        Pattern p = Pattern.compile(".*://.*/(.*)/.*");
        Matcher m = p.matcher(drugrefUrl);
        m.matches();

        String output = "";
        output += "<b>Currently checking \"" + m.group(1) + ".properties\" file..." + "</b><br />";
        output += verifyDrugrefProperties(catalinaHome.getPath() + "/" + m.group(1) + ".properties");
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
}
