<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    exclude-result-prefixes="xs"
    xmlns:drugis="http://drugis.org"
    version="3.0">
    
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
   
    
    <xsl:function name="drugis:create-note">
        <xsl:param name="hook_name" />
        <xsl:param name="notes" />
        <xsl:param name="do-close" />
        <xsl:variable name="apos">'</xsl:variable>
        <xsl:if test="boolean($notes/note)">
            INSERT INTO notes ("note_hook_id", "text", "source") VALUES 
            <xsl:for-each select="$notes/note">
                ((SELECT id FROM <xsl:value-of select="$hook_name" /> LIMIT 1), 
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

    <xsl:template match="addis-data">
        INSERT INTO code_systems (code_system, code_system_name) SELECT '<xsl:value-of select="$snomed" />', 'Systematized Nomenclature of Medicine-Clinical Terms (SNOMED CT)' WHERE '<xsl:value-of select="$snomed" />' NOT IN (SELECT code_system FROM code_systems);
        INSERT INTO code_systems (code_system, code_system_name) SELECT '<xsl:value-of select="$atc" />', 'Anatomical Therapeutic Chemical (ATC) classification' WHERE '<xsl:value-of select="$atc" />' NOT IN (SELECT code_system FROM code_systems);; 
        <xsl:apply-templates select="units/unit" />
        <xsl:apply-templates select="indications/indication" />
        <xsl:apply-templates select="drugs/drug" />
        <xsl:apply-templates select="endpoints/endpoint|populationCharacteristics/populationCharacteristic|adverseEvents/adverseEvent"></xsl:apply-templates>
        <xsl:apply-templates select="studies/study"></xsl:apply-templates>
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
        INSERT INTO variables (name, description, type, direction, measurement_type, unit) SELECT  
            '<xsl:value-of select="@name" />',
            '<xsl:value-of select="@description" />',
            '<xsl:value-of select="concat(upper-case(substring(name(.), 1, 1)), substring(name(), 2))" />', 
            <xsl:value-of select="drugis:null-or-value(@direction)" />,
            <xsl:choose>
                <xsl:when test="continuous">'CONTINUOUS',
                    '<xsl:value-of select="continuous/@unitOfMeasurement" />'
                </xsl:when>
                <xsl:when test="rate">'RATE', NULL</xsl:when>
                <xsl:when test="categorical">'CATEGORICAL', NULL</xsl:when>
            </xsl:choose> WHERE '<xsl:value-of select="@name" />' NOT IN (SELECT name FROM variables);
        <xsl:apply-templates select="categorical/category"/>
    </xsl:template>
    
    <xsl:template match="categorical/category">
        INSERT INTO variable_categories (variable_name, category_name) VALUES ('<xsl:value-of select="../../@name" />', '<xsl:value-of select="text()" />');
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
        )
        INSERT INTO studies (
        name, note_hook, blinding_type_note_hook, title, title_note_hook, indication, 
        allocation_type_note_hook, objective, allocation_type, blinding_type,
        number_of_centers, created_at, source, exclusion, inclusion, status, end_date, start_date) VALUES ( 
            '<xsl:value-of select="@name" />',
            (SELECT id FROM note_hook LIMIT 1),
            (SELECT id FROM blinding_type_note_hook LIMIT 1),
            <xsl:value-of select="drugis:null-or-value(drugis:escape-apostrophe(characteristics/title/value/text()))" />,
            (SELECT id FROM title_note_hook LIMIT 1),
            '<xsl:value-of select="indication/@name" />',
            (SELECT id FROM allocation_type_note_hook LIMIT 1),
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
            <xsl:value-of select="drugis:null-or-value(characteristics/study_start/text())" />);

        <xsl:apply-templates select="characteristics/references" />
        <xsl:apply-templates select="arms/arm" />
        <xsl:apply-templates select="epochs/epoch" />
        <xsl:apply-templates select="measurements/measurement" />
        <xsl:apply-templates select="activities/studyActivity" />
        
        <xsl:variable name="study_note">(SELECT note_hook FROM studies WHERE name = '<xsl:value-of select="@name" />)</xsl:variable>
        <xsl:value-of select="drugis:create-note($study_note, notes, true())" />
        
        <xsl:variable name="blinding_type_note">(SELECT blinding_type_note_hook FROM studies WHERE name = '<xsl:value-of select="@name" />)</xsl:variable>
        <xsl:value-of select="drugis:create-note($blinding_type_note, characteristics/blinding/notes, true())" />
        
        <xsl:variable name="title_note">(SELECT title_note_hook FROM studies WHERE name = '<xsl:value-of select="@name" />)</xsl:variable>
        <xsl:value-of select="drugis:create-note($title_note, characteristics/title/notes, true())" />
        
        <xsl:variable name="allocation_type_note">(SELECT allocation_type_note_hook FROM studies WHERE name = '<xsl:value-of select="@name" />)</xsl:variable>
        <xsl:value-of select="drugis:create-note($allocation_type_note, characteristics/allocation/notes, true())" />
    </xsl:template>
    
    <xsl:template match="characteristics/references">
        <xsl:for-each select="pubMedId">
            INSERT INTO study_references (study_name, id) VALUES ( 
               '<xsl:value-of select="../../../@name" />',
               '<xsl:value-of select="text()" />');
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="arms/arm">
        WITH arm_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), notes AS (
            <xsl:value-of select="drugis:create-note('arm_note_hook', notes, false())" />
        )
        INSERT INTO arms (study_name, arm_name, arm_size, note_hook) VALUES (
            '<xsl:value-of select="../../@name" />',
            '<xsl:value-of select="@name" />',
            '<xsl:value-of select="@size" />',
            (SELECT id FROM arm_note_hook LIMIT 1));
    </xsl:template>

   <xsl:template match="epochs/epoch">
        WITH epoch_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), notes AS (
            <xsl:value-of select="drugis:create-note('epoch_note_hook', notes, false())" />
        )
        INSERT INTO epochs (study_name, epoch_name, duration, note_hook) VALUES ( 
            '<xsl:value-of select="../../@name" />',
            '<xsl:value-of select="@name" />', 
            <xsl:value-of select="drugis:null-or-value(duration/text())" />,
            (SELECT id FROM epoch_note_hook LIMIT 1));
    </xsl:template>
    
    <xsl:template match="measurements/measurement">
        <xsl:variable name="studyOutcomeId" select="studyOutcomeMeasure/@id" />
        <xsl:variable name="studyOutcome" select="../../studyOutcomeMeasures/studyOutcomeMeasure[@id=$studyOutcomeId]" />
        <xsl:variable name="studyName" select="../../@name" />
        <xsl:variable name="variableName" select="($studyOutcome/populationCharacteristic|$studyOutcome/adverseEvent|$studyOutcome/endpoint)/@name" />
        WITH variable AS ( 
            SELECT id FROM variables WHERE name = '<xsl:value-of select="$variableName" />'
        ), arm AS ( 
            SELECT id FROM arms WHERE study_name = '<xsl:value-of select="$studyName" />' AND arm_name = '<xsl:value-of select="arm/@name" />' 
        ), epoch AS ( 
            SELECT id FROM epochs WHERE study_name = '<xsl:value-of select="$studyName" />' AND epoch_name = '<xsl:value-of select="whenTaken/epoch/@name" />' 
        ),  measurement_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
        ), notes AS ( 
            <xsl:value-of select="drugis:create-note('epoch_note_hook', studyOutcome/notes, false())" />
        ) INSERT INTO measurements ("variable_id", "study_name", "arm_id", "epoch_id", "primary", "offset_from_epoch", "before_epoch", "note_hook") VALUES ( 
            (SELECT id FROM variable LIMIT 1),
            '<xsl:value-of select="$studyName" />',
            (SELECT id FROM arm LIMIT 1),
            (SELECT id FROM epoch LIMIT 1),
            <xsl:value-of select="$studyOutcome/@primary" />,
            '<xsl:value-of select="whenTaken/@howLong" />',
            <xsl:choose>
                <xsl:when test="boolean(whenTaken/@relativeTo = 'BEFORE_EPOCH_END')">true</xsl:when>
                <xsl:when test="boolean(whenTaken/@relativeTo = 'FROM_EPOCH_START')">false</xsl:when>
                <xsl:otherwise>NULL</xsl:otherwise>
            </xsl:choose>,
            (SELECT id FROM measurement_note_hook LIMIT 1)); 
    
        <xsl:choose>
            <xsl:when test="continuousMeasurement">
                WITH result AS ( 
                    INSERT INTO measurement_results (std_dev, mean, sample_size) VALUES (
                        <xsl:value-of select="drugis:null-or-value(string(continuousMeasurement/@stdDev))" />,
                        <xsl:value-of select="drugis:null-or-value(string(continuousMeasurement/@mean))" />,
                        <xsl:value-of select="drugis:null-or-value(string(continuousMeasurement/@sampleSize))" />
                    ) RETURNING id
                ), measurement AS (
                    <xsl:value-of select="drugis:select-measurement($studyName, arm/@name, whenTaken/epoch/@name, $variableName)" />
                )
                INSERT INTO measurements_results (measurement_id, result_id) VALUES ( 
                    (SELECT id FROM measurement LIMIT 1), 
                    (SELECT id FROM result LIMIT 1));
            </xsl:when>
            <xsl:when test="rateMeasurement">
                WITH result AS ( 
                    INSERT INTO measurement_results (rate, sample_size) VALUES (
                    <xsl:value-of select="drugis:null-or-value(string(rateMeasurement/@rate))" />,
                    <xsl:value-of select="drugis:null-or-value(string(rateMeasurement/@sampleSize))" />
                    ) RETURNING id
                ), measurement AS (
                    <xsl:value-of select="drugis:select-measurement($studyName, arm/@name, whenTaken/epoch/@name, $variableName)" />
                )
                INSERT INTO measurements_results (measurement_id, result_id) VALUES ( 
                    (SELECT id FROM measurement LIMIT 1), 
                    (SELECT id FROM result LIMIT 1));
            </xsl:when>
            <xsl:when test="categoricalMeasurement">
                <xsl:for-each select="categoricalMeasurement/category">
                    WITH result AS ( 
                        INSERT INTO measurement_results (rate) VALUES (
                            <xsl:value-of select="drugis:null-or-value(string(@rate))" />
                        ) RETURNING id
                    ), measurement AS (
                        <xsl:value-of select="drugis:select-measurement($studyName, ../../arm/@name, ../../whenTaken/epoch/@name, $variableName)" />
                    )
                    INSERT INTO measurements_results (measurement_id, result_id, category_id) VALUES ( 
                        (SELECT id FROM measurement LIMIT 1), 
                        (SELECT id FROM result LIMIT 1),
                        (SELECT id FROM variable_categories 
                            WHERE variable_name = '<xsl:value-of select="$variableName"/>' AND category_name = '<xsl:value-of select="@name" />' LIMIT 1));
                </xsl:for-each>
            </xsl:when>
        </xsl:choose>
    </xsl:template> 
    
    <xsl:function name="drugis:select-measurement">
        <xsl:param name="study"/>
        <xsl:param name="arm"/>
        <xsl:param name="epoch"/>
        <xsl:param name="variable" />
        SELECT id FROM measurements WHERE 
                variable_id = (SELECT id FROM variables WHERE name = '<xsl:value-of select="$variable" />')
            AND arm_id = (SELECT id FROM arms WHERE study_name = '<xsl:value-of select="$study" />' AND arm_name = '<xsl:value-of select="$arm" />')
            AND study_name = '<xsl:value-of select="$study" />' 
            AND epoch_id = (SELECT id FROM epochs WHERE study_name = '<xsl:value-of select="$study" />' AND epoch_name = '<xsl:value-of select="$epoch" />')
    </xsl:function>
        
    <xsl:template match="activities/studyActivity">
        <xsl:variable name="isDrugActivity" select="boolean(activity/treatment)" />
        <xsl:variable name="studyName" select="../../@name" />
        <xsl:variable name="activityName" select="@name" />
        WITH activity AS (
            INSERT INTO activities (name, study_name, type) VALUES (
                '<xsl:value-of select="@name" />',
                '<xsl:value-of select="$studyName" />',
                <xsl:choose>
                    <xsl:when test="$isDrugActivity">'TREATMENT'</xsl:when>
                    <xsl:when test="activity/predefined">'<xsl:value-of select="activity/predefined/text()"/>'</xsl:when>
                    <xsl:otherwise>'OTHER'</xsl:otherwise>
                </xsl:choose> ) RETURNING id)
        INSERT INTO designs ("arm_id", "epoch_id", "activity_id") VALUES 
        
        <xsl:for-each select="usedBy">
            ((SELECT id FROM arms WHERE study_name = '<xsl:value-of select="$studyName" />' AND arm_name = '<xsl:value-of select="@arm" />'),
            (SELECT id FROM epochs WHERE study_name = '<xsl:value-of select="$studyName" />' AND epoch_name = '<xsl:value-of select="@epoch" />'),
            (SELECT id FROM activity LIMIT 1))
            <xsl:if test="position() != last()">,</xsl:if>
        </xsl:for-each>;
        
        <xsl:if test="$isDrugActivity">
            <xsl:for-each select="activity/treatment/drugTreatment">
             WITH treatment AS ( 
                INSERT INTO treatments (activity_id, drug_name, periodicity) VALUES ( 
                    (SELECT id FROM activities WHERE study_name = '<xsl:value-of select="$studyName" />' AND name = '<xsl:value-of select="$activityName" />'),
                    '<xsl:value-of select="drug/@name" />',
                    '<xsl:value-of select="(fixedDose|flexibleDose)/doseUnit/@perTime" />') RETURNING id) 
                INSERT INTO treatment_dosings (treatment_id, planned_time, min_dose, max_dose, scale_modifier, unit) VALUES ( 
                    (SELECT id FROM treatment LIMIT 1),
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
                        '<xsl:value-of select="fixedDose/doseUnit/unit/@name" />'
                    </xsl:when>
                    <xsl:otherwise>
                        NULL, NULL, NULL, NULL
                    </xsl:otherwise>
                </xsl:choose>);
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>