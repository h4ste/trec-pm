//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.25 at 12:59:43 PM CDT 
//


package edu.utdallas.hltri.data.clinical_trials.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for unknown_status_enum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="unknown_status_enum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unknown status"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "unknown_status_enum")
@XmlEnum
public enum UnknownStatusEnum {

    @XmlEnumValue("Unknown status")
    UNKNOWN_STATUS("Unknown status");
    private final String value;

    UnknownStatusEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnknownStatusEnum fromValue(String v) {
        for (UnknownStatusEnum c: UnknownStatusEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
