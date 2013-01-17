/*
Navicat PGSQL Data Transfer

Source Server         : addis-bridged
Source Server Version : 90104
Source Host           : localhost:5432
Source Database       : addis_experimental
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90104
File Encoding         : 65001

Date: 2012-08-30 16:21:11
*/


-- ----------------------------
-- Table structure for "public"."activities"
-- ----------------------------
DROP TABLE "public"."activities";
CREATE TABLE "public"."activities" (
"id" int4 NOT NULL,
"study_id" int4 NOT NULL,
"name" varchar(255) NOT NULL,
"drug_activity_id" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."activities"."drug_activity_id" IS 'If null, no study_drug_activty was performed';

-- ----------------------------
-- Records of activities
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."arms"
-- ----------------------------
DROP TABLE "public"."arms";
CREATE TABLE "public"."arms" (
"id" int4 NOT NULL,
"study_id" int4 NOT NULL,
"arm_name" varchar(255) NOT NULL,
"arm_size" varchar(255),
"note_hook" int4
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of arms
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."code_systems"
-- ----------------------------
DROP TABLE "public"."code_systems";
CREATE TABLE "public"."code_systems" (
"code_system" varchar(255) NOT NULL,
"code_system_name" varchar(255)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of code_systems
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."designs"
-- ----------------------------
DROP TABLE "public"."designs";
CREATE TABLE "public"."designs" (
"arm_id" int4 NOT NULL,
"epoch_id" int4 NOT NULL,
"activity_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of designs
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."drug_activities"
-- ----------------------------
DROP TABLE "public"."drug_activities";
CREATE TABLE "public"."drug_activities" (
"activity_id" int4 NOT NULL,
"drug_name" varchar(255),
"min_dose" varchar(255),
"max_dose" varchar(255),
"unit_ucum_id" varchar(255)
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."drug_activities"."min_dose" IS 'min_dose < max_dose';

-- ----------------------------
-- Records of drug_activities
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."drugs"
-- ----------------------------
DROP TABLE "public"."drugs";
CREATE TABLE "public"."drugs" (
"name" varchar(255) NOT NULL,
"atc_code" varchar(255) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of drugs
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."epochs"
-- ----------------------------
DROP TABLE "public"."epochs";
CREATE TABLE "public"."epochs" (
"id" int4 NOT NULL,
"study_id" int4 NOT NULL,
"epoch_name" varchar(255) NOT NULL,
"duration" interval(6),
"note_hook" int4
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of epochs
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."indications"
-- ----------------------------
DROP TABLE "public"."indications";
CREATE TABLE "public"."indications" (
"name" varchar(255) NOT NULL,
"code" varchar(255),
"code_system" varchar(255)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of indications
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."measurement_results"
-- ----------------------------
DROP TABLE "public"."measurement_results";
CREATE TABLE "public"."measurement_results" (
"id" int4 NOT NULL,
"std_dev" float4,
"mean" float4,
"rate" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."measurement_results"."std_dev" IS 'null if rate or categorical';
COMMENT ON COLUMN "public"."measurement_results"."mean" IS 'null if rate or categorical';
COMMENT ON COLUMN "public"."measurement_results"."rate" IS 'null if continuous';

-- ----------------------------
-- Records of measurement_results
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."measurements"
-- ----------------------------
DROP TABLE "public"."measurements";
CREATE TABLE "public"."measurements" (
"id" int4 NOT NULL,
"variable_id" int4 NOT NULL,
"study_id" int4 NOT NULL,
"arm_id" int4,
"epoch_id" int4 NOT NULL,
"offset_from_epoch" date NOT NULL,
"note_hook" int4
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."measurements"."arm_id" IS 'If null it references the overall column in the results table';
COMMENT ON COLUMN "public"."measurements"."offset_from_epoch" IS 'Can be negative';

-- ----------------------------
-- Records of measurements
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."measurements_results"
-- ----------------------------
DROP TABLE "public"."measurements_results";
CREATE TABLE "public"."measurements_results" (
"measurement_id" int4 NOT NULL,
"result_id" int4 NOT NULL,
"category" varchar(255)
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."measurements_results"."category" IS 'Only applicable for categorical measurements';

-- ----------------------------
-- Records of measurements_results
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."note_hooks"
-- ----------------------------
DROP TABLE "public"."note_hooks";
CREATE TABLE "public"."note_hooks" (
"id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of note_hooks
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."notes"
-- ----------------------------
DROP TABLE "public"."notes";
CREATE TABLE "public"."notes" (
"id" int4 NOT NULL,
"note_hook_id" int4 NOT NULL,
"text" text
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of notes
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."objectives"
-- ----------------------------
DROP TABLE "public"."objectives";
CREATE TABLE "public"."objectives" (
"id" int4 NOT NULL,
"objective" varchar(255) NOT NULL,
"objective_nr" varchar(255) NOT NULL,
"primary" bool
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of objectives
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."references"
-- ----------------------------
DROP TABLE "public"."references";
CREATE TABLE "public"."references" (
"study_id" int4 NOT NULL,
"type" varchar NOT NULL,
"value" varchar(255),
"timestamp" date
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."references"."type" IS 'e.g. PubMed, DOI, ClinicalTrials';

-- ----------------------------
-- Records of references
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."studies"
-- ----------------------------
DROP TABLE "public"."studies";
CREATE TABLE "public"."studies" (
"id" int4 NOT NULL,
"name" varchar(255),
"note_hook" int4,
"blinding_type_note_hook" int4,
"title" text,
"title_note_hook" int4,
"indication" varchar(255),
"allocation_type_note_hook" int4,
"objective_id" int4,
"allocation_type" varchar(255),
"blinding_type" varchar(255),
"number_of_centers" int2,
"created_at" date,
"source" varchar(255),
"exclusion" text,
"inclusion" text,
"status" varchar(255),
"end_date" date,
"start_date" date
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."studies"."allocation_type" IS 'One of UNKNOWN, RANDOMIZED, NONRANDOMIZED';
COMMENT ON COLUMN "public"."studies"."blinding_type" IS 'One of OPEN, SINGLE_BLIND, DOUBLE_BLIND, TRIPLE_BLIND, UNKNOWN';
COMMENT ON COLUMN "public"."studies"."source" IS 'One of MANUAL, CLINICALTRIALS';
COMMENT ON COLUMN "public"."studies"."status" IS 'One of NOT_YET_RECRUITING, RECRUITING, ENROLLING, ACTIVE, COMPLETED, SUSPENDED, TERMINATED, WITHDRAWN, UNKNOWN';

-- ----------------------------
-- Records of studies
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."variable_categories"
-- ----------------------------
DROP TABLE "public"."variable_categories";
CREATE TABLE "public"."variable_categories" (
"variable_id" int4 NOT NULL,
"category_name" varchar NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of variable_categories
-- ----------------------------

-- ----------------------------
-- Table structure for "public"."variables"
-- ----------------------------
DROP TABLE "public"."variables";
CREATE TABLE "public"."variables" (
"id" int4 NOT NULL,
"name" varchar(255) NOT NULL,
"description" text,
"direction" varchar(255),
"type" varchar,
"measurement_type" varchar
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."variables"."direction" IS 'One of: LOWER_IS_BETTER, HIGHER_IS_BETTER';

-- ----------------------------
-- Records of variables
-- ----------------------------

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table "public"."activities"
-- ----------------------------
ALTER TABLE "public"."activities" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."arms"
-- ----------------------------
ALTER TABLE "public"."arms" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."code_systems"
-- ----------------------------
ALTER TABLE "public"."code_systems" ADD PRIMARY KEY ("code_system");

-- ----------------------------
-- Primary Key structure for table "public"."designs"
-- ----------------------------
ALTER TABLE "public"."designs" ADD PRIMARY KEY ("arm_id", "epoch_id");

-- ----------------------------
-- Primary Key structure for table "public"."drug_activities"
-- ----------------------------
ALTER TABLE "public"."drug_activities" ADD PRIMARY KEY ("activity_id");

-- ----------------------------
-- Uniques structure for table "public"."drugs"
-- ----------------------------
ALTER TABLE "public"."drugs" ADD UNIQUE ("atc_code");

-- ----------------------------
-- Primary Key structure for table "public"."drugs"
-- ----------------------------
ALTER TABLE "public"."drugs" ADD PRIMARY KEY ("name");

-- ----------------------------
-- Primary Key structure for table "public"."epochs"
-- ----------------------------
ALTER TABLE "public"."epochs" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."indications"
-- ----------------------------
ALTER TABLE "public"."indications" ADD PRIMARY KEY ("name");

-- ----------------------------
-- Primary Key structure for table "public"."measurement_results"
-- ----------------------------
ALTER TABLE "public"."measurement_results" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."measurements"
-- ----------------------------
ALTER TABLE "public"."measurements" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."measurements_results"
-- ----------------------------
ALTER TABLE "public"."measurements_results" ADD PRIMARY KEY ("measurement_id", "result_id");

-- ----------------------------
-- Primary Key structure for table "public"."note_hooks"
-- ----------------------------
ALTER TABLE "public"."note_hooks" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."notes"
-- ----------------------------
ALTER TABLE "public"."notes" ADD PRIMARY KEY ("id", "note_hook_id");

-- ----------------------------
-- Uniques structure for table "public"."objectives"
-- ----------------------------
ALTER TABLE "public"."objectives" ADD UNIQUE ("id");

-- ----------------------------
-- Primary Key structure for table "public"."objectives"
-- ----------------------------
ALTER TABLE "public"."objectives" ADD PRIMARY KEY ("id", "objective", "objective_nr");

-- ----------------------------
-- Primary Key structure for table "public"."references"
-- ----------------------------
ALTER TABLE "public"."references" ADD PRIMARY KEY ("study_id", "type");

-- ----------------------------
-- Uniques structure for table "public"."studies"
-- ----------------------------
ALTER TABLE "public"."studies" ADD UNIQUE ("name");

-- ----------------------------
-- Primary Key structure for table "public"."studies"
-- ----------------------------
ALTER TABLE "public"."studies" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."variable_categories"
-- ----------------------------
ALTER TABLE "public"."variable_categories" ADD PRIMARY KEY ("variable_id");

-- ----------------------------
-- Uniques structure for table "public"."variables"
-- ----------------------------
ALTER TABLE "public"."variables" ADD UNIQUE ("name");

-- ----------------------------
-- Primary Key structure for table "public"."variables"
-- ----------------------------
ALTER TABLE "public"."variables" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."activities"
-- ----------------------------
ALTER TABLE "public"."activities" ADD FOREIGN KEY ("drug_activity_id") REFERENCES "public"."drug_activities" ("activity_id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."arms"
-- ----------------------------
ALTER TABLE "public"."arms" ADD FOREIGN KEY ("note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."arms" ADD FOREIGN KEY ("study_id") REFERENCES "public"."studies" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."designs"
-- ----------------------------
ALTER TABLE "public"."designs" ADD FOREIGN KEY ("epoch_id") REFERENCES "public"."epochs" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."designs" ADD FOREIGN KEY ("activity_id") REFERENCES "public"."activities" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."designs" ADD FOREIGN KEY ("arm_id") REFERENCES "public"."arms" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."drug_activities"
-- ----------------------------
ALTER TABLE "public"."drug_activities" ADD FOREIGN KEY ("drug_name") REFERENCES "public"."drugs" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."epochs"
-- ----------------------------
ALTER TABLE "public"."epochs" ADD FOREIGN KEY ("note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."epochs" ADD FOREIGN KEY ("study_id") REFERENCES "public"."studies" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."indications"
-- ----------------------------
ALTER TABLE "public"."indications" ADD FOREIGN KEY ("code_system") REFERENCES "public"."code_systems" ("code_system") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."measurements"
-- ----------------------------
ALTER TABLE "public"."measurements" ADD FOREIGN KEY ("study_id") REFERENCES "public"."studies" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."measurements" ADD FOREIGN KEY ("epoch_id") REFERENCES "public"."epochs" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."measurements" ADD FOREIGN KEY ("variable_id") REFERENCES "public"."variables" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."measurements" ADD FOREIGN KEY ("note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."measurements" ADD FOREIGN KEY ("arm_id") REFERENCES "public"."arms" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."measurements_results"
-- ----------------------------
ALTER TABLE "public"."measurements_results" ADD FOREIGN KEY ("result_id") REFERENCES "public"."measurement_results" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."measurements_results" ADD FOREIGN KEY ("measurement_id") REFERENCES "public"."measurements" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."notes"
-- ----------------------------
ALTER TABLE "public"."notes" ADD FOREIGN KEY ("note_hook_id") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."references"
-- ----------------------------
ALTER TABLE "public"."references" ADD FOREIGN KEY ("study_id") REFERENCES "public"."studies" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."studies"
-- ----------------------------
ALTER TABLE "public"."studies" ADD FOREIGN KEY ("title_note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."studies" ADD FOREIGN KEY ("blinding_type_note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."studies" ADD FOREIGN KEY ("note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."studies" ADD FOREIGN KEY ("allocation_type_note_hook") REFERENCES "public"."note_hooks" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."studies" ADD FOREIGN KEY ("indication") REFERENCES "public"."indications" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."studies" ADD FOREIGN KEY ("objective_id") REFERENCES "public"."objectives" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."variable_categories"
-- ----------------------------
ALTER TABLE "public"."variable_categories" ADD FOREIGN KEY ("variable_id") REFERENCES "public"."variables" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
