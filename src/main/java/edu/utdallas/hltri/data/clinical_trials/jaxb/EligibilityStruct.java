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
 * <p>Java class for eligibility_struct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eligibility_struct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="study_pop" type="{}textblock_struct" minOccurs="0"/>
 *         &lt;element name="sampling_method" type="{}sampling_method_enum" minOccurs="0"/>
 *         &lt;element name="criteria" type="{}textblock_struct" minOccurs="0"/>
 *         &lt;element name="gender" type="{}gender_enum"/>
 *         &lt;element name="gender_based" type="{}yes_no_enum" minOccurs="0"/>
 *         &lt;element name="gender_description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minimum_age" type="{}age_pattern"/>
 *         &lt;element name="maximum_age" type="{}age_pattern"/>
 *         &lt;element name="healthy_volunteers" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eligibility_struct", propOrder = {
    "studyPop",
    "samplingMethod",
    "criteria",
    "gender",
    "genderBased",
    "genderDescription",
    "minimumAge",
    "maximumAge",
    "healthyVolunteers"
})
public class EligibilityStruct
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "study_pop")
    protected TextblockStruct studyPop;
    @XmlElement(name = "sampling_method")
    @XmlSchemaType(name = "string")
    protected SamplingMethodEnum samplingMethod;
    protected TextblockStruct criteria;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected GenderEnum gender;
    @XmlElement(name = "gender_based")
    @XmlSchemaType(name = "string")
    protected YesNoEnum genderBased;
    @XmlElement(name = "gender_description")
    protected String genderDescription;
    @XmlElement(name = "minimum_age", required = true)
    protected String minimumAge;
    @XmlElement(name = "maximum_age", required = true)
    protected String maximumAge;
    @XmlElement(name = "healthy_volunteers")
    protected String healthyVolunteers;

    /**
     * Gets the value of the studyPop property.
     * 
     * @return
     *     possible object is
     *     {@link TextblockStruct }
     *     
     */
    public TextblockStruct getStudyPop() {
        return studyPop;
    }

    /**
     * Sets the value of the studyPop property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextblockStruct }
     *     
     */
    public void setStudyPop(TextblockStruct value) {
        this.studyPop = value;
    }

    /**
     * Gets the value of the samplingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link SamplingMethodEnum }
     *     
     */
    public SamplingMethodEnum getSamplingMethod() {
        return samplingMethod;
    }

    /**
     * Sets the value of the samplingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link SamplingMethodEnum }
     *     
     */
    public void setSamplingMethod(SamplingMethodEnum value) {
        this.samplingMethod = value;
    }

    /**
     * Gets the value of the criteria property.
     * 
     * @return
     *     possible object is
     *     {@link TextblockStruct }
     *     
     */
    public TextblockStruct getCriteria() {
        return criteria;
    }

    /**
     * Sets the value of the criteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextblockStruct }
     *     
     */
    public void setCriteria(TextblockStruct value) {
        this.criteria = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link GenderEnum }
     *     
     */
    public GenderEnum getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderEnum }
     *     
     */
    public void setGender(GenderEnum value) {
        this.gender = value;
    }

    /**
     * Gets the value of the genderBased property.
     * 
     * @return
     *     possible object is
     *     {@link YesNoEnum }
     *     
     */
    public YesNoEnum getGenderBased() {
        return genderBased;
    }

    /**
     * Sets the value of the genderBased property.
     * 
     * @param value
     *     allowed object is
     *     {@link YesNoEnum }
     *     
     */
    public void setGenderBased(YesNoEnum value) {
        this.genderBased = value;
    }

    /**
     * Gets the value of the genderDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenderDescription() {
        return genderDescription;
    }

    /**
     * Sets the value of the genderDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenderDescription(String value) {
        this.genderDescription = value;
    }

    /**
     * Gets the value of the minimumAge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimumAge() {
        return minimumAge;
    }

    /**
     * Sets the value of the minimumAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimumAge(String value) {
        this.minimumAge = value;
    }

    /**
     * Gets the value of the maximumAge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaximumAge() {
        return maximumAge;
    }

    /**
     * Sets the value of the maximumAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaximumAge(String value) {
        this.maximumAge = value;
    }

    /**
     * Gets the value of the healthyVolunteers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHealthyVolunteers() {
        return healthyVolunteers;
    }

    /**
     * Sets the value of the healthyVolunteers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHealthyVolunteers(String value) {
        this.healthyVolunteers = value;
    }

}
