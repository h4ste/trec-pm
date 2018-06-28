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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for period_struct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="period_struct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="milestone_list">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="milestone" type="{}milestone_struct" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="drop_withdraw_reason_list" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="drop_withdraw_reason" type="{}milestone_struct" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "period_struct", propOrder = {
    "title",
    "milestoneList",
    "dropWithdrawReasonList"
})
public class PeriodStruct
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(name = "milestone_list", required = true)
    protected PeriodStruct.MilestoneList milestoneList;
    @XmlElement(name = "drop_withdraw_reason_list")
    protected PeriodStruct.DropWithdrawReasonList dropWithdrawReasonList;

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the milestoneList property.
     * 
     * @return
     *     possible object is
     *     {@link PeriodStruct.MilestoneList }
     *     
     */
    public PeriodStruct.MilestoneList getMilestoneList() {
        return milestoneList;
    }

    /**
     * Sets the value of the milestoneList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeriodStruct.MilestoneList }
     *     
     */
    public void setMilestoneList(PeriodStruct.MilestoneList value) {
        this.milestoneList = value;
    }

    /**
     * Gets the value of the dropWithdrawReasonList property.
     * 
     * @return
     *     possible object is
     *     {@link PeriodStruct.DropWithdrawReasonList }
     *     
     */
    public PeriodStruct.DropWithdrawReasonList getDropWithdrawReasonList() {
        return dropWithdrawReasonList;
    }

    /**
     * Sets the value of the dropWithdrawReasonList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeriodStruct.DropWithdrawReasonList }
     *     
     */
    public void setDropWithdrawReasonList(PeriodStruct.DropWithdrawReasonList value) {
        this.dropWithdrawReasonList = value;
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
     *         &lt;element name="drop_withdraw_reason" type="{}milestone_struct" maxOccurs="unbounded" minOccurs="0"/>
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
        "dropWithdrawReason"
    })
    public static class DropWithdrawReasonList
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "drop_withdraw_reason")
        protected List<MilestoneStruct> dropWithdrawReason;

        /**
         * Gets the value of the dropWithdrawReason property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dropWithdrawReason property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDropWithdrawReason().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MilestoneStruct }
         * 
         * 
         */
        public List<MilestoneStruct> getDropWithdrawReason() {
            if (dropWithdrawReason == null) {
                dropWithdrawReason = new ArrayList<MilestoneStruct>();
            }
            return this.dropWithdrawReason;
        }

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
     *         &lt;element name="milestone" type="{}milestone_struct" maxOccurs="unbounded"/>
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
        "milestone"
    })
    public static class MilestoneList
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(required = true)
        protected List<MilestoneStruct> milestone;

        /**
         * Gets the value of the milestone property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the milestone property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMilestone().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MilestoneStruct }
         * 
         * 
         */
        public List<MilestoneStruct> getMilestone() {
            if (milestone == null) {
                milestone = new ArrayList<MilestoneStruct>();
            }
            return this.milestone;
        }

    }

}
