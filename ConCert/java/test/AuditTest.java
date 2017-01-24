package oscar.util;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

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
    *  verifyOscar(): ***FIX MY PARAMETERS***
    *  Verify all possible Oscar deployments.
    *  Grab all possible Oscar deployed folder names in root directory
    *  and push onto stack. Pop names off of the stack and verify
    *  each properties file that exists in "catalinaHome" directory.
    */

    /*
    *  oscarBuild(String fileName):
    *  Read Oscar buildtag of properties file.
    */

    @Test
    public void isMatchTrueOscarBuild() throws IOException {
        File tempFile = folder.newFile("correctInfo.properties");
        FileUtils.writeStringToFile(tempFile, "buildtag=oscar15BetaMaster-454");
        String expectedResult = "Oscar build and version: oscar15BetaMaster-454<br />";
        assertEquals(expectedResult, Audit.oscarBuild(tempFile.getPath()));
    }

    @Test
    public void isMatchFalseOscarBuild() throws IOException {
        File tempFile = folder.newFile("notCorrectInfo.properties");
        FileUtils.writeStringToFile(tempFile, "#buildtag=oscar15BetaMaster-454");
        String expectedResult = "Oscar build/version cannot be found.<br />";
        assertEquals(expectedResult, Audit.oscarBuild(tempFile.getPath()));
    }

    @Test
    public void exceptionOscarBuild() throws IOException {
        File tempFile = folder.newFile("file.txt");
        String expectedResult = "Could not read properties file to detect Oscar build.<br />";      
        assertEquals(expectedResult, Audit.oscarBuild(tempFile.toString()));
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
        FileUtils.writeStringToFile(tempFile, "HL7TEXT_LABS=true\n"
                                                + "SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "drugref_url=/pathtodrugref/");
        String expectedResult = "\"drugref_url\" tag is configured as: /pathtodrugref/<br />"
                                    + "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is configured as: yes<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />";
        assertEquals(expectedResult, Audit.verifyOscarProperties(tempFile.getPath()));
    }

    // isMatch1, !isMatch2, isMatch3, !isMatch4
    @Test
    public void isMatch13VerifyOscarProperties() throws IOException {
        File tempFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(tempFile, "HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");
        String expectedResult = "\"TMP_DIR\" tag is configured as: /pathtotmpdir/<br />"
                                    + "\"HL7TEXT_LABS\" tag is configured as: true<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is not configured properly.<br />"
                                    + "\"drugref_url\" tag is not configured properly.<br />";
        assertEquals(expectedResult, Audit.verifyOscarProperties(tempFile.getPath()));
    }

    // !isMatch1, !isMatch2, !isMatch3, !isMatch4
    @Test
    public void isMatchFalseVerifyOscarProperties() throws IOException {
        File tempFile = folder.newFile("nonCorrectInfo.properties");
        FileUtils.writeStringToFile(tempFile, "#HL7TEXT_LABS=true\n"
                                                + "#SINGLE_PAGE_CHART=yes\n"
                                                + "#TMP_DIR=/pathtotmpdir/\n"
                                                + "#drugref_url=/pathtodrugref/");
        String expectedResult = "\"HL7TEXT_LABS\" tag is not configured properly.<br />"
                                    + "\"SINGLE_PAGE_CHART\" tag is not configured properly.<br />"
                                    + "\"TMP_DIR\" tag is not configured properly.<br />"
                                    + "\"drugref_url\" tag is not configured properly.<br />";
        assertEquals(expectedResult, Audit.verifyOscarProperties(tempFile.getPath()));
    }

    @Test
    public void exceptionVerifyOscarProperties() throws IOException {
        File tempFile = folder.newFile("file.txt");
        String expectedResult = "Could not read properties file to verify Oscar tags.";      
        assertEquals(expectedResult, Audit.verifyOscarProperties(tempFile.toString()));
    }

    /*
    *  verifyDrugRef(): ***FIX MY PARAMETERS***
    *  Verify all possible Drugref deployments.
    *  Grab all possible Drugref deployed folder names in root directory
    *  and push onto stack. Pop names off of the stack and verify
    *  each properties file that exists in "catalinaHome" directory.
    */

    /*
    *  verifyDrugRefProperties(String fileName):
    *  Read "db_user," "db_url," and "db_driver" tags of Drugref properties file.
    *
    *  Note: There are 9 unique permutations for the isMatch behavior of this method.
    */

    // isMatch1, isMatch2, isMatch3
    @Test
    public void isMatchAllVerifyDrugrefProperties() throws IOException {
        File tempFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(tempFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "db_driver=com.mysql.jdbc.Driver\n");
        String expectedResult = "\"db_driver\" tag is configured as: com.mysql.jdbc.Driver<br />"
                                    + "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />";
        assertEquals(expectedResult, Audit.verifyDrugrefProperties(tempFile.getPath()));
    }

    // isMatch1, isMatch2, !isMatch3
    @Test
    public void isMatch12VerifyDrugrefProperties() throws IOException {
        File tempFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(tempFile, "db_user=root\n"
                                                + "db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "#db_driver=com.mysql.jdbc.Driver\n");
        String expectedResult = "\"db_url\" tag is configured as: jdbc:mysql://127.0.0.1:3306/drugref<br />"
                                    + "\"db_user\" tag is configured as: root<br />"
                                    + "\"db_driver\" tag is not configured properly.<br />";
        assertEquals(expectedResult, Audit.verifyDrugrefProperties(tempFile.getPath()));
    }

    // !isMatch1, !isMatch2, !isMatch3
    @Test
    public void isMatchFalseVerifyDrugrefProperties() throws IOException {
        File tempFile = folder.newFile("semiCorrectInfo.properties");
        FileUtils.writeStringToFile(tempFile, "#db_user=root\n"
                                                + "#db_url=jdbc:mysql://127.0.0.1:3306/drugref\n"
                                                + "#db_driver=com.mysql.jdbc.Driver\n");
        String expectedResult = "\"db_user\" tag is not configured properly.<br />"
                                    + "\"db_url\" tag is not configured properly.<br />"
                                    + "\"db_driver\" tag is not configured properly.<br />";
        assertEquals(expectedResult, Audit.verifyDrugrefProperties(tempFile.getPath()));
    }

    @Test
    public void exceptionVerifyDrugrefProperties() throws IOException {
        File tempFile = folder.newFile("file.txt");
        String expectedResult = "Could not read properties file to verify Drugref tags.";      
        assertEquals(expectedResult, Audit.verifyDrugrefProperties(tempFile.toString()));
    }

    /*
    *  tomcatReinforcement():
    *  Read "xmx" and "xms" values of Tomcat.
    */

    /*
    *  grabFiles(File directory, String regex):
    *  Loop through folders/files in directory and push all possible files
    *  (using pattern matching) onto the Stack.
    */

    @Test
    public void allGrabFiles() throws IOException {
        File testingFolder = folder.newFolder("testingFolder"); // grabFiles requires File object
        File tempFolder1 = new File(testingFolder.getPath() + "/oscar15");
        tempFolder1.mkdir();
        File tempFolder2 = new File(testingFolder.getPath() + "/oscar15_bc");
        tempFolder2.mkdir();
        File tempFolder3 = new File(testingFolder.getPath() + "/oscar3foo");
        tempFolder3.mkdir();
        Stack<String> expectedResult = new Stack<String>();
        expectedResult.push("oscar15");
        expectedResult.push("oscar15_bc");
        expectedResult.push("oscar3foo");
        assertEquals(expectedResult, Audit.grabFiles(testingFolder, "^(oscar[0-9]*\\w*)$"));
    }

    @Test
    public void oneGrabFiles() throws IOException {
        File testingFolder = folder.newFolder("testingFolder");
        File tempFolder1 = new File(testingFolder.getPath() + "/foobar1");
        tempFolder1.mkdir();
        File tempFolder2 = new File(testingFolder.getPath() + "/foobar912foobar");
        tempFolder2.mkdir();
        File tempFolder3 = new File(testingFolder.getPath() + "/oscar10");
        tempFolder3.mkdir();
        Stack<String> expectedResult = new Stack<String>();
        expectedResult.push("oscar10");
        assertEquals(expectedResult, Audit.grabFiles(testingFolder, "^(oscar[0-9]*\\w*)$"));
    }

    @Test
    public void noFileListGrabFiles() throws IOException {
        File testingFolder = folder.newFolder("testingFolder");
        Stack<String> expectedResult = new Stack<String>(); // empty stack
        assertEquals(expectedResult, Audit.grabFiles(testingFolder, "^(oscar[0-9]*\\w*)$"));
    }

    /* VERIFYOSCAR/VERIFYDRUGREF COVER THIS
    @Test
    public void noFilesGrabFiles() throws IOException {
        File tempFile = folder.newFile("correctInfo");
        FileUtils.writeStringToFile(tempFile, "DISTRIB_DESCRIPTION=\"Ubuntu 14.04.5 LTS\"");
        String expectedResult = "\"Ubuntu 14.04.5 LTS\"";
        assertEquals(expectedResult, Audit.serverVersion(tempFile.getPath()));
    }*/
}
