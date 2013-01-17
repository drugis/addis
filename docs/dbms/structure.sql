CREATE TABLE "treatment_dosings" (
  "treatment_id" int4,
  "planned_time" interval,
  "min_dose" varchar,
  "max_dose" varchar,
  "scale_modifier" varchar,
  "unit" varchar,
  PRIMARY KEY ("treatment_id", "planned_time")
);
CREATE INDEX ON "treatment_dosings" ("treatment_id") WHERE "planned_time" IS NULL;

CREATE TABLE "treatments" (
  "id" serial,
  "activity_id" int4,
  "drug_name" varchar,
  "periodicity" interval,
  PRIMARY KEY ("id")
);

CREATE TYPE activity_type AS ENUM ('SCREENING', 'RANDOMIZATION', 'WASH_OUT', 'FOLLOW_UP', 'TREATMENT', 'OTHER');

CREATE TABLE "activities" (
  "id" serial,
  "study_name" varchar,
  "name" varchar NOT NULL,
  "type" activity_type,
  PRIMARY KEY ("id") 
);
CREATE UNIQUE INDEX ON "activities" ("study_name", "name");

CREATE TABLE "designs" (
  "arm_id" int4,
  "epoch_id" int4,
  "activity_id" int4,
  PRIMARY KEY ("arm_id", "epoch_id") 
);

CREATE TABLE "epochs" (
  "id" serial,
  "study_name" varchar,
  "epoch_name" varchar NOT NULL,
  "duration" interval DEFAULT 'P0D',
  "note_hook" int4,
  PRIMARY KEY ("id") 
);
CREATE UNIQUE INDEX ON "epochs" ("study_name", "epoch_name");

CREATE TABLE "arms" (
  "id" serial,
  "study_name" varchar,
  "arm_name" varchar NOT NULL,
  "arm_size" varchar,
  "note_hook" int4,
  PRIMARY KEY ("id") 
);
CREATE UNIQUE INDEX ON "arms" ("study_name", "arm_name");

CREATE TABLE "drugs" (
  "name" varchar, 
  "code" varchar,
  "code_system" varchar,
  PRIMARY KEY ("name")
);
CREATE UNIQUE INDEX ON "drugs" ("code", "code_system");

CREATE TABLE "units" ( 
  "name" varchar,
  "symbol" varchar, 
  "ucum" varchar, 
  PRIMARY key ("name")
);
CREATE INDEX ON "units" ("ucum");

CREATE TYPE allocation_type AS ENUM ('UNKNOWN', 'RANDOMIZED', 'NONRANDOMIZED');
CREATE TYPE blinding_type AS ENUM ('OPEN', 'SINGLE_BLIND', 'DOUBLE_BLIND', 'TRIPLE_BLIND', 'UNKNOWN');
CREATE TYPE source AS ENUM ('MANUAL', 'CLINICALTRIALS');
CREATE TYPE status AS ENUM ('NOT_YET_RECRUITING', 'RECRUITING', 'ENROLLING', 'ACTIVE', 'COMPLETED', 'SUSPENDED', 'TERMINATED', 'WITHDRAWN', 'UNKNOWN');

CREATE TABLE "studies" (
  "id" serial,
  "name" varchar,
  "title" text,
  "indication" varchar,
  "objective" text,
  "allocation_type" allocation_type,
  "blinding_type" blinding_type,
  "number_of_centers" int2,
  "created_at" date,
  "source" source,
  "exclusion" text,
  "inclusion" text,
  "status" status,
  "start_date" date,
  "end_date" date,
  "note_hook" int4,
  "blinding_type_note_hook" int4,
  "title_note_hook" int4,
  "allocation_type_note_hook" int4,
  PRIMARY KEY ("name")
);
CREATE INDEX ON "studies" ("id");

CREATE TABLE "study_references" (
  "study_name" varchar,
  "id" varchar, 
  "repostitory" text DEFAULT 'PubMed',
  PRIMARY KEY ("study_name", "id")
);

CREATE TYPE direction as ENUM ('HIGHER_IS_BETTER', 'LOWER_IS_BETTER'); 
CREATE TYPE measurement_type as ENUM ('CONTINUOUS', 'RATE', 'CATEGORICAL'); 
CREATE TYPE variable_type as ENUM ('PopulationCharacteristic', 'Endpoint', 'AdverseEvent'); 

CREATE TABLE "variables" (
  "id" serial UNIQUE, 
  "name" varchar,
  "description" text,
  "type" variable_type,
  "direction" direction,
  "measurement_type" measurement_type,
  "unit" varchar,
  "code" varchar,
  "code_system" varchar,
  PRIMARY KEY ("name")
);
CREATE INDEX variable_id_idx ON "variables" ("id");
CREATE UNIQUE INDEX variables_code_idx ON "variables" ("name", "code", "code_system");

CREATE TABLE "variable_categories" (
  "id" serial,
  "variable_name" varchar,
  "category_name" varchar,
  PRIMARY KEY ("id") 
);
CREATE UNIQUE INDEX variable_category_idx ON "variable_categories" ("variable_name", "category_name");

COMMENT ON COLUMN "variables"."type" IS 'If type is PopulationCharacteristic then direction has no value. 
                                         If type is AdverseEvent then measurement_type is always rate';

CREATE TABLE "measurements" (
  "id" serial,
  "variable_id" int4,
  "study_name" varchar,
  "arm_id" int4,
  "epoch_id" int4,
  "primary" bool,
  "offset_from_epoch" interval,
	"before_epoch" bool,
  "note_hook" int4,
  PRIMARY KEY ("id") 
);
CREATE INDEX measurements_idx ON "measurements" ("study_name");

