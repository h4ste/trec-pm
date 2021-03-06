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
 *         &lt;element ref="{}MedlineCitation"/&gt;
 *         &lt;element ref="{}PubmedData" minOccurs="0"/&gt;
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
    "medlineCitation",
    "pubmedData"
})
@XmlRootElement(name = "PubmedArticle")
public class PubmedArticle implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "MedlineCitation", required = true)
    protected MedlineCitation medlineCitation;
    @XmlElement(name = "PubmedData")
    protected PubmedData pubmedData;

    /**
     * Gets the value of the medlineCitation property.
     * 
     * @return
     *     possible object is
     *     {@link MedlineCitation }
     *     
     */
    public MedlineCitation getMedlineCitation() {
        return medlineCitation;
    }

    /**
     * Sets the value of the medlineCitation property.
     * 
     * @param value
     *     allowed object is
     *     {@link MedlineCitation }
     *     
     */
    public void setMedlineCitation(MedlineCitation value) {
        this.medlineCitation = value;
    }

    /**
     * Gets the value of the pubmedData property.
     * 
     * @return
     *     possible object is
     *     {@link PubmedData }
     *     
     */
    public PubmedData getPubmedData() {
        return pubmedData;
    }

    /**
     * Sets the value of the pubmedData property.
     * 
     * @param value
     *     allowed object is
     *     {@link PubmedData }
     *     
     */
    public void setPubmedData(PubmedData value) {
        this.pubmedData = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final PubmedArticle that = ((PubmedArticle) object);
        {
            MedlineCitation leftMedlineCitation;
            leftMedlineCitation = this.getMedlineCitation();
            MedlineCitation rightMedlineCitation;
            rightMedlineCitation = that.getMedlineCitation();
            if (this.medlineCitation!= null) {
                if (that.medlineCitation!= null) {
                    if (!leftMedlineCitation.equals(rightMedlineCitation)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.medlineCitation!= null) {
                    return false;
                }
            }
        }
        {
            PubmedData leftPubmedData;
            leftPubmedData = this.getPubmedData();
            PubmedData rightPubmedData;
            rightPubmedData = that.getPubmedData();
            if (this.pubmedData!= null) {
                if (that.pubmedData!= null) {
                    if (!leftPubmedData.equals(rightPubmedData)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.pubmedData!= null) {
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
            MedlineCitation theMedlineCitation;
            theMedlineCitation = this.getMedlineCitation();
            if (this.medlineCitation!= null) {
                currentHashCode += theMedlineCitation.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            PubmedData thePubmedData;
            thePubmedData = this.getPubmedData();
            if (this.pubmedData!= null) {
                currentHashCode += thePubmedData.hashCode();
            }
        }
        return currentHashCode;
    }

}
