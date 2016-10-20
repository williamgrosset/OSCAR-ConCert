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
* - Do tags have to be capitalized?
* - Go to highest version number of file (use REGex)
*/
public class Audit {

	/* 
	*  Run "lsb_release -r" command and extract release value.
	*  Read properties file if command does not exist?
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
	* NOTE: CURRENTLY NOT USED
	* - Check to see if "lsb release" command exists.
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
    * TODO: 
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
    * TODO: 
    * - Check to see that this command will always work (similar to Ubuntu version)
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

    private static void verifyOscar() {
        File directory = searchForDirectory("/var/lib/tomcat7", ".*catalina.base.*");
        Stack<String> files = grabFiles(directory, "^(oscar)[0-9]*?.*(properties)$");
        
        // Verify files
        /*
        * while (!files.empty())
        *   files.pop()
        *   oscarBuild(file.pop()) // Keep file as String or as File object?
        *   verifyOscarProperties(file.pop())
        */
    }

	/*
    *  - Search in $CATALINA_BASE/webapps directory for all folders that could be deployed
    *    (EDGE CASE: if the folder does NOT contain the pattern "oscar")
	*  - Search in CATALINA BASE directory using command: ps -ef | grep tomcat7 (search for tag)
    *  - Grab the CATALINA BASE directory from the command output
    *  - Return directory name
	*  - Fix RE for in pattern
	*/
	private static File searchForDirectory(String defaultPath, String regex) {
		CharSequence pathName = "";
		Stack<String> files = new Stack<String>();
		boolean isMatch = false;
		File directory = new File(defaultPath);

		// Search for "CATALINA BASE" directory
        try {
			String s = "";
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", "ps -ef | grep tomcat7"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s);
                isMatch = matcher.matches();
                //isMatch = Pattern.matches(regex, s);
				System.out.println("Did we get a match?: " + isMatch);
                if (isMatch) {
                    // Properly take substring (start from match.end() up until we hit whitespace -> pathName)
                    pathName = matcher.group(0);
					//pathName = cs.subSequence(60, 80);
                    System.out.println("Currently in TRY/CATCH block, our value of pathname: " + pathName); // check if path is correct
					break;
                }
            }
            System.out.println("VALUE OF S (line we are reading through): " + s);
            p.destroy();
            if (!isMatch) {
                System.out.println("DEFAULT PATH USED: " + directory);
                return new File(defaultPath);
            } else {
                System.out.println("PATHNAME FOUND AND USED: " + directory);
                return new File(pathName.toString());
            }
        } catch (Exception e) {
            System.out.println("Process check for Tomcat7 failed (using defaultPath): " + e);
            return new File(defaultPath);
        }
	}

    /*
    *  - Loop through folders/files in directory
    *  - Push files onto Stack w/ pattern
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
            System.out.println("Our stack is empty -> return false.");
            return files;
        }         
        return files;    
    }

    /*
    * TODO: 
    * - Check to see where this file is located (and what it could be named)
    */
    private static void oscarBuild(String fileName) {
        String s;
        File oscar = new File (fileName);
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
    * TODO: 
    * - Check to see where this file is located (and what it could be named)
    */
	private static void verifyOscarProperties(String fileName) {
		String s;
        File oscar = new File(fileName);
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
    
    private static void verifyDrugref() {}

    /*
    * TODO: 
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
    * TODO: 
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
            //verifyOscar();
			//oscarBuild();
			//verifyOscar();
			//verifyDrugref();
			//verifyTomcat();
		} else if (os.toLowerCase().equals("windows")) { // fix me to RE

		} else { // unix

		}
	}
}
