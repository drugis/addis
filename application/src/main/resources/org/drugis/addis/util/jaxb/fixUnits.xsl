<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tf="http://example.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-3.xsd"
    exclude-result-prefixes="xs tf" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>    
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="drugTreatment">
        <drugTreatment>
            <xsl:for-each select="./flexibleDose">
                <flexibleDose>
                    <xsl:attribute name="minDose">
                        <xsl:value-of select="@minDose"/>
                    </xsl:attribute>
                    <xsl:attribute name="maxDose">
                        <xsl:value-of select="@maxDose"/>
                    </xsl:attribute>
                    <doseUnit scaleModifier="MILLI" perTime="P1D">
                        <unit name="gram"/>
                    </doseUnit>
                </flexibleDose>
            </xsl:for-each>
            <xsl:for-each select="./fixedDose">
                <fixedDose>
                    <xsl:attribute name="quantity">
                        <xsl:value-of select="@quantity"/>
                    </xsl:attribute>
                    <doseUnit scaleModifier="MILLI" perTime="P1D">
                        <unit name="gram"/>
                    </doseUnit>
                </fixedDose>
            </xsl:for-each>
            <xsl:for-each select="./drug">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                </xsl:copy>
            </xsl:for-each>
        </drugTreatment>
    </xsl:template>
</xsl:stylesheet>