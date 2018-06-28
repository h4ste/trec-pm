//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.24 at 11:14:59 AM CDT 
//


package edu.utdallas.hltri.data.medline.jaxb.struct;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}Year"/&gt;
 *         &lt;element ref="{}Month"/&gt;
 *         &lt;element ref="{}Day"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "year",
    "month",
    "day"
})
@XmlRootElement(name = "DateCreated")
public class DateCreated implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Year", required = true)
    protected String year;
    @XmlElement(name = "Month", required = true)
    protected String month;
    @XmlElement(name = "Day", required = true)
    protected String day;

    /**
     * Gets the value of the year property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYear(String value) {
        this.year = value;
    }

    /**
     * Gets the value of the month property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth(String value) {
        this.month = value;
    }

    /**
     * Gets the value of the day property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDay(String value) {
        this.day = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final DateCreated that = ((DateCreated) object);
        {
            String leftYear;
            leftYear = this.getYear();
            String rightYear;
            rightYear = that.getYear();
            if (this.year!= null) {
                if (that.year!= null) {
                    if (!leftYear.equals(rightYear)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.year!= null) {
                    return false;
                }
            }
        }
        {
            String leftMonth;
            leftMonth = this.getMonth();
            String rightMonth;
            rightMonth = that.getMonth();
            if (this.month!= null) {
                if (that.month!= null) {
                    if (!leftMonth.equals(rightMonth)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.month!= null) {
                    return false;
                }
            }
        }
        {
            String leftDay;
            leftDay = this.getDay();
            String rightDay;
            rightDay = that.getDay();
            if (this.day!= null) {
                if (that.day!= null) {
                    if (!leftDay.equals(rightDay)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.day!= null) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        int currentHashCode = 1;
        {
            currentHashCode = (currentHashCode* 31);
            String theYear;
            theYear = this.getYear();
            if (this.year!= null) {
                currentHashCode += theYear.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theMonth;
            theMonth = this.getMonth();
            if (this.month!= null) {
                currentHashCode += theMonth.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theDay;
            theDay = this.getDay();
            if (this.day!= null) {
                currentHashCode += theDay.hashCode();
            }
        }
        return currentHashCode;
    }

}
