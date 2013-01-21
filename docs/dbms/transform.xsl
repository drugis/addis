<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:drugis="http://drugis.org"
    exclude-result-prefixes="xs drugis"
    version="3.0">
    
    <xsl:param name="projectName" as="xs:string" />
    <xsl:param name="projectDescription" as="xs:string" />
    
    <xsl:output method="text" indent="yes" media-type="text/sql"/> 
    <xsl:strip-space elements="*"/>
    
    <xsl:variable name="snomed">2.16.840.1.113883.6.96</xsl:variable>
    <xsl:variable name="atc">2.16.840.1.113883.6.73</xsl:variable>
    
    <xsl:function name="drugis:null-or-value">
        <xsl:param name="value" />
        <xsl:choose>
            <xsl:when test="$value != ''"> '<xsl:value-of select="$value" />'</xsl:when>
            <xsl:otherwise>NULL</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="drugis:escape-apostrophe">
        <xsl:param name="string" />
        <xsl:variable name="apostrophe">'</xsl:variable>
        <xsl:value-of select="translate($string, $apostrophe, '')" disable-output-escaping="yes" />
    </xsl:function>
   
    
    <xsl:function name="drugis:create-notes">
        <xsl:param name="hook_name" />
        <xsl:param name="notes" />
        <xsl:param name="do-close" />
        <xsl:variable name="apos">'</xsl:variable>
        <xsl:if test="boolean($notes/note)">
            INSERT INTO notes ("note_hook_id", "text", "source") VALUES 
            <xsl:for-each select="$notes/note">
                ((SELECT * FROM <xsl:value-of select="$hook_name" /> ), 
                '<xsl:value-of select="drugis:escape-apostrophe(text())"></xsl:value-of>',
                '<xsl:value-of select="@source" />')
                <xsl:if test="position() != last()">,</xsl:if>
            </xsl:for-each>
            <xsl:if test="$do-close">;</xsl:if>
        </xsl:if>
        <xsl:if test="not(boolean($notes/note)) and not($do-close)">
            SELECT 0 <xsl:if test="$do-close">;</xsl:if>
        </xsl:if>
    </xsl:function>

    <xsl:function name="drugis:get-project-var">
        <xsl:param name="varName" />
        (SELECT id FROM project_variables, variables WHERE
            project_variables.variable_id = variables.id AND
            project_variables.project_id = (SELECT id FROM projects WHERE name='<xsl:value-of select="$projectName"/>') AND
            variables.name = '<xsl:value-of select="$varName" />' LIMIT 1)
    </xsl:function>
    
    <xsl:function name="drugis:get-study">
        <xsl:param name="name" />
        (SELECT id FROM study_name_id WHERE name='<xsl:value-of select="$name" />')
    </xsl:function>
    
    <xsl:function name="drugis:get-study-var">
        <xsl:param name="studyName" />
        <xsl:param name="varName" />
        (SELECT id FROM study_variables, variables WHERE
            study_variables.variable_id = variables.id AND
            study_variables.study_id = <xsl:value-of select="drugis:get-study($studyName)"/> AND
            variables.name = '<xsl:value-of select="$varName" />' LIMIT 1)
    </xsl:function>
    
    <xsl:function name="drugis:create-mm-name">
        <xsl:param name="epochName" />
        <xsl:param name="offset" />
        <xsl:param name="relativeTo" />
        '<xsl:value-of select="$offset" />&#160;<xsl:value-of select="translate(lower-case($relativeTo), '_', ' ')"/>&#160;<xsl:value-of select="$epochName"/>'
    </xsl:function>
    
    <xsl:function name="drugis:get-mm-primary">
        <xsl:param name="epochName" />
        <xsl:param name="offset" />
        <xsl:param name="relativeTo" />
        <xsl:choose>
            <xsl:when test="$epochName = 'Main phase' and $offset = 'P0D' and $relativeTo = 'BEFORE_EPOCH_END'">'TRUE'</xsl:when>
            <xsl:otherwise>'FALSE'</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    

    <xsl:template match="addis-data">
        BEGIN TRANSACTION;
        
        INSERT INTO code_systems (code_system, code_system_name) SELECT '<xsl:value-of select="$snomed" />', 'Systematized Nomenclature of Medicine-Clinical Terms (SNOMED CT)' WHERE '<xsl:value-of select="$snomed" />' NOT IN (SELECT code_system FROM code_systems);
        INSERT INTO code_systems (code_system, code_system_name) SELECT '<xsl:value-of select="$atc" />', 'Anatomical Therapeutic Chemical (ATC) classification' WHERE '<xsl:value-of select="$atc" />' NOT IN (SELECT code_system FROM code_systems);; 
        <!-- temporary table to match (project-specific) study names to (global) studies.id -->
        CREATE TEMPORARY TABLE "study_name_id" (
            "name" varchar,
            "id" int4,
            PRIMARY KEY ("name")
        );
        
        INSERT INTO "projects" ("name", "description") VALUES ('<xsl:value-of select="$projectName"/>', '<xsl:value-of select="$projectDescription"/>');
        
        <xsl:apply-templates select="units/unit" />
        <xsl:apply-templates select="indications/indication" />
        <xsl:apply-templates select="drugs/drug" />
        <xsl:apply-templates select="endpoints/endpoint|populationCharacteristics/populationCharacteristic|adverseEvents/adverseEvent"></xsl:apply-templates>
        <xsl:apply-templates select="studies/study"></xsl:apply-templates>
        
        COMMIT;
    </xsl:template>
   
    <xsl:template match="units/unit">
        INSERT INTO units (name, symbol) SELECT '<xsl:value-of select="@name"/>', '<xsl:value-of select="@symbol"/>' WHERE '<xsl:value-of select="@name"/>' NOT IN (SELECT name FROM units);
    </xsl:template>
   
    <xsl:template match="indications/indication">
        INSERT INTO indications (name, code, code_system) SELECT 
            '<xsl:value-of select="@name" />',
            '<xsl:value-of select="@code" />',
            '<xsl:value-of select="$snomed" />';
    </xsl:template>
    
    <xsl:template match="drugs/drug">
        INSERT INTO drugs (name, code, code_system) SELECT 
            '<xsl:value-of select="@name" />',
            '<xsl:value-of select="@atcCode" />',
            '<xsl:value-of select="$atc" />' WHERE '<xsl:value-of select="@name" />' NOT IN (SELECT name FROM drugs);
    </xsl:template>

    <xsl:template match="endpoints/endpoint|populationCharacteristics/populationCharacteristic|adverseEvents/adverseEvent">
        <xsl:if test="continuous/@unitOfMeasurement">
            INSERT INTO units (name) SELECT ('<xsl:value-of select="continuous/@unitOfMeasurement"/>') WHERE NOT EXISTS (SELECT name FROM units WHERE name = '<xsl:value-of select="continuous/@unitOfMeasurement"/>');
        </xsl:if>       
        WITH variable_id AS (
        INSERT INTO variables (name, description, direction, measurement_type, unit) VALUES (   
            '<xsl:value-of select="@name" />',
            '<xsl:value-of select="@description" />',
            <xsl:value-of select="drugis:null-or-value(@direction)" />,
            <xsl:choose>
                <xsl:when test="continuous">'CONTINUOUS',
                    '<xsl:value-of select="continuous/@unitOfMeasurement" />'
                </xsl:when>
                <xsl:when test="rate">'RATE', NULL</xsl:when>
                <xsl:when test="categorical">'CATEGORICAL', NULL</xsl:when>
            </xsl:choose>) RETURNING id)
        INSERT INTO project_variables (project_id, variable_id) VALUES ( 
            (SELECT id FROM projects WHERE name = '<xsl:value-of select="$projectName" />'), 
            (SELECT id FROM variable_id LIMIT 1)
         );
        <xsl:apply-templates select="categorical/category"/>
    </xsl:template>
    
    <xsl:template match="categorical/category">
        INSERT INTO variable_categories (variable_id, category_name) VALUES (
            <xsl:value-of select="drugis:get-project-var(../../@name)"/>,
            '<xsl:value-of select="text()" />');
    </xsl:template>
    
    <xsl:template match="studies/study">
        WITH note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), blinding_type_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), allocation_type_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), title_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), study AS (
            INSERT INTO studies (
            name, note_hook, blinding_type_note_hook, title, title_note_hook, indication, 
            allocation_type_note_hook, objective, allocation_type, blinding_type,
            number_of_centers, created_at, source, exclusion, inclusion, status, end_date, start_date) VALUES ( 
                '<xsl:value-of select="@name" />',
                (SELECT id FROM note_hook),
                (SELECT id FROM blinding_type_note_hook),
                <xsl:value-of select="drugis:null-or-value(drugis:escape-apostrophe(characteristics/title/value/text()))" />,
                (SELECT id FROM title_note_hook ),
                '<xsl:value-of select="indication/@name" />',
                (SELECT id FROM allocation_type_note_hook ),
                <xsl:value-of select="drugis:null-or-value(characteristics/objective/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/allocation/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/blinding/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/centers/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/creation_date/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/source/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(drugis:escape-apostrophe(characteristics/exclusion/value/text()))"></xsl:value-of>,
                <xsl:value-of select="drugis:null-or-value(drugis:escape-apostrophe(characteristics/inclusion/value/text()))"></xsl:value-of>,
                <xsl:value-of select="drugis:null-or-value(characteristics/status/value/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/study_end/text())" />,
                <xsl:value-of select="drugis:null-or-value(characteristics/study_start/text())" />)  RETURNING id
        ) INSERT INTO study_name_id (name, id) VALUES ('<xsl:value-of select="@name" />', (SELECT id FROM study));

        <!-- insert empty string for "total study population" so as not to upset key constraints -->
        INSERT INTO arms (study_id, name) VALUES (
        <xsl:value-of select="drugis:get-study(@name)" />, '');

        <xsl:apply-templates select="characteristics/references" />
        <xsl:apply-templates select="arms/arm" />
        <xsl:apply-templates select="epochs/epoch" />
        <xsl:apply-templates select="studyOutcomeMeasures/studyOutcomeMeasure" />
        <xsl:apply-templates select="measurements/measurement" />
        <xsl:apply-templates select="activities/studyActivity" />
        
        <xsl:variable name="study_note">(SELECT note_hook FROM studies WHERE id = <xsl:value-of select="drugis:get-study(@name)" />) AS study_note_hook</xsl:variable>
        <xsl:value-of select="drugis:create-notes($study_note, notes, true())" />
        
        <xsl:variable name="blinding_type_note">(SELECT blinding_type_note_hook FROM studies WHERE id = <xsl:value-of select="drugis:get-study(@name)" />) AS note_hook</xsl:variable>
        <xsl:value-of select="drugis:create-notes($blinding_type_note, characteristics/blinding/notes, true())" />
        
        <xsl:variable name="title_note">(SELECT title_note_hook FROM studies WHERE id = <xsl:value-of select="drugis:get-study(@name)" />) AS note_hook</xsl:variable>
        <xsl:value-of select="drugis:create-notes($title_note, characteristics/title/notes, true())" />
        
        <xsl:variable name="allocation_type_note">(SELECT allocation_type_note_hook FROM studies WHERE id = <xsl:value-of select="drugis:get-study(@name)" />) AS note_hook</xsl:variable>
        <xsl:value-of select="drugis:create-notes($allocation_type_note, characteristics/allocation/notes, true())" />
    </xsl:template>
    
    <xsl:template match="characteristics/references">
        <xsl:for-each select="pubMedId">
            INSERT INTO study_references (study_id, id) VALUES (
               <xsl:value-of select="drugis:get-study(../../../@name)" />,
               '<xsl:value-of select="text()" />');
        </xsl:for-each>
    </xsl:template>

    <!-- Design -->
    <xsl:template match="arms/arm">
        WITH arm_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), notes AS (
            <xsl:value-of select="drugis:create-notes('arm_note_hook', notes, false())" />
        )
        INSERT INTO arms (study_id, name, arm_size, note_hook) VALUES (
            <xsl:value-of select="drugis:get-study(../../@name)" />,
            '<xsl:value-of select="@name" />',
            '<xsl:value-of select="@size" />',
            (SELECT id FROM arm_note_hook ));
    </xsl:template>

   <xsl:template match="epochs/epoch">
        WITH epoch_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), notes AS (
            <xsl:value-of select="drugis:create-notes('epoch_note_hook', notes, false())" />
        )
        INSERT INTO epochs (study_id, name, duration, note_hook) VALUES ( 
            <xsl:value-of select="drugis:get-study(../../@name)" />,
            '<xsl:value-of select="@name" />', 
            <xsl:value-of select="drugis:null-or-value(duration/text())" />,
            (SELECT id FROM epoch_note_hook ));
    </xsl:template>
    
    <xsl:template match="studyOutcomeMeasures/studyOutcomeMeasure">
        <xsl:apply-templates select="whenTaken"/>
        
        <xsl:variable name="tagName" select="name(./(endpoint|populationCharacteristic|adverseEvent))" />
        <xsl:variable name="varType" select="concat(upper-case(substring($tagName, 1, 1)), substring($tagName, 2))" />
        <xsl:variable name="varName" select="(endpoint|populationCharacteristic|adverseEvent)/@name" />
        
        WITH study_var AS (
            INSERT INTO variables  (name, description, direction, measurement_type, unit)
                SELECT name, description, direction, measurement_type, unit FROM variables 
                WHERE variables.id = <xsl:value-of select="drugis:get-project-var($varName)"/>
                RETURNING id
        ), som_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id 
        ), notes AS (
            <xsl:value-of select="drugis:create-notes('som_note_hook', notes, false())" />
        )
        INSERT INTO study_variables (study_id, variable_id, note_hook, is_primary, variable_type) 
            VALUES (<xsl:value-of select="drugis:get-study(../../@name)"/>,
                (SELECT id FROM study_var),
                (SELECT id FROM som_note_hook),
                <xsl:value-of select="@primary"/>,
                '<xsl:value-of select="$varType"/>'
                );

        WITH study_var AS <xsl:value-of select="drugis:get-study-var(../../@name, $varName)"/>
        INSERT INTO variable_categories (variable_id, category_name) 
            SELECT study_var.id, variable_categories.category_name FROM study_var, variable_categories
                WHERE variable_id = <xsl:value-of select="drugis:get-project-var($varName)"/>;
        
        INSERT INTO variable_map (sub, super) VALUES (
            <xsl:value-of select="drugis:get-study-var(../../@name, $varName)"/>,
            <xsl:value-of select="drugis:get-project-var($varName)"/>
        );        
    </xsl:template>
    
    <!-- ensure the whenTaken exists (as a measurement_moments.*) -->
    <xsl:template match="studyOutcomeMeasures/studyOutcomeMeasure/whenTaken">
        WITH note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id 
        ), notes AS ( 
            <xsl:value-of select="drugis:create-notes('note_hook', ../notes, false())" />
        )
        <xsl:variable name="name" select="drugis:create-mm-name(epoch/@name, @howLong, @relativeTo)" />
        INSERT INTO measurement_moments ("study_id", "name", "epoch_name", "primary", "offset_from_epoch", "before_epoch", "note_hook") 
        SELECT <xsl:value-of select="drugis:get-study(../../../@name)"/>,
            <xsl:value-of select="$name"/>,
            '<xsl:value-of select="epoch/@name"/>',
            <xsl:value-of select="drugis:get-mm-primary(epoch/@name, @howLong, @relativeTo)"/>,
            '<xsl:value-of select="@howLong"/>',
            '<xsl:value-of select="@relativeTo"/>',
            (SELECT id FROM note_hook)
        WHERE <xsl:value-of select="$name"/> NOT IN (
            SELECT name FROM measurement_moments
            WHERE study_id = <xsl:value-of select="drugis:get-study(../../../@name)"/>
        );
    </xsl:template>
    
    
    <xsl:function name="drugis:insert-measurement">
        <xsl:param name="studyName" />
        <xsl:param name="varName" />
        <xsl:param name="armName" />
        <xsl:param name="mmName" />
        <xsl:param name="attribute" />
        <xsl:param name="intVal" />
        <xsl:param name="realVal" />
        
        <xsl:variable name="studyId" select="drugis:get-study($studyName)" />
        WITH variable AS ( 
        <xsl:value-of select="drugis:get-study-var($studyName, $varName)" />
        ) INSERT INTO measurements ("study_id", "variable_id", "arm_name", "measurement_moment_name", 
            "attribute", "integer_value", "real_value") VALUES (
        <xsl:value-of select="$studyId" />,
        (SELECT id FROM variable),
        '<xsl:value-of select="$armName" />',
        <xsl:value-of select="$mmName" />,
        '<xsl:value-of select="$attribute" />',
        <xsl:value-of select="drugis:null-or-value($intVal)" />,
        <xsl:value-of select="drugis:null-or-value($realVal)" />
        );
    </xsl:function>
    
    <!-- Measurements --> 
    <xsl:template match="measurements/measurement">
        <xsl:variable name="somId" select="studyOutcomeMeasure/@id" />
        <xsl:variable name="studyOutcome" select="../../studyOutcomeMeasures/studyOutcomeMeasure[@id=$somId]" />
        <xsl:variable name="studyName" select="../../@name"></xsl:variable>
        <xsl:variable name="studyId" select="drugis:get-study($studyName)" />
        <xsl:variable name="varName" select="($studyOutcome/populationCharacteristic|$studyOutcome/adverseEvent|$studyOutcome/endpoint)/@name" />
        <xsl:variable name="mmName" select="drugis:create-mm-name(whenTaken/epoch/@name, whenTaken/@howLong, whenTaken/@relativeTo)"/>

        <xsl:choose>
            <xsl:when test="continuousMeasurement">
                <xsl:value-of select="drugis:insert-measurement($studyName, $varName, arm/@name, $mmName, 'mean', xs:null, continuousMeasurement/@mean)" />
                <xsl:value-of select="drugis:insert-measurement($studyName, $varName, arm/@name, $mmName, 'standard deviation', xs:null, continuousMeasurement/@stdDev)" />
                <xsl:value-of select="drugis:insert-measurement($studyName, $varName, arm/@name, $mmName, 'sample size', continuousMeasurement/@sampleSize, xs:null)" />
            </xsl:when>
            <xsl:when test="rateMeasurement">
                <xsl:value-of select="drugis:insert-measurement($studyName, $varName, arm/@name, $mmName, 'rate', rateMeasurement/@rate, xs:null)" />
                <xsl:value-of select="drugis:insert-measurement($studyName, $varName, arm/@name, $mmName, 'sample size', rateMeasurement/@sampleSize, xs:null)" />
            </xsl:when>
            <xsl:when test="categoricalMeasurement">
                <xsl:for-each select="categoricalMeasurement/category">
                    <xsl:value-of select="drugis:insert-measurement($studyName, $varName, ../../arm/@name, $mmName, @name, @rate, xs:null)" />
                </xsl:for-each>
            </xsl:when>
        </xsl:choose>
    </xsl:template> 
    
    <!-- Activities --> 
    <xsl:template match="activities/studyActivity">
        <xsl:variable name="isDrugActivity" select="boolean(activity/treatment)" />
        <xsl:variable name="studyId" select="drugis:get-study(../../@name)" />
        <xsl:variable name="activityName" select="@name" />
        INSERT INTO activities (name, study_id, type) VALUES (
            '<xsl:value-of select="@name" />',
            <xsl:value-of select="$studyId" />,
            <xsl:choose>
                <xsl:when test="$isDrugActivity">'TREATMENT'</xsl:when>
                <xsl:when test="activity/predefined">'<xsl:value-of select="activity/predefined/text()"/>'</xsl:when>
                <xsl:otherwise>'OTHER'</xsl:otherwise>
            </xsl:choose>);
        
        <xsl:for-each select="usedBy">
            INSERT INTO designs ("study_id", "arm_name", "epoch_name", "activity_name") VALUES (
                <xsl:value-of select="$studyId"/>,
                '<xsl:value-of select="@arm"/>',
                '<xsl:value-of select="@epoch"/>',
                '<xsl:value-of select="../@name" />');
        </xsl:for-each>
        
        <xsl:if test="$isDrugActivity">
            <xsl:for-each select="activity/treatment/drugTreatment">
             WITH treatment AS ( 
                INSERT INTO treatments (study_id, activity_name, drug_name, periodicity) VALUES ( 
                    <xsl:value-of select="$studyId" />,
                    '<xsl:value-of select="../../../@name" />',
                    '<xsl:value-of select="drug/@name" />',
                    '<xsl:value-of select="(fixedDose|flexibleDose)/doseUnit/@perTime" />') RETURNING id) 
                INSERT INTO treatment_dosings (treatment_id, planned_time, min_dose, max_dose, scale_modifier, unit) VALUES ( 
                    (SELECT id FROM treatment ),
                    'P0D',
                <xsl:choose>
                    <xsl:when test="fixedDose">
                        '<xsl:value-of select="fixedDose/@quantity" />',
                        '<xsl:value-of select="fixedDose/@quantity" />',
                        '<xsl:value-of select="fixedDose/doseUnit/@scaleModifier" />',
                        '<xsl:value-of select="fixedDose/doseUnit/unit/@name" />'
                    </xsl:when>
                    <xsl:when test="flexibleDose">
                        '<xsl:value-of select="flexibleDose/@minDose" />',
                        '<xsl:value-of select="flexibleDose/@maxDose" />',
                        '<xsl:value-of select="flexibleDose/doseUnit/@scaleModifier" />',
                        '<xsl:value-of select="flexibleDose/doseUnit/unit/@name" />'
                    </xsl:when>
                    <xsl:otherwise>
                        NULL, NULL, NULL, NULL
                    </xsl:otherwise>
                </xsl:choose>);
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>