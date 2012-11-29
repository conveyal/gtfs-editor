# --- !Ups

-- ----------------------------
--  Table structure for "gtfssnapshotmerge"
-- ----------------------------
CREATE TABLE "gtfssnapshotmerge" (
	"id" int8 NOT NULL,
	"description" varchar(255),
	"mergecomplete" timestamp(6) NULL,
	"mergestarted" timestamp(6) NULL,
	"status" varchar(255),
	"snapshot_id" int8
);

-- ----------------------------
--  Table structure for "gisroutesegment"
-- ----------------------------
CREATE TABLE "gisroutesegment" (
	"id" int8 NOT NULL,
	"reverse" bool,
	"segment" "geometry",
	"frompoint_id" int8,
	"topoint_id" int8
);

-- ----------------------------
--  Table structure for "gisroutealignment"
-- ----------------------------
CREATE TABLE "gisroutealignment" (
	"id" int8 NOT NULL,
	"description" varchar(255),
	"gtfsshape" "geometry",
	"reversealignment" bool,
	"gisroute_id" int8
);

-- ----------------------------
--  Table structure for "trippattern_trippatternstop"
-- ----------------------------
CREATE TABLE "trippattern_trippatternstop" (
	"trippattern_id" int8 NOT NULL,
	"patternstops_id" int8 NOT NULL
);

-- ----------------------------
--  Table structure for "servicecalendardate"
-- ----------------------------
CREATE TABLE "servicecalendardate" (
	"id" int8 NOT NULL,
	"date" timestamp(6) NULL,
	"description" varchar(255),
	"exceptiontype" varchar(255),
	"gtfsserviceid" varchar(255),
	"calendar_id" int8
);

-- ----------------------------
--  Table structure for "tripshape"
-- ----------------------------
CREATE TABLE "tripshape" (
	"id" int8 NOT NULL,
	"describeddistance" float8,
	"description" varchar(255),
	"gtfsshapeid" varchar(255),
	"shape" "geometry",
	"simpleshape" "geometry",
	"agency_id" int8
);

-- ----------------------------
--  Table structure for "gtfssnapshot"
-- ----------------------------
CREATE TABLE "gtfssnapshot" (
	"id" int8 NOT NULL,
	"creationdate" timestamp(6) NULL,
	"description" varchar(255),
	"source" varchar(255)
);

-- ----------------------------
--  Table structure for "gisroutecontrolpoint"
-- ----------------------------
CREATE TABLE "gisroutecontrolpoint" (
	"id" int8 NOT NULL,
	"controlpoint" "geometry",
	"originalsequence" int4,
	"gisroute_id" int8
);

-- ----------------------------
--  Table structure for "trippattern"
-- ----------------------------
CREATE TABLE "trippattern" (
	"id" int8 NOT NULL,
	"endtime" int4,
	"headsign" varchar(255),
	"headway" int4,
	"longest" bool,
	"name" varchar(255),
	"saturday" bool,
	"starttime" int4,
	"sunday" bool,
	"usefrequency" bool,
	"weekday" bool,
	"route_id" int8,
	"shape_id" int8
);

-- ----------------------------
--  Table structure for "gisstop"
-- ----------------------------
CREATE TABLE "gisstop" (
	"id" int8 NOT NULL,
	"description" varchar(255),
	"oid" varchar(255),
	"shape" "geometry",
	"stopid" varchar(255),
	"stopname" varchar(255),
	"agency_id" int8,
	"gisupload_id" int8
);

-- ----------------------------
--  Table structure for "gisuploadfield"
-- ----------------------------
CREATE TABLE "gisuploadfield" (
	"id" int8 NOT NULL,
	"fieldname" varchar(255),
	"fieldposition" int8,
	"fieldtype" varchar(255),
	"gisupload_id" int8
);

-- ----------------------------
--  Table structure for "gtfssnapshotvalidation"
-- ----------------------------
CREATE TABLE "gtfssnapshotvalidation" (
	"id" int8 NOT NULL,
	"status" varchar(255),
	"validationdesciption" varchar(255),
	"snapshot_id" int8
);

-- ----------------------------
--  Table structure for "gisroutecontrolpointsequence"
-- ----------------------------
CREATE TABLE "gisroutecontrolpointsequence" (
	"id" int8 NOT NULL,
	"sequence" int4,
	"controlpoint_id" int8,
	"gisroutealignment_id" int8,
	"segment_id" int8
);

-- ----------------------------
--  Table structure for "gtfssnapshotexport"
-- ----------------------------
CREATE TABLE "gtfssnapshotexport" (
	"id" int8 NOT NULL,
	"calendars" varchar(255),
	"description" varchar(255),
	"mergecomplete" timestamp(6) NULL,
	"mergestarted" timestamp(6) NULL,
	"source" varchar(255),
	"status" varchar(255)
);

