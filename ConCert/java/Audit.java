package oscar.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class Audit extends Action {

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            if (servletRequest.getSession().getAttribute("userrole") == null)
                servletResponse.sendRedirect("../logout.jsp");
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        servletRequest.setAttribute("serverVersion", serverVersion("/etc/lsb-release"));
        servletRequest.setAttribute("mysqlVersion", mysqlVersion("mysql --version"));
        servletRequest.setAttribute("verifyTomcat", verifyTomcat());
        servletRequest.setAttribute("verifyOscar", verifyOscar("/webapps"));
        servletRequest.setAttribute("verifyDrugref", verifyDrugref());
        servletRequest.setAttribute("tomcatReinforcement", tomcatReinforcement());

        return actionMapping.findForward("success");
    }

    private static File catalinaBase = searchForDirectory("/var/lib/tomcat7", ".*(catalina\\.base\\S+).*", "CATALINA_BASE");
    private static File catalinaHome = searchForDirectory("/usr/share/tomcat7", ".*(catalina\\.home\\S+).*", "CATALINA_HOME");

    /*
    *  serverVersion(String: fileName):
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    */
    protected static String serverVersion(String fileName) {
        String output = "";
        try {
            File lsbRelease = new File(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(lsbRelease)));
            boolean isMatch = false;
            String line = "";

            while ((line = br.readLine()) != null) {
                isMatch = Pattern.matches("^(DISTRIB_DESCRIPTION=).*", line);
                if (isMatch) {
                    output = line.substring(20);
                    break;
                }
            }
            if (!isMatch) {
                output = "Could not detect Ubuntu server version.";
            }
            return output;
        } catch (Exception e) {
            output = "Could not run \"lsb release\" command to detect Ubuntu server version.";
            return output;
        }
    }

    /*
    *  verifyTomcat():
    *  Verify JVM/Tomcat7 versions.
    */
    protected static String verifyTomcat() {
        return JVMTomcat7(catalinaHome.getPath()+"/bin");
    }

    /*
    *  JVMTomcat7(String: binPath):
    *  Read bash script and extract JVM/Tomcat version information.
    */
    protected static String JVMTomcat7(String binPath) {
        String output = "";
        try {
            String cmd = new String(binPath + "/version.sh");
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            String line = "";

            while ((line = br.readLine()) != null) {
                isMatch1 = Pattern.matches("^(JVM Version:).*", line);
                isMatch2 = Pattern.matches("^(Server version:).*", line);
                if (isMatch1) {
                    output += "JVM Version: " + line.substring(16) + "<br />";
                    flag1 = true;
                }
                if (isMatch2) {
                    output += "Tomcat version: " + line.substring(16) + "<br />";
                    flag2 = true;
                }
                if (flag1 && flag2)
                    break;
            }
            if (!flag1)
                output += "JVM version cannot be found." + "<br />";
            if (!flag2)
                output += "Tomcat version cannot be found." + "<br />";
            p.destroy();
            return output;
        } catch (Exception e) {
            output = "Could not run \"version.sh\" bash script to detect JVM/Tomcat version(s).";
            return output;
        }
     }

    /*
    *  mysqlVersion(String: cmd):
    *  Run "mysql --version" command and extract version information.
    */
    protected static String mysqlVersion(String cmd) {
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";

            while ((line = br.readLine()) != null)
                output = line.substring(11);
            p.destroy();
            return output;
        } catch (Exception e) {
            output = "Could not run \"mysql --version\" command to detect MySQL version.";
            return output;
        }
    }

    /*
    *  verifyOscar(String: path):
    *  Verify all possible Oscar deployments.
    *  Grab all possible Oscar deployed folder names in root directory 
    *  and push onto stack. Pop names off of the stack and verify 
    *  each properties file that exists in "catalinaHome" directory.
    */
    protected static String verifyOscar(String path) {
        String output = "";
        File webApps = new File(catalinaBase.getPath() + path);
        Stack<String> files = grabFiles(webApps, "^(oscar[0-9]*\\w*)$");

        if (files.empty()) {
            output = "Could not find any properties files for Oscar." + "<br />";
        }
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            output += "<b>Currently checking \"oscar_mcmaster.properties\" file..." + "</b><br />";
            output += oscarBuild("/var/lib/tomcat7/webapps/" + file + "/WEB-INF/classes/oscar_mcmaster");
            output += "<b>Currently checking \"" + file + ".properties\" file..." + "</b><br />";
            output += oscarBuild(catalinaHome+"/"+file);
            output += verifyOscarProperties(catalinaHome+"/"+file);
        }
        return output;
    }

    /*
    *  oscarBuild(String: fileName):
    *  Read Oscar buildtag of properties file.
    */
    protected static String oscarBuild(String fileName) {
        String output = "";
        try {
            File oscar = new File(fileName + ".properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
            boolean isMatch = false;
            String line = "";

            while ((line = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;
                isMatch = Pattern.matches("^(buildtag=).*", line);
                if (isMatch) {
                    output += "Oscar build and version: " + line.substring(9) + "<br />";
                    break;
                }
            }
            if (!isMatch) {
                output += "Oscar build/version cannot be found." + "<br />";
            }
            return output;
        } catch (Exception e) {
            output = "Could not read properties file to detect Oscar build." + "<br />";
            return output;
        }
    }

    /*
    *  verifyOscarProperties(String: fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," and
    *  "TMP_DIR" tags of properties file.
    */
    protected static String verifyOscarProperties(String fileName) {
        String output = "";
        try {
            File oscar = new File(fileName + ".properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean isMatch3 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            String line = "";

            while ((line = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;
                isMatch1 = Pattern.matches("^(HL7TEXT_LABS=).*", line);
                isMatch2 = Pattern.matches("^(SINGLE_PAGE_CHART=).*", line);
                isMatch3 = Pattern.matches("^(TMP_DIR=).*", line);
                if (isMatch1) { // HL7TEXT_LABS=
                    flag1 = true;
                    output += "\"HL7TEXT_LABS\" tag is configured as: " + line.substring(13) + "<br />";
                }
                if (isMatch2) { // SINGLE_PAGE_CHART=
                    flag2 = true;
                    output += "\"SINGLE_PAGE_CHART\" tag is configured as: " + line.substring(18) + "<br />";
                }
                if (isMatch3) { // TMP_DIR=
                    flag3 = true;
                    output += "\"TMP_DIR tag\" is configured as: " + line.substring(8) + "<br />";
                }
                if (flag1 && flag2 && flag3)
                    break;
            }
            if (!flag1)
                output += "\"HL7TEXT_LABS\" tag is not set to \"yes\" and is not configured properly." + "<br />";
            if (!flag2)
                output += "\"SINGLE_PAGE_CHART\" tag not set to \"true\" and is not configured properly." + "<br />";
            if (!flag3)
                output += "\"TMP_DIR\" tag is not set to a directory and is not configured properly." + "<br />";
            return output;
        } catch (Exception e) {
            output = "Could not read properties file to verify Oscar tags.";
            return output;
        }
    }

    /*
    *  verifyDrugref():
    *  Verify all Drugref deployments.
    *  Grab all possible Drugref deployed folder names in root directory 
    *  and push onto stack. Pop names off of the stack and verify 
    *  each properties file that exists in "catalinaHome" directory.
    */
    protected static String verifyDrugref() {
        String output = "";
        File webApps = new File(catalinaBase.getPath()+"/webapps");
        Stack<String> files = grabFiles(webApps, "^(drugref[0-9]*\\w*)$");

        if (files.empty()) {
            output = "Could not find any properties files for Drugref.";
        }
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            output += "<b>Currently checking \"" + file + ".properties\" file..." + "</b><br />";
            output += verifyDrugrefProperties(catalinaHome+"/"+file);
        }
        return output;
    }

    /*
    *  verifyDrugrefProperties(String: fileName):
    *  Read "db_user," "db_url," "db_driver," and
    *  "drugref_url" tags of properties file.
    */
    protected static String verifyDrugrefProperties(String fileName) {
        String output = "";
        try {
            File drugref = new File(fileName + ".properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(drugref)));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean isMatch3 = false;
            boolean isMatch4 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            boolean flag4 = false;
            String line = "";

            while ((line = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", line))
                    continue;
                isMatch1 = Pattern.matches("^(db_user=).*", line);
                isMatch2 = Pattern.matches("^(db_url=).*", line);
                isMatch3 = Pattern.matches("^(db_driver=).*", line);
                isMatch4 = Pattern.matches("^(drugref_url=).*", line);
                if (isMatch1) { // db_user=
                    flag1 = true;
                    output += "\"db_user\" tag is configured as: " + line.substring(8) + "<br />";
                }
                if (isMatch2) { // db_url=
                    flag2 = true;
                    output += "\"db_url\" tag is configured as: " + line.substring(8) + "<br />";
                }
                if (isMatch3) { // db_driver=
                    flag3 = true;
                    output += "\"db_driver\" tag is configured as: " + line.substring(10) + "<br />";
                }
                if (isMatch4) { // drugref_url=
                    flag4 = true;
                    output += "\"drugref_url\" tag is configured as: " + line.substring(12) + "<br />";
                }
                if (flag1 && flag2 && flag3 && flag4)
                    break;
            }
            if (!flag1)
                output += "\"db_user\" tag not configured properly." + "<br />";
            if (!flag2)
                output += "\"db_url\" tag not configured properly." + "<br />";
            if (!flag3)
                output += "\"db_driver\" tag not configured properly." + "<br />";
            if (!flag4)
                output += "\"drugref_url\" tag not configured properly." + "<br />";
            return output;
        } catch (Exception e) {
            output = "Could not read properties file to verify Drugref tags.";
            return output;
        }
    }

    /*
    *  tomcatReinforcement():
    *  Read "xmx" and "xms" values of Tomcat.
    */
    protected static String tomcatReinforcement() {
        String output = "";
        try {
            String xmx = "";
            String xms = "";
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "/bin/ps -ef | /bin/grep tomcat"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            String line = "";

            while ((line = br.readLine()) != null) {
                Pattern pattern1 = Pattern.compile(".*(Xmx[0-9]+m).*");
                Matcher matcher1 = pattern1.matcher(line);
                isMatch1 = matcher1.matches();
                Pattern pattern2 = Pattern.compile(".*(Xms[0-9]+m).*");
                Matcher matcher2 = pattern2.matcher(line);
                isMatch2 = matcher2.matches();

                if (isMatch1) {
                    xmx = matcher1.group(1);
                    String[] xmxString = xmx.toString().split("x");
                    flag1 = true;
                    output += "Xmx value: " + xmxString[1] + "<br />";
                }
                if (isMatch2) {
                    xms = matcher2.group(1);
                    String[] xmsString = xms.toString().split("s");
                    flag2 = true;
                    output += "Xms value: " + xmsString[1] + "<br />";
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
            p.destroy();
            return output;
        } catch (Exception e) {
            output = "Could not find Tomcat process to detect amount of memory allocated.";
            return output;
        }
    }

    /////////////////////////////////
    //////// HELPER METHODS /////////
    /////////////////////////////////

    /*
    *  searchForDirectory(String: defaultPath, String: regex, String: defaultPathName):
    *  Run "ps" command to find Tomact7 details and
    *  then use pattern matching to find desired tags
    *  (i.e "$CATALINA_HOME" full path name).
    */
    protected static File searchForDirectory(String defaultPath, String regex, String defaultPathName) {
        CharSequence pathName = "";
        boolean isMatch = false;
        Stack<String> files = new Stack<String>();

        try {
            String s = "";
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "/bin/ps -ef | /bin/grep tomcat7"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((s = br.readLine()) != null) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s);
                isMatch = matcher.matches();
                if (isMatch) {
                    pathName = matcher.group(1);
                    String[] path = pathName.toString().split("=");
                    pathName = path[1];
                    break;
                }
            }
            p.destroy();
            // Use default file path
            if (!isMatch) {
                return new File(defaultPath + "/");
            }
            return new File(pathName.toString() + "/"); // type CharSequence (needs to be String to create File object)
        // Use default file path
        } catch (Exception e) {
            return new File(defaultPath + "/");
        }
    }

    /*
    *  grabFiles(File: directory, String: regex):
    *  Loop through folders/files in directory and
    *  push all possible files (pattern matching)
    *  onto the Stack.
    */
    protected static Stack<String> grabFiles(File directory, String regex) {
        String[] fileList = directory.list();
        Stack<String> files = new Stack<String>();

        // We did not find a file
        if (fileList == null || fileList.length == 0) {
            return files;
        }

        Arrays.sort(fileList);
        for (int i = 0; i < fileList.length; i++) {
            if (Pattern.matches(regex, fileList[i])) {
                files.push(fileList[i]);
            }
        }
        return files;
    }
}
