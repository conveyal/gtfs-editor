#!/bin/bash -xe
sudo apt-get --yes update
sudo apt-get --yes install git unzip openjdk-7-jre
wget http://downloads.typesafe.com/releases/play-1.2.5.zip
unzip play-1.2.5.zip
git clone https://github.com/brunosan/gtfs-editor.git
cd gtfs-editor
cp conf/application.conf.template conf/application.conf
../play-1.2.5/play dependencies
../play-1.2.5/play run
