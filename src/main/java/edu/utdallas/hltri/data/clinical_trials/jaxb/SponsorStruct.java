//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.25 at 12:59:43 PM CDT 
//


package edu.utdallas.hltri.data.clinical_trials.jaxb;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sponsor_struct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sponsor_struct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="agency_class" type="{}agency_class_enum"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sponsor_struct", propOrder = {
    "agency",
    "agencyClass"
})
public class SponsorStruct
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String agency;
    @XmlElement(name = "agency_class", required = true)
    @XmlSchemaType(name = "string")
    protected AgencyClassEnum agencyClass;

    /**
     * Gets the value of the agency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgency() {
        return agency;
    }

    /**
     * Sets the value of the agency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgency(String value) {
        this.agency = value;
    }

    /**
     * Gets the value of the agencyClass property.
     * 
     * @return
     *     possible object is
     *     {@link AgencyClassEnum }
     *     
     */
    public AgencyClassEnum getAgencyClass() {
        return agencyClass;
    }

    /**
     * Sets the value of the agencyClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgencyClassEnum }
     *     
     */
    public void setAgencyClass(AgencyClassEnum value) {
        this.agencyClass = value;
    }

}
