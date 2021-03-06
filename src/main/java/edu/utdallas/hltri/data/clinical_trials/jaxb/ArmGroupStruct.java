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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for arm_group_struct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="arm_group_struct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arm_group_label" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="arm_group_type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "arm_group_struct", propOrder = {
    "armGroupLabel",
    "armGroupType",
    "description"
})
public class ArmGroupStruct
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "arm_group_label", required = true)
    protected String armGroupLabel;
    @XmlElement(name = "arm_group_type")
    protected String armGroupType;
    protected String description;

    /**
     * Gets the value of the armGroupLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArmGroupLabel() {
        return armGroupLabel;
    }

    /**
     * Sets the value of the armGroupLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArmGroupLabel(String value) {
        this.armGroupLabel = value;
    }

    /**
     * Gets the value of the armGroupType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArmGroupType() {
        return armGroupType;
    }

    /**
     * Sets the value of the armGroupType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArmGroupType(String value) {
        this.armGroupType = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
