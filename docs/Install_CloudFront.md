# Installing the GTFS Editor on AWS with CloudFront

Creating a GTFS Editor with Amazon CloudFront is much easier, as it can all be done via web, in only a few steps.

You can also follow the [creation from scratch instructions](Install.md) if you require higher control of your instance.

This is the step by step process:

1. Visit http://aws.amazon.com and sig in to your Console.

2. Click on CloudFormation ("Templated AWS Resource Creation") and then on "Create New Stack".

3. Give a name to the stack (for example, `GTFSEditor`) and upload [this template file](../cloudfront/GTFS_Editor_CloudFront.json) to Amazon S3.

4. You can leave the default values for `InstanceType` (the type of EC2 instance) and `IpLocation` (the IP address range that can access this instance), but make sure you select a `KeyName` (existing EC2 KeyPair) to enable access to the instance.

5. Click Next and then Create. Your new GTFS Editor instance is now being created.

You can see the creation process in the `Events` tab. Once this process is finished, you can switch to the `Outputs` tab where you can find the public IP of this instance.

Finally, point your browser to `http://[your-ip-here]:9000` to configure the GTFS Editor (it might a take a couple of minutes for the instance to download the GTFS Editor code and its dependencies).

## Create the admin user and default agency

You need to enter information in two different screens.

1. In the first screen you create an username, password, and enter your email address.

2. In the second screen you create the default agency. These are some valid sample values:

* Agency: `GTSFDemo`
* Agency name: `Demo Transit Authority`
* Agency URL: `http://[your-ip-here]:9000`
* Agency timezone: `America/New_York`
* Agency language: `English`
