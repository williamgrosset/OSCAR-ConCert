#!/bin/sh
set -e # exit on errors
# Warning!!!
# /etc/mysql/my.cnf sets default_storage_engine=MYISAM
# Otherwise unit testing fails due to foreign key constraint violations
if ! pgrep mysqld > /dev/null
then
  sudo service mysql start
  sleep 3
fi
export CATALINA_HOME=/usr/share/tomcat7
export CATALINA_BASE=/var/lib/tomcat7
export JAVA_OPTS="-Djava.awt.headless=true -Xmx1024m -Xms1024m -XX:MaxPermSize=512m -server"
if pgrep tomcat7 > /dev/null
then
  sudo /etc/init.d/tomcat7 start
else
  sudo /etc/init.d/tomcat7 restart
fi
