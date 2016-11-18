#!/bin/bash

#
# TODO:
# 1. Fix hard code port for Drugref verification
# 2. Ignore uppercase when searching for regular expressions
#

cd $HOME
echo ">> Hello, $USER. Welcome to the bash script written for the OSP Installation Audit."
echo ">> CATEGORY 1:"

#1. (UBUNTU SERVER)
#
# Runs the "lsb_release" command to find
# release version and then echos the substring
# of only the version value.
#
ubuntuVersion()
{
	string="$(lsb_release -r)"
	echo ">> Ubuntu sever version: ${string:9}"
}
ubuntuVersion


#2. (MYSQL VERSION)
#
# Runs the "mysql" command to find
# version number and then echos the substring
# of only the version value.
#
mysqlVersion()
{
	string="$(mysql --version)"
	echo ">> MySQL version: ${string:11}"
}
mysqlVersion


#3, #4. (JVM & TOMCAT7 VERSION)
#
# Runs the "./version.sh" bash command to find
# the matching regular expression string and then
# echo the substring of the version value.	
#
javatomcat7Version() 
{
	cd $CATALINA_HOME/bin/
	strJVM=""
	strTomcat=""

	while IFS= read -r line; do
		if [[ $line =~ "Server version:" ]]; then
			strJVM="${line:16}"
			echo ">> Tomcat7 version: $strJVM"				
		fi
		if [[ $line =~ "JVM Version:" ]]; then
			strTomcat="${line:16}"
			echo ">> JVM version: $strTomcat"
		fi

	done < <(./version.sh)
	cd $HOME
}
javatomcat7Version


#5 (OSCAR VERSION & BUILD)
#
# Reads the "oscar.properties" file to find
# the matching regular expression string and then
# echos the version and build of OSCAR.
#
oscarVersionBuild()
{
	cd $CATALINA_HOME
	file="oscar.properties"
	flag1=false
	string=""

	while read -r line; do
		[[ "$line" =~ ^#.*$ ]] && continue
		if [[ $line =~ ^"buildtag=" ]]; then
			str=$line
			string="${str:9}"
			flag1=true
		fi
	done < "$file"

	if [ "$flag1" == true ]; then
		echo ">> OSCAR version and build: $string"
	else
		echo ">> Check OSCAR version and/or build."
	fi 
	cd $HOME
}
oscarVersionBuild


#6 (VERIFY OSCAR PROPERTIES)
#
# Reads the "oscar.properties" file and sets
# a flag if the regular expression string matches
# and then echos if the test is passed.
#
verifyOscar()
{
	cd /usr/share/tomcat7
	file="oscar.properties"
	flag1=false
	flag2=false
	flag3=false

	while read -r line; do
		[[ "$line" =~ ^#.*$ ]] && continue
		if [[ $line =~ ^"HL7TEXT_LABS=yes" ]]; then
			flag1=true;
		fi	
		if [[ $line =~ ^"SINGLE_PAGE_CHART=true" ]]; then
			flag2=true;
		fi
		if [[ $line =~ ^"TMP_DIR=/tmp" ]]; then
			flag3=true;
		fi
	done < "$file"

	if [ "$flag1" == true ] && [ "$flag2" == true ] && [ "$flag3" == true ]; then
		echo ">> Verify test for OSCAR properties: [PASS]"
	else
		echo ">> Verify test for OSCAR properties: [FAIL]"
	fi
	cd $HOME
}
verifyOscar


#7 (VERIFY DRUGREF2 PROPERTIES)
#
# Reads the "drugref2.properties" file and sets
# a flag if the regular expression string matches
# and then echos if the test is passed.
#
verifyDrugref()
 {
	cd /usr/share/tomcat7
	file="drugref2.properties"
	flag1=false
	flag2=false
	flag3=false
	flag4=false

	while read -r line; do
		[[ "$line" =~ ^#.*$ ]] && continue
		if [ "$line" == "db_user=root" ]; then
		      flag1=true
		fi
        	if [ "$line" == "db_url=jdbc:mysql://127.0.0.1:3306/drugref" ]; then
	              flag2=true;
	        fi      
	        if [ "$line" == "db_driver=com.mysql.jdbc.Driver" ]; then
	              flag3=true;
		fi
		if [ "$line" == "drugref_url=http://127.0.0.1:8080/drugref2/DrugrefService" ]; then
		      flag4=true;
		fi
		done < "$file"
	
		if [ "$flag1" == true ] && [ "$flag2" == true ] && [ "$flag3" == true ] && [ "$flag4" == true ]; then
	      		echo ">> Verify test for Drugref2 properties: [PASS]"
		else
	      		echo ">> Verify test for Drugref2 properties: [FAIL]"
		fi
		cd $HOME
}
verifyDrugref


#8 (VERIFY TOMCAT7 REINFORCEMENT)
#
# Reads the "tomcat7" file and sets
# a flag if the regular expression string matches
# and then echos if the test is passed.
#
verifyTomcat7()
{
	cd /etc/default
	file="tomcat7"
	flag1=false
	while read -r line; do
	        [[ "$line" =~ ^#.*$ ]] && continue
	        if [ "$line" == "JAVA_OPTS=\"-Djava.awt.headless=true -Xmx1024m -Xms256m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalMode -server\"" ]; then
	        	flag1=true
		fi
	done < "$file"

	if [ "$flag1" == true ]; then
		echo ">> Verify test for Tomcat7 reinforcement: [PASS]"
	else
		echo ">> Verify test for Tomcat7 reinforcement: [FAIL]"
	fi
	cd $HOME
}
verifyTomcat7


#9	
##echo "CHECK FOR OSCAR/DRUGREF DATABASE"
