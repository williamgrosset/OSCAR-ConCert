//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.03 at 01:48:51 AM EDT 
//


package org.oscarehr.hospitalReportManager.xsd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for officialSpokenLanguageCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="officialSpokenLanguageCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;maxLength value="3"/>
 *     &lt;enumeration value="ENG"/>
 *     &lt;enumeration value="FRE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "officialSpokenLanguageCode")
@XmlEnum
public enum OfficialSpokenLanguageCode {

    ENG,
    FRE;

    public String value() {
        return name();
    }

    public static OfficialSpokenLanguageCode fromValue(String v) {
        return valueOf(v);
    }

}