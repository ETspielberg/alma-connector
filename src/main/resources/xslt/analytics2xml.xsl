<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rs="urn:schemas-microsoft-com:xml-analysis:rowset"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:saw-sql="urn:saw-sql"
                exclude-result-prefixes="xsl xsd rs saw-sql"
>

    <xsl:param name="baseurl">https://api-eu.hosted.exlibrisgroup.com/almaws/v1/analytics/reports</xsl:param>
    <xsl:param name="apikey"/>

    <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

    <xsl:strip-space elements="*"/>

    <xsl:variable name="schema" select="//xsd:schema"/>

    <xsl:template match="/">
        <result>
            <xsl:apply-templates select="report"/>
        </result>
    </xsl:template>

    <xsl:template match="report">
        <xsl:for-each select="//rs:rowset/rs:Row">
            <row>
                <xsl:for-each select="rs:*[starts-with(local-name(),'Column')][not(local-name()='Column0')]">
                    <xsl:element
                            name="{translate($schema//xsd:element[@name=local-name(current())]/@saw-sql:columnHeading,' ','')}">
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:for-each>
            </row>
        </xsl:for-each>
        <xsl:for-each select="QueryResult[string-length(ResumptionToken) &gt; 0 and IsFinished='false']">
            <xsl:variable name="url" select="concat($baseurl,'?apikey=',$apikey,'&amp;token=',ResumptionToken)"/>
            <xsl:apply-templates select="document($url)/report"/>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>