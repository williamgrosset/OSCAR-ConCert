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

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.apache.commons.io.FileUtils;
import java.lang.reflect.Field;

public class AuditTest {

    Audit audit = new Audit();
    private Field catalinaBase;
    private Field catalinaHome;
    private Field lsbRelease;
    private Field tomcatSettings;
    private Field jvmVersion;
    private Field tomcatVersion;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void initialize() throws IOException, NoSuchFieldException, IllegalAccessException {
        File catalinaBaseFolder = folder.newFolder("catalinaBase");
        File catalinaHomeFolder = folder.newFolder("catalinaHome");
        File lsbReleaseFile = folder.newFile("lsb-release");
        File tomcatSettingsFile = folder.newFile("tomcat411");
        String jvmVersionValue = "1.7.0_111";
        String tomcatVersionValue = "Apache Tomcat/7.0.52 (Ubuntu)";

        catalinaBase = audit.getClass().getDeclaredField("catalinaBase");
        catalinaHome = audit.getClass().getDeclaredField("catalinaHome");
        lsbRelease = audit.getClass().getDeclaredField("lsbRelease");
        tomcatSettings = audit.getClass().getDeclaredField("tomcatSettings");
        jvmVersion = audit.getClass().getDeclaredField("jvmVersion");
        tomcatVersion = audit.getClass().getDeclaredField("tomcatVersion");

        catalinaBase.setAccessible(true);
        catalinaHome.setAccessible(true);
        lsbRelease.setAccessible(true);
        tomcatSettings.setAccessible(true);
        jvmVersion.setAccessible(true);
        tomcatVersion.setAccessible(true);

        catalinaBase.set(audit, catalinaBaseFolder);
        catalinaHome.set(audit, catalinaHomeFolder);
        lsbRelease.set(audit, lsbReleaseFile);
        tomcatSettings.set(audit, tomcatSettingsFile);
        jvmVersion.set(audit, jvmVersionValue);
        tomcatVersion.set(audit, tomcatVersionValue);
    }

    /*
    *  serverVersion():
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    */
 
    @Test
    public void isMatchTrueServerVersion() throws IOException, IllegalAccessException {
        FileUtils.writeStringToFile((File)lsbRelease.get(audit), "DISTRIB_DESCRIPTION=\"Ubuntu 14.04.5 LTS\"");

        String expectedResult = "\"Ubuntu 14.04.5 LTS\"";
        assertEquals(expectedResult,audit.serverVersion());
    }

    @Test
    public void isMatchFalseServerVersion() throws IOException, IllegalAccessException {
        FileUtils.writeStringToFile((File)lsbRelease.get(audit), "randomtag=");
        
        String expectedResult = "Could not detect Ubuntu server version.";
        assertEquals(expectedResult, audit.serverVersion());
    }

    @Test
    public void emptyPathServerVersion() throws IOException, IllegalAccessException {
        lsbRelease.set(audit, new File(""));

        String expectedResult = "Could not read \"lsb-release\" file to detect Ubuntu server version.";      
        assertEquals(expectedResult, audit.serverVersion());
    }
    
    @Test
    public void nullServerVersion() throws IOException, IllegalAccessException {
        lsbRelease.set(audit, null);

        String expectedResult = "Could not read \"lsb-release\" file to detect Ubuntu server version.";      
        assertEquals(expectedResult, audit.serverVersion());
    }

    /*
    *  databaseInfo():
    *  Retrieve url, username, and password information from Oscar properties
    *  to make a connection with our database. From our connection, we can
    *  retrieve which database type we are connected to and the database version.
    */

    /******* TEST METHODS HERE *******/

    /*
    *  verifyTomcat():
    *  Extract JVM version from system properties and server information
    *  from servlet.
    */

