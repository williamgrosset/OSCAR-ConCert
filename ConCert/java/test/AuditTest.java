package oscar.util;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AuditTest {
 
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /*
    *  serverVersion(String: fileName):
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    */
 
    @Test
    public void exceptionServerVersion() throws IOException {
        File tempFile = folder.newFile("file.txt");
        String expectedResult = "Could not run \"lsb release\" command to detect Ubuntu server version.";
        assertEquals(expectedResult, Audit.serverVersion(tempFile.toString()));
    }

    @Test
    public void isMatchTrueServerVersion() throws IOException {
        String expectedResult = "\"Ubuntu 14.04.5 LTS\"";
        assertEquals(expectedResult, Audit.serverVersion("/etc/lsb-release"));
    }

    @Test
    public void isMatchFalseServerVersion() throws IOException {
        //File tempFile = folder.newFile("fake-lsb-release");
        String expectedResult = "Could not detect Ubuntu server version.";
        //assertEquals(expectedResult, Audit.serverVersion(tempFile.toString()));
        assertEquals(expectedResult, Audit.serverVersion("/etc/environment"));
    }

    /*
    *  JVMTomcat7(String: binPath):
    *  Read bash script and extract JVM/Tomcat version information.
    */

    @Test
    public void isMatchTrueJVMTomcat7() {
        String expectedResult = "Tomcat version: Apache Tomcat/7.0.52 (Ubuntu)<br />JVM Version: 1.7.0_80-b15<br />";
        assertEquals(expectedResult, Audit.JVMTomcat7("/usr/share/tomcat7/bin"));
    }

    /*
    *  mysqlVersion(String: cmd):
    *  Run "mysql --version" command and extract version information.
    */
    @Test
    public void exceptionMysqlVersion() {
        String expectedResult = "Could not run \"mysql --version\" command to detect MySQL version.";
        assertEquals(expectedResult, Audit.mysqlVersion("notamysqlcommand --version"));
    }

    @Test
    public void isMatchMysqlVesion() {
        String expectedResult = "14.14 Distrib 5.5.53, for debian-linux-gnu (x86_64) using readline 6.3";
        assertEquals(expectedResult, Audit.mysqlVersion("mysql --version"));
    }

    /*
    *  verifyOscar(String: path):
    *  Verify all possible Oscar deployments.
    *  Grab all possible Oscar deployed folder names in root directory 
    *  and push onto stack. Pop names off of the stack and verify 
    *  each properties file that exists in "catalinaHome" directory.
    */
}
