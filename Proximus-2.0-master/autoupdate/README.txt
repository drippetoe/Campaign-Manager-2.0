# autoupdate/README.txt
The auto update utility is a very raw java application that downloads content
from a Proximus App Server instance. The utility consists of an application
jar: 
	proximus-auto-update.jar 

and depends on two additional jar files:

	libs/gson-1.7.1.jar
	libs/proximus-common-2.0.jar

This utility may be run from any dreamplug device and will attempt to connect
to the hard coded appserver:

	http://appserver[-dev].proximusmobility.net:8989/sysops/(D|P)###

Note:
	appserver-dev.proximusmobility.net:8989 [developement instance]
	appserver.proximusmobility.net:8989	[production instance]

	/sysops/D0001;
		- D#### indicates it is a development instance.
		- P#### indicates a production instance.

	** The instances will increment based on major appserver node
		releases where version and format of the resulting
		response schemas may change.


##############################################################################
  Change Log:

[WRW:10/07/2011]
Initial release of the utility. This version is very bare bones with no
configuration options available. The auto-update server is running
within a jersey.grizzly container for the time being but will need to be
migrated to an application such as Apache's Tomcat. Just place the jar 
and libs folder within the devices /root path. To execute the utility use
the command:

	java -jar proximus-auto-update.jar

And the utility will attempt to connect to the instance:

	http://appserver-dev.proximusmobility.net:8989/sysops/D0001

And download a software version schema. The schema provides information about
the files to be downloaded and installed. Look for additional details for the
format of the schema and the location of the packaged files.
