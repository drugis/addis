<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"/>
	<xsl:template match="/">
		<addis-data xsi:noNamespaceSchemaLocation="./ADDIS-Schema.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<xsl:apply-templates/>
		</addis-data>
	</xsl:template>

	<xsl:template match="/addis-data/indications">
		<indications>
			<xsl:for-each select="indication">
				<indication>
					<xsl:attribute name="code">
						<xsl:value-of select="@code"/>
					</xsl:attribute>
					<xsl:attribute name="name">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
				</indication>
			</xsl:for-each>
		</indications>
	</xsl:template>
	<xsl:template match="/addis-data//endpoints">
		<endpoints>
			<xsl:for-each select="endpoint">
				<endpoint>
					<xsl:call-template name="outcomeMeasure"/>
				</endpoint>
			</xsl:for-each>
		</endpoints>
	</xsl:template>
	<xsl:template match="/addis-data/adverseEvents">
		<adverseEvents>
			<xsl:for-each select="adverseEvent">
				<adverseEvent>
					<xsl:call-template name="outcomeMeasure"/>
				</adverseEvent>
			</xsl:for-each>
		</adverseEvents>
	</xsl:template>
	<xsl:template name="outcomeMeasure">
		<xsl:attribute name="name">
			<xsl:value-of select="@name"/>
		</xsl:attribute>
		<xsl:attribute name="description">
			<xsl:value-of select="@description"/>
		</xsl:attribute>
		<xsl:choose>
			<xsl:when test="type/@value=&quot;RATE&quot;">
				<xsl:element name="rate">
					<xsl:attribute name="direction">
						<xsl:value-of select="direction/@value"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:when test="type/@value=&quot;CONTINUOUS&quot;">
				<xsl:element name="continuous">
					<xsl:attribute name="direction">
						<xsl:value-of select="direction/@value"/>
					</xsl:attribute>
					<xsl:attribute name="unitOfMeasurement">
						<xsl:value-of select="@unitOfMeasurement"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