-- ----------------------------
--  Table structure for "trip"
-- ----------------------------
CREATE TABLE "trip" (
	"id" int8 NOT NULL,
	"blockid" varchar(255),
	"gtfstripid" varchar(255),
	"tripdirection" varchar(255),
	"tripheadsign" varchar(255),
	"tripshortname" varchar(255),
	"pattern_id" int8,
	"route_id" int8,
	"servicecalendar_id" int8,
	"servicecalendardate_id" int8,
	"shape_id" int8
);

-- ----------------------------
--  Table structure for "gisroute"
-- ----------------------------
CREATE TABLE "gisroute" (
	"id" int8 NOT NULL,
	"description" varchar(255),
	"oid" varchar(255),
	"originalshape" "geometry",
	"routeid" varchar(255),
	"routename" varchar(255),
	"routetype" varchar(255),
	"agency_id" int8,
	"gisupload_id" int8
);

-- ----------------------------
--  Table structure for "agency"
-- ----------------------------
CREATE TABLE "agency" (
	"id" int8 NOT NULL,
	"color" varchar(255),
	"gtfsagencyid" varchar(255),
	"lang" varchar(255),
	"name" varchar(255),
	"phone" varchar(255),
	"systemmap" bool,
	"timezone" varchar(255),
	"url" varchar(255)
);

-- ----------------------------
--  Table structure for "route"
-- ----------------------------
CREATE TABLE "route" (
	"id" int8 NOT NULL,
	"gtfsrouteid" varchar(255),
	"routecolor" varchar(255),
	"routedesc" varchar(255),
	"routelongname" varchar(255),
	"routeshortname" varchar(255),
	"routetextcolor" varchar(255),
	"routetype" varchar(255),
	"routeurl" varchar(255),
	"saturday" bool,
	"sunday" bool,
	"weekday" bool,
	"agency_id" int8,
	"gisroute_id" int8,
	"gisupload_id" int8,
	"aircon" bool,
	"comments" varchar(255)
);

-- ----------------------------
--  Table structure for "servicecalendar"
-- ----------------------------
CREATE TABLE "servicecalendar" (
	"id" int8 NOT NULL,
	"description" varchar(255),
	"enddate" timestamp(6) NULL,
	"friday" bool,
	"gtfsserviceid" varchar(255),
	"monday" bool,
	"saturday" bool,
	"startdate" timestamp(6) NULL,
	"sunday" bool,
	"thursday" bool,
	"tuesday" bool,
	"wednesday" bool,
	"agency_id" int8
);

-- ----------------------------
--  Table structure for "trippatternstop"
-- ----------------------------
CREATE TABLE "trippatternstop" (
	"id" int8 NOT NULL,
	"defaultdistance" float8,
	"defaultdwelltime" int4,
	"defaulttraveltime" int4,
	"stopsequence" int4,
	"pattern_id" int8,
	"stop_id" int8
);

-- ----------------------------
--  Table structure for "gtfssnapshotmergetask"
-- ----------------------------
CREATE TABLE "gtfssnapshotmergetask" (
	"id" int8 NOT NULL,
	"description" varchar(255),
	"status" varchar(255),
	"taskcompleted" timestamp(6) NULL,
	"taskstarted" timestamp(6) NULL,
	"merge_id" int8
);

-- ----------------------------
--  Table structure for "stop"
-- ----------------------------
CREATE TABLE "stop" (
	"id" int8 NOT NULL,
	"gtfsstopid" varchar(255),
	"location" "geometry",
	"locationtype" varchar(255),
	"parentstation" varchar(255),
	"stopcode" varchar(255),
	"stopdesc" varchar(255),
	"stopname" varchar(255),
	"stopurl" varchar(255),
	"zoneid" varchar(255),
	"agency_id" int8
);

-- ----------------------------
--  Table structure for "gisupload"
-- ----------------------------
CREATE TABLE "gisupload" (
	"id" int8 NOT NULL,
	"creationdate" timestamp(6) NULL,
	"description" varchar(255),
	"fielddescription" varchar(255),
	"fieldid" varchar(255),
	"fieldname" varchar(255),
	"fieldtype" varchar(255),
	"status" varchar(255),
	"type" varchar(255),
	"agency_id" int8
);

-- ----------------------------
--  Table structure for "gisexport_agency"
-- ----------------------------
CREATE TABLE "gisexport_agency" (
	"gisexport_id" int8 NOT NULL,
	"agencies_id" int8 NOT NULL
);

-- ----------------------------
--  Table structure for "stoptime"
-- ----------------------------
CREATE TABLE "stoptime" (
	"id" int8 NOT NULL,
	"arrivaltime" int4,
	"departuretime" int4,
	"dropofftype" varchar(255),
	"pickuptype" varchar(255),
	"shapedisttraveled" float8,
	"stopheadsign" varchar(255),
	"stopsequence" int4,
	"stop_id" int8,
	"trip_id" int8
);

