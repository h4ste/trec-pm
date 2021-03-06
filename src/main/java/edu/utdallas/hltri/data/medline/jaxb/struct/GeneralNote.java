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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="Owner" default="NLM"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="NLM"/&gt;
 *             &lt;enumeration value="NASA"/&gt;
 *             &lt;enumeration value="PIP"/&gt;
 *             &lt;enumeration value="KIE"/&gt;
 *             &lt;enumeration value="HSR"/&gt;
 *             &lt;enumeration value="HMD"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "GeneralNote")
public class GeneralNote implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlValue
    protected String value;
    @XmlAttribute(name = "Owner")
    protected String owner;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        if (owner == null) {
            return "NLM";
        } else {
            return owner;
        }
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final GeneralNote that = ((GeneralNote) object);
        {
            String leftValue;
            leftValue = this.getValue();
            String rightValue;
            rightValue = that.getValue();
            if (this.value!= null) {
                if (that.value!= null) {
                    if (!leftValue.equals(rightValue)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.value!= null) {
                    return false;
                }
            }
        }
        {
            String leftOwner;
            leftOwner = this.getOwner();
            String rightOwner;
            rightOwner = that.getOwner();
            if (this.owner!= null) {
                if (that.owner!= null) {
                    if (!leftOwner.equals(rightOwner)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.owner!= null) {
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
            String theValue;
            theValue = this.getValue();
            if (this.value!= null) {
                currentHashCode += theValue.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theOwner;
            theOwner = this.getOwner();
            if (this.owner!= null) {
                currentHashCode += theOwner.hashCode();
            }
        }
        return currentHashCode;
    }

}
