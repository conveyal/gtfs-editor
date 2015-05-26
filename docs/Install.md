# Installing the GTFS Editor on AWS from Scratch

This document describes how to install the [GTFS Editor](https://github.com/conveyal/gtfs-editor) on AWS.

You can also follow the [CloudFront installation instructions](Install_CloudFront.md) for a more straightforward process that only requires a browser.

Please note that this process assumes that you have signed up for AWS and that the [AWS CLI](http://aws.amazon.com/cli/) is already installed on your system. To verify that you have it installed, simply run:

```
$ aws --version
```

You should see something like:

```
aws-cli/1.7.29 Python/2.7.9 Darwin/14.3.0
```

Otherwise, you can install it and configure it with:

```
$ pip install awscli
$ aws configure
```

## Step by step installation

### Create the VM

The VM creation process is pretty standard, with only a few considerations:

* GTFS Editor assumes Ubuntu Server 12.04 LTS.
* We choose a memory optimized instance (`m2.xlarge` by default) to help importing feeds.
* We need to open the 9000 port for web requests

This is the step by step process:

* Create a new `gtfs-sg` security group:

```
aws ec2 create-security-group --group-name gtfs-sg --description "Security group for the GTFS editing framework"
aws ec2 authorize-security-group-ingress --group-name gtfs-sg --protocol tcp --port 22 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-name gtfs-sg --protocol tcp --port 9000 --cidr 0.0.0.0/0
```

* Create the `gtfs-key` key pair (and remove it from version control):

```
aws ec2 create-key-pair --key-name gtfs-key --query 'KeyMaterial' --output text > gtfs-key.pem
chmod 400 gtfs-key.pem
echo "gtfs-key.pem" >> .gitignore
```

* Launch the instance (this assumes an install on `us-east-1`, US East, N. Virginia):

```
aws ec2 run-instances --image-id ami-487a3920 --count 1 --instance-type m2.xlarge --key-name gtfs-key --security-groups gtfs-sg --query 'Instances[0].InstanceId'
```

Write down the `InstanceId` and use it to get the public IP address of the instance:

```
aws ec2 describe-instances --instance-ids [your-id-here] --query 'Reservations[0].Instances[0].PublicIpAddress'
```

Finally, you now can connect via ssh with your brand new VM:

```
ssh -i gtfs-key.pem ubuntu@[your-ip-here]
```

### Set up the VM

Now that the VM is up and running, we need to set up the software.

Install some system dependencies (Git, Unzip, and Java):

```
sudo apt-get update
sudo apt-get install git unzip openjdk-7-jre
```

Download and unpack the Play Framework (note that the GTFS Editor requires the 1.2.x version):

```
wget http://downloads.typesafe.com/releases/play-1.2.5.zip
unzip play-1.2.5.zip
```

Download the GTFS Editor from GitHub:

```
git clone https://github.com/conveyal/gtfs-editor.git
cd gtfs-editor
```

Create the default configuration file:

```
cp conf/application.conf.template conf/application.conf
```

Finally, download the dependencies and run the app:

```
../play-1.2.5/play dependencies
../play-1.2.5/play run
```

After a few seconds, you can point your browser to `http://[your-ip-here]:9000` to continue with the web installation.

### Create the admin user and default agency

You need to enter information in two different screens.

1. In the first screen you create an username, password, and enter your email address.

2. In the second screen you create the default agency. These are some valid sample values:

* Agency: `GTSFDemo`
* Agency name: `Demo Transit Authority`
* Agency URL: `http://[your-ip-here]:9000`
* Agency timezone: `America/New_York`
* Agency language: `English`
