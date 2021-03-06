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
 * <p>Java class for expanded_access_info_struct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="expanded_access_info_struct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="expanded_access_type_individual" type="{}yes_no_enum" minOccurs="0"/>
 *         &lt;element name="expanded_access_type_intermediate" type="{}yes_no_enum" minOccurs="0"/>
 *         &lt;element name="expanded_access_type_treatment" type="{}yes_no_enum" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "expanded_access_info_struct", propOrder = {
    "expandedAccessTypeIndividual",
    "expandedAccessTypeIntermediate",
    "expandedAccessTypeTreatment"
})
public class ExpandedAccessInfoStruct
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "expanded_access_type_individual")
    @XmlSchemaType(name = "string")
    protected YesNoEnum expandedAccessTypeIndividual;
    @XmlElement(name = "expanded_access_type_intermediate")
    @XmlSchemaType(name = "string")
    protected YesNoEnum expandedAccessTypeIntermediate;
    @XmlElement(name = "expanded_access_type_treatment")
    @XmlSchemaType(name = "string")
    protected YesNoEnum expandedAccessTypeTreatment;

    /**
     * Gets the value of the expandedAccessTypeIndividual property.
     * 
     * @return
     *     possible object is
     *     {@link YesNoEnum }
     *     
     */
    public YesNoEnum getExpandedAccessTypeIndividual() {
        return expandedAccessTypeIndividual;
    }

    /**
     * Sets the value of the expandedAccessTypeIndividual property.
     * 
     * @param value
     *     allowed object is
     *     {@link YesNoEnum }
     *     
     */
    public void setExpandedAccessTypeIndividual(YesNoEnum value) {
        this.expandedAccessTypeIndividual = value;
    }

    /**
     * Gets the value of the expandedAccessTypeIntermediate property.
     * 
     * @return
     *     possible object is
     *     {@link YesNoEnum }
     *     
     */
    public YesNoEnum getExpandedAccessTypeIntermediate() {
        return expandedAccessTypeIntermediate;
    }

    /**
     * Sets the value of the expandedAccessTypeIntermediate property.
     * 
     * @param value
     *     allowed object is
     *     {@link YesNoEnum }
     *     
     */
    public void setExpandedAccessTypeIntermediate(YesNoEnum value) {
        this.expandedAccessTypeIntermediate = value;
    }

    /**
     * Gets the value of the expandedAccessTypeTreatment property.
     * 
     * @return
     *     possible object is
     *     {@link YesNoEnum }
     *     
     */
    public YesNoEnum getExpandedAccessTypeTreatment() {
        return expandedAccessTypeTreatment;
    }

    /**
     * Sets the value of the expandedAccessTypeTreatment property.
     * 
     * @param value
     *     allowed object is
     *     {@link YesNoEnum }
     *     
     */
    public void setExpandedAccessTypeTreatment(YesNoEnum value) {
        this.expandedAccessTypeTreatment = value;
    }

}