    @Test
    public void matchVerifyTomcat() throws IOException, IllegalAccessException {
        String expectedResult = "JVM Version: " + jvmVersion.get(audit) + "<br />"
                                    + "Tomcat version: " + tomcatVersion.get(audit) + "<br />";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    @Test
    public void emptyVerifyTomcat() throws IllegalAccessException {
        jvmVersion.set(audit, "");
        tomcatVersion.set(audit, "");
        String expectedResult = "Please verify that Tomcat is setup correctly.";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    @Test
    public void nullVerifyTomcat() throws IllegalAccessException {
        jvmVersion.set(audit, null);
        tomcatVersion.set(audit, null);

        String expectedResult = "Please verify that Tomcat is setup correctly.";
        assertEquals(expectedResult, audit.verifyTomcat());
    }

    /*
    *  verifyOscar(String webAppsPath):
    *  Verify all possible Oscar deployments.
    *  Grab all possible Oscar deployed folder names in root directory
    *  and push onto stack. Pop names off of the stack and verify
    *  each properties file that exists in "catalinaHome" directory.
    */

    @Test
    public void nonEmptyVerifyOscar() throws IOException, IllegalAccessException {
        File testingFolder = folder.newFolder("testingFolder");
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        catalinaHome.set(audit, catalinaHomeFolder);
        File oscar15Folder = new File(testingFolder.getPath() + "/oscar15");
        oscar15Folder.mkdir();
        File oscar15ClassesFolder = new File(testingFolder.getPath() + "/oscar15/WEB-INF/classes");
        oscar15ClassesFolder.mkdir();

        // put oscar_mcmaster.properties file in directory above
        File oscarMcmaster1 = new File(oscar15ClassesFolder.getPath() + "/oscar_mcmaster.properties");
        FileUtils.writeStringToFile(oscarMcmaster1, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/\n"
                                                + "buildtag=oscar15BetaMaster-454");
        File oscar15bcFolder = new File(testingFolder.getPath() + "/oscar15_bc");
        oscar15bcFolder.mkdir();
        File oscar15bcClassesFolder = new File(testingFolder.getPath() + "/oscar15_bc/WEB-INF/classes");
        oscar15bcClassesFolder.mkdir();

        // put oscar_mcmaster.properties file in directory above
        File oscarMcmaster2 = new File(oscar15bcClassesFolder.getPath() + "/oscar_mcmaster.properties");
        FileUtils.writeStringToFile(oscarMcmaster2, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/\n"
                                                + "buildtag=oscar15BetaMaster-454");

        // create oscar15.properties file & oscar15_bc.properties file in testingFolder directory
        File oscar15 = new File(testingFolder.getPath() + "/oscar15.properties");
        File oscar15bc = new File(testingFolder.getPath() + "/oscar15_bc.properties");
        FileUtils.writeStringToFile(oscar15, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/\n"
                                                + "buildtag=oscar15BetaMaster-454");
        FileUtils.writeStringToFile(oscar15bc, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/\n"
                                                + "buildtag=oscar15BetaMaster-454");

        String expectedResult = "<b>Currently checking \"oscar_mcmaster.properties\" file for \"oscar15_bc\"...</b><br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />"
                                    + "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "<b>Currently checking \"oscar15_bc.properties\" file...</b><br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />"
                                    + "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "<b>Currently checking \"oscar_mcmaster.properties\" file for \"oscar15\"...</b><br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />"
                                    + "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "<b>Currently checking \"oscar15.properties\" file...</b><br />"
                                    + "Oscar build and version: oscar15BetaMaster-454<br />"
                                    + "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
   + "\"HL7TEXT_LABS\" tag is configured as: true<br />";
        assertEquals(expectedResult, audit.verifyOscar(testingFolder.getPath() + "/"));
    }

    @Test
    public void emptyVerifyOscar() throws IOException, IllegalAccessException {
        File testingFolder = folder.newFolder("testingFolder");
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        catalinaHome.set(audit, catalinaHomeFolder);
        File randomFolder = new File(testingFolder.getPath() + "/foobar");
        randomFolder.mkdir();

        String expectedResult = "Could not find any properties files for Oscar.<br />";
        assertEquals(expectedResult, audit.verifyOscar(testingFolder.getPath() + "/"));
    }

    @Test
    public void nullVerifyOscar() throws IOException, NoSuchFieldException, IllegalAccessException {
        catalinaBase.set(audit, null);
        catalinaHome.set(audit, null);
        File testingFolder = folder.newFolder("testingFolder");
        File randomFolder = new File(testingFolder.getPath() + "/foobar");
        randomFolder.mkdir();

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyOscar(testingFolder.getPath() + "/"));
    }

    @Test
    public void emptyPathVerifyOscar() throws IOException, NoSuchFieldException, IllegalAccessException {
        File catalinaBaseFolder = new File("");
        File catalinaHomeFolder = catalinaBaseFolder;
        catalinaBase.set(audit, catalinaBaseFolder);
        catalinaHome.set(audit, catalinaHomeFolder);
        File testingFolder = folder.newFolder("testingFolder");
        File randomFolder = new File(testingFolder.getPath() + "/foobar");
        randomFolder.mkdir();

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyOscar(testingFolder.getPath() + "/"));
    }

