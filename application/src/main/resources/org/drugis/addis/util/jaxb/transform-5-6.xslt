<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tf="http://nonexistentdomainforfunctions.com"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="http://drugis.org/files/addis-6.xsd"
	exclude-result-prefixes="xs tf" version="2.0">
	<xsl:output indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	 
	<xsl:template match="addis-data">
		<xsl:copy>
			<xsl:apply-templates select="units|indications|drugs"/>
			<xsl:element name="treatmentCategorizations" />
			<xsl:apply-templates select="endpoints|adverseEvents|populationCharacteristics|studies|metaAnalyses|benefitRiskAnalyses"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="metaAnalyses/*/alternative">
		<xsl:copy>
			<treatmentDefinition>
				<xsl:for-each select="drugs/*">
					<trivialCategory>
						<xsl:attribute name="drug">
							<xsl:value-of select="@name"></xsl:value-of>
						</xsl:attribute>
					</trivialCategory>
				</xsl:for-each>
			</treatmentDefinition>
			<xsl:apply-templates select="arms"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="consistencyResults//alternative|inconsistencyResults//alternative|nodeSplitResults//alternative|metaBenefitRiskAnalysis/alternatives/alternative">
		<treatmentDefinition>
			<xsl:for-each select="*">
				<trivialCategory>
					<xsl:attribute name="drug">
						<xsl:value-of select="@name"></xsl:value-of>
					</xsl:attribute>
				</trivialCategory>
			</xsl:for-each>
		</treatmentDefinition>
	</xsl:template>
	
	<xsl:template match="metaBenefitRiskAnalysis/baseline">
		<xsl:copy>
			<treatmentDefinition>
				<xsl:for-each select="*">
					<trivialCategory>
						<xsl:attribute name="drug">
							<xsl:value-of select="@name"></xsl:value-of>
						</xsl:attribute>
					</trivialCategory>
				</xsl:for-each>
			</treatmentDefinition>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