-- ----------------------------
--  Table structure for "gtfssnapshotexport_agency"
-- ----------------------------
CREATE TABLE "gtfssnapshotexport_agency" (
	"gtfssnapshotexport_id" int8 NOT NULL,
	"agencies_id" int8 NOT NULL
);

-- ----------------------------
--  Table structure for "gisexport"
-- ----------------------------
CREATE TABLE "gisexport" (
	"id" int8 NOT NULL,
	"creationdate" timestamp(6) NULL,
	"description" varchar(255),
	"status" varchar(255),
	"type" varchar(255)
);

-- ----------------------------
--  Primary key structure for table "gtfssnapshotmerge"
-- ----------------------------
ALTER TABLE "gtfssnapshotmerge" ADD CONSTRAINT "gtfssnapshotmerge_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisroutesegment"
-- ----------------------------
ALTER TABLE "gisroutesegment" ADD CONSTRAINT "gisroutesegment_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisroutealignment"
-- ----------------------------
ALTER TABLE "gisroutealignment" ADD CONSTRAINT "gisroutealignment_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "servicecalendardate"
-- ----------------------------
ALTER TABLE "servicecalendardate" ADD CONSTRAINT "servicecalendardate_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "tripshape"
-- ----------------------------
ALTER TABLE "tripshape" ADD CONSTRAINT "tripshape_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gtfssnapshot"
-- ----------------------------
ALTER TABLE "gtfssnapshot" ADD CONSTRAINT "gtfssnapshot_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisroutecontrolpoint"
-- ----------------------------
ALTER TABLE "gisroutecontrolpoint" ADD CONSTRAINT "gisroutecontrolpoint_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "trippattern"
-- ----------------------------
ALTER TABLE "trippattern" ADD CONSTRAINT "trippattern_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisstop"
-- ----------------------------
ALTER TABLE "gisstop" ADD CONSTRAINT "gisstop_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisuploadfield"
-- ----------------------------
ALTER TABLE "gisuploadfield" ADD CONSTRAINT "gisuploadfield_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gtfssnapshotvalidation"
-- ----------------------------
ALTER TABLE "gtfssnapshotvalidation" ADD CONSTRAINT "gtfssnapshotvalidation_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisroutecontrolpointsequence"
-- ----------------------------
ALTER TABLE "gisroutecontrolpointsequence" ADD CONSTRAINT "gisroutecontrolpointsequence_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gtfssnapshotexport"
-- ----------------------------
ALTER TABLE "gtfssnapshotexport" ADD CONSTRAINT "gtfssnapshotexport_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "trip"
-- ----------------------------
ALTER TABLE "trip" ADD CONSTRAINT "trip_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisroute"
-- ----------------------------
ALTER TABLE "gisroute" ADD CONSTRAINT "gisroute_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "agency"
-- ----------------------------
ALTER TABLE "agency" ADD CONSTRAINT "agency_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "route"
-- ----------------------------
ALTER TABLE "route" ADD CONSTRAINT "route_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "servicecalendar"
-- ----------------------------
ALTER TABLE "servicecalendar" ADD CONSTRAINT "servicecalendar_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "trippatternstop"
-- ----------------------------
ALTER TABLE "trippatternstop" ADD CONSTRAINT "trippatternstop_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gtfssnapshotmergetask"
-- ----------------------------
ALTER TABLE "gtfssnapshotmergetask" ADD CONSTRAINT "gtfssnapshotmergetask_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "stop"
-- ----------------------------
ALTER TABLE "stop" ADD CONSTRAINT "stop_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisupload"
-- ----------------------------
ALTER TABLE "gisupload" ADD CONSTRAINT "gisupload_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "stoptime"
-- ----------------------------
ALTER TABLE "stoptime" ADD CONSTRAINT "stoptime_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "gisexport"
-- ----------------------------
ALTER TABLE "gisexport" ADD CONSTRAINT "gisexport_pkey" PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;


# --- !Downs

DROP TABLE "gtfssnapshotmerge";
DROP TABLE "gisroutesegment";
DROP TABLE "gisroutealignment";
DROP TABLE "trippattern_trippatternstop";
DROP TABLE "servicecalendardate";
DROP TABLE "tripshape";
DROP TABLE "gtfssnapshot";
DROP TABLE "gisroutecontrolpoint";
DROP TABLE "trippattern";
DROP TABLE "gisstop";
DROP TABLE "gisuploadfield";
DROP TABLE "gtfssnapshotvalidation";
DROP TABLE "gisroutecontrolpointsequence";
DROP TABLE "gtfssnapshotexport";
DROP TABLE "trip";
DROP TABLE "gisroute";
DROP TABLE "agency";
DROP TABLE "route";
DROP TABLE "servicecalendar";
DROP TABLE "trippatternstop";
DROP TABLE "gtfssnapshotmergetask";
DROP TABLE "stop";
DROP TABLE "gisupload";
DROP TABLE "gisexport_agency";
DROP TABLE "stoptime";
DROP TABLE "gtfssnapshotexport_agency";
DROP TABLE "gisexport";
