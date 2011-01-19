<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
	<xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
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
	<xsl:template match="/addis-data/endpoints">
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
	<xsl:template match="/addis-data/drugs">
		<drugs>
			<xsl:for-each select="drug">
				<drug>
					<xsl:attribute name="name">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<xsl:attribute name="atcCode">
						<xsl:value-of select="@atcCode"/>
					</xsl:attribute>
				</drug>
			</xsl:for-each>
		</drugs>
	</xsl:template>
	<xsl:template match="addis-data/populationCharacteristics">
		<populationCharacteristics>
			<xsl:for-each select="continuousCharacteristic">
				<xsl:element name="populationCharacteristic">
					<xsl:attribute name="name">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<xsl:attribute name="description">
						<xsl:value-of select="@description"/>
					</xsl:attribute>
					<xsl:element name="continuous">
						<xsl:attribute name="unitOfMeasurement">
							<xsl:value-of select="@unitOfMeasurement"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="rateCharacteristic">
				<xsl:element name="populationCharacteristic">
					<xsl:attribute name="name">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<xsl:attribute name="description">
						<xsl:value-of select="@description"/>
					</xsl:attribute>
					<xsl:element name="rate"/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="categoricalCharacteristic">
				<xsl:element name="populationCharacteristic">
					<xsl:attribute name="name">
						<xsl:value-of select="@name"/>
					</xsl:attribute>
					<xsl:attribute name="description">
						<xsl:value-of select="@description"/>
					</xsl:attribute>
					<xsl:for-each select="categories/string">
						<xsl:element name="category">
							<xsl:value-of select="@value"/>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:for-each>
		</populationCharacteristics>
	</xsl:template>
	<xsl:template match="addis-data/studies">
		<studies>
			<xsl:for-each select="study">
				<xsl:element name="study">
					<xsl:attribute name="name">
						<xsl:value-of select="@studyId"/>
					</xsl:attribute>
					<xsl:element name="indication">
						<xsl:attribute name="name">
							<xsl:variable name="id" select="indication/@ref"/>
							<xsl:value-of select="/addis-data/indications/indication[@id=$id]/@name"/>
						</xsl:attribute>
						<notes>
							<note source="MANUAL"/>
						</notes>
					</xsl:element>
					<characteristics>
						<xsl:for-each select="characteristics/*[not(self::PUBMED)]">
							<xsl:element name="{translate(local-name(), $ucletters, $lcletters)}">
								<value>
									<xsl:value-of select="@value"/>
								</value>
								<notes>
									<note source="MANUAL"/>
								</notes>
							</xsl:element>
						</xsl:for-each>
						<references>
							<xsl:for-each select="characteristics/PUBMED/pubMedId">
								<xsl:element name="pubMedId">
									<xsl:value-of select="@value"/>
								</xsl:element>
							</xsl:for-each>
						</references>
					</characteristics>
					<studyOutcomeMeasures>
						<xsl:for-each select="endpoints/endpoint|adverseEvents/adverseEvent|populationCharacteristics/populationCharacteristic">
							<xsl:element name="studyOutcomeMeasure">
								<xsl:variable name="id" select="@ref"/>
								<xsl:variable name="tmpname" select="(//populationCharacteristic[@id=$id] | //endpoint[@id=$id] | //adverseEvent[@id=$id])/@name"/>
								<xsl:attribute name="name">
									<xsl:value-of select="$tmpname"/>
								</xsl:attribute>
								<xsl:element name="{name()}">
									<xsl:attribute name="name">
										<xsl:value-of select="$tmpname"/>
									</xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</studyOutcomeMeasures>
					<arms>
						<xsl:for-each select="arms/arm">
							<xsl:element name="arm">
								<xsl:attribute name="id">
									<xsl:value-of select="@id"/>
								</xsl:attribute>
								<xsl:attribute name="size">
									<xsl:value-of select="@size"/>
								</xsl:attribute>
								<xsl:choose>
									<xsl:when test="dose/@class=&quot;fixedDose&quot;">
										<xsl:element name="fixedDose">
											<xsl:attribute name="quantity">
												<xsl:value-of select="dose/@quantity"/>
											</xsl:attribute>
											<xsl:attribute name="unit">
												<xsl:value-of select="dose/unit/@value"/>
											</xsl:attribute>
										</xsl:element>
									</xsl:when>
									<xsl:when test="dose/@class=&quot;flexibleDose&quot;">
										<xsl:element name="flexibleDose">
											<xsl:attribute name="minDose">
												<xsl:value-of select="dose/@minDose"/>
											</xsl:attribute>
											<xsl:attribute name="maxDose">
												<xsl:value-of select="dose/@maxDose"/>
											</xsl:attribute>
											<xsl:attribute name="unit">
												<xsl:value-of select="dose/unit/@value"/>
											</xsl:attribute>
										</xsl:element>
									</xsl:when>
								</xsl:choose>
								<xsl:element name="drug">
									<xsl:variable name="id" select="drug/@ref"/>
									<xsl:attribute name="name">
										<xsl:value-of select="/addis-data/drugs/drug[@id=$id]/@name"/>
									</xsl:attribute>
								</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</arms>
					<measurements>
						<xsl:for-each select="measurements/measurement">
							<measurement>
								<xsl:element name="studyOutcomeMeasure">
									<xsl:variable name="id" select="outcomeMeasure/@ref"/>
									<xsl:variable name="tmpname" select="(//populationCharacteristic[@id=$id] | //endpoint[@id=$id] | //adverseEvent[@id=$id])/@name"/>
									<xsl:attribute name="name">
										<xsl:value-of select="$tmpname"/>
									</xsl:attribute>
								</xsl:element>
								<xsl:element name="arm">
									<xsl:attribute name="id">
										<xsl:value-of select="arm/@ref"/>
									</xsl:attribute>
								</xsl:element>
								<xsl:choose>
									<xsl:when test="measurement/@class=&quot;rateMeasurement&quot;">
										<xsl:element name="rateMeasurement">
											<xsl:attribute name="rate">
												<xsl:value-of select="measurement/@rate"/>
											</xsl:attribute>
											<xsl:attribute name="sampleSize">
												<xsl:value-of select="measurement/@sampleSize"/>
											</xsl:attribute>
										</xsl:element>
									</xsl:when>
									<xsl:when test="measurement/@class=&quot;continuousMeasurement&quot;">
										<xsl:element name="continuousMeasurement">
											<xsl:attribute name="mean">
												<xsl:value-of select="measurement/@mean"/>
											</xsl:attribute>
											<xsl:attribute name="sampleSize">
												<xsl:value-of select="measurement/@sampleSize"/>
											</xsl:attribute>
											<xsl:attribute name="stdDev">
												<xsl:value-of select="measurement/@stdDev"/>
											</xsl:attribute>
										</xsl:element>
									</xsl:when>
									<xsl:when test="measurement/@class=&quot;frequencyMeasurement&quot;">
										<xsl:element name="categoricalMeasurement">
											<xsl:for-each select="measurement/frequencies/frequency">
												<xsl:element name="category">
													<xsl:attribute name="name">
														<xsl:value-of select="@category"/>
													</xsl:attribute>
													<xsl:attribute name="rate">
														<xsl:value-of select="@count"/>
													</xsl:attribute>
												</xsl:element>
											</xsl:for-each>
										</xsl:element>
									</xsl:when>
								</xsl:choose>
							</measurement>

						</xsl:for-each>
					</measurements>
				</xsl:element>
			</xsl:for-each><!-- study -->
		</studies>
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
