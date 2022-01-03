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

    <xsl:template match="/">
        <result>
            <xsl:apply-templates select="report"/>
        </result>
    </xsl:template>

    <xsl:template match="report">
        <isFinished>
            <xsl:value-of select="QueryResult/IsFinished/."/>
        </isFinished>
        <resumptionToken>
            <xsl:if test="//ResumptionToken">
                <xsl:apply-templates select="//ResumptionToken"/>
            </xsl:if>
        </resumptionToken>

        <xsl:apply-templates select="//rs:rowset/rs:Row"/>

    </xsl:template>

    <xsl:template match="rs:Row">
        <row>
            <xsl:for-each select="rs:*[starts-with(local-name(),'Column')][not(local-name()='Column0')]">
                <xsl:element
                        name="{local-name(current())}">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>
        </row>
    </xsl:template>

    <xsl:template match="ResumptionToken">
        <xsl:value-of select="."/>
    </xsl:template>

</xsl:stylesheet>