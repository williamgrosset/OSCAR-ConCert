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
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.apache.commons.io.FileUtils;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

/*
*  github.com/williamgrosset
*/
public class AuditTest {

    Audit audit = new Audit();
    private Field catalinaBase;
    private Field catalinaHome;
    private Field lsbRelease;
    private Field tomcatSettings;
    private Field jvmVersion;
    private Field tomcatVersion;
    private Field webAppName;
    private Field drugrefUrl;

    @Before
    public void initialize() throws IOException, NoSuchFieldException, IllegalAccessException {
        File catalinaBaseFolder = Files.createTempDirectory("catalinaBase").toFile();
        File catalinaHomeFolder = Files.createTempDirectory("catalinaHome").toFile();
        File lsbReleaseFile = File.createTempFile("fakelsbReleaseFile", null, new File("/tmp"));
        File tomcatSettingsFile = File.createTempFile("tomcat411", null, new File("/tmp"));
        String jvmVersionValue = "1.7.0_111";
        String tomcatVersionValue = "Apache Tomcat/7.0.52 (Ubuntu)";
        String webAppNameValue = "oscar15";
        String drugrefUrlValue = "http://localhost:8080/drugref/DrugrefService";

        catalinaBase = audit.getClass().getDeclaredField("catalinaBase");
        catalinaHome = audit.getClass().getDeclaredField("catalinaHome");
        lsbRelease = audit.getClass().getDeclaredField("lsbRelease");
        tomcatSettings = audit.getClass().getDeclaredField("tomcatSettings");
        jvmVersion = audit.getClass().getDeclaredField("jvmVersion");
        tomcatVersion = audit.getClass().getDeclaredField("tomcatVersion");
        webAppName = audit.getClass().getDeclaredField("webAppName");
        drugrefUrl = audit.getClass().getDeclaredField("drugrefUrl");

        catalinaBase.setAccessible(true);
        catalinaHome.setAccessible(true);
        lsbRelease.setAccessible(true);
        tomcatSettings.setAccessible(true);
        jvmVersion.setAccessible(true);
        tomcatVersion.setAccessible(true);
        webAppName.setAccessible(true);
        drugrefUrl.setAccessible(true);

        catalinaBase.set(audit, catalinaBaseFolder);
        catalinaHome.set(audit, catalinaHomeFolder);
        lsbRelease.set(audit, lsbReleaseFile);
        tomcatSettings.set(audit, tomcatSettingsFile);
        jvmVersion.set(audit, jvmVersionValue);
        tomcatVersion.set(audit, tomcatVersionValue);
        webAppName.set(audit, webAppNameValue);
        drugrefUrl.set(audit, drugrefUrlValue);
    }

    /*
    *  serverVersion():
    *  Read "/etc/lsb-release" file and extract Linux server version. The
    *  file should be available on Ubuntu and Debian distributions.
    */
 
    @Test
    public void isMatchTrueServerVersion() throws IOException, IllegalAccessException {
        FileUtils.writeStringToFile((File)lsbRelease.get(audit), "DISTRIB_DESCRIPTION=\"Ubuntu 14.04.5 LTS\"");
        String expectedResult = "Version: \"Ubuntu 14.04.5 LTS\"";
        assertEquals(expectedResult,audit.serverVersion());
    }

    @Test
    public void isMatchFalseServerVersion() throws IOException, IllegalAccessException {
        FileUtils.writeStringToFile((File)lsbRelease.get(audit), "randomtag=");
        String expectedResult = "Could not detect Linux server version.";
        assertEquals(expectedResult, audit.serverVersion());
    }

    @Test
    public void emptyPathServerVersion() throws IOException, IllegalAccessException {
        lsbRelease.set(audit, new File(""));
        String expectedResult = "Could not read \"lsb-release\" file to detect Linux server version.";      
        assertEquals(expectedResult, audit.serverVersion());
    }
    
    @Test
    public void nullServerVersion() throws IOException, IllegalAccessException {
        lsbRelease.set(audit, null);
        String expectedResult = "Could not read \"lsb-release\" file to detect Linux server version.";      
        assertEquals(expectedResult, audit.serverVersion());
    }

    /*
    *  databaseInfo():
    *  Establish a connection to our database and retrieve the database type and 
    *  version from our DatabaseMetaData object.
    */

    /******* TEST METHODS HERE *******/

    /*
    *  verifyTomcat():
    *  Extract JVM version from system properties and server version information 
    *  from servlet.
    */

