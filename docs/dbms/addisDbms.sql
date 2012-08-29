/*
 Navicat Premium Data Transfer

 Source Server         : addis_postgres
 Source Server Type    : PostgreSQL
 Source Server Version : 90104
 Source Host           : localhost
 Source Database       : addis
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90104
 File Encoding         : utf-8

 Date: 08/29/2012 10:45:39 AM
*/

-- ----------------------------
--  Table structure for "categorical_variables"
-- ----------------------------
DROP TABLE IF EXISTS "categorical_variables";
CREATE TABLE "categorical_variables" (
	"name" varchar(255) NOT NULL,
	"category" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "categorical_variables" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "continuous_variables"
-- ----------------------------
DROP TABLE IF EXISTS "continuous_variables";
CREATE TABLE "continuous_variables" (
	"name" varchar(255) NOT NULL,
	"measurement_description" text,
	"measurement_unit" varchar(255)
)
WITH (OIDS=FALSE);
ALTER TABLE "continuous_variables" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_epochs"
-- ----------------------------
DROP TABLE IF EXISTS "study_epochs";
CREATE TABLE "study_epochs" (
	"study" varchar NOT NULL,
	"name" varchar NOT NULL,
	"epoch_nr" int8 NOT NULL,
	"duration" interval(6)
)
WITH (OIDS=FALSE);
ALTER TABLE "study_epochs" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_references"
-- ----------------------------
DROP TABLE IF EXISTS "study_references";
CREATE TABLE "study_references" (
	"study" varchar NOT NULL,
	"pubmed_id" int8 NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "study_references" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "variables"
-- ----------------------------
DROP TABLE IF EXISTS "variables";
CREATE TABLE "variables" (
	"name" varchar(255) NOT NULL,
	"description" varchar(255),
	"variable_type" varchar(255),
	"direction" "directiontype",
	"child_type" "variablechildtype"
)
WITH (OIDS=FALSE);
ALTER TABLE "variables" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_outcome_measures"
-- ----------------------------
DROP TABLE IF EXISTS "study_outcome_measures";
CREATE TABLE "study_outcome_measures" (
	"study" varchar(255) NOT NULL,
	"outcome_measure_nr" int8 NOT NULL,
	"primary" bool,
	"variable" varchar(255),
	"duration" interval(6),
	"relative_to" varchar(255),
	"epoch" varchar(255)
)
WITH (OIDS=FALSE);
ALTER TABLE "study_outcome_measures" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "drugs"
-- ----------------------------
DROP TABLE IF EXISTS "drugs";
CREATE TABLE "drugs" (
	"name" varchar(255) NOT NULL,
	"atc_code" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "drugs" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_objectives"
-- ----------------------------
DROP TABLE IF EXISTS "study_objectives";
CREATE TABLE "study_objectives" (
	"study" varchar NOT NULL,
	"objective_nr" int8 NOT NULL,
	"objective" varchar(255) NOT NULL,
	"primary" bool
)
WITH (OIDS=FALSE);
ALTER TABLE "study_objectives" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "continuous_study_measurements"
-- ----------------------------
DROP TABLE IF EXISTS "continuous_study_measurements";
CREATE TABLE "continuous_study_measurements" (
	"study" varchar NOT NULL,
	"measurement_nr" int2 NOT NULL,
	"mean" float8,
	"std_dev" float8,
	"sample_size" int8
)
WITH (OIDS=FALSE);
ALTER TABLE "continuous_study_measurements" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "rate_study_measurements"
-- ----------------------------
DROP TABLE IF EXISTS "rate_study_measurements";
CREATE TABLE "rate_study_measurements" (
	"study" varchar NOT NULL,
	"measurement_nr" int2 NOT NULL,
	"rate" int2,
	"sample_size" int2
)
WITH (OIDS=FALSE);
ALTER TABLE "rate_study_measurements" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_activity_usages"
-- ----------------------------
DROP TABLE IF EXISTS "study_activity_usages";
CREATE TABLE "study_activity_usages" (
	"study" varchar(255) NOT NULL,
	"arm" varchar(255) NOT NULL,
	"epoch" varchar(255) NOT NULL,
	"activity" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "study_activity_usages" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_measurements"
-- ----------------------------
DROP TABLE IF EXISTS "study_measurements";
CREATE TABLE "study_measurements" (
	"study" varchar NOT NULL,
	"measurement_nr" int2 NOT NULL,
	"outcome_measure_nr" int8 NOT NULL,
	"arm" varchar,
	"type" "variablechildtype" NOT NULL,
	"how_long" interval(6),
	"relative_to" varchar
)
WITH (OIDS=FALSE);
ALTER TABLE "study_measurements" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_activity_drugs"
-- ----------------------------
DROP TABLE IF EXISTS "study_activity_drugs";
CREATE TABLE "study_activity_drugs" (
	"study" varchar(255) NOT NULL,
	"activity" varchar(255) NOT NULL,
	"drug" varchar(255) NOT NULL,
	"min_dose" float8,
	"max_dose" float8,
	"per_time" interval(6),
	"unit" varchar(255)
)
WITH (OIDS=FALSE);
ALTER TABLE "study_activity_drugs" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "code_systems"
-- ----------------------------
DROP TABLE IF EXISTS "code_systems";
CREATE TABLE "code_systems" (
	"code_system" varchar(255) NOT NULL,
	"code_system_name" varchar(255)
)
WITH (OIDS=FALSE);
ALTER TABLE "code_systems" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_activities"
-- ----------------------------
DROP TABLE IF EXISTS "study_activities";
CREATE TABLE "study_activities" (
	"study" varchar(255) NOT NULL,
	"name" varchar(255) NOT NULL,
	"activity_category" varchar
)
WITH (OIDS=FALSE);
ALTER TABLE "study_activities" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "categorical_study_measurements"
-- ----------------------------
DROP TABLE IF EXISTS "categorical_study_measurements";
CREATE TABLE "categorical_study_measurements" (
	"study" varchar NOT NULL,
	"measurement_nr" int2 NOT NULL,
	"category" varchar NOT NULL,
	"rate" int2
)
WITH (OIDS=FALSE);
ALTER TABLE "categorical_study_measurements" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "studies"
-- ----------------------------
DROP TABLE IF EXISTS "studies";
CREATE TABLE "studies" (
	"name" varchar(255) NOT NULL,
	"indication" varchar(255),
	"title" text,
	"allocation_type" "allocationvaluetype",
	"blinding_type" "blindingvaluetype",
	"number_of_centers" int8 NOT NULL,
	"start_date" date NOT NULL,
	"end_date" date NOT NULL,
	"status" "studystatustype",
	"inclusion" varchar,
	"exclusion" varchar(255),
	"source" "studysourcetype",
	"created_at" date NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "studies" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "indications"
-- ----------------------------
DROP TABLE IF EXISTS "indications";
CREATE TABLE "indications" (
	"name" varchar(255) NOT NULL,
	"code" varchar(255) NOT NULL,
	"code_system" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "indications" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "study_arms"
-- ----------------------------
DROP TABLE IF EXISTS "study_arms";
CREATE TABLE "study_arms" (
	"study" varchar NOT NULL,
	"name" varchar NOT NULL,
	"arm_size" int8
)
WITH (OIDS=FALSE);
ALTER TABLE "study_arms" OWNER TO "postgres";

-- ----------------------------
--  Table structure for "units"
-- ----------------------------
DROP TABLE IF EXISTS "units";
CREATE TABLE "units" (
	"ucum_code" varchar(255) NOT NULL
)
WITH (OIDS=FALSE);
ALTER TABLE "units" OWNER TO "postgres";

-- ----------------------------
--  Primary key structure for table "categorical_variables"
-- ----------------------------
ALTER TABLE "categorical_variables" ADD CONSTRAINT "CategoricalVariables_pkey" PRIMARY KEY ("name", "category") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "continuous_variables"
-- ----------------------------
ALTER TABLE "continuous_variables" ADD CONSTRAINT "ContinuousVariables_pkey" PRIMARY KEY ("name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_epochs"
-- ----------------------------
ALTER TABLE "study_epochs" ADD CONSTRAINT "StudyEpochs_pkey" PRIMARY KEY ("study", "name", "epoch_nr") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "variables"
-- ----------------------------
ALTER TABLE "variables" ADD CONSTRAINT "Variables_pkey" PRIMARY KEY ("name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_outcome_measures"
-- ----------------------------
ALTER TABLE "study_outcome_measures" ADD CONSTRAINT "StudyOutcomeMeasures_pkey" PRIMARY KEY ("study", "outcome_measure_nr") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "drugs"
-- ----------------------------
ALTER TABLE "drugs" ADD CONSTRAINT "Drugs_pkey" PRIMARY KEY ("name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_objectives"
-- ----------------------------
ALTER TABLE "study_objectives" ADD CONSTRAINT "StudyObjectives_pkey" PRIMARY KEY ("study", "objective_nr", "objective") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "continuous_study_measurements"
-- ----------------------------
ALTER TABLE "continuous_study_measurements" ADD CONSTRAINT "continuous_study_measurements_pkey" PRIMARY KEY ("study", "measurement_nr") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "rate_study_measurements"
-- ----------------------------
ALTER TABLE "rate_study_measurements" ADD CONSTRAINT "rate_study_measurements_pkey" PRIMARY KEY ("study", "measurement_nr") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_activity_usages"
-- ----------------------------
ALTER TABLE "study_activity_usages" ADD CONSTRAINT "StudyActivityUsages_pkey" PRIMARY KEY ("study", "arm", "epoch") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_measurements"
-- ----------------------------
ALTER TABLE "study_measurements" ADD CONSTRAINT "study_measurements_pkey" PRIMARY KEY ("study", "measurement_nr") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_activity_drugs"
-- ----------------------------
ALTER TABLE "study_activity_drugs" ADD CONSTRAINT "StudyActivityDrugs_pkey" PRIMARY KEY ("study", "activity", "drug") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "code_systems"
-- ----------------------------
ALTER TABLE "code_systems" ADD CONSTRAINT "CodeSystems_pkey" PRIMARY KEY ("code_system") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_activities"
-- ----------------------------
ALTER TABLE "study_activities" ADD CONSTRAINT "StudyActivities_pkey" PRIMARY KEY ("study", "name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "categorical_study_measurements"
-- ----------------------------
ALTER TABLE "categorical_study_measurements" ADD CONSTRAINT "categorical_study_measurements_pkey" PRIMARY KEY ("study", "measurement_nr") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "studies"
-- ----------------------------
ALTER TABLE "studies" ADD CONSTRAINT "Studies_pkey" PRIMARY KEY ("name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "indications"
-- ----------------------------
ALTER TABLE "indications" ADD CONSTRAINT "Indications_pkey" PRIMARY KEY ("name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "study_arms"
-- ----------------------------
ALTER TABLE "study_arms" ADD CONSTRAINT "StudyArms_pkey" PRIMARY KEY ("study", "name") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Primary key structure for table "units"
-- ----------------------------
ALTER TABLE "units" ADD CONSTRAINT "Units_pkey" PRIMARY KEY ("ucum_code") NOT DEFERRABLE INITIALLY IMMEDIATE;

