#!/bin/bash

# Install apt dependencies
sudo apt-get -y install python-setuptools

# update apt
sudo apt-get update

sudo apt-get install -y dpkg-deb

# add Java 8 PPA repo
sudo apt-get -y install python-software-properties
sudo add-apt-repository -y ppa:webupd8team/java
# install Java 8
sudo echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get update
sudo apt-get install -y oracle-java8-installer
# set default
sudo apt-get install -y oracle-java8-set-default

# install sbt
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
sudo apt-get update -y
sudo apt-get install -y sbt
