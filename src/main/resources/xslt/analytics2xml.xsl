<?xml version="1.0" encoding="UTF-8"?>
<!-- written by Frank Luetzenkirchen: frank.luetzenkirchen@uni-due.de -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rs="urn:schemas-microsoft-com:xml-analysis:rowset"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:saw-sql="urn:saw-sql"
  exclude-result-prefixes="xsl xsd rs saw-sql"
>

	<xsl:param name="baseurl">https://api-eu.hosted.exlibrisgroup.com/almaws/v1/analytics/reports</xsl:param>
	<xsl:param name="apikey">l8xx58da980166f24430be5b2cc7cc76b6d0</xsl:param>

	<xsl:output method="xml" indent="yes" />

	<xsl:strip-space elements="*" />

	<xsl:variable name="schema" select="//xsd:schema" />

	<xsl:template match="/">
		<results>
			<rows>
			<xsl:apply-templates select="report" />
			</rows>
		</results>
	</xsl:template>

	<xsl:template match="report">
		<xsl:for-each select="//rs:rowset/rs:Row">
			<row>
				<xsl:for-each select="rs:*[starts-with(local-name(),'Column')][not(local-name()='Column0')]">
					<xsl:element name="{translate($schema//xsd:element[@name=local-name(current())]/@saw-sql:columnHeading,' ','')}">
						<xsl:value-of select="." />
					</xsl:element>
				</xsl:for-each>
			</row>
		</xsl:for-each>
		<xsl:for-each select="QueryResult[IsFinished='false']">
			<xsl:variable name="url" select="concat($baseurl,'?apikey=',$apikey,'&amp;token=',ResumptionToken)" />
			<xsl:apply-templates select="document($url)/report" />
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>