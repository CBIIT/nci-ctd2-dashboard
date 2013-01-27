# About
This project encapsulates three modules representing the whole CTD^2 Dashboard project:

* *Core*: contains the main data structures and basic DAO methods.
* *Admin*: contains importers/normalizers/converters for populating the database (depends on _core_)
* *Web*: provides a Web-based API for querying the database; also contains a thin-client (known as _Dashboard Web UI_) that helps with basic navigation

# Install
The code is structured as a _Maven_ project. 
We suggest you work in an environment with `JDK 1.6.x`, `Tomcat 6.x`, `MySQL 5.0.x` and `Maven 3.x.x` properly installed.

Before running any _mvn_ jobs, make sure you do the basic configuration.
Example configuration files are provided within the distribution.
The easiest way to do the configuration is as follows:

	# this is for Log4j properties, no need to edit unless you want your logs to be saved
	cp -f core/src/main/resources/log4j.properties.example core/src/main/resources/log4j.properties
	# the following contains main properties (including database user/password)
	# please make sure you edit minimally the USER/PASSWORD pair and the TEST/MAIN database URLs.
	cp -f core/src/main/resources/META-INF/spring/dashboard.properties.example core/src/main/resources/META-INF/spring/dashboard.properties

After you are done with the configuration, you will be good to go ahead for a basic _install_:

	mvn clean install

The command above will compile the project, run the tests and create the necessary jar/war files for you.
Make sure the tests are sucessfully completed.
From here on you can follow the module-specific notes below.
  
# Notes about moudles
## Core: Data structures and DAO methods
This module tries to capture the main proposed database schema outlined below.
![Dashboard DB schema](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/dashboard_schema.png)

## Admin: Importers/Converters and 
TODO

## Web: CTD^2 Dashboard UI/Web API
### Deploy
This module will create a single _war_ file for the Web API/UI.
You can simply deploy this application, with the following generic command:

	cp -f web/target/web.war $TOMCAT_HOME/webapps/ctd2-dashboard.war

or better, especially if you do not want to deal with a new Tomcat installation, you can take advantage of the _mvn tomcat_ plugin:

	cd web/
	mvn tomcat:run-war

and point your browser to [http://localhost:8080/dashboard-webapp](http://localhost:8080/dashboard-webapp).

### Notes about design
Here are a few quick comments from Arman `arman@cbio.mskcc.org` about this prototype:

 * This is just the first iteration, so please feel free to comment and hopefully the design will converge accordingly
 * I decided to color code main categories (blue/red/orange/green) and I think it turns out quite well since it will allow any cross-link (e.g. from drug-view to target-view) to pop-up to the eye
 * I have not optimized things on the mobile side, so although I believe it will work fine on an iPad; it might lack some of the eye-candies on a mobile phone due to size limitations.
 * I have altered the main "tiles-design" pattern a little bit in order to be able to put an emphasis on "Stories" -- so this is why it is being shown by default. This is something between what Chris initially suggested and what Daniela wanted to see. 
