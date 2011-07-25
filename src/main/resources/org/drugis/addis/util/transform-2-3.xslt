<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tf="http://example.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-3.xsd"
    exclude-result-prefixes="xs tf" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/addis-data">
        <addis-data xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-3.xsd">
            <xsl:for-each select="./*">
                <xsl:copy>
                    <xsl:apply-templates select="node()"/>
                </xsl:copy>
            </xsl:for-each>
        </addis-data>
    </xsl:template>
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="alternative/drug">
        <drugs>
            <drug>
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </drug>
        </drugs>
    </xsl:template>
    <xsl:template match="activity/treatment">
        <treatment>
            <drugTreatment>
                <xsl:for-each select="child::node()">
                    <xsl:copy>
                        <xsl:apply-templates select="@*"/>
                    </xsl:copy>
                </xsl:for-each>
            </drugTreatment>
        </treatment>
    </xsl:template>
    <xsl:template match="metaBenefitRiskAnalysis/baseline">
        <baseline>
            <drug>
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/> 
                </xsl:attribute>
            </drug>
        </baseline>
    </xsl:template>
    <xsl:template match="metaBenefitRiskAnalysis/drugs">
        <alternatives>
            <xsl:for-each select="child::node()">
                <alternative>
                    <drug>
                        <xsl:attribute name="name">
                            <xsl:value-of select="@name"/> 
                        </xsl:attribute>
                    </drug>
                </alternative>
            </xsl:for-each>
        </alternatives>
    </xsl:template>
    <xsl:template match="addis-data/endpoints/endpoint|addis-data/adverseEvents/adverseEvent">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="direction">
                <xsl:value-of select="child::node()/@direction"/>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="continuous|rate">
        <xsl:copy>
            <xsl:apply-templates select="@*[not(name()='direction')]"/>
       </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
