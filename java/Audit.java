import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
* MASTER TODO:
* - Proper error handling (all methods)
* - Fix regular expressions (all methods)
* - OS compatible
*/
public class Audit {

	/*
	* TODO: 
	* - Check to see that this command will always work
	*/
	private static void serverVersion() {
		String s;
        	try {
        		Process p = Runtime.getRuntime().exec("lsb_release -r");
                	BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                	while ((s = br.readLine()) != null)
                		System.out.println("Ubuntu server version: " + s.substring(9));
                	p.destroy();
        	} catch (Exception e) {
                	System.out.println("Server version error: " + e);
         	}
	}

        /*
        * TODO: 
        * - Check to see where this bash script is located (and what it could be named)
        */
	private static void JVMTomcat7() {
		String s;
        	String pattern = ".*JVM Version:.*";
        	String cmd = new String("/usr/share/tomcat7/bin/version.sh");
        	try {
        		Process p = Runtime.getRuntime().exec(cmd);
                	BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			boolean isMatch = false;
                	while ((s = br.readLine()) != null) {
                		isMatch = Pattern.matches(pattern, s);
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
	* - Ignore commented lines
        */
	private static void oscarBuild() {
		String s;
		String pattern = ".*buildtag=.*";
		File oscar = new File("/usr/share/tomcat7/oscar.properties");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
			boolean isMatch = false;
			while ((s = br.readLine()) != null) {
				//if (line is a comment)
				//	continue;
				isMatch = Pattern.matches(pattern, s);
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
	* - Proper breaks out of loop
	* - Ignore commented lines
        */
	private static void verifyOscar() {
		String s;
                String pattern1 = ".*HL7TEXT_LABS=.*";
		String pattern2 = ".*SINGLE_PAGE_CHART=.*";
		String pattern3 = ".*TMP_DIR=.*";
                File oscar = new File("/usr/share/tomcat7/oscar.properties");
                try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(oscar)));
                        while ((s = br.readLine()) != null) {
                                boolean isMatch1 = Pattern.matches(pattern1, s);
				boolean isMatch2 = Pattern.matches(pattern2, s);
				boolean isMatch3 = Pattern.matches(pattern3, s);
                                if (isMatch1) {
					if (s.substring(13).toLowerCase().equals("yes")) {
						System.out.println(s);
					}
                                }
				if (isMatch2) {
					if (s.substring(18).toLowerCase().equals("true")) {
						System.out.println(s);
					}
				}
				if (isMatch3) {
					if (!s.substring(8).equals("")) {
						System.out.println(s);
					}
				}
                        }
			//if (!isMatch1)
			//	System.out.println("\"HL7TEXT_LABS\" tag not configured properly.");
			//if (!isMatch2)
			//	System.out.println("\"SINGLE_PAGE_CHART\" tag not configured properly.");
			//if (!isMatch3)
			//	System.out.println("\"TMP_DIR\" tag not configured properly.");
                } catch (Exception e) {
                        System.out.println("Oscar verification error: " + e);
                }
	}

        /*
        * TODO: 
        * - Check to see where this file is located (and what it could be named)
	* - Remove hard code for port number (use guest port)
	* - Ignore commented lines
        */
	private static void verifyDrugref() {
                String s;
                String pattern1 = ".*db_user=.*";
                String pattern2 = ".*db_url=.*";
                String pattern3 = ".*db_driver=.*";
		String pattern4 = ".*drugref_url=.*";
                File drugref = new File("/usr/share/tomcat7/drugref2.properties");
                try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(drugref)));
                        while ((s = br.readLine()) != null) {
                                boolean isMatch1 = Pattern.matches(pattern1, s);
                                boolean isMatch2 = Pattern.matches(pattern2, s);
                                boolean isMatch3 = Pattern.matches(pattern3, s);
				boolean isMatch4 = Pattern.matches(pattern4, s);
                                if (isMatch1) {
                                        if (!s.substring(8).equals("")) {
                                                System.out.println(s);
                                        }
                                }
                                if (isMatch2) {
                                        if (s.substring(7).toLowerCase().equals("jdbc:mysql://127.0.0.1:3306/drugref")) {
                                                System.out.println(s);
                                        }
                                }
                                if (isMatch3) {
                                        if (s.substring(10).toLowerCase().equals("com.mysql.jdbc.driver")) {
                                                System.out.println(s);
                                        }
                                }
				if (isMatch4) {
					if (s.substring(12).toLowerCase().equals("http://127.0.0.1:8080/drugref2/drugrefservice")) {
						System.out.println(s);
					}
				}
				//if (!isMatch1)
                        	//      System.out.println("\"HL7TEXT_LABS\" tag not configured properly.");
                        	//if (!isMatch2)
                        	//      System.out.println("\"SINGLE_PAGE_CHART\" tag not configured properly.");
                        	//if (!isMatch3)
                        	//      System.out.println("\"TMP_DIR\" tag not configured properly.");
				//if (!isMatch4)
				// 	System.out.println("\"Blabla\" tag not configured properly.");
                        }
                } catch (Exception e) {
                        System.out.println("Drugref verification error: " + e);
                }
        }

        /*
        * TODO: 
        * - Check to see that this command will always work
	* - Ignore commented lines
	* - Figure out how to check for increased memory resources
        */
	private static void verifyTomcat() {
	        String s;
                String pattern = ".*JAVA_OPTS=.*";
                File tomcat = new File("/etc/default/tomcat7");
                try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(tomcat)));
                        while ((s = br.readLine()) != null) {
                                boolean isMatch = Pattern.matches(pattern, s);
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
		} else if (false) {

		} else {

		}
	}
}
