import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* MASTER TODO:
* - Proper error handling (all methods)
* - OS compatible
* - Do tags have to be capitalized?
*/
public class Audit {

	/*
	* TODO: 
	* - Check to see that this command will always work
	*/
	private static void serverVersion() {
        	try {
			boolean check = checkLSB();
			if (!check)
				installLSB();
			else {
				String s;
        			Process p = Runtime.getRuntime().exec("lsb_release -r");
                		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                		while ((s = br.readLine()) != null)
                			System.out.println("Ubuntu server version: " + s.substring(9));
                		p.destroy();
			}
        	} catch (Exception e) {
                	System.out.println("Server version error: " + e);
         	}
	}

	/*
	* Check to see if "lsb release" command exists.
	*/	
	private static boolean checkLSB() {
		return false;
	}

	private static void installLSB() {
		try {	
			System.out.println("Currently installing \"lsb release\" command for Ubuntu...");
			Process install = Runtime.getRuntime().exec("sudo apt-get install lsb-release");
			install.destroy();
		} catch (Exception e) {
			System.out.println("Could not install \"lsb release\" command for Ubuntu: " + e);
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

        /*
        * TODO: 
        * - Check to see where this file is located (and what it could be named)
        */
	private static void oscarBuild() {
		String s;
		File oscar = new File("/usr/share/tomcat7/oscar.properties");
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
				if (isMatch1 && isMatch2 && isMatch3)
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
				if (isMatch1 && isMatch2 && isMatch3 && isMatch4)
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
        * - Check to see that this command will always work
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
