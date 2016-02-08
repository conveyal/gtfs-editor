Installation Instructions
=========================

The following instructions assume installation on an Ubuntu server with sudo permissions. These steps assume Ubuntu 12.04.5 LTS, if using different version of Ubuntu the available versions of software may change requiring slight modifications to these steps.

Install java jre

	sudo apt-get install openjdk-7-jre


Install play framework 1.2.5 (this software depends on v1.2.x, version 2.x+ is not compatable)

	wget http://downloads.typesafe.com/releases/play-1.2.5.zip


Unzip play framework

	unzip play-1.2.5.zip

Download gtfs-editor software

	git clone https://github.com/conveyal/gtfs-editor.git

	cd gtfs-editor


Configure gtfs-editor application.conf

	cp conf/application.conf.template conf/application.conf

Install dependencies

        [path to play1.2.5]/play dependencies


Run application

	[path to play1.2.5]/play deps --sync

	[path to play1.2.5]/play run


Request site via web browser

http://localhost:9000/


Follow setup instructions

Troubleshooting:

1) Ensure that you have access to port 9000 and that it is allowed through any firewall or routing configuration. For example, Amazon AWS won’t allow access by unless explicitly granted for port 9000.
