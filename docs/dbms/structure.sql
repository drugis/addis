CREATE TYPE activity_type AS ENUM ('SCREENING', 'RANDOMIZATION', 'WASH_OUT', 'FOLLOW_UP', 'TREATMENT', 'OTHER');
CREATE TYPE allocation_type AS ENUM ('UNKNOWN', 'RANDOMIZED', 'NONRANDOMIZED');
CREATE TYPE blinding_type AS ENUM ('OPEN', 'SINGLE_BLIND', 'DOUBLE_BLIND', 'TRIPLE_BLIND', 'UNKNOWN');
CREATE TYPE source AS ENUM ('MANUAL', 'CLINICALTRIALS');
CREATE TYPE status AS ENUM ('NOT_YET_RECRUITING', 'RECRUITING', 'ENROLLING', 'ACTIVE', 'COMPLETED', 'SUSPENDED', 'TERMINATED', 'WITHDRAWN', 'UNKNOWN');
CREATE TYPE measurement_type as ENUM ('CONTINUOUS', 'RATE', 'CATEGORICAL');
CREATE TYPE variable_type as ENUM ('PopulationCharacteristic', 'Endpoint', 'AdverseEvent');
CREATE TYPE epoch_offset as ENUM ('FROM_EPOCH_START', 'BEFORE_EPOCH_END');
CREATE TYPE concept_type as ENUM ('DRUG', 'INDICATION', 'UNIT', 'VARIABLE');

CREATE EXTENSION "uuid-ossp";

CREATE TABLE "code_systems" (
  "code_system" varchar,
  "code_system_name" varchar,
  PRIMARY KEY ("code_system")
);

CREATE TABLE "concepts" (
  "id" uuid,
  "name" varchar NOT NULL,
  "description" text,
  "type" concept_type,
  "code" varchar,
  "code_system" varchar REFERENCES code_systems (code_system),
  "owner" varchar NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE("code", "code_system")
);

CREATE TABLE "concept_map" (
  "sub" uuid REFERENCES concepts (id),
  "super" uuid REFERENCES concepts (id),
  PRIMARY KEY ("sub", "super")
);

CREATE TABLE "note_hooks" (
  "id" bigserial,
  PRIMARY KEY ("id")
);

CREATE TABLE "notes" (
  "id" bigserial,
  "note_hook_id" bigint REFERENCES note_hooks (id),
  "text" text,
  "source" source,
  PRIMARY KEY ("id", "note_hook_id")
);
CREATE TABLE "treatments" (
  "id" bigserial,
  "study_id" bigint,
  "activity_name" varchar NOT NULL,
  "drug_concept" uuid REFERENCES concepts (id),
  "periodicity" interval DEFAULT 'P0D',
  PRIMARY KEY ("id"),
  UNIQUE("study_id", "activity_name", "drug_concept")
);

CREATE TABLE "treatment_dosings" (
  "treatment_id" bigint REFERENCES treatments (id),
  "planned_time" interval,
  "min_dose" varchar,
  "max_dose" varchar,
  "scale_modifier" varchar,
  "unit_concept" uuid REFERENCES concepts (id),
  PRIMARY KEY ("treatment_id", "planned_time")
);
CREATE INDEX ON "treatment_dosings" ("treatment_id") WHERE "planned_time" IS NULL;

CREATE TABLE "activities" (
  "study_id" bigint,
  "name" varchar,
  "type" activity_type,
  PRIMARY KEY ("study_id", "name")
);
ALTER TABLE "treatments" ADD CONSTRAINT "treatment_activity_fkey" FOREIGN KEY ("study_id", "activity_name") REFERENCES "activities" ("study_id", "name");

CREATE TABLE "studies" (
  "id" bigserial,
  "name" varchar NOT NULL,
  "title" text,
  "indication_concept" uuid REFERENCES concepts (id),
  "objective" text,
  "allocation_type" allocation_type,
  "blinding_type" blinding_type,
  "number_of_centers" int2,
  "created_at" date,
  "source" source DEFAULT 'MANUAL',
  "exclusion" text,
  "inclusion" text,
  "status" status,
  "start_date" date,
  "end_date" date,
  "note_hook" bigint REFERENCES note_hooks (id),
  "blinding_type_note_hook" bigint REFERENCES note_hooks (id),
  "title_note_hook" bigint REFERENCES note_hooks (id),
  "allocation_type_note_hook" bigint REFERENCES note_hooks (id),
  PRIMARY KEY ("id")
);
CREATE INDEX ON "studies" ("name");
CREATE INDEX ON "studies" ("indication_concept");