COMMENT ON COLUMN "measurements"."arm_id" IS 'If null it references the overall column in the results table';
COMMENT ON COLUMN "measurements"."offset_from_epoch" IS 'Can be negative';

CREATE TABLE "measurements_results" (
  "measurement_id" int4 NOT NULL,
  "result_id" int4 NOT NULL,
  "category_id" int4,
  PRIMARY KEY ("measurement_id", "result_id") 
);

COMMENT ON COLUMN "measurements_results"."category_id" IS 'Only applicable for categorical measurements';

CREATE TABLE "measurement_results" (
  "id" serial,
  "std_dev" float4,
  "mean" float4,
  "rate" int4,
  "sample_size" int4,
  PRIMARY KEY ("id") 
);

CREATE TABLE "indications" (
  "name" varchar,
  "code" varchar,
  "code_system" varchar,
  PRIMARY KEY ("name") 
);

CREATE TABLE "code_systems" (
  "code_system" varchar,
  "code_system_name" varchar,
  PRIMARY KEY ("code_system") 
);

CREATE TABLE "note_hooks" (
  "id" serial,
  PRIMARY KEY ("id") 
);

CREATE TABLE "notes" (
  "id" serial,
  "note_hook_id" int4,
  "text" text,
  "source" source,
  PRIMARY KEY ("id", "note_hook_id") 
);

ALTER TABLE "variable_categories" ADD CONSTRAINT "variable_category_fkey" FOREIGN KEY ("variable_name") REFERENCES "variables" ("name");
ALTER TABLE "measurements_results" ADD CONSTRAINT "measurements_category_fkey" FOREIGN KEY ("category_id") REFERENCES "variable_categories" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "study_measurement_fkey" FOREIGN KEY ("study_name") REFERENCES "studies" ("name");
ALTER TABLE "measurements" ADD CONSTRAINT "variable_measurement_fkey" FOREIGN KEY ("variable_id") REFERENCES "variables" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "epoch_measurement_fkey" FOREIGN KEY ("epoch_id") REFERENCES "epochs" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "arm_measurement_fkey" FOREIGN KEY ("arm_id") REFERENCES "arms" ("id");
ALTER TABLE "arms" ADD CONSTRAINT "study_arm_fkey" FOREIGN KEY ("study_name") REFERENCES "studies" ("name");
ALTER TABLE "epochs" ADD CONSTRAINT "study_epoch_fkey" FOREIGN KEY ("study_name") REFERENCES "studies" ("name");
ALTER TABLE "designs" ADD CONSTRAINT "design_arm_fkey" FOREIGN KEY ("arm_id") REFERENCES "arms" ("id");
ALTER TABLE "designs" ADD CONSTRAINT "design_epoch_fkey" FOREIGN KEY ("epoch_id") REFERENCES "epochs" ("id");
ALTER TABLE "designs" ADD CONSTRAINT "design_activity_fkey" FOREIGN KEY ("activity_id") REFERENCES "activities" ("id");
ALTER TABLE "treatments" ADD CONSTRAINT "treatments_drug_fkey" FOREIGN KEY ("drug_name") REFERENCES "drugs" ("name");
ALTER TABLE "treatment_dosings" ADD CONSTRAINT "treatment_dosings_unit_fkey" FOREIGN KEY ("unit") REFERENCES "units" ("name");
ALTER TABLE "treatment_dosings" ADD CONSTRAINT "treatment_dosings_fkey" FOREIGN KEY ("treatment_id") REFERENCES "treatments" ("id");
ALTER TABLE "treatments" ADD CONSTRAINT "treatment_activity_fkey" FOREIGN KEY ("activity_id") REFERENCES "activities" ("id");

ALTER TABLE "measurements_results" ADD CONSTRAINT "measurements_measurement_results_fkey" FOREIGN KEY ("measurement_id") REFERENCES "measurements" ("id");
ALTER TABLE "measurements_results" ADD CONSTRAINT "measurements_result_results_fkey" FOREIGN KEY ("result_id") REFERENCES "measurement_results" ("id");
ALTER TABLE "indications" ADD CONSTRAINT "indication_code_system_fkey" FOREIGN KEY ("code_system") REFERENCES "code_systems" ("code_system");
ALTER TABLE "studies" ADD CONSTRAINT "study_indication_fkey" FOREIGN KEY ("indication") REFERENCES "indications" ("name");
ALTER TABLE "notes" ADD CONSTRAINT "note_note_hooks" FOREIGN KEY ("note_hook_id") REFERENCES "note_hooks" ("id");
ALTER TABLE "measurements" ADD CONSTRAINT "measurement_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "arms" ADD CONSTRAINT "study_arms_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "epochs" ADD CONSTRAINT "study_epochs_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_note_hook_fkey" FOREIGN KEY ("note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_blinding_type_note_hook_fkey" FOREIGN KEY ("blinding_type_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "study_title_note_hook_fkey" FOREIGN KEY ("title_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "studies" ADD CONSTRAINT "allocation_type_note_hook_fkey" FOREIGN KEY ("allocation_type_note_hook") REFERENCES "note_hooks" ("id");
ALTER TABLE "drugs" ADD CONSTRAINT "drugs_code_system_fkey" FOREIGN KEY ("code_system") REFERENCES "code_systems" ("code_system");
ALTER TABLE "variables" ADD CONSTRAINT "variable_unit_fkey" FOREIGN KEY ("unit") REFERENCES "units" ("name");
