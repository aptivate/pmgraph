#!/bin/bash
#### Define Constants
SUBNET="`ifconfig  | grep 'inet addr:'| grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $1}' | cut -d. -f 1,2,3`."

#### Define variables
done=1
user="`whoami`"

if [ "$user" != "root" ]
then
	echo "Root privileges are required to install pmGraph. Please become superuser before executing the installer."
	exit 1
fi

echo "In order to install pmGraph, you will need to accept the EULA for some Java components."

# Append app repository for PMGraph in /etc/apt/sources.list

grep "# app repository for PMGRAPH" /etc/apt/sources.list

if [ $? -ne 0 ]
then
	echo "# app repository for PMGRAPH
deb http://ppa.launchpad.net/pmgraph/ppa/ubuntu hardy main
deb-src http://ppa.launchpad.net/pmgraph/ppa/ubuntu hardy main" >> /etc/apt/sources.list
fi

until [ $done -ne 1 ]
do
	read -p "Please enter the first 3 parts of your subnet (e.g. if your local network is $SUBNET""0/24 the value should be $SUBNET) or press enter to use the value shown in the example:" value

	# Check value of entered expression is valid
	# Based on an article by Mitch Frazier found at http://www.linuxjournal.com/content/validating-ip-address-bash-script as of 14/08/2009
	if [ "$value" != "" ] 
	then
		isValid=1
		if [[ $value =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.$ ]]
		then
			OIFS=$IFS
			IFS='.'
			startIP=($value)
			IFS=$OIFS
			[[ ${startIP[0]} -le 255 && ${startIP[1]} -le 255 && ${startIP[2]} -le 255 && "${startIP[3]}" = "" ]]
			isValid=$?
		fi

		if [ $isValid -ne 1 ]
		then
			done=0
			SUBNET=$value
		fi
	else
		done=0
	fi
done

stty -echo

# Set a new database password
read -p "Enter a new database password: " aPassword

stty echo

# Get PGP key
apt-key adv --recv-keys --keyserver keyserver.ubuntu.com 72318B8DAC9C70E6

# Update apt-get repository list and install pmGraph
apt-get update

 
apt-get -y install pmgraph

result=$?
if [ $result -eq 100 ]
then
	echo "Installation of pmGraph failed. The installer was unable to install one or more of the components for pmGraph."
	exit 1
fi

CATALINA_DEFAULT_BASE=`grep -B1 CATALINA_BASE /etc/default/tomcat6 | grep Default | sed -e 's/.*: //'`
. /etc/default/tomcat6

CATALINA_BASE=${CATALINA_BASE:-$CATALINA_DEFAULT_BASE}

# Configure server and pmacct 
perl -w -i -p -e "s/10.0.156./$SUBNET/g" $CATALINA_BASE/webapps/pmgraph/WEB-INF/classes/database.properties

perl -w -i -p -e "s/10.0.156./$SUBNET/g" /etc/pmacct/pmacctd.conf

done=1

echo "Granting database permissions. The MySQL root password is required for this stage."

until [ $done -ne 1 ]
do
	echo "GRANT SELECT,INSERT, LOCK TABLES ON pmacct.* TO 'pmacct'@'localhost' identified by '$aPassword';" | mysql -u root -p 
	done=$?
done

perl -w -i -p -e "s/^<entry key=\"DatabasePass\">\w*<\/entry>/<entry key=\"DatabasePass\">$aPassword<\/entry>/" $CATALINA_BASE/webapps/pmgraph/WEB-INF/classes/database.properties

perl -w -i -p -e "s/^sql_passwd: \w*/sql_passwd: $aPassword/" /etc/pmacct/pmacctd.conf 

# Restart server and pmacct
/etc/init.d/tomcat6 restart

/etc/init.d/pmacct restart
