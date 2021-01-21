<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rs="urn:schemas-microsoft-com:xml-analysis:rowset"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:saw-sql="urn:saw-sql"
  exclude-result-prefixes="xsl xsd rs saw-sql"
>

  <xsl:output method="xml" indent="yes" />

  <xsl:strip-space elements="*" />
  
  <xsl:template match="/*">
    <itemLoanedReport>
      <xsl:apply-templates select="//rs:rowset/rs:Row" />
	</itemLoanedReport>
  </xsl:template>
  
  <xsl:template match="rs:Row">
    <itemLoaned>
	  <xsl:apply-templates select="rs:*[starts-with(local-name(),'Column')][not(local-name()='Column0')]" />
	</itemLoaned>
  </xsl:template>
  
  <xsl:template match="rs:*[starts-with(local-name(),'Column')]">
    <xsl:element name="{translate(//xsd:element[@name=local-name(current())]/@saw-sql:columnHeading,' ','')}">
	  <xsl:value-of select="." />
	</xsl:element>
  </xsl:template>
  
</xsl:stylesheet>