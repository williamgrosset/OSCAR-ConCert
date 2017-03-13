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
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.*;

/*
*  github.com/williamgrosset
*/
public class PropertyCheckTest {

    Audit audit = new Audit();
    private Field catalinaBase;
    private Field catalinaHome;
    private Field lsbRelease;
    private Field tomcatSettings;
    private Field jvmVersion;
    private Field tomcatVersion;
    private Field webAppName;
    private Field drugrefUrl;
    private Method serverVersion;
    private Method verifyTomcat;
    private Method verifyOscar;
    private Method oscarBuild;
    private Method verifyOscarProperties;
    private Method verifyDrugref;
    private Method verifyDrugrefProperties;
    private Method tomcatReinforcement;

    @Before
    public void initialize() throws IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
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

        serverVersion = audit.getClass().getDeclaredMethod("serverVersion");
        verifyTomcat = audit.getClass().getDeclaredMethod("verifyTomcat");
        verifyOscar = audit.getClass().getDeclaredMethod("verifyOscar");
        oscarBuild = audit.getClass().getDeclaredMethod("oscarBuild", String.class);
        verifyOscarProperties = audit.getClass().getDeclaredMethod("verifyOscarProperties", String.class);
        verifyDrugref = audit.getClass().getDeclaredMethod("verifyDrugref");
        verifyDrugrefProperties = audit.getClass().getDeclaredMethod("verifyDrugrefProperties", String.class);
        tomcatReinforcement = audit.getClass().getDeclaredMethod("tomcatReinforcement");

        serverVersion.setAccessible(true);
        verifyTomcat.setAccessible(true);
        verifyOscar.setAccessible(true);
        oscarBuild.setAccessible(true);
        verifyOscarProperties.setAccessible(true);
        verifyDrugref.setAccessible(true);
        verifyDrugrefProperties.setAccessible(true);
        tomcatReinforcement.setAccessible(true);
    }

    public void isMatchCheckProperty() {

    }

    // isMatch
    // !isMatch
    // invalid property

    /*
    *  verifyOscarProperties(String fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and
    *  "drugref_url" tags of Oscar properties file.
    */

    // isMatch1, isMatch2, isMatch3, isMatch4
    @Test
    public void isMatchAllVerifyOscarProperties() throws IOException, IllegalAccessException, InvocationTargetException {
        File correctFile = File.createTempFile("correctInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(correctFile, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/");

        String expectedResult = "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />";
        assertEquals(expectedResult, verifyOscarProperties.invoke(audit, correctFile.getPath()));
        correctFile.deleteOnExit();
    }

    // isMatch1, !isMatch2, isMatch3, !isMatch4
    @Test
    public void isMatch13VerifyOscarProperties() throws IOException, IllegalAccessException, InvocationTargetException {
        File semiCorrectFile = File.createTempFile("semiCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(semiCorrectFile, "HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");

        String expectedResult = "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "Could not detect \"SINGLE_PAGE_CHART\" tag.<br />"
                                    + "Could not detect \"drugref_url\" tag.<br />";
        assertEquals(expectedResult, verifyOscarProperties.invoke(audit, semiCorrectFile.getPath()));
        semiCorrectFile.deleteOnExit();
    }

    // !isMatch1, !isMatch2, !isMatch3, !isMatch4
    @Test
    public void isMatchFalseVerifyOscarProperties() throws IOException, IllegalAccessException, InvocationTargetException {
        File notCorrectFile = File.createTempFile("notCorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(notCorrectFile, "#HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "#TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");

        String expectedResult = "Could not detect \"HL7TEXT_LABS\" tag.<br />"
                                    + "Could not detect \"SINGLE_PAGE_CHART\" tag.<br />"
                                    + "Could not detect \"TMP_DIR\" tag.<br />"
                                    + "Could not detect \"drugref_url\" tag.<br />";
        assertEquals(expectedResult, verifyOscarProperties.invoke(audit, notCorrectFile.getPath()));
        notCorrectFile.deleteOnExit();
    }

    @Test
    public void exceptionVerifyOscarProperties() throws IOException, IllegalAccessException, InvocationTargetException {
        File unreadableFile = Files.createTempDirectory("notAFile").toFile();
        String expectedResult = "Could not read properties file to verify Oscar tags.";      
        assertEquals(expectedResult, verifyOscarProperties.invoke(audit, unreadableFile.getPath()));
        unreadableFile.deleteOnExit();
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
        serverVersion = null;
        verifyTomcat = null;
        verifyOscar = null;
        oscarBuild = null;
        verifyOscarProperties = null;
        verifyDrugref = null;
        verifyDrugrefProperties = null;
        tomcatReinforcement = null;

        assertNull(catalinaBase);
        assertNull(catalinaHome);
        assertNull(lsbRelease);
        assertNull(jvmVersion);
        assertNull(tomcatVersion);
        assertNull(webAppName);
        assertNull(drugrefUrl);
        assertNull(serverVersion);
        assertNull(verifyTomcat);
        assertNull(verifyOscar);
        assertNull(oscarBuild);
        assertNull(verifyOscarProperties);
        assertNull(verifyDrugref);
        assertNull(verifyDrugrefProperties);
        assertNull(tomcatReinforcement);
    }
}