CREATE TABLE "projects" (
  "id" bigserial,
  "name" varchar NOT NULL,
  "description" text,
  PRIMARY KEY ("id")
);

CREATE TABLE "project_studies" (
  "project_id" bigint REFERENCES projects (id),
  "study_id" bigint REFERENCES studies (id),
  PRIMARY KEY ("project_id", "study_id")
);

CREATE TABLE "study_references" (
  "study_id" bigint REFERENCES studies (id),
  "id" varchar,
  "repository" text DEFAULT 'PubMed',
  PRIMARY KEY ("study_id", "id")
);

CREATE TABLE "epochs" (
  "study_id" bigint REFERENCES studies (id),
  "name" varchar,
  "duration" interval DEFAULT 'P0D',
  "note_hook" bigint REFERENCES note_hooks (id),
  PRIMARY KEY ("study_id", "name")
);

CREATE TABLE "arms" (
  "study_id" bigint REFERENCES studies (id),
  "name" varchar,
  "arm_size" varchar,
  "note_hook" bigint REFERENCES note_hooks (id),
  PRIMARY KEY ("study_id", "name")
);
COMMENT ON COLUMN "arms"."name" IS 'Empty string indicates "total population"';

CREATE TABLE "designs" (
  "study_id" bigint,
  "arm_name" varchar,
  "epoch_name" varchar,
  "activity_name" varchar,
  PRIMARY KEY ("study_id", "arm_name", "epoch_name")
);
ALTER TABLE "designs" ADD CONSTRAINT "design_arm_fkey" FOREIGN KEY ("study_id", "arm_name") REFERENCES "arms" ("study_id", "name");
ALTER TABLE "designs" ADD CONSTRAINT "design_epoch_fkey" FOREIGN KEY ("study_id", "epoch_name") REFERENCES "epochs" ("study_id", "name");
ALTER TABLE "designs" ADD CONSTRAINT "design_activity_fkey" FOREIGN KEY ("study_id", "activity_name") REFERENCES "activities" ("study_id", "name");

CREATE TABLE "study_variables" (
  "study_id" bigint,
  "variable_concept" uuid REFERENCES concepts (id),
  "is_primary" bool,
  "measurement_type" measurement_type,
  "unit_concept" uuid REFERENCES concepts (id),
  "variable_type" variable_type,
  "note_hook" bigint REFERENCES note_hooks (id),
  PRIMARY KEY ("study_id", "variable_concept")
);

CREATE TABLE "variable_categories" (
  "variable_concept" uuid REFERENCES concepts (id),
  "category_name" varchar,
  PRIMARY KEY ("variable_concept", "category_name")
);

CREATE TABLE "measurements" (
  "study_id" bigint,
  "variable_concept" uuid REFERENCES concepts (id),
  "measurement_moment_name" varchar,
  "arm_name" varchar,
  "attribute" varchar,
  "integer_value" bigint,
  "real_value" float,
  PRIMARY KEY ("variable_concept", "measurement_moment_name", "arm_name", "attribute")
);
COMMENT ON COLUMN "measurements"."variable_concept" IS 'Uniquely identifies the study';
ALTER TABLE "measurements" ADD CONSTRAINT "variable_measurement_fkey" FOREIGN KEY ("study_id", "variable_concept") REFERENCES "study_variables" ("study_id", "variable_concept");
ALTER TABLE "measurements" ADD CONSTRAINT "arm_measurement_fkey" FOREIGN KEY ("study_id", "arm_name") REFERENCES "arms" ("study_id", "name");

CREATE TABLE "measurement_moments" (
  "study_id" bigint,
  "name" varchar,
  "epoch_name" varchar,
  "is_primary" bool,
  "offset_from_epoch" interval,
  "before_epoch" epoch_offset,
  "note_hook" bigint REFERENCES note_hooks (id),
  PRIMARY KEY ("study_id", "name"),
  UNIQUE ("study_id", "epoch_name", "offset_from_epoch", "before_epoch")
);
ALTER TABLE "measurement_moments" ADD CONSTRAINT "epoch_study_measurement_fkey" FOREIGN KEY ("study_id", "epoch_name") REFERENCES "epochs" ("study_id", "name");
ALTER TABLE "measurements" ADD CONSTRAINT "measurement_moments_fkey" FOREIGN KEY ("study_id", "measurement_moment_name") REFERENCES "measurement_moments" ("study_id", "name");
