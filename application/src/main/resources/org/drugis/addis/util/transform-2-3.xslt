<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tf="http://nonexistentdomainforfunctions.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-3.xsd"
    exclude-result-prefixes="xs tf" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:function name="tf:getRelativeTo">
        <xsl:param name="study"/>
        <xsl:param name="studyOutcomeMeasureId"/>
        <xsl:variable name="som" select="$study/studyOutcomeMeasures/studyOutcomeMeasure[@id=$studyOutcomeMeasureId]"/>
        <xsl:choose>
            <xsl:when test="$som/populationCharacteristic">FROM_EPOCH_START</xsl:when>
            <xsl:otherwise>BEFORE_EPOCH_END</xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="tf:getTreatmentEpochName">
        <xsl:param name="study"/>
        <xsl:variable name="treatment" select="$study/activities/studyActivity/activity/treatment"/>
        <xsl:variable name="matchingEpochNames" select="$treatment/../../usedBy/@epoch" />
        <xsl:variable name="epochs" select="$study/epochs/epoch[@name = $matchingEpochNames]/@name"/>
        <xsl:choose>
            <xsl:when test="$treatment">
                <xsl:value-of select="$epochs[1]"/>
                    <!-- $treatment[last()]/../../usedBy/@epoch"/> -->
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$study/epochs/epoch[last()]/@name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:template match="/addis-data">
        <addis-data xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-3.xsd">
            <units>
                <unit name="gram" symbol="g"/>
                <unit name="liter" symbol="l"/>
            </units>
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
    
    <xsl:template match="activity/treatment|activity/combinationTreatment">
        <treatment>
            <xsl:for-each select="./treatment|../treatment">
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
            </xsl:for-each>
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
    
    <xsl:template match="study/measurements/measurement">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
            <whenTaken howLong="P0D">
                <xsl:attribute name="relativeTo">
                    <xsl:value-of select="tf:getRelativeTo(../.., studyOutcomeMeasure/@id)"/>
                </xsl:attribute>
                <xsl:element name="epoch">
                    <xsl:attribute name="name">
                        <xsl:value-of select="tf:getTreatmentEpochName(../..)"/>
                    </xsl:attribute>
                </xsl:element>
            </whenTaken>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="study/studyOutcomeMeasures/studyOutcomeMeasure">
        <xsl:copy>
            <xsl:apply-templates select="node()[name() != 'notes']|@*"/>
            <whenTaken howLong="P0D">
                <xsl:attribute name="relativeTo">
                    <xsl:value-of select="tf:getRelativeTo(../.., @id)"/>
                </xsl:attribute>
                <xsl:element name="epoch">
                    <xsl:attribute name="name">
                        <xsl:value-of select="tf:getTreatmentEpochName(../..)"/>
                    </xsl:attribute>
                </xsl:element>
            </whenTaken>
            <notes>
                <xsl:for-each select="notes/note">
                    <xsl:copy>
                        <xsl:apply-templates select="node()|@*"/>
                    </xsl:copy>
                </xsl:for-each>
            </notes>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
