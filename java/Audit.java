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
	* Check to see if "lsb release" command exists.
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
		}
		catch (Exception e) {
			System.out.println("command -v lsb_release don't work!?!?: " + e);
			return false;
		}		
	}

	/* CANNOT EDIT SYSTEM WHILE AUDITING
	private static void installLSB() {
		try {	
			Process install = null;
			System.out.println("Currently installing \"lsb release\" command for Ubuntu...");
			install = Runtime.getRuntime().exec("sudo apt-get install lsb-release");
			install.getOutput().write("y\n".getBytes());
			install.destroy();
			System.out.println("\"lsb release\" install successful.");
		} catch (Exception e) {
			System.out.println("Could not install \"lsb release\" command for Ubuntu: " + e);
		}
	}
	*/
	
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

        /*
        * TODO: 
        * - Check to see where this file is located (and what it could be named)
        */
	private static void oscarBuild() {
		if (!searchForFiles("/usr/share/tomcat7/", "^(oscar)[0-9]*?.*(properties)$")) {
			System.out.println("Could not find file for Oscar build/version.");
			return;
		}
		String s;
		File oscar = new File ("/usr/share/tomcat7/oscar.properties");
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
	* NOTE:
	*  - File object can be considered a file or a directory!
	*  - Can't change working directory while executing a Java program
	*  - But can use Process/ProcessBuilder to list all files in the directory
	*/
	private static boolean searchForFile(String path, String pattern) {		
		String filename = "";
		filename = pattern;
		File file = new File(path + filename);	
		
		if (file.canRead()) {
			System.out.println("SEARCH FOR FILE CHECK: " + file.canRead() + " and DIRECTORY?: " + file.isDirectory());
			return true;
		}
		else {
			System.out.println("SEARCH FOR FILE CHECK: " + file.canRead());
			return false;	
		}
	}
	
	/*
	*  - Read/sort through all files that have "pattern" in name
        *  - Take the highest version value (i.e read "oscar15.properties" vs. "oscar2.properties")
	*/
	private static boolean searchForFiles(String path, String pattern) {
                String filename = "";
		Stack<String> files = new Stack<String>(); 
                File directory = new File(path); 
		
		String[] fileList = directory.list();
		System.out.println("All files in directory:");
		for (int i = 0; i < fileList.length; i++) {
			Arrays.sort(fileList);
			System.out.println(fileList[i]);
		}
		
		System.out.println("Adding all possible files:");
		for (int i = 0; i < fileList.length; i++) {
			if (Pattern.matches(pattern, fileList[i])) {
				System.out.println(fileList[i]);
				files.push(fileList[i]);
			}
		}
		
                if (directory.canRead()) {
                        System.out.println("SEARCH FOR FILE CHECK: " + directory.canRead() + " and DIRECTORY?: " + directory.isDirectory());
                        return true;
                }
                else {
                        System.out.println("SEARCH FOR FILE CHECK: " + directory.canRead());
                        return false;
                }
                // search in directory for all the files that contain the pattern (if no file w/ pattern is found, return false)        
                // grab first one
                // return true??    
	}

        /*
        * TODO: 
        * - Check to see where this file is located (and what it could be named)
        */
	private static void verifyOscar() {
		String s;
                File oscar = new File("/usr/share/tomcat7/oscar.properties");
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
                                if (isMatch1) {
					if (s.substring(13).toLowerCase().equals("yes")) {
						flag1 = true;
						System.out.println(s);
					}
                                }
				if (isMatch2) {
					if (s.substring(18).toLowerCase().equals("true")) {
						flag2 = true;
						System.out.println(s);
					}
				}
				if (isMatch3) {
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
        * TODO: 
        * - Check to see where this file is located (and what it could be named)
	* - Remove hard code for port number (use guest port)
        */
	private static void verifyDrugref() {
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
                                if (isMatch1) {
                                        if (!s.substring(8).equals("")) {
						flag1 = true;
                                                System.out.println(s);
                                        }
                                }
                                if (isMatch2) {
                                        if (s.substring(7).toLowerCase().equals("jdbc:mysql://127.0.0.1:3306/drugref")) { // will port value &
	                                        flag2 = true;        							  // drugref always be
						System.out.println(s);							  // these values?
                                        }
                                }
                                if (isMatch3) {
                                        if (s.substring(10).toLowerCase().equals("com.mysql.jdbc.driver")) {
                                                flag3 = true;
						System.out.println(s);
                                        }
                                }
				if (isMatch4) {
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
			serverVersion();
			JVMTomcat7();
			mysqlVersion();
			oscarBuild();
			verifyOscar();
			verifyDrugref();
			verifyTomcat();
		} else if (os.toLowerCase().equals("windows")) { // fix me to RE

		} else { // unix

		}
	}
}
