Installation Instructions
=========================

The following instructions assume installation on an Ubuntu server with sudo permissions.

Install database

	sudo apt-get install postgresql-9.1
	sudo apt-get install postgresql-9.1-postgis

Install java jre

	sudo apt-get install openjdk-7-jre

Install play framework 1.2.5

	wget http://downloads.typesafe.com/releases/play-1.2.5.zip

Unzip play framework

	unzip play-1.2.5.zip

Create database

	sudo su postgres
	createdb gtfs-editor 

	psql gtfs-editor < /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql

Change database security settings to allow local access. Note: this is acceptable for testing/development. For production applications please consult Postgres documentation on how to create and configure use accounts.

	sudo nano /etc/postgresql/9.1/main/pg_hba.conf

Change lines referencing local ip4/ip6 access to “trust”:

	# "local" is for Unix domain socket connections only
	local   all             all                      trust
	# IPv4 local connections:
	host    all             all    127.0.0.1/32      trust
	# IPv6 local connections:
	host    all             all    ::1/128           trust


Restart database 

	sudo /etc/init.d/postgresql restart


Download gtfs-editor software

	git clone https://github.com/conveyal/gtfs-editor.git

	cd gtfs-editor


Configure gtfs-editor application.conf
	
	cd conf/

	cp application.conf.template application.conf


Run application

	cd ../

	[path to play1.2.5]/play run 


Request site via web browser

http://localhost:9000/


Follow setup instructions 

Troubleshooting: 

1) Ensure that you have access to port 9000 and that it is allowed through any firewall or routing configuration. For example, Amazon AWS won’t allow access by unless explicitly granted for port 9000.