    /*
    *  oscarBuild(String fileName):
    *  Read Oscar buildtag of properties file.
    */

    @Test
    public void isMatchTrueOscarBuild() throws IOException {
        File correctFile = folder.newFile("correctInfo.properties");
        FileUtils.writeStringToFile(correctFile, "buildtag=oscar15BetaMaster-454");

        String expectedResult = "Oscar build and version: oscar15BetaMaster-454<br />";
        assertEquals(expectedResult, audit.oscarBuild(correctFile.getPath()));
    }

    @Test
    public void isMatchFalseOscarBuild() throws IOException {
        File notCorrectFile = folder.newFile("notCorrectInfo.properties");
        FileUtils.writeStringToFile(notCorrectFile, "#buildtag=oscar15BetaMaster-454");

        String expectedResult = "Oscar build/version cannot be found.<br />";
        assertEquals(expectedResult, audit.oscarBuild(notCorrectFile.getPath()));
    }

    @Test
    public void exceptionOscarBuild() throws IOException {
        File unreadableFile = folder.newFolder("fakeFile");

        String expectedResult = "Could not read properties file to detect Oscar build.<br />";      
        assertEquals(expectedResult, audit.oscarBuild(unreadableFile.getPath()));
    }

    /*
    *  verifyOscarProperties(String fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and
    *  "drugref_url" tags of Oscar properties file.
    *
    *  Note: There are 16 unique permutations for the 'isMatch' behavior of this method.
    */

