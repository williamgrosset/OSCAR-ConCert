/***********************************
 * OUTDATED: PLEASE SEE Audit.java * 
 ***********************************/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleAudit {
  
    private static File catalinaBase = searchForDirectory("/var/lib/tomcat7", ".*(catalina\\.base\\S+).*", "CATALINA_BASE");
    private static File catalinaHome = searchForDirectory("/usr/share/tomcat7", ".*(catalina\\.home\\S+).*", "CATALINA_HOME");

    /*
    *  serverVersion():
    *  Read "/etc/lsb-release" file and extract Ubuntu server version.
    */
    private static void serverVersion() {
        System.out.println("Currently checking Ubuntu server version...");
        try {
            String s = "";
            File lsbRelease = new File("/etc/lsb-release");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(lsbRelease)));
            boolean isMatch = false;

            while ((s = br.readLine()) != null) {
                isMatch = Pattern.matches("^(DISTRIB_DESCRIPTION=).*", s);
                if (isMatch) {
                    System.out.println("Ubuntu server version: " + s.substring(20));
                    break;
                }
            }
            if (!isMatch) {
                System.out.println("Could not detect Ubuntu server version.");
            }
        } catch (Exception e) {
            System.out.println("Could not run \"lsb release\" command to detect Ubuntu server version: " + e.getMessage());
        }
    }

    /*
    *  verifyTomcat():
    *  Verify JVM/Tomcat7 versions.
    */
    private static void verifyTomcat() {
        System.out.println("Verifying Tomcat...");
        JVMTomcat7(catalinaHome.getPath()+"/bin"); 
    }

    /* 
    *  JVMTomcat7(String: binPath):
    *  Read bash script and extract JVM/Tomcat version information.
    */
    private static void JVMTomcat7(String binPath) {
        try {
            String s = "";
            String cmd = new String(binPath + "/version.sh");
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            
            while ((s = br.readLine()) != null) {
                isMatch1 = Pattern.matches("^(JVM Version:).*", s);
                isMatch2 = Pattern.matches("^(Server version:).*", s);
                if (isMatch1) {
                    System.out.println("JVM Version: " + s.substring(16));
                    flag1 = true;
                }
                if (isMatch2) {
                    System.out.println("Tomcat version: " + s.substring(16));
                    flag2 = true;
                }
                if (flag1 && flag2)
                    break;
            }
            if (!flag1)
                System.out.println("JVM version cannot be found.");
            if (!flag2)
                System.out.println("Tomcat version cannot be found.");
            p.destroy();
        } catch (Exception e) {
            System.out.println("Could not run \"version.sh\" bash script to detect JVM/Tomcat version(s): " + e.getMessage());
        }
    }
	
    /* 
    *  mysqlVersion():
    *  Run "mysql --version" command and extract version value.
    */
    private static void mysqlVersion() { 
        System.out.println("Currently checking MySQL version...");
        try {
            String s = "";
            Process p = Runtime.getRuntime().exec("mysql --version");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((s = br.readLine()) != null)
                System.out.println("MySQL version: " + s.substring(11));
            p.destroy();
        } catch (Exception e) {
            System.out.println("Could not run \"mysql --version\" command to detect MySQL version: " + e.getMessage());
        }
    }

    /* 
    *  verifyOscar():
    *  Verify all Oscar deployments.
    */
    private static void verifyOscar() {
        System.out.println("Verifying Oscar...");
        
        File webApps = new File(catalinaBase.getPath()+"/webapps");
        System.out.println("Grabbing possible Oscar files...");
        Stack<String> files = grabFiles(webApps, "^(oscar[0-9]*\\w*)$");
        

        if (files.empty()) {
            System.out.println("Could not find any properties files for Oscar.");
        }
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            // Verify "oscar_mcmaster.properties" file (not on Stack)
            System.out.println("Currently checking \"oscar_mcmaster.properties\" file...");
            oscarBuild("/var/lib/tomcat7/webapps/" + file + "/WEB-INF/classes/oscar_mcmaster");
            // Verify properties file (on Stack)
            System.out.println("Currently checking \"" + file + ".properties\" file...");
            oscarBuild(catalinaHome+"/"+file);
            verifyOscarProperties(catalinaHome+"/"+file);
        }
    }

    /*
    *  oscarMcmasterBuild(String: fileName):
    *  Read Oscar buildtag of mcmaster properties file.
    */ 
    private static void oscarMcmasterBuild(String fileName) {
        try {
            String s = "";
            File oscarMcmaster = new File(fileName + ".properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscarMcmaster)));
            boolean isMatch = false;

            while ((s = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", s))
                    continue;
                isMatch = Pattern.matches("^(buildtag=).*", s);
                if (isMatch) {
                    System.out.println("Oscar build and version: " + s.substring(9));
                    break;
                }
            }
            if (!isMatch) {
                System.out.println("Oscar build/version cannot be found.");
            }
        } catch (Exception e) {
            System.out.println("Could not read \"oscar_mcmaster.properties\" file to detect build: " + e.getMessage());
        }

    }

    /* 
    *  oscarBuild(String: fileName):
    *  Read Oscar buildtag of properties file.
    */
    private static void oscarBuild(String fileName) {
        try {
            String s = "";
            File oscar = new File(fileName + ".properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
            boolean isMatch = false; 

            while ((s = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", s))
                    continue;
                isMatch = Pattern.matches("^(buildtag=).*", s);
                if (isMatch) {
                    System.out.println("Oscar build and version: " + s.substring(9));
                    break;
                }   
            }
            if (!isMatch) {
                System.out.println("Oscar build/version cannot be found.");
            }   
        } catch (Exception e) {
            System.out.println("Could not read properties file to detect Oscar build: " + e.getMessage());
        }   
    }

    /*
    *  verifyOscarProperties(String: fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," and
    *  "TMP_DIR" tags of properties file.
    */
    private static void verifyOscarProperties(String fileName) {
        try {
            String s = "";
            File oscar = new File(fileName + ".properties");
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean isMatch3 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;

            while ((s = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", s))
                    continue;
                isMatch1 = Pattern.matches("^(HL7TEXT_LABS=).*", s);
                isMatch2 = Pattern.matches("^(SINGLE_PAGE_CHART=).*", s);
                isMatch3 = Pattern.matches("^(TMP_DIR=).*", s);
                if (isMatch1) { // HL7TEXT_LABS=
                    flag1 = true;
                    System.out.println("\"HL7TEXT_LABS\" tag is configured as: " + s.substring(13));
                }
                if (isMatch2) { // SINGLE_PAGE_CHART=
                    flag2 = true;
                    System.out.println("\"SINGLE_PAGE_CHART\" tag is configured as: " + s.substring(18));
                }
                if (isMatch3) { // TMP_DIR=
                    flag3 = true;
                    System.out.println("\"TMP_DIR tag\" is configured as: " + s.substring(8));
                }
                if (flag1 && flag2 && flag3)
                    break;
            }
            if (!flag1)
                System.out.println("\"HL7TEXT_LABS\" tag is not set to \"yes\" and is not configured properly.");        
            if (!flag2)
                System.out.println("\"SINGLE_PAGE_CHART\" tag not set to \"true\" and is not configured properly.");    
            if (!flag3)
                System.out.println("\"TMP_DIR\" tag is not set to a directory and is not configured properly.");              
        } catch (Exception e) {
            System.out.println("Could not read properties file to verify Oscar tags: " + e.getMessage());
        }
    }
    
    /*
    *  verifyDrugref():
    *  Verify all Drugref deployments.
    */
    private static void verifyDrugref() {
        System.out.println("Verifying Drugref...");
       
        File webApps = new File(catalinaBase.getPath()+"/webapps");
        System.out.println("Grabbing possible Drugref files...");
        Stack<String> files = grabFiles(webApps, "^(drugref[0-9]*\\w*)$");

        if (files.empty()) {
            System.out.println("Could not find any properties files for Drugref.");
        } 
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            System.out.println("Currently checking \"" + file + ".properties\" file...");
            verifyDrugrefProperties(catalinaHome+"/"+file);
        }
    }

    /*
    *  verifyDrugrefProperties(String: fileName):
    *  Read "db_user," "db_url," "db_driver," and
    *  "drugref_url" tags of properties file.
    */
    private static void verifyDrugrefProperties(String fileName) {
        try {
            String s = "";
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

            while ((s = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", s))
                    continue;
                isMatch1 = Pattern.matches("^(db_user=).*", s);
                isMatch2 = Pattern.matches("^(db_url=).*", s);
                isMatch3 = Pattern.matches("^(db_driver=).*", s);
                isMatch4 = Pattern.matches("^(drugref_url=).*", s);
                if (isMatch1) { // db_user=
                    flag1 = true;
                    System.out.println("\"db_user\" tag is configured as: " + s.substring(8));
                }
                if (isMatch2) { // db_url=
                    flag2 = true;        							                              
                    System.out.println("\"db_url\" tag is configured as: " + s.substring(8));
                }
                if (isMatch3) { // db_driver=
                    flag3 = true;
                    System.out.println("\"db_driver\" tag is configured as: " + s.substring(10));
                }
                if (isMatch4) { // drugref_url=
                    flag4 = true;
                    System.out.println("\"drugref_url\" tag is configured as: " + s.substring(12));
                }
                if (flag1 && flag2 && flag3 && flag4)
                    break;
            }
            if (!flag1)
                System.out.println("\"db_user\" tag not configured properly."); 	
            if (!flag2)
                System.out.println("\"db_url\" tag not configured properly.");          
            if (!flag3)
                System.out.println("\"db_driver\" tag not configured properly.");       
            if (!flag4)
                System.out.println("\"drugref_url\" tag not configured properly.");     
        } catch (Exception e) {
            System.out.println("Could not read properties file to verify Drugref tags: " + e.getMessage());
        }
    }

    /*
    *  tomcatReinforcement():
    *  Read "JAVA_OPTS" tag in properties file.
    */
    private static void tomcatReinforcement() {
        try {
            String s = "";
            String xmx = "";
            String xms = "";
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "/bin/ps -ef | /bin/grep tomcat"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            boolean isMatch1 = false;
            boolean isMatch2 = false;
            boolean flag1 = false;
            boolean flag2 = false;
            
            System.out.println("Currently checking Tomcat reinforcement...");
            while ((s = br.readLine()) != null) {
                Pattern pattern1 = Pattern.compile(".*(Xmx[0-9]+m).*");
                Matcher matcher1 = pattern1.matcher(s);
                isMatch1 = matcher1.matches();
                Pattern pattern2 = Pattern.compile(".*(Xms[0-9]+m).*");
                Matcher matcher2 = pattern2.matcher(s);
                isMatch2 = matcher2.matches();

                if (isMatch1) {
                    xmx = matcher1.group(1);
                    String[] xmxString = xmx.toString().split("x");
                    flag1 = true;
                    System.out.println("Xmx value: " + xmxString[1]);
                }
                if (isMatch2) {
                    xms = matcher2.group(1);
                    String[] xmsString = xms.toString().split("s");
                    flag2 = true;
                    System.out.println("Xms value: " + xmsString[1]);
                }
                if (flag1 && flag2)
                    break;
            }
            if (!flag1) {
                System.out.println("Could not detect Xmx value.");
            }
            if (!flag2) {
                System.out.println("Could not detect Xms value.");
            }
            p.destroy();
        } catch (Exception e) {
            System.out.println("Could not find Tomcat process to detect amount of memory allocation: " + e.getMessage());
        }
    }

    /////////////////////////////////
    //////// HELPER METHODS /////////
    /////////////////////////////////

    /* 
    *  searchForDirectory(String: defaultPath, String: regex):
    *  Run "ps" command to find Tomact7 details and
    *  then use pattern matching to find desired tags
    *  (i.e "$CATALINA_HOME" full path name).
    */
    private static File searchForDirectory(String defaultPath, String regex, String defaultPathName) {
        CharSequence pathName = "";
        boolean isMatch = false;
        Stack<String> files = new Stack<String>();

        System.out.println("Currently looking for " + defaultPathName + " path...");
        try {
            String s = "";
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "/bin/ps -ef | /bin/grep tomcat"});
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
            if (!isMatch) {
                System.out.println("Could not find specified path (using " + defaultPathName + " defaultPath).");
                return new File(defaultPath + "/");
            }
            return new File(pathName.toString() + "/"); // type CharSequence (needs to be String to create File object)
        } catch (Exception e) {
            System.out.println("Process check for Tomcat7 failed (using defaultPath): " + e.getMessage());
            return new File(defaultPath + "/");
        }
    }

    /*
    *  grabFiles(File: directory, String: regex):
    *  Loop through folders/files in directory and
    *  push all possible files (pattern matching)
    *  onto the Stack.
    */
    private static Stack<String> grabFiles(File directory, String regex) {
        String[] fileList = directory.list();
        Stack<String> files = new Stack<String>();

        // We did not find a file
        if (fileList == null || fileList.length == 0) {
            System.out.println("No possible files were found in directory.");
            return files;
        }
        
        Arrays.sort(fileList);
        // List all deployed folders in directory      
        //System.out.println("All deployed folders in directory:");
        //for (int i = 0; i < fileList.length; i++) {
        //    System.out.println(fileList[i]);
        //}
         
        // List all possible file(s) that we are looking for
        System.out.println("Adding all possible file(s):");
        for (int i = 0; i < fileList.length; i++) {
            if (Pattern.matches(regex, fileList[i])) {
                files.push(fileList[i]);
            }
        }  
        return files;    
    }

    /////////////////////////////////
    ///////// MAIN METHOD ///////////
    /////////////////////////////////

    public static void main(String args[]) {
        System.out.println("CATEGORY 1 AUDIT: ");
        serverVersion();
        mysqlVersion();
        verifyTomcat();
        verifyOscar();
        verifyDrugref();
        tomcatReinforcement();
    }
}