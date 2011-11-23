<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tf="http://nonexistentdomainforfunctions.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-4.xsd"
    exclude-result-prefixes="xs tf" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>    
    
    <xsl:template match="/addis-data">
        <addis-data xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-4.xsd">
            <xsl:for-each select="./*">
                <xsl:copy>
                    <xsl:apply-templates select="node()"/>
                </xsl:copy>
            </xsl:for-each>
        </addis-data>
    </xsl:template>
    
    <xsl:template match="benefitRiskAnalyses/studyBenefitRiskAnalysis">
        <xsl:copy>
            <xsl:apply-templates select="@*|indication|study"/>
            <baseline>
                <xsl:element name="arm">
                    <xsl:attribute name="study">
                        <xsl:value-of select="arms/arm[1]/@study"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:value-of select="arms/arm[1]/@name"/>
                    </xsl:attribute>
                </xsl:element>
            </baseline>
            <xsl:apply-templates select="arms|outcomeMeasures"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
