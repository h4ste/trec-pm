//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.08.24 at 11:14:59 AM CDT 
//


package edu.utdallas.hltri.data.medline.jaxb.struct;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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
 *         &lt;element ref="{}PublicationType" maxOccurs="unbounded"/&gt;
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
    "publicationTypes"
})
@XmlRootElement(name = "PublicationTypeList")
public class PublicationTypes implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PublicationType", required = true)
    protected Set<PublicationType> publicationTypes;

    /**
     * Gets the value of the publicationTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the publicationTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPublicationTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PublicationType }
     * 
     * 
     */
    public Set<PublicationType> getPublicationTypes() {
        if (publicationTypes == null) {
            publicationTypes = new HashSet<>();
        }
        return this.publicationTypes;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final PublicationTypes that = ((PublicationTypes) object);
        {
            Set<PublicationType> leftPublicationTypes;
            leftPublicationTypes = (((this.publicationTypes!= null)&&(!this.publicationTypes.isEmpty()))?this.getPublicationTypes():null);
            Set<PublicationType> rightPublicationTypes;
            rightPublicationTypes = (((that.publicationTypes!= null)&&(!that.publicationTypes.isEmpty()))?that.getPublicationTypes():null);
            if ((this.publicationTypes!= null)&&(!this.publicationTypes.isEmpty())) {
                if ((that.publicationTypes!= null)&&(!that.publicationTypes.isEmpty())) {
                    if (!leftPublicationTypes.equals(rightPublicationTypes)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.publicationTypes!= null)&&(!that.publicationTypes.isEmpty())) {
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
            Set<PublicationType> thePublicationTypes;
            thePublicationTypes = (((this.publicationTypes!= null)&&(!this.publicationTypes.isEmpty()))?this.getPublicationTypes():null);
            if ((this.publicationTypes!= null)&&(!this.publicationTypes.isEmpty())) {
                currentHashCode += thePublicationTypes.hashCode();
            }
        }
        return currentHashCode;
    }

}