    // isMatch1, isMatch2, isMatch3, isMatch4
    @Test
    public void isMatchAllVerifyOscarProperties() throws IOException {
        File correctFile = folder.newFile("correctInfo.properties");
        FileUtils.writeStringToFile(correctFile, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/");

        String expectedResult = "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />";
        assertEquals(expectedResult, audit.verifyOscarProperties(correctFile.getPath()));
    }

    // isMatch1, !isMatch2, isMatch3, !isMatch4
    @Test
    public void isMatch13VerifyOscarProperties() throws IOException {
        File semiCorrectFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(semiCorrectFile, "HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");

        String expectedResult = "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is not configured properly.<br />"
                                    + "\"drugref_url\" tag is not configured properly.<br />";
        assertEquals(expectedResult, audit.verifyOscarProperties(semiCorrectFile.getPath()));
    }

    // !isMatch1, !isMatch2, !isMatch3, !isMatch4
    @Test
    public void isMatchFalseVerifyOscarProperties() throws IOException {
        File notCorrectFile = folder.newFile("nonCorrectInfo.properties");
        FileUtils.writeStringToFile(notCorrectFile, "#HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "#TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");

        String expectedResult = "\"HL7TEXT_LABS\" tag is not configured properly.<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is not configured properly.<br />"
                                    + "\"TMP_DIR\" tag is not configured properly.<br />"
                                    + "\"drugref_url\" tag is not configured properly.<br />";
        assertEquals(expectedResult, audit.verifyOscarProperties(notCorrectFile.getPath()));
    }

    @Test
    public void exceptionVerifyOscarProperties() throws IOException {
        File unreadableFile = folder.newFolder("fakeFile");

        String expectedResult = "Could not read properties file to verify Oscar tags.";      
        assertEquals(expectedResult, audit.verifyOscarProperties(unreadableFile.getPath()));
    }

    /*
    *  verifyDrugRef(String webAppsPath):
    *  Verify all possible Drugref deployments.
    *  Grab all possible Drugref deployed folder names in root directory
    *  and push onto stack. Pop names off of the stack and verify
    *  each properties file that exists in "catalinaHome" directory.
    */

    @Test
    public void nonEmptyVerifyDrugref() throws IOException, IllegalAccessException {
        File testingFolder = folder.newFolder("testingFolder");
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        catalinaHome.set(audit, catalinaHomeFolder);
        File drugref10Folder = new File(testingFolder.getPath() + "/drugref10");
        drugref10Folder.mkdir();
        File drugref10ontFolder = new File(testingFolder.getPath() + "/drugref10_ont");
        drugref10ontFolder.mkdir();

        File drugref10 = new File(testingFolder.getPath() + "/drugref10.properties");
        File drugref10ont = new File(testingFolder.getPath() + "/drugref10_ont.properties");
        FileUtils.writeStringToFile(drugref10, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "db_driver=com.mysql.jdbc.Driver");
        FileUtils.writeStringToFile(drugref10ont, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "<b>Currently checking \"drugref10_ont.properties\" file...</b><br />" 
                                    + "\"db_driver\" tag is configured as: com.mysql.jdbc.Driver<br />"
                                    + "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />"
                                    + "<b>Currently checking \"drugref10.properties\" file...</b><br />" 
                                    + "\"db_driver\" tag is configured as: com.mysql.jdbc.Driver<br />"
                                    + "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />";
        assertEquals(expectedResult, audit.verifyDrugref(testingFolder.getPath() + "/"));
    }

    @Test
    public void emptyVerifyDrugref() throws IOException, IllegalAccessException {
        File testingFolder = folder.newFolder("testingFolder");
        File catalinaHomeFolder = new File(testingFolder.getPath() + "/");
        catalinaHome.set(audit, catalinaHomeFolder);
        File randomFolder = new File(testingFolder.getPath() + "/foobar");
        randomFolder.mkdir();

        String expectedResult = "Could not find any properties files for Drugref.";
        assertEquals(expectedResult, audit.verifyDrugref(testingFolder.getPath() + "/"));
    }

    @Test
    public void nullVerifyDrugref() throws IOException, IllegalAccessException {
        catalinaBase.set(audit, null);
        catalinaHome.set(audit, null);
        File testingFolder = folder.newFolder("testingFolder");

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyDrugref(testingFolder.getPath() + "/"));
    }

    @Test
    public void emptyPathVerifyDrugref() throws IOException, IllegalAccessException {
        File catalinaBaseFolder = new File("");
        File catalinaHomeFolder = catalinaBaseFolder;
        catalinaBase.set(audit, catalinaBaseFolder);
        catalinaHome.set(audit, catalinaHomeFolder);
        File testingFolder = folder.newFolder("testingFolder");

        String expectedResult = "Please verify that your \"catalina.base\" and \"catalina.home\" directories are setup correctly.";
        assertEquals(expectedResult, audit.verifyDrugref(testingFolder.getPath() + "/"));
    }
    /*
    *  verifyDrugRefProperties(String fileName):
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref properties file.
    *
    *  Note: There are 9 unique permutations for the 'isMatch' behavior of this method.
    */

    // isMatch1, isMatch2, isMatch3
    @Test
    public void isMatchAllVerifyDrugrefProperties() throws IOException {
        File correctFile = folder.newFile("correctInfo.properties");
        FileUtils.writeStringToFile(correctFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "\"db_driver\" tag is configured as: com.mysql.jdbc.Driver<br />"
                                    + "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />";
        assertEquals(expectedResult, audit.verifyDrugrefProperties(correctFile.getPath()));
    }

    // isMatch1, isMatch2, !isMatch3
    @Test
    public void isMatch12VerifyDrugrefProperties() throws IOException {
        File semiCorrectFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(semiCorrectFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "#db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />"
                                    + "\"db_driver\" tag is not configured properly.<br />";
        assertEquals(expectedResult, audit.verifyDrugrefProperties(semiCorrectFile.getPath()));
    }

    // !isMatch1, !isMatch2, !isMatch3
    @Test
    public void isMatchFalseVerifyDrugrefProperties() throws IOException {
        File semiCorrectFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(semiCorrectFile, "#db_user=root\n"
                                                + "#db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "#db_driver=com.mysql.jdbc.Driver");

        String expectedResult = "\"db_user\" tag is not configured properly.<br />"
                                    + "\"db_url\" tag is not configured properly.<br />"
                                    + "\"db_driver\" tag is not configured properly.<br />";
        assertEquals(expectedResult, audit.verifyDrugrefProperties(semiCorrectFile.getPath()));
    }

    @Test
    public void exceptionVerifyDrugrefProperties() throws IOException {
        File unreadableFile = folder.newFolder("fakeFile");

        String expectedResult = "Could not read properties file to verify Drugref tags.";      
        assertEquals(expectedResult, audit.verifyDrugrefProperties(unreadableFile.getPath()));
    }

    /*
    *  tomcatReinforcement():
    *  Run "ps -ef | grep $tomcat", with $tomcat being the tomcat folder
    *  found in catalinaBase.getPath(). Read "xmx" and "xms" values of 
    *  the running Tomcat application.
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
        File tomcatSettingsFolder = folder.newFolder("aRandomFolder");
        tomcatSettings.set(audit, tomcatSettingsFolder);

        String expectedResult = "Could not detect Tomcat memory allocation in Tomcat settings file.";
        assertEquals(expectedResult, audit.tomcatReinforcement());
    }

    /*
    *  grabFiles(File directory, String regex):
    *  Loop through folders/files in directory and push all possible files
    *  (using pattern matching) onto the Stack.
    */

    @Test
    public void allGrabFiles() throws IOException {
        File testingFolder = folder.newFolder("testingFolder");
        File oscar15Folder = new File(testingFolder.getPath() + "/oscar15");
        oscar15Folder.mkdir();
        File oscar15bcFolder = new File(testingFolder.getPath() + "/oscar15_bc");
        oscar15bcFolder.mkdir();
        File oscar3fooFolder = new File(testingFolder.getPath() + "/oscar3foo");
        oscar3fooFolder.mkdir();

        Stack<String> expectedStack = new Stack<String>();
        expectedStack.push("oscar15");
        expectedStack.push("oscar15_bc");
        expectedStack.push("oscar3foo");

        assertEquals(expectedStack, audit.grabFiles(testingFolder, "^(oscar[0-9]*\\w*)$"));
    }

    @Test
    public void oneGrabFiles() throws IOException {
        File testingFolder = folder.newFolder("testingFolder");
        File randomFolder1 = new File(testingFolder.getPath() + "/foobar1");
        randomFolder1.mkdir();
        File randomFolder2 = new File(testingFolder.getPath() + "/foobar912foobar");
        randomFolder2.mkdir();
        File oscar10Folder = new File(testingFolder.getPath() + "/oscar10");
        oscar10Folder.mkdir();

        Stack<String> expectedStack = new Stack<String>();
        expectedStack.push("oscar10");

        assertEquals(expectedStack, audit.grabFiles(testingFolder, "^(oscar[0-9]*\\w*)$"));
    }

    @Test
    public void noFileListGrabFiles() throws IOException {
        File testingFolder = folder.newFolder("testingFolder");

        Stack<String> expectedStack = new Stack<String>(); // empty stack
        assertEquals(expectedStack, audit.grabFiles(testingFolder, "^(oscar[0-9]*\\w*)$"));
    }

    @Test
    public void nonFolderListGrabFiles() throws IOException {
        File testingFile = folder.newFile("testingFile.txt");

        Stack<String> expectedStack = new Stack<String>(); // empty stack
        assertEquals(expectedStack, audit.grabFiles(testingFile, "^(oscar[0-9]*\\w*)$"));
    }

    @After
    public void tearDown() {
        catalinaBase = null;
        catalinaHome = null;
        lsbRelease = null;
        jvmVersion = null;
        tomcatVersion = null;

        assertNull(catalinaBase);
        assertNull(catalinaHome);
        assertNull(lsbRelease);
        assertNull(jvmVersion);
        assertNull(tomcatVersion);
    }
}
