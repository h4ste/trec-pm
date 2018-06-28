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
 *         &lt;element ref="{}PublisherName"/&gt;
 *         &lt;element ref="{}PublisherLocation" minOccurs="0"/&gt;
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
    "publisherName",
    "publisherLocation"
})
@XmlRootElement(name = "Publisher")
public class Publisher implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "PublisherName", required = true)
    protected PublisherName publisherName;
    @XmlElement(name = "PublisherLocation")
    protected String publisherLocation;

    /**
     * Gets the value of the publisherName property.
     * 
     * @return
     *     possible object is
     *     {@link PublisherName }
     *     
     */
    public PublisherName getPublisherName() {
        return publisherName;
    }

    /**
     * Sets the value of the publisherName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublisherName }
     *     
     */
    public void setPublisherName(PublisherName value) {
        this.publisherName = value;
    }

    /**
     * Gets the value of the publisherLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisherLocation() {
        return publisherLocation;
    }

    /**
     * Sets the value of the publisherLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisherLocation(String value) {
        this.publisherLocation = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Publisher that = ((Publisher) object);
        {
            PublisherName leftPublisherName;
            leftPublisherName = this.getPublisherName();
            PublisherName rightPublisherName;
            rightPublisherName = that.getPublisherName();
            if (this.publisherName!= null) {
                if (that.publisherName!= null) {
                    if (!leftPublisherName.equals(rightPublisherName)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.publisherName!= null) {
                    return false;
                }
            }
        }
        {
            String leftPublisherLocation;
            leftPublisherLocation = this.getPublisherLocation();
            String rightPublisherLocation;
            rightPublisherLocation = that.getPublisherLocation();
            if (this.publisherLocation!= null) {
                if (that.publisherLocation!= null) {
                    if (!leftPublisherLocation.equals(rightPublisherLocation)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.publisherLocation!= null) {
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
            PublisherName thePublisherName;
            thePublisherName = this.getPublisherName();
            if (this.publisherName!= null) {
                currentHashCode += thePublisherName.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String thePublisherLocation;
            thePublisherLocation = this.getPublisherLocation();
            if (this.publisherLocation!= null) {
                currentHashCode += thePublisherLocation.hashCode();
            }
        }
        return currentHashCode;
    }

}
