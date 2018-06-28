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
 *         &lt;element ref="{}RegistryNumber"/&gt;
 *         &lt;element ref="{}NameOfSubstance"/&gt;
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
    "registryNumber",
    "nameOfSubstance"
})
@XmlRootElement(name = "Chemical")
public class Chemical implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RegistryNumber", required = true)
    protected String registryNumber;
    @XmlElement(name = "NameOfSubstance", required = true)
    protected NameOfSubstance nameOfSubstance;

    /**
     * Gets the value of the registryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistryNumber() {
        return registryNumber;
    }

    /**
     * Sets the value of the registryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistryNumber(String value) {
        this.registryNumber = value;
    }

    /**
     * Gets the value of the nameOfSubstance property.
     * 
     * @return
     *     possible object is
     *     {@link NameOfSubstance }
     *     
     */
    public NameOfSubstance getNameOfSubstance() {
        return nameOfSubstance;
    }

    /**
     * Sets the value of the nameOfSubstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameOfSubstance }
     *     
     */
    public void setNameOfSubstance(NameOfSubstance value) {
        this.nameOfSubstance = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Chemical that = ((Chemical) object);
        {
            String leftRegistryNumber;
            leftRegistryNumber = this.getRegistryNumber();
            String rightRegistryNumber;
            rightRegistryNumber = that.getRegistryNumber();
            if (this.registryNumber!= null) {
                if (that.registryNumber!= null) {
                    if (!leftRegistryNumber.equals(rightRegistryNumber)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.registryNumber!= null) {
                    return false;
                }
            }
        }
        {
            NameOfSubstance leftNameOfSubstance;
            leftNameOfSubstance = this.getNameOfSubstance();
            NameOfSubstance rightNameOfSubstance;
            rightNameOfSubstance = that.getNameOfSubstance();
            if (this.nameOfSubstance!= null) {
                if (that.nameOfSubstance!= null) {
                    if (!leftNameOfSubstance.equals(rightNameOfSubstance)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.nameOfSubstance!= null) {
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
            String theRegistryNumber;
            theRegistryNumber = this.getRegistryNumber();
            if (this.registryNumber!= null) {
                currentHashCode += theRegistryNumber.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            NameOfSubstance theNameOfSubstance;
            theNameOfSubstance = this.getNameOfSubstance();
            if (this.nameOfSubstance!= null) {
                currentHashCode += theNameOfSubstance.hashCode();
            }
        }
        return currentHashCode;
    }

}
