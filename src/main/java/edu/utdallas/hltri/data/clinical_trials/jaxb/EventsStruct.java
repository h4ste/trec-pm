//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.25 at 12:59:43 PM CDT 
//


package edu.utdallas.hltri.data.clinical_trials.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for events_struct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="events_struct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="frequency_threshold" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default_vocab" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="default_assessment" type="{}event_assessment_enum" minOccurs="0"/>
 *         &lt;element name="category_list">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="category" type="{}event_category_struct" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "events_struct", propOrder = {
    "frequencyThreshold",
    "defaultVocab",
    "defaultAssessment",
    "categoryList"
})
public class EventsStruct
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "frequency_threshold")
    protected String frequencyThreshold;
    @XmlElement(name = "default_vocab")
    protected String defaultVocab;
    @XmlElement(name = "default_assessment")
    @XmlSchemaType(name = "string")
    protected EventAssessmentEnum defaultAssessment;
    @XmlElement(name = "category_list", required = true)
    protected EventsStruct.CategoryList categoryList;

    /**
     * Gets the value of the frequencyThreshold property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrequencyThreshold() {
        return frequencyThreshold;
    }

    /**
     * Sets the value of the frequencyThreshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrequencyThreshold(String value) {
        this.frequencyThreshold = value;
    }

    /**
     * Gets the value of the defaultVocab property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultVocab() {
        return defaultVocab;
    }

    /**
     * Sets the value of the defaultVocab property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultVocab(String value) {
        this.defaultVocab = value;
    }

    /**
     * Gets the value of the defaultAssessment property.
     * 
     * @return
     *     possible object is
     *     {@link EventAssessmentEnum }
     *     
     */
    public EventAssessmentEnum getDefaultAssessment() {
        return defaultAssessment;
    }

    /**
     * Sets the value of the defaultAssessment property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventAssessmentEnum }
     *     
     */
    public void setDefaultAssessment(EventAssessmentEnum value) {
        this.defaultAssessment = value;
    }

    /**
     * Gets the value of the categoryList property.
     * 
     * @return
     *     possible object is
     *     {@link EventsStruct.CategoryList }
     *     
     */
    public EventsStruct.CategoryList getCategoryList() {
        return categoryList;
    }

    /**
     * Sets the value of the categoryList property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventsStruct.CategoryList }
     *     
     */
    public void setCategoryList(EventsStruct.CategoryList value) {
        this.categoryList = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="category" type="{}event_category_struct" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "category"
    })
    public static class CategoryList
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected List<EventCategoryStruct> category;

        /**
         * Gets the value of the category property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the category property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCategory().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EventCategoryStruct }
         * 
         * 
         */
        public List<EventCategoryStruct> getCategory() {
            if (category == null) {
                category = new ArrayList<EventCategoryStruct>();
            }
            return this.category;
        }

    }

}
