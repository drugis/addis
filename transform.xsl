<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    exclude-result-prefixes="xs"
    version="3.0">
    
    <xsl:output method="text" indent="no"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:variable name="snomed">2.16.840.1.113883.6.96</xsl:variable>
    <xsl:variable name="atc">2.16.840.1.113883.6.73</xsl:variable>
    
    <xsl:template match="addis-data">
        BEGIN; 
        INSERT INTO code_systems (code_system, code_system_name) VALUES ('<xsl:value-of select="$snomed" />', 'Systematized Nomenclature of Medicine-Clinical Terms (SNOMED CT)');
        INSERT INTO code_systems (code_system, code_system_name) VALUES ('<xsl:value-of select="$atc" />', 'Anatomical Therapeutic Chemical (ATC) classification');
        <xsl:apply-templates select="indications/indication" />
        <xsl:apply-templates select="drugs/drug" />
        <xsl:apply-templates select="endpoints/endpoint|populationCharacteristics/populationCharacteristic|adverseEvents/adverseEvent"></xsl:apply-templates>
        <xsl:apply-templates select="studies/study"></xsl:apply-templates>
        COMMIT;
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
            '<xsl:value-of select="@name"></xsl:value-of>',
            '<xsl:value-of select="@description"></xsl:value-of>',
            '<xsl:value-of select="concat(upper-case(substring(name(.), 1, 1)), substring(name(), 2))"></xsl:value-of>', 
            <xsl:choose>
                <xsl:when test="@direction != ''"> '<xsl:value-of select="@direction"></xsl:value-of>',</xsl:when>
                <xsl:otherwise>NULL,</xsl:otherwise>
                
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="continuous">'CONTINUOUS',
                    '<xsl:value-of select="continuous/@unitOfMeasurement" />'
                </xsl:when>
                <xsl:when test="rate">'RATE', NULL</xsl:when>
                <xsl:when test="categorical">'CATEGORICAL', NULL</xsl:when>
            </xsl:choose>);

        <xsl:apply-templates select="categorical/category" ></xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="categorical/category">
        INSERT INTO variable_categories (variable_name, category_name) VALUES ('<xsl:value-of select="../../@name"></xsl:value-of>', '<xsl:value-of select="text()"></xsl:value-of>');
    </xsl:template>
    
    <xsl:template match="studies/study">
   
    </xsl:template>
    
</xsl:stylesheet>