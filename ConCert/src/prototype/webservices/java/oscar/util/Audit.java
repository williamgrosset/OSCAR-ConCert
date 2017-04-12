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

import org.oscarehr.util.DbConnectionFilter;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
*  Class that audits a live OSCAR application and contains information
*  regarding OSCAR's application environment.
*
*  NOTE: Permission checks using SecurityInfoManager.class with an HTTP 
*  session should be done before accessing auditing information.
*  
*  github.com/williamgrosset
*/
public class Audit {

    // Utility variables for Audit class
    private File catalinaBase;
    private File catalinaHome;
    private File lsbRelease;
    private File tomcatSettings;
    private Connection connection;

    // Property tags for Audit state
    private String serverVersion;
    private String dbType;
    private String dbVersion;
    private String jvmVersion;
    private String tomcatVersion;
    private String webAppName;
    private String xmx;
    private String xms;
    private String build;
    private String buildDate;
    private String hl7TextLabs;
    private String singlePageChart;
    private String tmpDir;
    private String drugrefUrl;
    private String dbUser;
    private String dbUrl;
    private String dbDriver;

    public Audit() {
        catalinaBase = getCatalinaBase();
        catalinaHome = getCatalinaHome();
        lsbRelease = getLsbRelease();
        tomcatSettings = getTomcatSettings(7);
        jvmVersion = systemGetJvmVersion();
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public String getDbType() {
        return this.dbType;
    }

    public String getDbVersion() {
        return this.dbVersion;
    }

    public String getJvmVersion() {
        return this.jvmVersion;
    }

    public String getTomcatVersion() {
        return this.tomcatVersion;
    }

    public String getWebAppName() {
        return this.webAppName;
    }

    public String getXmx() {
        return this.xmx;
    }

    public String getXms() {
        return this.xms;
    }

    public String getBuild() {
        return this.build;
    }

    public String getBuildDate() {
        return this.buildDate;
    }

    public String getHl7TextLabs() {
        return this.hl7TextLabs;
    }

    public String getSinglePageChart() {
        return this.singlePageChart;
    }

    public String getTmpDir() {
        return this.tmpDir;
    }
    
    public String getDrugrefUrl() {
        return this.drugrefUrl;
    }

    public String getDbUser() {
        return this.dbUser;
    }

    public String getDbUrl() {
        return this.dbUrl; 
    }

    public String getDbDriver() {
        return this.dbDriver;
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
            // Version not supported
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
    private String systemGetJvmVersion() {
        try {
            return System.getProperty("java.version");
        } catch (Exception e) {
            return "";
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
    public String serverVersion() {
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
                    String serverVersion = line.substring(matcherDIST_DESC.group(1).length()); 
                    this.serverVersion = serverVersion;
                    return "Version: " + this.serverVersion;
                }
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
    public String databaseInfo() {
        try {
            connection = DbConnectionFilter.getThreadLocalDbConnection();
            if (connection == null) throw new NullPointerException();

            StringBuilder output = new StringBuilder();
            DatabaseMetaData metaData = connection.getMetaData();
            String dbType = metaData.getDatabaseProductName();
            String dbVersion = metaData.getDatabaseProductVersion();

            this.dbType = dbType;
            this.dbVersion = dbVersion;

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
    

    /*
    *  Extract JVM version from system properties and server version information 
    *  from servlet.
    *
    *  @param tomcatVersion: Tomcat version.
    *  @return output: JVM and Tomcat version information.
    */
    public String verifyTomcat(String tomcatVersion) {
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (jvmVersion == null || jvmVersion.equals(""))
            return "Could not detect JVM version from system properties.";

        this.jvmVersion = jvmVersion;
        this.tomcatVersion = tomcatVersion;

        StringBuilder output = new StringBuilder();
        output.append("JVM Version: " + this.jvmVersion + "<br />");
        output.append("Tomcat version: " + this.tomcatVersion);
        return output.toString();
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
    *  @param tomcatVersion: Tomcat version.
    *  @param webAppName: Web application name for OSCAR.
    *  @return output: Combined output of Oscar build and properties information.
    */
    public String verifyOscar(String tomcatVersion, String webAppName) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("") 
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (webAppName == null || webAppName.equals(""))
            return "Could not detect the Oscar webapps directory name.";

        this.tomcatVersion = tomcatVersion;
        this.webAppName = webAppName;

        StringBuilder output = new StringBuilder();
        // Tomcat 7
        if (extractTomcatVersionNumber(tomcatVersion) == 7) {
            output.append("<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />");
            output.append(oscarBuild(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
            output.append(verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
            output.append("<br /><b>Currently checking \"" + webAppName + ".properties\" file in \"catalina.home\" directory..." + "</b><br />");
            output.append(oscarBuild(catalinaHome.getPath() + "/" + webAppName + ".properties"));
            output.append(verifyOscarProperties(catalinaHome.getPath() + "/" + webAppName + ".properties"));
            output.append("<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />");
        // Tomcat 8
        } else if (extractTomcatVersionNumber(tomcatVersion) == 8) {
            output.append("<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR..." + "</b><br />");
            output.append(oscarBuild(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
            output.append(verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
            output.append("<br /><b>Currently checking \"" + webAppName + ".properties\" file in \"catalina.home\" directory..." + "</b><br />");
            output.append(oscarBuild(System.getProperty("user.home") + "/" + webAppName + ".properties"));
            output.append(verifyOscarProperties(System.getProperty("user.home") + "/" + webAppName + ".properties"));
            output.append("<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />");
        // No Tomcat version found
        } else {
            output.append("Could not detect Tomcat version number to determine audit check for Oscar properties.");
        }
        return output.toString();
    }

    /*
    *  Read Oscar "buildtag" and "buildDateTime" of properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Current Oscar build, version, and date of build.
    */
    private String oscarBuild(String fileName) {
        try {
            if (fileName == null || fileName.equals(""))
                return "Could not detect filename for properties file.";

            String line = "";
            StringBuilder output = new StringBuilder();
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
                        String build = line.substring(matcherBuildtag.group(1).length());
                        this.build = build;
                        flag1 = true;
                        output.append("Oscar build and version: " + this.build + "<br />");
                    }
                }
                if (!flag2) {
                    if (matcherBuildDateTime.matches()) { // buildDateTime=
                        String buildDate = line.substring(matcherBuildDateTime.group(1).length());
                        this.buildDate = buildDate;
                        flag2 = true;
                        output.append("Oscar build date and time: " + this.buildDate + "<br />");
                    }
                }
                if (flag1 && flag2)
                    break;
            }

            if (!flag1)
                output.append("Could not detect Oscar build tag." + "<br />");
            if (!flag2)
                output.append("Could not detect Oscar build date and time." + "<br />");
            return output.toString();
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
            if (fileName == null || fileName.equals(""))
                return "Could not detect filename for properties file.";

            String line = "";
            StringBuilder output = new StringBuilder();
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
                        String hl7TextLabs = line.substring(matcherHL7TEXT_LABS.group(1).length());
                        this.hl7TextLabs = hl7TextLabs;
                        flag1 = true;
                        output.append("\"HL7TEXT_LABS\" tag is configured as: " + this.hl7TextLabs + "<br />");
                    }
                }
                if (!flag2) {
                    if (matcherSINGLE_PAGE_CHART.matches()) { // SINGLE_PAGE_CHART=
                        String singlePageChart = line.substring(matcherSINGLE_PAGE_CHART.group(1).length());
                        this.singlePageChart = singlePageChart;
                        flag2 = true;
                        output.append("\"SINGLE_PAGE_CHART\" tag is configured as: " + this.singlePageChart + "<br />");
                    }
                }
                if (!flag3) {
                    if (matcherTMP_DIR.matches()) { // TMP_DIR=
                        String tmpDir = line.substring(matcherTMP_DIR.group(1).length());
                        this.tmpDir = tmpDir;
                        flag3 = true;
                        output.append("\"TMP_DIR\" tag is configured as: " + this.tmpDir + "<br />");
                    }
                }
                if (!flag4) {
                    if (matcherDrugrefUrl.matches()) { // drugref_url=
                        String drugrefUrl = line.substring(matcherDrugrefUrl.group(1).length());
                        this.drugrefUrl = drugrefUrl;
                        flag4 = true;
                        output.append("\"drugref_url\" tag is configured as: " + this.drugrefUrl + "<br />");
                    }
                }
                if (flag1 && flag2 && flag3 && flag4)
                    break;
            }
            if (!flag1)
                output.append("Could not detect \"HL7TEXT_LABS\" tag." + "<br />");
            if (!flag2)
                output.append("Could not detect \"SINGLE_PAGE_CHART\" tag." + "<br />");
            if (!flag3)
                output.append("Could not detect \"TMP_DIR\" tag." + "<br />");
            if (!flag4)
                output.append("Could not detect \"drugref_url\" tag." + "<br />");
            return output.toString();
        } catch (Exception e) {
            return "Could not read properties file to verify Oscar tags. " + e.getMessage();
        }
    }

    /*
    *  Verify the current Drugref instance. Check the custom properties file found 
    *  in the appropriate Tomcat directory.
    *
    *  @param tomcatVersion: Tomcat version.
    *  @param webAppName: Web application name for OSCAR.
    *  @return output: Output of Drugref properties information.
    */
    public String verifyDrugref(String tomcatVersion, String webAppName) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals("")) {
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        }
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (drugrefUrl.equals("")) {
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        }

        this.tomcatVersion = tomcatVersion;
        this.webAppName = webAppName;

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

    /*
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref properties file.
    *
    *  @param fileName: Path to properties file.
    *  @return output: Output of the required tags in the Drugref properties file.
    */
    private String verifyDrugrefProperties(String fileName) {
        try {
            if (fileName == null || fileName.equals(""))
                return "Could not detect filename for properties file.";

            String line = "";
            StringBuilder output = new StringBuilder();
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
                        String dbUser = line.substring(matcherDb_user.group(1).length());
                        this.dbUser = dbUser;
                        flag1 = true;
                        output.append("\"db_user\" tag is configured as: " + this.dbUser + "<br />");
                    }
                }
                if (!flag2) {
                    if (matcherDb_url.matches()) { // db_url=
                        String dbUrl = line.substring(matcherDb_url.group(1).length());
                        this.dbUrl = dbUrl;
                        flag2 = true;
                        output.append("\"db_url\" tag is configured as: " + this.dbUrl + "<br />");
                    }
                }
                if (!flag3) {
                    if (matcherDb_driver.matches()) { // db_driver=
                        String dbDriver = line.substring(matcherDb_driver.group(1).length());
                        this.dbDriver = dbDriver; 
                        flag3 = true;
                        output.append("\"db_driver\" tag is configured as: " + this.dbDriver + "<br />");
                    }
                }
                if (flag1 && flag2 && flag3)
                    break;
            }

            if (!flag1)
                output.append("Could not detect \"db_user\" tag." + "<br />");
            if (!flag2)
                output.append("Could not detect \"db_url\" tag." + "<br />");
            if (!flag3)
                output.append("Could not detect \"db_driver\" tag." + "<br />");
            return output.toString();
        } catch (Exception e) {
            return "Could not read properties file to verify Drugref tags.";
        }
    }

    /*
    *  Read through the Tomcat settings file and echo the Xmx and Xms values to 
    *  the user.
    *
    *  @param tomcatVersion: Tomcat version.
    *  @return output: Xmx value (maximum memory allocation) and Xms value (minimum 
    *  memory allocation) for JVM heap size.
    */
    public String tomcatReinforcement(String tomcatVersion) {
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (catalinaBase == null || catalinaBase.getPath().equals(""))
            return "Please verify that your \"catalina.base\" directory is setup correctly.";

        this.tomcatVersion = tomcatVersion;

        try {
            // Determine which version of Tomcat settings file to check
            int version = extractTomcatVersionNumber(tomcatVersion);
            tomcatSettings = getTomcatSettings(version);
            if (tomcatSettings == null || tomcatSettings.getPath().equals(""))
                return "Could not detect Tomcat settings file."; 

            String line = "";
            StringBuilder output = new StringBuilder();
            ReversedLinesFileReader rf = new ReversedLinesFileReader(tomcatSettings);
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternXmx = Pattern.compile(".*(Xmx[0-9]+m).*");
            Pattern patternXms = Pattern.compile(".*(Xms[0-9]+m).*");
            boolean flag1 = false;
            boolean flag2 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (matcherComment.matches()) continue;
                Matcher matcherXmx = patternXmx.matcher(line);
                Matcher matcherXms = patternXms.matcher(line);

                if (!flag1) {
                    if (matcherXmx.matches()) { // e.g. Xmx2056m
                        String xmx = matcherXmx.group(1).substring(3);
                        this.xmx = xmx;
                        flag1 = true;
                        output.append("Xmx value: " + this.xmx + "<br />");
                    }
                }
                if (!flag2) {
                    if (matcherXms.matches()) { // e.g. Xms1024m
                        String xms = matcherXms.group(1).substring(3);
                        this.xms = xms;
                        flag2 = true;
                        output.append("Xms value: " + this.xms + "<br />");
                    }
                }
                if (flag1 && flag2)
                    break;
            }

            if (!flag1) {
                output.append("Could not detect Xmx value." + "<br />");
            }
            if (!flag2) {
                output.append("Could not detect Xms value." + "<br />");
            }
            return output.toString();
        } catch (Exception e) {
            return "Could not detect Tomcat memory allocation in Tomcat settings file.";
        }
    }
}
