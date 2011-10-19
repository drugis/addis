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
    <xsl:template match="study/studyOutcomeMeasures/studyOutcomeMeasure">
        <xsl:copy>
            <xsl:apply-templates select="node()[name() != 'notes']|@*"/>
            <whenTaken relativeTo="BEFORE_EPOCH_END" howLong="P0D">
                <xsl:element name="epoch">
                    <xsl:attribute name="name">
                        <xsl:value-of select="../../epochs/epoch[last()]/@name"/>
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