    @Test
    public void matchVerifyTomcat() throws IOException, IllegalAccessException {
        String expectedResult = "JVM Version: " + jvmVersion.get(audit) + "<br />"
                                    + "Tomcat version: " + tomcatVersion.get(audit) + "<br />";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    @Test
    public void emptyTomcatVersionVerifyTomcat() throws IllegalAccessException {
        tomcatVersion.set(audit, "");
        String expectedResult = "Could not detect Tomcat version.";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    @Test
    public void emptyJvmVersionVerifyTomcat() throws IllegalAccessException {
        jvmVersion.set(audit, "");
        String expectedResult = "Could not detect JVM version from system properties.";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    @Test
    public void nullTomcatVersionVerifyTomcat() throws IllegalAccessException {
        tomcatVersion.set(audit, null);
        String expectedResult = "Could not detect Tomcat version.";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    @Test
    public void nullJvmVersionVerifyTomcat() throws IllegalAccessException {
        jvmVersion.set(audit, null);
        String expectedResult = "Could not detect JVM version from system properties.";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    /*
    *  verifyOscar():
    *  Verify the current Oscar instance. Check build, version, and the default
    *  properties file in the WAR and the properties file found in Tomcat's 
    *  "catalina.home" directory.
    */

    @Test
    public void matchVerifyOscar() throws IOException, IllegalAccessException {
        File testingFolder = Files.createTempDirectory("testingFolder").toFile();
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        File catalinaBaseFolder = catalinaHomeFolder;
        catalinaHome.set(audit, catalinaHomeFolder);
        catalinaBase.set(audit, catalinaBaseFolder);
        File oscar15ClassesFolder = new File(testingFolder.getPath() + "/webapps/oscar15/WEB-INF/classes");
        oscar15ClassesFolder.mkdir();

        File oscarMcmaster = new File(oscar15ClassesFolder.getPath() + "/oscar_mcmaster.properties");
        FileUtils.writeStringToFile(oscarMcmaster, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/\n"
                                                + "buildtag=oscar15BetaMaster-454\n"
                                                + "buildDateTime=2017-02-08 08:25 PM");

        File oscar15 = new File(testingFolder.getPath() + "/oscar15.properties");
        FileUtils.writeStringToFile(oscar15, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/\n"
                                                + "buildtag=oscar15BetaMaster-454\n"
                                                + "buildDateTime=2017-02-08 08:25 PM");

        String expectedResult = "<b>Currently checking default \"oscar_mcmaster.properties\" file in the deployed WAR...</b><br />"
                                    + "Oscar build date and time: 2017-02-08 08:25 PM<br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />"
                                    + "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br /><br />"
                                    + "<b>Currently checking \"oscar15.properties\" file in \"catalina.home\" directory...</b><br />"
                                    + "Oscar build date and time: 2017-02-08 08:25 PM<br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />"
                                    + "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "<br /><b>NOTE:</b> The properties file found in the \"catalina.home\" directory will overwrite the default properties file in the deployed WAR.<br />";
        assertEquals(expectedResult, audit.verifyOscar());
        testingFolder.deleteOnExit();
    }

    @Test
    public void emptyWebAppNameVerifyOscar() throws IOException, IllegalAccessException {
        File testingFolder = Files.createTempDirectory("testingFolder").toFile();
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        catalinaHome.set(audit, catalinaHomeFolder);
        webAppName.set(audit, "");

        String expectedResult = "Could not detect the Oscar webapps directory name.";
        assertEquals(expectedResult, audit.verifyOscar());
        testingFolder.deleteOnExit();
    }

    @Test
    public void nullVerifyOscar() throws IOException, NoSuchFieldException, IllegalAccessException {
        catalinaBase.set(audit, null);
        catalinaHome.set(audit, null);

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyOscar());
    }

    @Test
    public void emptyPathVerifyOscar() throws IOException, NoSuchFieldException, IllegalAccessException {
        File catalinaBaseFolder = new File("");
        File catalinaHomeFolder = catalinaBaseFolder;
        catalinaBase.set(audit, catalinaBaseFolder);
        catalinaHome.set(audit, catalinaHomeFolder);

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyOscar());
    }

    /*
    *  oscarBuild(String fileName):
    *  Read Oscar "buildtag" and "buildDateTime" of properties file.
    */

    @Test
    public void isMatchTrueOscarBuild() throws IOException {
        File correctFile = File.createTempFile("correctInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(correctFile, "buildtag=oscar15BetaMaster-454\n"
                                                    + "buildDateTime=2017-02-08 08:25 PM");

        String expectedResult = "Oscar build date and time: 2017-02-08 08:25 PM<br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />";
        assertEquals(expectedResult, audit.oscarBuild(correctFile.getPath()));
        correctFile.deleteOnExit();
    }

    @Test
    public void isMatchFalseOscarBuild() throws IOException {
        File notCorrectFile = File.createTempFile("notCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(notCorrectFile, "#buildtag=oscar15BetaMaster-454");

        String expectedResult = "Could not detect Oscar build tag.<br />"
                                    + "Could not detect Oscar build date and time.<br />";
        assertEquals(expectedResult, audit.oscarBuild(notCorrectFile.getPath()));
        notCorrectFile.deleteOnExit();
    }

    @Test
    public void exceptionOscarBuild() throws IOException {
        File unreadableFile = Files.createTempDirectory("notAFile").toFile();
        String expectedResult = "Could not read properties file to detect Oscar build.<br />";      
        assertEquals(expectedResult, audit.oscarBuild(unreadableFile.getPath()));
        unreadableFile.deleteOnExit();
    }

    /*
    *  verifyOscarProperties(String fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and
    *  "drugref_url" tags of Oscar properties file.
    */

    // isMatch1, isMatch2, isMatch3, isMatch4
    @Test
    public void isMatchAllVerifyOscarProperties() throws IOException {
        File correctFile = File.createTempFile("correctInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(correctFile, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/");

        String expectedResult = "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />";
        assertEquals(expectedResult, audit.verifyOscarProperties(correctFile.getPath()));
        correctFile.deleteOnExit();
    }

    // isMatch1, !isMatch2, isMatch3, !isMatch4
    @Test
    public void isMatch13VerifyOscarProperties() throws IOException {
        File semiCorrectFile = File.createTempFile("semiCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(semiCorrectFile, "HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");

        String expectedResult = "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "Could not detect \"SINGLE_PAGE_CHART\" tag.<br />"
                                    + "Could not detect \"drugref_url\" tag.<br />";
        assertEquals(expectedResult, audit.verifyOscarProperties(semiCorrectFile.getPath()));
        semiCorrectFile.deleteOnExit();
    }

    // !isMatch1, !isMatch2, !isMatch3, !isMatch4
    @Test
    public void isMatchFalseVerifyOscarProperties() throws IOException {
        File notCorrectFile = File.createTempFile("notCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(notCorrectFile, "#HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "#TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");

        String expectedResult = "Could not detect \"HL7TEXT_LABS\" tag.<br />"
                                    + "Could not detect \"SINGLE_PAGE_CHART\" tag.<br />"
                                    + "Could not detect \"TMP_DIR\" tag.<br />"
                                    + "Could not detect \"drugref_url\" tag.<br />";
        assertEquals(expectedResult, audit.verifyOscarProperties(notCorrectFile.getPath()));
        notCorrectFile.deleteOnExit();
    }

    @Test
    public void exceptionVerifyOscarProperties() throws IOException {
        File unreadableFile = Files.createTempDirectory("notAFile").toFile();
        String expectedResult = "Could not read properties file to verify Oscar tags.";      
        assertEquals(expectedResult, audit.verifyOscarProperties(unreadableFile.getPath()));
        unreadableFile.deleteOnExit();
    }

    /*
    *  verifyDrugRef():
    *  Verify the current Drugref instance. Check the properties file found in
    *  Tomcat's "catalina.home" directory.
    */

    @Test
    public void matchVerifyDrugref() throws IOException, IllegalAccessException {
        File testingFolder = Files.createTempDirectory("testingFolder").toFile();
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        catalinaHome.set(audit, catalinaHomeFolder);

        File drugrefFile = new File(testingFolder.getPath() + "/drugref.properties");
        FileUtils.writeStringToFile(drugrefFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "<b>Currently checking \"drugref.properties\" file...</b><br />" 
                                    + "\"db_driver\" tag is configured as: com.mysql.jdbc.Driver<br />"
                                    + "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />";
        assertEquals(expectedResult, audit.verifyDrugref());
        testingFolder.deleteOnExit();
    }

    @Test
    public void emptyDrugrefUrlVerifyDrugref() throws IOException, IllegalAccessException {
        File testingFolder = Files.createTempDirectory("testingFolder").toFile();
        File catalinaHomeFolder = new File(testingFolder.getPath()  + "/");
        catalinaHome.set(audit, catalinaHomeFolder);
        drugrefUrl.set(audit, "");

        String expectedResult = "Please ensure that your Oscar properties \"drugref_url\" tag is set correctly.";
        assertEquals(expectedResult, audit.verifyDrugref());
        testingFolder.deleteOnExit();
    }

    @Test
    public void nullVerifyDrugref() throws IOException, IllegalAccessException {
        catalinaBase.set(audit, null);
        catalinaHome.set(audit, null);

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyDrugref());
    }

    @Test
    public void emptyPathVerifyDrugref() throws IOException, IllegalAccessException {
        File catalinaBaseFolder = new File("");
        File catalinaHomeFolder = catalinaBaseFolder;
        catalinaBase.set(audit, catalinaBaseFolder);
        catalinaHome.set(audit, catalinaHomeFolder);

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyDrugref());
    }
    /*
    *  verifyDrugRefProperties(String fileName):
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref 
    *  properties file.
    */

    // isMatch1, isMatch2, isMatch3
    @Test
    public void isMatchAllVerifyDrugrefProperties() throws IOException {
        File correctFile = File.createTempFile("correctInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(correctFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "\"db_driver\" tag is configured as: com.mysql.jdbc.Driver<br />"
                                    + "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />";
        assertEquals(expectedResult, audit.verifyDrugrefProperties(correctFile.getPath()));
        correctFile.deleteOnExit();
    }

    // isMatch1, isMatch2, !isMatch3
    @Test
    public void isMatch12VerifyDrugrefProperties() throws IOException {
        File semiCorrectFile = File.createTempFile("semiCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(semiCorrectFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "#db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />"
                                    + "Could not detect \"db_driver\" tag.<br />";
        assertEquals(expectedResult, audit.verifyDrugrefProperties(semiCorrectFile.getPath()));
        semiCorrectFile.deleteOnExit();
    }

    // !isMatch1, !isMatch2, !isMatch3
    @Test
    public void isMatchFalseVerifyDrugrefProperties() throws IOException {
        File semiCorrectFile = File.createTempFile("semiCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(semiCorrectFile, "#db_user=root\n"
                                                + "#db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "#db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "Could not detect \"db_user\" tag.<br />"
                                    + "Could not detect \"db_url\" tag.<br />"
                                    + "Could not detect \"db_driver\" tag.<br />";
        assertEquals(expectedResult, audit.verifyDrugrefProperties(semiCorrectFile.getPath()));
        semiCorrectFile.deleteOnExit();
    }

    @Test
    public void exceptionVerifyDrugrefProperties() throws IOException {
        File unreadableFile = Files.createTempDirectory("notAFile").toFile();
        String expectedResult = "Could not read properties file to verify Drugref tags.";      
        assertEquals(expectedResult, audit.verifyDrugrefProperties(unreadableFile.getPath()));
        unreadableFile.deleteOnExit();
    }

    /*
    *  tomcatReinforcement():
    *  Read through the Tomcat settings file and output the Xmx and Xms values
    *  to the user.
    */
    
    @Test
    public void isMatchTrueTomcatReinforcement() throws IOException, IllegalAccessException {
        FileUtils.writeStringToFile((File)tomcatSettings.get(audit), "-Djava.awt.headless=true -Xmx1024m -Xms256m -XX");
        String expectedResult = "Xmx value: 1024m<br />Xms value: 256m<br />";
        assertEquals(expectedResult, audit.tomcatReinforcement());
    }

    @Test
    public void isMatchFalseTomcatReinforcement() throws IOException, IllegalAccessException {
        FileUtils.writeStringToFile((File)tomcatSettings.get(audit), "nothing in this file we need");
        String expectedResult = "Could not detect Xmx value.<br />Could not detect Xms value.<br />";
        assertEquals(expectedResult, audit.tomcatReinforcement());
    }

    @Test
    public void emptyPathTomcatReinforcement() throws IOException, IllegalAccessException {
        File catalinaBaseFolder = new File("");
        catalinaBase.set(audit, catalinaBaseFolder);
        File tomcatSettingsFile = new File("");
        tomcatSettings.set(audit, tomcatSettingsFile);

        String expectedResult = "Please verify that your \"catalina.base\" directory is setup correctly.";
        assertEquals(expectedResult, audit.tomcatReinforcement());
    }

    @Test
    public void nullTomcatReinforcement() throws IOException, IllegalAccessException {
        catalinaBase.set(audit, null);
        tomcatSettings.set(audit, null);

        String expectedResult = "Please verify that your \"catalina.base\" directory is setup correctly.";
        assertEquals(expectedResult, audit.tomcatReinforcement());
    }

    @Test
    public void exceptionTomcatReinforcement() throws IOException, IllegalAccessException {
        File tomcatSettingsFolder = Files.createTempDirectory("notAFile").toFile();
        tomcatSettings.set(audit, tomcatSettingsFolder);

        String expectedResult = "Could not detect Tomcat memory allocation in Tomcat settings file.";
        assertEquals(expectedResult, audit.tomcatReinforcement());
        tomcatSettingsFolder.deleteOnExit();
    }

    @After
    public void tearDown() {
        catalinaBase = null;
        catalinaHome = null;
        lsbRelease = null;
        jvmVersion = null;
        tomcatVersion = null;
        webAppName = null;
        drugrefUrl = null;

        assertNull(catalinaBase);
        assertNull(catalinaHome);
        assertNull(lsbRelease);
        assertNull(jvmVersion);
        assertNull(tomcatVersion);
        assertNull(webAppName);
        assertNull(drugrefUrl);
    }
}