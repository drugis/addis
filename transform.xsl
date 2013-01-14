<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    exclude-result-prefixes="xs"
    xmlns:drugis="http://drugis.org"
    version="3.0">
    
    <xsl:output method="text" indent="no"/>
    <xsl:strip-space elements="*"/>
    <xsl:import-schema schema-location="addis-6.xsd" />
    
    <xsl:variable name="snomed">2.16.840.1.113883.6.96</xsl:variable>
    <xsl:variable name="atc">2.16.840.1.113883.6.73</xsl:variable>
    
    <xsl:function name="drugis:null-or-value">
        <xsl:param name="value" />
        <xsl:choose>
            <xsl:when test="$value != ''"> '<xsl:value-of select="$value" />'</xsl:when>
            <xsl:otherwise>NULL</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:template match="addis-data">
        INSERT INTO code_systems (code_system, code_system_name) VALUES ('<xsl:value-of select="$snomed" />', 'Systematized Nomenclature of Medicine-Clinical Terms (SNOMED CT)');
        INSERT INTO code_systems (code_system, code_system_name) VALUES ('<xsl:value-of select="$atc" />', 'Anatomical Therapeutic Chemical (ATC) classification');
        <xsl:apply-templates select="indications/indication" />
        <xsl:apply-templates select="drugs/drug" />
        <xsl:apply-templates select="endpoints/endpoint|populationCharacteristics/populationCharacteristic|adverseEvents/adverseEvent"></xsl:apply-templates>
        <xsl:apply-templates select="studies/study"></xsl:apply-templates>
    </xsl:template>
   
    <xsl:template match="indications/indication">
        INSERT INTO indications (name, code, code_system) VALUES (
            '<xsl:value-of select="@name"></xsl:value-of>',
            '<xsl:value-of select="@code"></xsl:value-of>',
            '<xsl:value-of select="$snomed"></xsl:value-of>');
    </xsl:template>
    
    <xsl:template match="drugs/drug">
        INSERT INTO drugs (name, code, code_system) VALUES (
            '<xsl:value-of select="@name"></xsl:value-of>',
            '<xsl:value-of select="@atcCode"></xsl:value-of>',
            '<xsl:value-of select="$atc"></xsl:value-of>');
    </xsl:template>

    <xsl:template match="endpoints/endpoint|populationCharacteristics/populationCharacteristic|adverseEvents/adverseEvent">
        <xsl:if test="continuous/@unitOfMeasurement">
            INSERT INTO units (name) SELECT ('<xsl:value-of select="continuous/@unitOfMeasurement"/>') WHERE NOT EXISTS (SELECT name FROM units WHERE name = '<xsl:value-of select="continuous/@unitOfMeasurement"/>');
        </xsl:if>
        
        INSERT INTO variables (name, description, type, direction, measurement_type, unit) VALUES ( 
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
            </xsl:choose>);
        <xsl:apply-templates select="categorical/category"/>
    </xsl:template>
    
    <xsl:template match="categorical/category">
        INSERT INTO variable_categories (variable_name, category_name) VALUES ('<xsl:value-of select="../../@name"></xsl:value-of>', '<xsl:value-of select="text()"></xsl:value-of>');
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
        name, 
        note_hook, 
        blinding_type_note_hook, 
        title, 
        title_note_hook, 
        indication, 
        allocation_type_note_hook, 
        objective, 
        allocation_type,
        blinding_type,
        number_of_centers,
        created_at,
        source,
        exclusion,
        inclusion,
        status,
        end_date,
        start_date) VALUES ( 
            '<xsl:value-of select="@name" />',
            (SELECT id FROM note_hook LIMIT 1),
            (SELECT id FROM blinding_type_note_hook LIMIT 1),
            '<xsl:value-of select="characteristics/title/value/text()" />',
            (SELECT id FROM title_note_hook LIMIT 1),
            '<xsl:value-of select="indication/@name" />',
            (SELECT id FROM allocation_type_note_hook LIMIT 1),
            <xsl:value-of select="drugis:null-or-value(characteristics/objective/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/allocation/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/blinding/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/centers/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/creation_date/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/source/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/exclusion/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/inclusion/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/status/value/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/study_end/text())" />,
            <xsl:value-of select="drugis:null-or-value(characteristics/study_start/text())" />);
        <xsl:apply-templates select="characteristics/references" />
        <xsl:apply-templates select="arms/arm" />
        <xsl:apply-templates select="epochs/epoch" />
        <xsl:apply-templates select="measurements/measurement" />
    </xsl:template>
    
    <xsl:template match="characteristics/references">
        <xsl:for-each select="pubMedId">
        INSERT INTO study_references (study_name, id) VALUES ( 
           '<xsl:value-of select="../../../@name" />',
           '<xsl:value-of select="text()" />);
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="arms/arm">
        WITH arm_note_hook AS ( 
            INSERT INTO note_hooks (id) VALUES (DEFAULT) RETURNING id
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
        )
        INSERT INTO epochs (study_name, epoch_name, duration, note_hook) VALUES ( 
            '<xsl:value-of select="../../@name" />',
            '<xsl:value-of select="@name" />', 
            '<xsl:value-of select="duration/text()" />',
            (SELECT id FROM arm_note_hook LIMIT 1));
    </xsl:template>
    
    <xsl:template match="measurements/measurement">
        <xsl:variable name="studyOutcomeId" select="studyOutcomeMeasure/@id" />
        <xsl:variable name="studyOutcome" select="../../studyOutcomeMeasures/studyOutcomeMeasure[@id=$studyOutcomeId]" />
        
        INSERT INTO measurements (variable_id, study_name, arm_id, epoch_id, primary, offset_from_epoch, note_hook) VALUES ( 
            <xsl:value-of select="$studyOutcome/@primary" />
        );
    </xsl:template>
    
</xsl:stylesheet>