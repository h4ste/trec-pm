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
 *         &lt;element ref="{}GrantID" minOccurs="0"/&gt;
 *         &lt;element ref="{}Acronym" minOccurs="0"/&gt;
 *         &lt;element ref="{}Agency"/&gt;
 *         &lt;element ref="{}Country"/&gt;
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
    "grantID",
    "acronym",
    "agency",
    "country"
})
@XmlRootElement(name = "Grant")
public class Grant implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GrantID")
    protected String grantID;
    @XmlElement(name = "Acronym")
    protected String acronym;
    @XmlElement(name = "Agency", required = true)
    protected String agency;
    @XmlElement(name = "Country", required = true)
    protected String country;

    /**
     * Gets the value of the grantID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrantID() {
        return grantID;
    }

    /**
     * Sets the value of the grantID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrantID(String value) {
        this.grantID = value;
    }

    /**
     * Gets the value of the acronym property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * Sets the value of the acronym property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcronym(String value) {
        this.acronym = value;
    }

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
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Grant that = ((Grant) object);
        {
            String leftGrantID;
            leftGrantID = this.getGrantID();
            String rightGrantID;
            rightGrantID = that.getGrantID();
            if (this.grantID!= null) {
                if (that.grantID!= null) {
                    if (!leftGrantID.equals(rightGrantID)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.grantID!= null) {
                    return false;
                }
            }
        }
        {
            String leftAcronym;
            leftAcronym = this.getAcronym();
            String rightAcronym;
            rightAcronym = that.getAcronym();
            if (this.acronym!= null) {
                if (that.acronym!= null) {
                    if (!leftAcronym.equals(rightAcronym)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.acronym!= null) {
                    return false;
                }
            }
        }
        {
            String leftAgency;
            leftAgency = this.getAgency();
            String rightAgency;
            rightAgency = that.getAgency();
            if (this.agency!= null) {
                if (that.agency!= null) {
                    if (!leftAgency.equals(rightAgency)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.agency!= null) {
                    return false;
                }
            }
        }
        {
            String leftCountry;
            leftCountry = this.getCountry();
            String rightCountry;
            rightCountry = that.getCountry();
            if (this.country!= null) {
                if (that.country!= null) {
                    if (!leftCountry.equals(rightCountry)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.country!= null) {
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
            String theGrantID;
            theGrantID = this.getGrantID();
            if (this.grantID!= null) {
                currentHashCode += theGrantID.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theAcronym;
            theAcronym = this.getAcronym();
            if (this.acronym!= null) {
                currentHashCode += theAcronym.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theAgency;
            theAgency = this.getAgency();
            if (this.agency!= null) {
                currentHashCode += theAgency.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theCountry;
            theCountry = this.getCountry();
            if (this.country!= null) {
                currentHashCode += theCountry.hashCode();
            }
        }
        return currentHashCode;
    }

}
