CREATE TABLE "drug_activities" (
  "activity_id" serial,
  "drug_name" varchar(255),
  "min_dose" varchar(255),
  "max_dose" varchar(255),
  "unit_ucum_id" varchar,
  PRIMARY KEY ("activity_id") 
);

CREATE TABLE "activities" (
  "id" serial,
  "study_name" varchar(255),
  "name" varchar(255) NOT NULL,
  "drug_activity_id" int4,
  PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "activities"."drug_activity_id" IS 'If null, no study_drug_activty was performed';

CREATE TABLE "designs" (
  "arm_id" int4,
  "epoch_id" int4,
  "activity_id" int4,
  PRIMARY KEY ("arm_id", "epoch_id") 
);

CREATE TABLE "epochs" (
  "id" serial,
  "study_name" varchar(255),
  "epoch_name" varchar(255) NOT NULL,
  "duration" interval(255),
  "note_hook" int4,
  PRIMARY KEY ("id") 
);

CREATE TABLE "arms" (
  "id" serial,
  "study_name" varchar(255),
  "arm_name" varchar(255) NOT NULL,
  "arm_size" varchar(255),
  "note_hook" int4,
  PRIMARY KEY ("id") 
);

CREATE TABLE "drugs" (
  "name" varchar(255),
  "atc_code" varchar(255) NOT NULL,
  PRIMARY KEY ("name") ,
  UNIQUE ("atc_code")
);

CREATE TYPE allocation_type AS ENUM ('UNKNOWN', 'RANDOMIZED', 'NONRANDOMIZED');
CREATE TYPE blinding_type AS ENUM ('OPEN', 'SINGLE_BLIND', 'DOUBLE_BLIND', 'TRIPLE_BLIND', 'UNKNOWN');
CREATE TYPE source AS ENUM ('MANUAL', 'CLINICALTRIALS');
CREATE TYPE status AS ENUM ('NOT_YET_RECRUITING', 'RECRUITING', 'ENROLLING', 'ACTIVE', 'COMPLETED', 'SUSPENDED', 'TERMINATED', 'WITHDRAWN', 'UNKNOWN');

CREATE TABLE "studies" (
  "id" serial,
  "name" varchar(255),
  "note_hook" int4,
  "blinding_type_note_hook" int4,
  "title" varchar(255),
  "title_note_hook" int4,
  "indication" varchar(255),
  "allocation_type_note_hook" int4,
  "objective_id" int4,
  "allocation_type" allocation_type,
  "blinding_type" blinding_type,
  "number_of_centers" int2,
  "created_at" date,
  "source" source,
  "exclusion" text,
  "inclusion" text,
  "status" status,
  "end_date" date,
  "start_date" date,
  PRIMARY KEY ("name")
);

CREATE TABLE "study_references" (
  "study_name" varchar(255), 
  "url" varchar(255), 
  "repostitory" text DEFAULT 'PubMed',
  PRIMARY KEY ("study_name", "url")
);

CREATE TYPE direction as ENUM ('HIGHER_IS_BETTER', 'LOWER_IS_BETTER'); 
CREATE TYPE measurement_type as ENUM ('CONTINUOUS', 'RATE', 'CATEGORICAL'); 
CREATE TYPE variable_type as ENUM ('PopulationCharacteristic', 'Endpoint', 'AdverseEvent'); 

CREATE TABLE "variables" (
  "name" varchar(255),
  "description" text,
  "type" variable_type,
  "direction" direction,
  "measurement_type" measurement_type,
  PRIMARY KEY ("name")
);
COMMENT ON COLUMN "variables"."type" IS 'If type is PopulationCharacteristic then direction has no value';
COMMENT ON COLUMN "variables"."type" IS 'If type is AdverseEvent then measurement_type is always rate';

CREATE TABLE "measurements" (
  "id" serial,
  "variable_name" varchar(255),
  "study_name" varchar(255),
  "arm_id" int4,
  "epoch_id" int4,
  "offset_from_epoch" date NOT NULL,
  "note_hook" int4,
  PRIMARY KEY ("id") 
);

COMMENT ON COLUMN "measurements"."arm_id" IS 'If null it references the overall column in the results table';
COMMENT ON COLUMN "measurements"."offset_from_epoch" IS 'Can be negative';

CREATE TABLE "variable_categories" (
  "id" serial UNIQUE,
  "variable_name" varchar(255),
  "category_name" varchar(255),
  PRIMARY KEY ("id", "variable_name", "category_name") 
);

CREATE TABLE "measurements_results" (
  "measurement_id" int4 NOT NULL,
  "result_id" int4 NOT NULL,
  "category" int4,
  PRIMARY KEY ("measurement_id", "result_id") 
);

COMMENT ON COLUMN "measurements_results"."category" IS 'Only applicable for categorical measurements';

CREATE TABLE "measurement_results" (
  "id" serial,
  "std_dev" float4,
  "mean" float4,
  "rate" int4,
  "sample_size" int4,
  PRIMARY KEY ("id") 
);

CREATE TABLE "objectives" (
  "id" int4 UNIQUE,
  "objective" varchar(255),
  "objective_nr" varchar(255),
  "primary" bool,
  PRIMARY KEY ("objective", "objective_nr")
);

CREATE TABLE "indications" (
  "name" varchar(255),
  "code" varchar(255),
  "code_system" varchar(255),
  PRIMARY KEY ("name") 
);

CREATE TABLE "code_systems" (
  "code_system" varchar(255),
  "code_system_name" varchar(255),
  PRIMARY KEY ("code_system") 
);

CREATE TABLE "note_hooks" (
  "id" int4,
  PRIMARY KEY ("id") 
);

CREATE TABLE "notes" (
  "id" int4,
  "note_hook_id" int4,
  "source" source,
  "text" text,
  PRIMARY KEY ("id", "note_hook_id") 
);


ALTER TABLE "variable_categories" ADD CONSTRAINT "variable_category_fkey" FOREIGN KEY ("variable_name") REFERENCES "variables" ("name");
ALTER TABLE "measurements_results" ADD CONSTRAINT "measurements_category_fkey" FOREIGN KEY ("category") REFERENCES "variable_categories" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "study_measurement_fkey" FOREIGN KEY ("study_name") REFERENCES "studies" ("name");
ALTER TABLE "measurements" ADD CONSTRAINT "variable_measurement_fkey" FOREIGN KEY ("variable_name") REFERENCES "variables" ("name");
ALTER TABLE "measurements" ADD CONSTRAINT "epoch_measurement_fkey" FOREIGN KEY ("epoch_id") REFERENCES "epochs" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "arm_measurement_fkey" FOREIGN KEY ("arm_id") REFERENCES "arms" ("id");
ALTER TABLE "arms" ADD CONSTRAINT "study_arm_fkey" FOREIGN KEY ("study_name") REFERENCES "studies" ("name");
ALTER TABLE "epochs" ADD CONSTRAINT "study_epoch_fkey" FOREIGN KEY ("study_name") REFERENCES "studies" ("name");
ALTER TABLE "designs" ADD CONSTRAINT "design_arm_fkey" FOREIGN KEY ("arm_id") REFERENCES "arms" ("id");
ALTER TABLE "designs" ADD CONSTRAINT "design_epoch_fkey" FOREIGN KEY ("epoch_id") REFERENCES "epochs" ("id");
ALTER TABLE "designs" ADD CONSTRAINT "design_activity_fkey" FOREIGN KEY ("activity_id") REFERENCES "activities" ("id");
ALTER TABLE "activities" ADD CONSTRAINT "drug_activity_fkey" FOREIGN KEY ("drug_activity_id") REFERENCES "drug_activities" ("activity_id");
ALTER TABLE "drug_activities" ADD CONSTRAINT "activity_drug_fkey" FOREIGN KEY ("drug_name") REFERENCES "drugs" ("name");
ALTER TABLE "measurements_results" ADD CONSTRAINT "measurements_measurement_results_fkey" FOREIGN KEY ("measurement_id") REFERENCES "measurements" ("id");
ALTER TABLE "measurements_results" ADD CONSTRAINT "measurements_result_results_fkey" FOREIGN KEY ("result_id") REFERENCES "measurement_results" ("id");
ALTER TABLE "indications" ADD CONSTRAINT "indication_code_system_fkey" FOREIGN KEY ("code_system") REFERENCES "code_systems" ("code_system");
ALTER TABLE "studies" ADD CONSTRAINT "study_indication_fkey" FOREIGN KEY ("indication") REFERENCES "indications" ("name");
ALTER TABLE "studies" ADD CONSTRAINT "study_objective_fkey" FOREIGN KEY ("objective_id") REFERENCES "objectives" ("id");
ALTER TABLE "notes" ADD CONSTRAINT "note_note_hooks" FOREIGN KEY ("note_hook_id") REFERENCES "note_hooks" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "measurement_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "arms" ADD CONSTRAINT "study_arms_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "epochs" ADD CONSTRAINT "study_epochs_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_blinding_type_note_hook_fkey" FOREIGN KEY ("blinding_type_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_title_note_hook_fkey" FOREIGN KEY ("title_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "allocation_type_note_hook_fkey" FOREIGN KEY ("allocation_type_note_hook") REFERENCES "note_hooks" ("id");

