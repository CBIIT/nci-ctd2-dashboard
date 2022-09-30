#!/bin/sh -x

if [ -z "$CATALINA_HOME" ]; then export CATALINA_HOME=~/apache-tomcat-8.5.50; fi
sudo cp target/registration.war $CATALINA_HOME/webapps