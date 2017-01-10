package oscar.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.lang.Object;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static org.junit.Assert.*;
import org.junit.Test;

public class AuditTest {
 
    /*
    *  serverVersion(String: fileName):
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    */
 
    @Test
    public void exceptionServerVersion() {
        String expectedResult = "Could not run \"lsb release\" command to detect Ubuntu server version.";
        assertEquals(expectedResult, Audit.serverVersion("/etc/aFileThatDoesNotExist"));
    }

    @Test
    public void isMatchTrueServerVersion() {
        String expectedResult = "\"Ubuntu 14.04.5 LTS\"";
        assertEquals(expectedResult, Audit.serverVersion("/etc/lsb-release"));
    }

    /* NEEDS FIXING: find a way to mock a file that does not contain the right tag
    @Test
    public void isMatchFalseServerVersion() {
        String expectedResult = "Could not detect Ubuntu server version.";
        assertEquals(expectedResult, Audit.serverVersion("/usr/share/tomcat7/oscar15_properties"));
    }
    */

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
