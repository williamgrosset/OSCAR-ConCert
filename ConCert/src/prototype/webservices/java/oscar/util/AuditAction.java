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

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import org.oscarehr.util.DbConnectionFilter;

import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.managers.SecurityInfoManager;

/*
*  github.com/williamgrosset
*/
public class AuditAction extends Action {

    private File catalinaBase;
    private File catalinaHome;
    private File lsbRelease;
    private File tomcatSettings;
    private String jvmVersion;
    private String tomcatVersion;
    private String webAppName;
    private String drugrefUrl;
    private Connection connection;
    private SecurityInfoManager securityInfoManager;

    public AuditAction() {
        catalinaBase = getCatalinaBase();
        catalinaHome = getCatalinaHome();
        lsbRelease = getLsbRelease();
        tomcatSettings = getTomcatSettings(7);
        jvmVersion = getJvmVersion();
        tomcatVersion = "";
        webAppName = "";
        drugrefUrl = "";
        connection = null;
        securityInfoManager = getSecurityInfoManager();
    }

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            tomcatVersion = servletRequest.getSession().getServletContext().getServerInfo();
            webAppName = servletRequest.getSession().getServletContext().getContextPath().replace("/", "");
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + "," + (String)servletRequest.getAttribute("user");
        if (!roleName.contains("admin") || securityInfoManager == null) {
            return actionMapping.findForward("unauthorized");
        }

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(servletRequest), "_admin", "r", null)) {
            throw new SecurityException("Missing required security object (_admin)");
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
    *  Retrieve "catalina.base" directory from system properties. 
    *
    *  @return catalinaBase: File object for "catalina.base" directory.
    */
    private File getCatalinaBase() {
        try {
            return new File(System.getProperty("catalina.base"));
        } catch (Exception e) {
            return new File("");
        }
    }

    /*
    *  Retrieve "catalina.home" directory from system properties. 
    *
    *  @return catalinaHome: File object for "catalina.home" directory.
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
    *  Retrieve settings file from appropriate Tomcat directory
    *  (currently supports version 7 and 8 of Tomcat).
    *
    *  @param version: Version of Tomcat.
    *  @return tomcatSettings: Settings file for Tomcat.
    */
    private File getTomcatSettings(int version) {
        try {
            if (catalinaBase == null || catalinaBase.getPath().equals(""))
                throw new FileNotFoundException();

            if (version == 7) {
                Pattern tomcatPattern = Pattern.compile(".*(tomcat[0-9]+)");
                Matcher tomcatMatch = tomcatPattern.matcher(catalinaBase.getPath());
                tomcatMatch.matches();
                return new File("/etc/default/" + tomcatMatch.group(1));
            } else if (version == 8) {
                return new File(catalinaBase.getPath() + "/bin/setenv.sh");
            } else {
                return new File("");
            }
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
            return System.getProperty("java.version");
        } catch (Exception e) {
            return "";
        }
    }

    /*
    *  Initialize SecurityInfoManager object.
    *
    *  @return securityInfoManager: Object for SecurityInfoManager.class.
    */
    private SecurityInfoManager getSecurityInfoManager() {
        try {
            return SpringUtils.getBean(SecurityInfoManager.class);
        } catch (Exception e) {
            return null;
        }
    }

    /*
    *  Read "/etc/lsb-release" file and extract Linux server version. The
    *  file should be available on Ubuntu and Debian distributions.
    *  NOTE: Majority of my methods require the ReversedLinesFilerReader class.
    *  Since I did not want to require another import for the default file reader,
    *  I went ahead and used ReversedLinesFileReader anyways.
    *
    *  @return output: Linux server version.
    */
    private String serverVersion() {
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

                if (matcherDIST_DESC.matches())
                    return "Version: " + line.substring(matcherDIST_DESC.group(1).length());
            }
            return "Could not detect Linux server version.";
        } catch (Exception e) {
            return "Could not read \"lsb-release\" file to detect Linux server version.";
        }
    }

    /*
    *  Establish a connection to our database and retrieve the database type and 
    *  version from our DatabaseMetaData object.
    *
    *  @return output: Database type and version.
    */
    private String databaseInfo() {
        String output = "";
        try {
            connection = DbConnectionFilter.getThreadLocalDbConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            output += "Type: " + metaData.getDatabaseProductName() + "<br />";
            output += "Version: " + metaData.getDatabaseProductVersion() + "<br />";
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
            return output;
        }
    }
    

    /*
    *  Extract JVM version from system properties and server version information 
    *  from servlet.
    *
    *  @return output: JVM and Tomcat version information.
    */
    private String verifyTomcat() {
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (jvmVersion == null || jvmVersion.equals(""))
            return "Could not detect JVM version from system properties.";

        String output = "";
        output += "JVM Version: " + jvmVersion + "<br />";
        output += "Tomcat version: " + tomcatVersion + "<br />";
        return output;
    }

    /*
    *  Extract Tomcat version number from server version information (via servlet).
    *
    *  @return value: Version number (7 or 8) of Tomcat. Return -1 if no match
    *  exists.
    */
    private int extractTomcatVersionNumber(String tomcatVersion) {
        Pattern tomcatVersionPattern = Pattern.compile(".*Tomcat/([0-9]).*");
        Matcher tomcatMatch = tomcatVersionPattern.matcher(tomcatVersion);
        if (tomcatMatch.matches()) {
            String version = tomcatMatch.group(1);
            return Integer.parseInt(version);
        } else {
            return -1;
        }
    }

    /*
    *  Verify the current Oscar instance. Check build, version, and the default 
    *  properties file in the WAR and the custom properties file in the appropriate
    *  Tomcat directory.
    *
    *  @return output: Combined output of Oscar build and properties information.
    */
    private String verifyOscar() {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("") 
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }
        if (webAppName == null || webAppName.equals(""))
            return "Could not detect the Oscar webapps directory name.";

        String output = "";
        // Tomcat 7
        if (extractTomcatVersionNumber(tomcatVersion) == 7) {
            output += "<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />";
            output += oscarBuild(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties");
            output += verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties");
            output += "<br /><b>Currently checking \"" + webAppName + ".properties\" file in \"catalina.home\" directory..." + "</b><br />";
            output += oscarBuild(catalinaHome.getPath() + "/" + webAppName + ".properties");
            output += verifyOscarProperties(catalinaHome.getPath() + "/" + webAppName + ".properties");
            output += "<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />";
        // Tomcat 8
        } else if (extractTomcatVersionNumber(tomcatVersion) == 8) {
            output += "<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />";
            output += oscarBuild(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties");
            output += verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties");
            output += "<br /><b>Currently checking \"" + webAppName + ".properties\" file in \"catalina.home\" directory..." + "</b><br />";
            output += oscarBuild(System.getProperty("user.home") + "/" + webAppName + ".properties");
            output += verifyOscarProperties(System.getProperty("user.home") + "/" + webAppName + ".properties");
            output += "<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />";
        // No Tomcat version found
        } else {
            output += "Could not detect Tomcat version number to determine audit check for Oscar properties.";
        }
        return output;
    }

    /*
    *  Read Oscar "buildtag" and "buildDateTime" of properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Current Oscar build, version, and date of build.
    */
    private String oscarBuild(String fileName) {
        try {
            String output = "";
            String line = "";
            ReversedLinesFileReader rf = new ReversedLinesFileReader(new File(fileName));
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternBuildtag = Pattern.compile("^(buildtag\\s?(=|:)).*");
            Pattern patternBuildDateTime = Pattern.compile("^(buildDateTime\\s?(=|:)).*");
            boolean flag1 = false;
            boolean flag2 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                Matcher matcherBuildtag = patternBuildtag.matcher(line);
                Matcher matcherBuildDateTime = patternBuildDateTime.matcher(line);
                if (matcherComment.matches()) continue;

                if (!flag1) {
                    if (matcherBuildtag.matches()) { // buildtag=
                        flag1 = true;
                        output += "Oscar build and version: " + line.substring(matcherBuildtag.group(1).length()) + "<br />";
                    }
                }
                if (!flag2) {
                    if (matcherBuildDateTime.matches()) { // buildDateTime=
                        flag2 = true;
                        output += "Oscar build date and time: " + line.substring(matcherBuildDateTime.group(1).length()) + "<br />";
                    }
                }
                if (flag1 && flag2)
                    break;
            }

            if (!flag1)
                output += "Could not detect Oscar build tag." + "<br />";
            if (!flag2)
                output += "Could not detect Oscar build date and time." + "<br />";
            return output;
        } catch (Exception e) {
            return "Could not read properties file to detect Oscar build.<br />";
        }
    }

    /*
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and "drugref_url" tags 
    *  of Oscar properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Output of the required tags in the Oscar properties file.
    */
    private String verifyOscarProperties(String fileName) {
        try {
            String output = "";
            String line = "";
            ReversedLinesFileReader rf = new ReversedLinesFileReader(new File(fileName));
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternHL7TEXT_LABS = Pattern.compile("^(HL7TEXT_LABS\\s?(=|:)).*");
            Pattern patternSINGLE_PAGE_CHART = Pattern.compile("^(SINGLE_PAGE_CHART\\s?(=|:)).*");
            Pattern patternTMP_DIR = Pattern.compile("^(TMP_DIR\\s?(=|:)).*");
            Pattern patternDrugrefUrl = Pattern.compile("^(drugref_url\\s?(=|:)).*");
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (matcherComment.matches()) continue;
                Matcher matcherHL7TEXT_LABS = patternHL7TEXT_LABS.matcher(line);
                Matcher matcherSINGLE_PAGE_CHART = patternSINGLE_PAGE_CHART.matcher(line);
                Matcher matcherTMP_DIR = patternTMP_DIR.matcher(line);
                Matcher matcherDrugrefUrl = patternDrugrefUrl.matcher(line);

                if (!flag1) {
                    if (matcherHL7TEXT_LABS.matches()) { // HL7TEXT_LABS=
                        flag1 = true;
                        output += "\"HL7TEXT_LABS\" tag is configured as: " + line.substring(matcherHL7TEXT_LABS.group(1).length()) + "<br />";
                    }
                }
                if (!flag2) {
                    if (matcherSINGLE_PAGE_CHART.matches()) { // SINGLE_PAGE_CHART=
                        flag2 = true;
                        output += "\"SINGLE_PAGE_CHART\" tag is configured as: " + line.substring(matcherSINGLE_PAGE_CHART.group(1).length()) + "<br />";
                    }
                }
                if (!flag3) {
                    if (matcherTMP_DIR.matches()) { // TMP_DIR=
                        flag3 = true;
                        output += "\"TMP_DIR\" tag is configured as: " + line.substring(matcherTMP_DIR.group(1).length()) + "<br />";
                    }
                }
                if (!flag4) {
                    if (matcherDrugrefUrl.matches()) { // drugref_url=
                        flag4 = true;
                        output += "\"drugref_url\" tag is configured as: " + line.substring(matcherDrugrefUrl.group(1).length()) + "<br />";
                        drugrefUrl = line.substring(12);
                    }
                }
                if (flag1 && flag2 && flag3 && flag4)
                    break;
            }
            if (!flag1)
                output += "Could not detect \"HL7TEXT_LABS\" tag." + "<br />";
            if (!flag2)
                output += "Could not detect \"SINGLE_PAGE_CHART\" tag." + "<br />";
            if (!flag3)
                output += "Could not detect \"TMP_DIR\" tag." + "<br />";
            if (!flag4)
                output += "Could not detect \"drugref_url\" tag." + "<br />";
            return output;
        } catch (Exception e) {
            return "Could not read properties file to verify Oscar tags. " + e.getMessage();
        }
    }

    /*
    *  Verify the current Drugref instance. Check the custom properties file found 
    *  in the appropriate Tomcat directory.
    *
    *  @return output: Output of Drugref properties information.
    */
    private String verifyDrugref() {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }
        if (drugrefUrl.equals("")) {
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        }

        // Grab deployed Drugref folder name and use as the file name for the properties file
        Pattern patternDrugrefUrl = Pattern.compile(".*://.*/(drugref.*)/.*");
        Matcher matcherDrugrefUrl = patternDrugrefUrl.matcher(drugrefUrl);
        if (matcherDrugrefUrl.matches()) {
            String output = "";
            // Tomcat 7
            if (extractTomcatVersionNumber(tomcatVersion) == 7) {
                output += "<b>Currently checking \"" + matcherDrugrefUrl.group(1) + ".properties\" file..." + "</b><br />";
                output += verifyDrugrefProperties(catalinaHome.getPath() + "/" + matcherDrugrefUrl.group(1) + ".properties");
            // Tomcat 8
            } else if (extractTomcatVersionNumber(tomcatVersion) == 8) {
                output += "<b>Currently checking \"" + matcherDrugrefUrl.group(1) + ".properties\" file..." + "</b><br />";
                output += verifyDrugrefProperties(System.getProperty("user.home") + "/" + matcherDrugrefUrl.group(1) + ".properties");
            // No Tomcat version found
            } else {
                output += "Could not detect Tomcat version number to determine audit check for Drugref properties.";
            }
            return output;
        } else {
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        }
    }

    /*
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Output of the required tags in the Drugref properties file.
    */
    private String verifyDrugrefProperties(String fileName) {
        try {
            String output = "";
            String line = "";
            ReversedLinesFileReader rf = new ReversedLinesFileReader(new File(fileName));
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternDb_user = Pattern.compile("^(db_user\\s?(=|:)).*");
            Pattern patternDb_url = Pattern.compile("^(db_url\\s?(=|:)).*");
            Pattern patternDb_driver = Pattern.compile("^(db_driver\\s?(=|:)).*");
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (Pattern.matches("^(#).*", line)) continue;
                Matcher matcherDb_user = patternDb_user.matcher(line);
                Matcher matcherDb_url = patternDb_url.matcher(line);
                Matcher matcherDb_driver = patternDb_driver.matcher(line);

                if (!flag1) {
                    if (matcherDb_user.matches()) { // db_user=
                        flag1 = true;
                        output += "\"db_user\" tag is configured as: " + line.substring(matcherDb_user.group(1).length()) + "<br />";
                    }
                }
                if (!flag2) {
                    if (matcherDb_url.matches()) { // db_url=
                        flag2 = true;
                        output += "\"db_url\" tag is configured as: " + line.substring(matcherDb_url.group(1).length()) + "<br />";
                    }
                }
                if (!flag3) {
                    if (matcherDb_driver.matches()) { // db_driver=
                        flag3 = true;
                        output += "\"db_driver\" tag is configured as: " + line.substring(matcherDb_driver.group(1).length()) + "<br />";
                    }
                }
                if (flag1 && flag2 && flag3)
                    break;
            }

            if (!flag1)
                output += "Could not detect \"db_user\" tag." + "<br />";
            if (!flag2)
                output += "Could not detect \"db_url\" tag." + "<br />";
            if (!flag3)
                output += "Could not detect \"db_driver\" tag." + "<br />";
            return output;
        } catch (Exception e) {
            return "Could not read properties file to verify Drugref tags.";
        }
    }

    /*
    *  Read through the Tomcat settings file and echo the Xmx and Xms values to 
    *  the user.
    *
    *  @return output: Xmx value (maximum memory allocation) and Xms value (minimum 
    *  memory allocation) for JVM heap size.
    */
    private String tomcatReinforcement() {
        if (catalinaBase == null || catalinaBase.getPath().equals(""))
            return "Please verify that your \"catalina.base\" directory is setup correctly.";

        try {
            // Determine which version of Tomcat settings file to check
            int version = extractTomcatVersionNumber(tomcatVersion);
            tomcatSettings = getTomcatSettings(version);
            if (tomcatSettings == null || tomcatSettings.getPath().equals(""))
                return "Could not detect Tomcat settings file."; 

            ReversedLinesFileReader rf = new ReversedLinesFileReader(tomcatSettings);
            String output = "";
            String line = "";
            boolean flag1 = false;
            boolean flag2 = false;
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternXmx = Pattern.compile(".*(Xmx[0-9]+m).*");
            Pattern patternXms = Pattern.compile(".*(Xms[0-9]+m).*");

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (matcherComment.matches()) continue;
                Matcher matcherXmx = patternXmx.matcher(line);
                Matcher matcherXms = patternXms.matcher(line);

                if (!flag1) {
                    if (matcherXmx.matches()) { // e.g. Xmx2056m
                        flag1 = true;
                        output += "Xmx value: " + matcherXmx.group(1).substring(3) + "<br />";
                    }
                }
                if (!flag2) {
                    if (matcherXms.matches()) { // e.g. Xms1024m
                        flag2 = true;
                        output += "Xms value: " + matcherXms.group(1).substring(3) + "<br />";
                    }
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
    
    /****************************************************
     *                                                  *
     *     METHODS USED FOR OSCAR REST WEB SERVICE      *
     *                                                  *
     ***************************************************/

    /*
    *  Public method for retrieving server version.
    *
    *  @return output: Linux server version.
    */
    public String getServerVersion() {
        return serverVersion();
    }

    /*
    *  Public method for retrieving database type and version.
    *
    *  @return output: Database type and version.
    */
    public String getDatabaseInfo() {
        return databaseInfo();
    }

    /*
    *  Public method for retrieving JVM version, Tomcat version from servlet 
    *  context, and maximum/minimum heap size allocated for Tomcat.
    *
    *  @param tomcatVersion: Tomcat version from HTTP session. 
    *  @return output: JVM version, Tomcat version, and max/min heap size.
    */
    public String getTomcatInfo(String tomcatVersion) {
        this.tomcatVersion = tomcatVersion;
        return verifyTomcat() + "\n" + tomcatReinforcement();
    }

    /*
    *  Public method for retrieving Oscar build tag and date. Also, property
    *  values of "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and "drugref_url" 
    *  tags.
    *
    *  @param tomcatVersion: Tomcat version from HTTP session. 
    *  @param webAppName: Oscar web application name from HTTP session.
    *  @return output: Output of tags in Oscar properties file.
    */
    public String getOscarInfo(String tomcatVersion, String webAppName) {
        this.tomcatVersion = tomcatVersion;
        this.webAppName = webAppName;
        return verifyOscar();
    }

    /*
    *  Public method for retrieving Drugref property values of "db_user,"
    *  "db_user", and "db_driver" tags.
    *
    *  @param tomcatVersion: Tomcat version from HTTP session. 
    *  @param webAppName: Oscar web application name from HTTP session.
    *  @return output: Output of tags in Drugref properties file.
    */
    public String getDrugrefInfo(String tomcatVersion, String webAppName) {
        this.tomcatVersion = tomcatVersion;
        this.webAppName = webAppName;
        return verifyDrugref();
    }
}
