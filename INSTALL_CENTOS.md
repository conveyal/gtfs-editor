# Install the GTFS Editor in a production environment on CentOS

Install postgresql

    sudo yum install postgresql postgresql-server postgis

Initialize the database cluster

    sudo service postgresql initdb

Create a database and user account

    sudo su - postgres
    psql
    CREATE DATABASE gtfs;
    CREATE USER gtfs PASSWORD '*******';
    GRANT ALL ON DATABASE gtfs TO gtfs;
    ^D

Install PostGIS
    createlang plpgsql gtfs
    psql gtfs < /usr/share/pgsql/contrib/postgis-64.sql
    psql gtfs < /usr/share/pgsql/contrib/spatial_ref_sys.sql
    psql gtfs
    GRANT ALL ON geometry_columns TO gtfs;
    GRANT ALL ON geography_columns TO gtfs;
    GRANT SELECT ON spatial_ref_sys TO gtfs;
    ^D
    exit

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
    # edit the database connection params to match what you set up above,
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
