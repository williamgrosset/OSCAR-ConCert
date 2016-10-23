import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* MASTER TODO:
* - Proper error handling (all methods)
* - OS compatible
*/
public class Audit {

	/* 
    *  serverVersion():
	*  Run "lsb_release -r" command and extract release value.
	*/
	private static void serverVersion() {
        try {
		    String s;
            Process p = Runtime.getRuntime().exec("lsb_release -r");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("Ubuntu server version: " + s.substring(9));
            p.destroy();
        } catch (Exception e) {
            System.out.println("Could not detect sever version.");
            System.out.println("Error: " + e);
        }
    }

	/*
    *  CURRENTLY NOT USED
    *  checkLSB():
	*  Check to see if "lsb release" command exists.
	*/	
	private static boolean checkLSB() {
		try {
			String str;
			Process p = Runtime.getRuntime().exec("which lsb_release");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((str = br.readLine()) != null) {
				return true;
			}
			return false;
		} catch (Exception e) {
			System.out.println("command -v lsb_release don't work!?!?: " + e);
			return false;
		}		
	}

    /* 
    *  JVMTomcat7():
    * - Check to see where this bash script is located (and what it could be named)
    */
	private static void JVMTomcat7() {
		String s;
        String cmd = new String("/usr/share/tomcat7/bin/version.sh");
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            boolean isMatch = false;
            while ((s = br.readLine()) != null) {
                isMatch = Pattern.matches("^(JVM Version:).*", s);
                if (isMatch) {
                    System.out.println(s);
                    break;
                }
            }
            if (!isMatch)
                System.out.println("JVM/Tomcat7 version cannot be found.");
            p.destroy();
        } catch (Exception e) {
            System.out.println("JVM/Tomcat7 version error: " + e);
        }
	}
	
    /* 
    *  mysqlVersion():
    *  Run "mysql --version" command and extract version value.
    */
	private static void mysqlVersion() {
		String s;
        try {
            Process p = Runtime.getRuntime().exec("mysql --version");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("MySQL version: " + s.substring(11));
            p.destroy();
        } catch (Exception e) {
            System.out.println("MySQL version error: " + e);
        }
	}

    /* 
    *  verifyOscar():
    *  Verify all Oscar deployments.
    */
    private static void verifyOscar() {
        File catalinaBase = searchForDirectory("/var/lib/tomcat7", ".*(catalina\\.base\\S+).*");
        File catalinaHome = searchForDirectory("/usr/share/tomcat7", ".*(catalina\\.home\\S+).*");
        File webApps = new File(catalinaBase.getPath()+"/webapps");
        Stack<String> files = grabFiles(webApps, "^(oscar[0-9]*?)$");
        
        // Verify files on the Stack
        while (!files.empty()) {
            String file = files.pop();
            System.out.println("Currently checking \"" + file + ".properties\" file...");
            oscarBuild(catalinaHome+"/"+file);
            verifyOscarProperties(catalinaHome+"/"+file);
        }
    }

	/* 
    *  searchForDirectory(String: defaultPath, String: regex):
	*  Run "ps" command to find Tomact7 details and
    *  then use pattern matching to find desired tags
    *  (i.e "$CATALINA_HOME" full path name).
	*/
	private static File searchForDirectory(String defaultPath, String regex) {
		CharSequence pathName = "";
		boolean isMatch = false;
        Stack<String> files = new Stack<String>();

        try {
			String s = "";
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep tomcat7"});
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
            System.out.println("VALUE OF S (line we are reading through): " + s);
            p.destroy();
            if (!isMatch) {
                System.out.println("DEFAULT PATH USED");
                return new File(defaultPath + "/");
            } else {
                System.out.println("PATHNAME FOUND AND USED");
                return new File(pathName.toString() + "/"); // type CharSequence (needs to be String to create File object)
            }
        } catch (Exception e) {
            System.out.println("Process check for Tomcat7 failed (using defaultPath): " + e);
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
        
        // List all deployed folders in directory      
        System.out.println("All deployed folders in directory:");
        for (int i = 0; i < fileList.length; i++) {
                Arrays.sort(fileList);
                System.out.println(fileList[i]);
        }
         
        // List all possible file(s) that we are looking for
        System.out.println("Adding all possible file(s):");
        for (int i = 0; i < fileList.length; i++) {
                if (Pattern.matches(regex, fileList[i])) {
                     System.out.println(fileList[i]);
                     files.push(fileList[i]);
                }
        }
         
        // We did not find a file   
        if (files.empty()) {
            System.out.println("No possible files were found.");
        }         
        return files;    
    }

    /* 
    *  oscarBuild(String: fileName):
    *  Read Oscar buildtag of properties file.
    */
    private static void oscarBuild(String fileName) {
        String s;
        File oscar = new File(fileName + ".properties");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
            boolean isMatch = false; 
            while ((s = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", s))
                    continue;
                isMatch = Pattern.matches("^(buildtag=).*", s);
                if (isMatch) {
                    System.out.println("Build and version: " + s.substring(9));
                    break;
                }   
            }
            if (!isMatch) {
                System.out.println("Oscar build/version cannot be found.");
            }   
        } catch (Exception e) {
            System.out.println("Oscar build error: " + e);
        }   
    }

    /*
    *  verifyOscarProperties(String: fileName):
    *  Read "HL7TEXT_LABS," "SINGLE_PAGE_CHART," and
    *  "TMP_DIR" tags of properties file.
    */
	private static void verifyOscarProperties(String fileName) {
		String s;
        File oscar = new File(fileName + ".properties");
        try {
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
					if (s.substring(13).toLowerCase().equals("yes")) {
						flag1 = true;
						System.out.println(s);
					}
                }
				if (isMatch2) { // SINGLE_PAGE_CHART=
					if (s.substring(18).toLowerCase().equals("true")) {
						flag2 = true;
						System.out.println(s);
					}
				}
				if (isMatch3) { // TMP_DIR=
					if (!s.substring(8).equals("")) {
						flag3 = true;
						System.out.println(s);
					}
				}
				if (flag1 && flag2 && flag3)
					break;
            }
			if (!flag1)
				System.out.println("\"HL7TEXT_LABS\" tag not configured properly.");        
			if (!flag2)
				System.out.println("\"SINGLE_PAGE_CHART\" tag not configured properly.");    
			if (!flag3)
				System.out.println("\"TMP_DIR\" tag not configured properly.");              
        } catch (Exception e) {
            System.out.println("Oscar verification error: " + e);
        }
	}
    
    /*
    *  verifyDrugref():
    *  Verify all Drugref deployments.
    */
    private static void verifyDrugref() {}

    /*
    *  verifyDrugrefProperties():
    * - Check to see where this file is located (and what it could be named)
	* - Remove hard code for port number (use guest port)
    */
	private static void verifyDrugrefProperties() {
        String s;
        File drugref = new File("/usr/share/tomcat7/drugref2.properties");
        try {
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
                    if (!s.substring(8).equals("")) {
                        flag1 = true;
                        System.out.println(s);
                    }
                }
                if (isMatch2) { // db_url=
                    if (s.substring(7).toLowerCase().equals("jdbc:mysql://127.0.0.1:3306/drugref")) { // will port value &
                        flag2 = true;        							                              // drugref always be
                        System.out.println(s);							                              // these values?
                    }
                }
                if (isMatch3) { // db_driver=
                    if (s.substring(10).toLowerCase().equals("com.mysql.jdbc.driver")) {
                        flag3 = true;
                        System.out.println(s);
                    }
                }
				if (isMatch4) { // drugref_url=
					if (!s.substring(12).toLowerCase().equals("")) { 
						flag4 = true;
						System.out.println(s);
					}
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
            System.out.println("Drugref verification error: " + e);
        }
    }

    /*
    *  verifyTomcat():
    * - Check to see where this file is located (and what it could be named)
	* - Figure out how to check for increased memory resources
    */
	private static void verifyTomcat() {
        String s;
        File tomcat = new File("/etc/default/tomcat7");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(tomcat)));
            while ((s = br.readLine()) != null) {
                if (Pattern.matches("^(#).*", s))
                    continue;
                boolean isMatch = Pattern.matches("^(JAVA_OPTS=).*", s);
                if (isMatch) {
                    if (!s.substring(10).equals("")) {
                        System.out.println(s);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Tomcat verification error: " + e);
        }
	}

	public static void main(String args[]) {
		// Verify operating system && run corresponding functions
		String os = System.getProperty("os.name");
		
		if (os.toLowerCase().equals("linux")) {
			//serverVersion();
			//JVMTomcat7();
			//mysqlVersion();
            verifyOscar();
			//oscarBuild();
			//verifyOscar();
			//verifyDrugref();
			//verifyTomcat();
		} else if (os.toLowerCase().equals("windows")) { // fix me to RE

		} else { // unix

		}
	}
}
