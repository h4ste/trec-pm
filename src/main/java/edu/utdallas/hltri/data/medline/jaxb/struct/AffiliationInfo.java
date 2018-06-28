//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.24 at 11:14:59 AM CDT 
//


package edu.utdallas.hltri.data.medline.jaxb.struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}Affiliation"/&gt;
 *         &lt;element ref="{}Identifier" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "affiliation",
    "identifiers"
})
@XmlRootElement(name = "AffiliationInfo")
public class AffiliationInfo implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Affiliation", required = true)
    protected Affiliation affiliation;
    @XmlElement(name = "Identifier")
    protected List<Identifier> identifiers;

    /**
     * Gets the value of the affiliation property.
     * 
     * @return
     *     possible object is
     *     {@link Affiliation }
     *     
     */
    public Affiliation getAffiliation() {
        return affiliation;
    }

    /**
     * Sets the value of the affiliation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Affiliation }
     *     
     */
    public void setAffiliation(Affiliation value) {
        this.affiliation = value;
    }

    /**
     * Gets the value of the identifiers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identifiers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentifiers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Identifier }
     * 
     * 
     */
    public List<Identifier> getIdentifiers() {
        if (identifiers == null) {
            identifiers = new ArrayList<Identifier>();
        }
        return this.identifiers;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final AffiliationInfo that = ((AffiliationInfo) object);
        {
            Affiliation leftAffiliation;
            leftAffiliation = this.getAffiliation();
            Affiliation rightAffiliation;
            rightAffiliation = that.getAffiliation();
            if (this.affiliation!= null) {
                if (that.affiliation!= null) {
                    if (!leftAffiliation.equals(rightAffiliation)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.affiliation!= null) {
                    return false;
                }
            }
        }
        {
            List<Identifier> leftIdentifiers;
            leftIdentifiers = (((this.identifiers!= null)&&(!this.identifiers.isEmpty()))?this.getIdentifiers():null);
            List<Identifier> rightIdentifiers;
            rightIdentifiers = (((that.identifiers!= null)&&(!that.identifiers.isEmpty()))?that.getIdentifiers():null);
            if ((this.identifiers!= null)&&(!this.identifiers.isEmpty())) {
                if ((that.identifiers!= null)&&(!that.identifiers.isEmpty())) {
                    if (!leftIdentifiers.equals(rightIdentifiers)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.identifiers!= null)&&(!that.identifiers.isEmpty())) {
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
            Affiliation theAffiliation;
            theAffiliation = this.getAffiliation();
            if (this.affiliation!= null) {
                currentHashCode += theAffiliation.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            List<Identifier> theIdentifiers;
            theIdentifiers = (((this.identifiers!= null)&&(!this.identifiers.isEmpty()))?this.getIdentifiers():null);
            if ((this.identifiers!= null)&&(!this.identifiers.isEmpty())) {
                currentHashCode += theIdentifiers.hashCode();
            }
        }
        return currentHashCode;
    }

}
