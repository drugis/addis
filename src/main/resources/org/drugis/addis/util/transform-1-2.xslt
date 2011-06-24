<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tf="http://example.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-2.xsd"
    exclude-result-prefixes="xs tf" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="addis-data/studies/study/arms/*">
        <arm>
            <xsl:attribute name="size">
                <xsl:value-of select="@size"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="concat(drug/@name, &quot;-&quot;, @id)"/>
            </xsl:attribute>
            <notes />
        </arm>
    </xsl:template>

    <xsl:template match="measurement">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:function name="tf:getArmName">
        <xsl:param name="root"/>
        <xsl:param name="studyName"/>
        <xsl:param name="armId"/>
        <xsl:variable name="armDrug" select="$root/addis-data/studies/study[@name=$studyName]/arms/arm[@id=$armId]/drug/@name"/>
        <xsl:value-of select="concat($armDrug, '-', $armId)"/>
    </xsl:function>

    <xsl:template match="measurements/measurement/arm">
        <xsl:variable name="armName" select="tf:getArmName(/, ../../../@name, @id)"/>
        <arm>
            <xsl:attribute name="name">
                <xsl:value-of select="$armName"/>
            </xsl:attribute>
        </arm>
    </xsl:template>
    
    <xsl:template match="study/studyOutcomeMeasures/studyOutcomeMeasure">
        <xsl:copy>
            <xsl:attribute name="primary">false</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="alternative/arms/arm | studyBenefitRiskAnalysis/arms/arm">
        <xsl:variable name="armName" select="tf:getArmName(/, @study, @id)"/>
        <arm>
            <xsl:attribute name="study">
                <xsl:value-of select="@study"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="$armName"/>
            </xsl:attribute>
        </arm>
    </xsl:template>
    
    <xsl:template match="studies/study/notes">
        <epochs>
            <xsl:if test="../characteristics/allocation/value = 'RANDOMIZED'">
                <epoch name="Randomization">
                    <notes/>
                </epoch>
            </xsl:if>
            <epoch name="Main phase">
                <notes/>
            </epoch>
        </epochs>
        <activities>
            <xsl:if test="../characteristics/allocation/value = 'RANDOMIZED'">
                <studyActivity>
                    <xsl:attribute name="name">Randomization</xsl:attribute>
                    <activity>
                        <predefined>RANDOMIZATION</predefined>
                    </activity>
                    <xsl:for-each select="../arms/arm">
                        <xsl:variable name="armName" select="tf:getArmName(/, ../../@name, @id)"/>
                        <usedBy>
                            <xsl:attribute name="epoch">Randomization</xsl:attribute>
                            <xsl:attribute name="arm">
                                <xsl:value-of select="$armName"/>
                            </xsl:attribute>
                        </usedBy>
                    </xsl:for-each>
                    <notes/>
                </studyActivity>
            </xsl:if>

            <xsl:for-each select="../arms/arm">
                <xsl:variable name="armName" select="tf:getArmName(/, ../../@name, @id)"/>
                <studyActivity>
                    <xsl:attribute name="name">
                        <xsl:value-of select="$armName"/>
                    </xsl:attribute>
                    <activity>
                        <treatment>
                            <xsl:apply-templates select="flexibleDose|fixedDose|drug"/>
                        </treatment>
                    </activity>
                    <usedBy>
                        <xsl:attribute name="epoch">Main phase</xsl:attribute>
                        <xsl:attribute name="arm">
                            <xsl:value-of select="$armName"/>
                        </xsl:attribute>
                    </usedBy>
                    <notes/>
                </studyActivity>
            </xsl:for-each>
        </activities>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
