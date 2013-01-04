# CTD^2 Dashboard Mock-up

Here are a few quick comments from Arman `arman@cbio.mskcc.org` about this prototype:

 * This is just the first iteration, so please feel free to comment and hopefully the design will converge accordingly
 * I decided to color code main categories (blue/red/orange/green) and I think it turns out quite well since it will allow any cross-link (e.g. from drug-view to target-view) to pop-up to the eye
 * I have not optimized things on the mobile side, so although I believe it will work fine on an iPad; it might lack some of the eye-candies on a mobile phone due to size limitations.
 * I have altered the main "tiles-design" pattern a little bit in order to be able to put an emphasis on "Stories" -- so this is why it is being shown by default. This is something between what Chris initially suggested and what Daniela wanted to see. 

# Basic install (via Maven)

Change your working directory to `ctd2-dashboard` and issue the following commands

	mvn clean install
	cp -f target/ctd2-dashboard.war $TOMCAT_HOME/webapps/

where `$TOMCAT_HOME` is where your Tomcat-like web server lives. 
