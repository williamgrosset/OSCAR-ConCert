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
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtilsOld;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
*  Class for auditing a live OSCAR application.
*
*  Author: github.com/williamgrosset
*/
public class Audit {

    // Utility variables for Audit class
    private File catalinaBase;
    private File catalinaHome;
    private File lsbRelease;
    private File tomcatSettings;
    private Connection connection;
    private static Logger logger = MiscUtilsOld.getLogger();

    // Property tags for Audit state
    private String systemVersion;
    private String dbType;
    private String dbVersion;
    private String jvmVersion;
    private String tomcatVersion;
    private String xmx;
    private String xms;
    private String webAppName;
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
        jvmVersion = System.getProperty("java.version");
    }

    public String getSystemVersion() {
        return this.systemVersion;
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

    public String getXmx() {
        return this.xmx;
    }

    public String getXms() {
        return this.xms;
    }

    public String getWebAppName() {
        return this.webAppName;
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
            logger.error(e.getStackTrace());
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
            logger.error(e.getStackTrace());
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
            logger.error(e.getStackTrace());
            return new File("");
        }
    }

    /*
    *  Retrieve settings file from appropriate Tomcat directory
    *  (currently supports version 7 and 8 of Tomcat).
    *
    *  @param version:         Version of Tomcat.
    *
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
                // Version not supported
                return new File("");
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return new File("");
        }
    }

    /*
    *  Read "/etc/lsb-release" file and extract Linux distribution version. The
    *  file should be available on Ubuntu and Debian distributions.
    *
    *  NOTE: Majority of my methods require the ReversedLinesFilerReader class.
    *  Since I did not want to require another import for the default file reader,
    *  I went ahead and used ReversedLinesFileReader anyways.
    *
    *  @return output: Linux distribution version.
    */
    public String verifySystemInfo() {
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
            logger.error(e.getStackTrace());
            return "Could not read \"lsb-release\" file to detect Linux server version.";
        }
    }

    /*
    *  Establish a connection to configured database and retrieve the database type 
    *  and version from the DatabaseMetaData object.
    *
    *  @return output: Database type and version.
    */
    public String verifyDatabaseInfo() {
        try {
            connection = DbConnectionFilter.getThreadLocalDbConnection();
            if (connection == null) throw new NullPointerException();

            StringBuilder output = new StringBuilder();
            DatabaseMetaData metaData = connection.getMetaData();
            this.dbType = metaData.getDatabaseProductName().trim();
            this.dbVersion = metaData.getDatabaseProductVersion().trim();

            output.append("Type: " + this.dbType + "\n");
            output.append("Version: " + this.dbVersion);
            return output.toString();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return "Cannot determine database type and version.";
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error(e.getStackTrace());
                    return "Cannot close connection to database.";
                }
            }
        }
    }
    
    /*
    *  Retrieve Tomcat server version information from servlet.
    *
    *  @param tomcatVersion: Tomcat version.
    *
    *  @return output:       Tomcat version information.
    */
    public String verifyTomcatVersion(String tomcatVersion) {
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";

        this.tomcatVersion = tomcatVersion;
        return this.tomcatVersion;
    }

    /*
    *  Read through the Tomcat settings file and grab both Xmx and Xms values.
    *
    *  @param tomcatVersion: Tomcat version.
    *
    *  @return output:       Xmx value (maximum memory allocation) and Xms value 
    *                        (minimum memory allocation) for JVM heap size.
    */
    public String verifyTomcatReinforcement(String tomcatVersion) {
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
            boolean flag1 = false, flag2 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (matcherComment.matches()) continue;
                Matcher matcherXmx = patternXmx.matcher(line);
                Matcher matcherXms = patternXms.matcher(line);

                if (!flag1 && matcherXmx.matches()) { // e.g. Xmx2056m
                    flag1 = true;
                    this.xmx = matcherXmx.group(1).substring(3);
                    output.append("Xmx value: " + this.xmx + "\n");
                }
                if (!flag2 && matcherXms.matches()) { // e.g. Xms1024m
                    flag2 = true;
                    this.xms = matcherXms.group(1).substring(3);
                    output.append("Xms value: " + this.xms + "\n");
                }
                if (flag1 && flag2)
                    break;
            }

            if (!flag1)
                output.append("Could not detect Xmx value." + "\n");
            if (!flag2)
                output.append("Could not detect Xms value." + "\n");
            return output.toString();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return "Could not detect Tomcat memory allocation in Tomcat settings file.";
        }
    }

    /*
    *  Verify the current Oscar instance. Check build, version, and the default 
    *  properties file in the WAR and the custom properties file in the appropriate
    *  Tomcat directory.
    *
    *  @param tomcatVersion: Tomcat version.
    *  @param webAppName:    Web application name for OSCAR.
    *  @param isMcmaster:    Boolean to check default McMaster properties file.
    *
    *  @return output:       Oscar build and properties information.
    */
    public String verifyOscar(String tomcatVersion, String webAppName, boolean isMcmaster) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("") 
                || catalinaHome.getPath().equals(""))
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (webAppName == null || webAppName.equals(""))
            return "Could not detect the Oscar webapps directory name.";

        this.webAppName = webAppName;
        StringBuilder output = new StringBuilder();

        if (extractTomcatVersionNumber(tomcatVersion) == 7 && isMcmaster)
            output.append(verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
        else if (extractTomcatVersionNumber(tomcatVersion) == 7 && !isMcmaster)
            output.append(verifyOscarProperties(catalinaHome.getPath() + "/" + webAppName + ".properties"));
        else if (extractTomcatVersionNumber(tomcatVersion) == 8 && isMcmaster)
            output.append(verifyOscarProperties(catalinaBase.getPath() + "/webapps/" + webAppName + "/WEB-INF/classes/oscar_mcmaster.properties"));
        else if (extractTomcatVersionNumber(tomcatVersion) == 8 && !isMcmaster)
            output.append(verifyOscarProperties(System.getProperty("user.home") + "/" + webAppName + ".properties"));
        else
            output.append("Could not detect Tomcat version number to determine audit check for Oscar properties.");

        return output.toString();
    }

    /*
    *  Read "buildtag", "buildDateTime", "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," 
    *  and "drugref_url" tags of Oscar properties file.
    *
    *  @param filename: Path to properties file.
    *
    *  @return output:  Output of the required tags in the Oscar properties file.
    */
    private String verifyOscarProperties(String filename) {
        try {
            if (filename == null || filename.equals(""))
                return "Could not detect filename for properties file.";

            String line = "";
            StringBuilder output = new StringBuilder();
            ReversedLinesFileReader rf = new ReversedLinesFileReader(new File(filename));
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternBuildtag = Pattern.compile("^(buildtag\\s?(=|:)).*");
            Pattern patternBuildDateTime = Pattern.compile("^(buildDateTime\\s?(=|:)).*");
            Pattern patternHL7TEXT_LABS = Pattern.compile("^(HL7TEXT_LABS\\s?(=|:)).*");
            Pattern patternSINGLE_PAGE_CHART = Pattern.compile("^(SINGLE_PAGE_CHART\\s?(=|:)).*");
            Pattern patternTMP_DIR = Pattern.compile("^(TMP_DIR\\s?(=|:)).*");
            Pattern patternDrugrefUrl = Pattern.compile("^(drugref_url\\s?(=|:)).*");
            boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false, flag5 = false, flag6 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (matcherComment.matches()) continue;
                Matcher matcherBuildtag = patternBuildtag.matcher(line);
                Matcher matcherBuildDateTime = patternBuildDateTime.matcher(line);
                Matcher matcherHL7TEXT_LABS = patternHL7TEXT_LABS.matcher(line);
                Matcher matcherSINGLE_PAGE_CHART = patternSINGLE_PAGE_CHART.matcher(line);
                Matcher matcherTMP_DIR = patternTMP_DIR.matcher(line);
                Matcher matcherDrugrefUrl = patternDrugrefUrl.matcher(line);

                if (!flag1 && matcherBuildtag.matches()) { // buildtag=
                    flag1 = true;
                    this.build = line.substring(matcherBuildtag.group(1).length()).trim();
                    output.append("Oscar build and version: " + this.build + "\n");
                } else if (!flag2 && matcherBuildDateTime.matches()) { // buildDateTime=
                    flag2 = true;
                    this.buildDate = line.substring(matcherBuildDateTime.group(1).length()).trim();
                    output.append("Oscar build date and time: " + this.buildDate + "\n");
                } else if (!flag3 && matcherHL7TEXT_LABS.matches()) { // HL7TEXT_LABS=
                    flag3 = true;
                    this.hl7TextLabs = line.substring(matcherHL7TEXT_LABS.group(1).length()).trim();
                    output.append("\"HL7TEXT_LABS\" tag is configured as: " + this.hl7TextLabs + "\n");
                } else if (!flag4 && matcherSINGLE_PAGE_CHART.matches()) { // SINGLE_PAGE_CHART=
                    flag4 = true;
                    this.singlePageChart = line.substring(matcherSINGLE_PAGE_CHART.group(1).length()).trim();
                    output.append("\"SINGLE_PAGE_CHART\" tag is configured as: " + this.singlePageChart + "\n");
                } else if (!flag5 && matcherTMP_DIR.matches()) { // TMP_DIR=
                    flag5 = true;
                    this.tmpDir = line.substring(matcherTMP_DIR.group(1).length()).trim();
                    output.append("\"TMP_DIR\" tag is configured as: " + this.tmpDir + "\n");
                } else if (!flag6 && matcherDrugrefUrl.matches()) { // drugref_url=
                    flag6 = true;
                    this.drugrefUrl = line.substring(matcherDrugrefUrl.group(1).length()).trim();
                    output.append("\"drugref_url\" tag is configured as: " + this.drugrefUrl + "\n");
                } else {
                    if (flag1 && flag2 && flag3 && flag4 && flag5 && flag6)
                        break;
                }
            }

            if (!flag1)
                output.append("Could not detect Oscar build tag." + "\n");
            if (!flag2)
                output.append("Could not detect Oscar build date and time." + "\n");
            if (!flag3)
                output.append("Could not detect \"HL7TEXT_LABS\" tag." + "\n");
            if (!flag4)
                output.append("Could not detect \"SINGLE_PAGE_CHART\" tag." + "\n");
            if (!flag5)
                output.append("Could not detect \"TMP_DIR\" tag." + "\n");
            if (!flag6)
                output.append("Could not detect \"drugref_url\" tag." + "\n");
            return output.toString();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return "Could not read properties file to verify Oscar tags.";
        }
    }

    /*
    *  Verify the current Drugref instance. Check the custom properties file found 
    *  in the appropriate Tomcat directory.
    *
    *  @param tomcatVersion: Tomcat version.
    *
    *  @return output:       Output of Drugref properties information.
    */
    public String verifyDrugref(String tomcatVersion) {
        if (catalinaBase == null || catalinaHome == null || catalinaBase.getPath().equals("")
                || catalinaHome.getPath().equals(""))
            return "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        if (tomcatVersion == null || tomcatVersion.equals(""))
            return "Could not detect Tomcat version.";
        if (drugrefUrl == null || drugrefUrl.equals(""))
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";

        // Grab deployed Drugref folder name and use as the file name for the properties file
        Pattern patternDrugrefUrl = Pattern.compile(".*://.*/(drugref.*)/.*");
        Matcher matcherDrugrefUrl = patternDrugrefUrl.matcher(drugrefUrl);

        if (matcherDrugrefUrl.matches()) {
            StringBuilder output = new StringBuilder();

            if (extractTomcatVersionNumber(tomcatVersion) == 7)
                output.append(verifyDrugrefProperties(catalinaHome.getPath() + "/" + matcherDrugrefUrl.group(1) + ".properties"));
            else if (extractTomcatVersionNumber(tomcatVersion) == 8)
                output.append(verifyDrugrefProperties(System.getProperty("user.home") + "/" + matcherDrugrefUrl.group(1) + ".properties"));
            else
                output.append("Could not detect Tomcat version number to determine audit check for Drugref properties.");

            return output.toString();
        } else {
            return "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        }
    }

    /*
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref properties file.
    *
    *  @param filename: Path to properties file.
    *
    *  @return output:  Output of the required tags in the Drugref properties file.
    */
    private String verifyDrugrefProperties(String filename) {
        try {
            if (filename == null || filename.equals(""))
                return "Could not detect filename for properties file.";

            String line = "";
            StringBuilder output = new StringBuilder();
            ReversedLinesFileReader rf = new ReversedLinesFileReader(new File(filename));
            Pattern patternComment = Pattern.compile("^(#).*");
            Pattern patternDb_user = Pattern.compile("^(db_user\\s?(=|:)).*");
            Pattern patternDb_url = Pattern.compile("^(db_url\\s?(=|:)).*");
            Pattern patternDb_driver = Pattern.compile("^(db_driver\\s?(=|:)).*");
            boolean flag1 = false, flag2 = false, flag3 = false;

            while ((line = rf.readLine()) != null) {
                Matcher matcherComment = patternComment.matcher(line);
                if (Pattern.matches("^(#).*", line)) continue;
                Matcher matcherDb_user = patternDb_user.matcher(line);
                Matcher matcherDb_url = patternDb_url.matcher(line);
                Matcher matcherDb_driver = patternDb_driver.matcher(line);

                if (!flag1 && matcherDb_user.matches()) { // db_user=
                    flag1 = true;
                    this.dbUser = line.substring(matcherDb_user.group(1).length()).trim();
                    output.append("\"db_user\" tag is configured as: " + this.dbUser + "\n");
                } else if (!flag2 && matcherDb_url.matches()) { // db_url=
                    flag2 = true;
                    this.dbUrl = line.substring(matcherDb_url.group(1).length()).trim();
                    output.append("\"db_url\" tag is configured as: " + this.dbUrl + "\n");
                } else if (!flag3 && matcherDb_driver.matches()) { // db_driver=
                    flag3 = true;
                    this.dbDriver = line.substring(matcherDb_driver.group(1).length()).trim();
                    output.append("\"db_driver\" tag is configured as: " + this.dbDriver + "\n");
                } else {
                    if (flag1 && flag2 && flag3)
                        break;
                }
            }

            if (!flag1)
                output.append("Could not detect \"db_user\" tag." + "\n");
            if (!flag2)
                output.append("Could not detect \"db_url\" tag." + "\n");
            if (!flag3)
                output.append("Could not detect \"db_driver\" tag." + "\n");
            return output.toString();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return "Could not read properties file to verify Drugref tags.";
        }
    }

    /*
    *  Extract Tomcat version number from server version information (via servlet).
    *
    *  @return value: Version number (7 or 8) of Tomcat. Return -1 if no match
    *                 exists.
    */
    private int extractTomcatVersionNumber(String tomcatVersion) {
        Pattern tomcatVersionPattern = Pattern.compile(".*Tomcat/([0-9]).*");
        Matcher tomcatMatch = tomcatVersionPattern.matcher(tomcatVersion);

        if (tomcatMatch.matches())
            return Integer.parseInt(tomcatMatch.group(1));
        else
            return -1;
    }
}
