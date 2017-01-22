package oscar.util;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.apache.commons.io.FileUtils;

public class AuditTest {
 
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /*
    *  serverVersion(String fileName):
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    */
 
    @Test
    public void exceptionServerVersion() throws IOException {
        File tempFile = folder.newFile("file.txt");
        String expectedResult = "Could not read \"lsb-release\" file to detect Ubuntu server version.";      
        assertEquals(expectedResult, Audit.serverVersion(tempFile.toString()));
    }

    @Test
    public void isMatchTrueServerVersion() throws IOException {
        File tempFile = folder.newFile("correctInfo");
        FileUtils.writeStringToFile(tempFile, "DISTRIB_DESCRIPTION=\"Ubuntu 14.04.5 LTS\"");
        String expectedResult = "\"Ubuntu 14.04.5 LTS\"";
        assertEquals(expectedResult, Audit.serverVersion(tempFile.getPath()));
    }

    @Test
    public void isMatchFalseServerVersion() throws IOException {
        File tempFile = folder.newFile("notCorrectInfo");
        FileUtils.writeStringToFile(tempFile, "randomtag=");
        String expectedResult = "Could not detect Ubuntu server version.";
        assertEquals(expectedResult, Audit.serverVersion(tempFile.getPath()));
    }

    /*
    *  JVMTomcat7(String binPath):
    *  Read bash script and extract JVM/Tomcat version information.
    */

    /*
    *  oscarBuild(String fileName):
    *  Read Oscar buildtag of properties file.
    */

    @Test
    public void isMatchOscarBuild() throws IOException {
        File tempFile = folder.newFile("correctInfo.properties");
        FileUtils.writeStringToFile(tempFile, "buildtag=oscar15BetaMaster-454");
        String expectedResult = "Oscar build and version: oscar15BetaMaster-454<br />";
        assertEquals(expectedResult, Audit.oscarBuild(tempFile.getPath()));
    }

    /*
    *  verifyOscarProperties(String fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," "TMP_DIR," and
    *  "drugref_url" tags of Oscar properties file.
    *
    *  Note: There are 16 unique permutations for the isMatch behavior of this method.
    */

    // isMatch1, isMatch2, isMatch3, isMatch4
    @Test
    public void isMatchAllVerifyOscarProperties() throws IOException {
        File tempFile = folder.newFile("correctInfo.properties");
        FileUtils.writeStringToFile(tempFile, "HL7TEXT_LABS=true\nSINGLE_PAGE_CHART=yes\nTMP_DIR=/pathtotmpdir/\ndrugref_url=/pathtodrugref/");
        String expectedResult = "\"drugref_url\" tag is configured as: /pathtodrugref/<br />\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />\"HL7TEXT_LABS\" tag is configured as: true<br />";
        assertEquals(expectedResult, Audit.verifyOscarProperties(tempFile.getPath()));
    }

    // isMatch1, !isMatch2, isMatch3, !isMatch4
    @Test
    public void isMatch13VerifyOscarProperties() throws IOException {


    }

    // !isMatch1, !isMatch2, !isMatch3, !isMatch4
    @Test
    public void isMatchFalseVerifyOscarProperties() throws IOException {


    }

    /*
    *  verifyDrugRefProperties(String fileName):
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref properties file.
    *
    *  Note: There are 9 unique permutations for the isMatch behavior of this method.
    */

    // isMatch1, isMatch2, isMatch3
    @Test
    public void isMatchAllVerifyDrugrefProperties() throws IOException {


    }

    // isMatch1, isMatch2, !isMatch3
    @Test
    public void isMatch12VerifyDrugrefProperties() throws IOException {


    }

    // !isMatch1, !isMatch2, !isMatch3
    @Test
    public void isMatchFalseVerifyDrugrefProperties() throws IOException {


    }
}
