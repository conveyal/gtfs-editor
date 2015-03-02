# Install the GTFS Editor in a production environment on CentOS

Install a JDK
    sudo yum install java-1.7.0-openjdk

Install git and unzip
    sudo yum install git unzip

Install the Play! framework
    cd /tmp
    wget http://downloads.typesafe.com/releases/play-1.2.5.zip
    cd /opt
    sudo unzip /tmp/play-1.2.5.zip
    cd /usr/local/bin
    sudo ln -s /opt/play-1.2.5/play

Get gtfs-editor
    cd /opt
    sudo git clone https://github.com/conveyal/gtfs-editor.git

Configure gtfs-editor
    cd gtfs-editor/conf
    sudo cp application.conf.template application.conf
    sudo vi application.conf
    # edit the data directory to where you want to store application data
    # create a new secret, bind the server to 127.0.0.1 so we can reverse-proxy

Install dependencies

    /usr/local/bin/play dependencies

Create a user to run GTFS editor, to keep things nice and secure
    sudo useradd gtfs
    sudo mkdir logs
    sudo mkdir public/data/ # if needed
    sudo chown gtfs logs
    sudo chown gtfs public/data

Start gtfs-editor
    sudo -u gtfs /usr/local/bin/play start --pid_file=logs/gtfseditor.pid
