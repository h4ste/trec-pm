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
 *         &lt;element ref="{}AbstractText" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{}CopyrightInformation" minOccurs="0"/&gt;
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
    "abstractTexts",
    "copyrightInformation"
})
@XmlRootElement(name = "Abstract")
public class Abstract implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AbstractText", required = true)
    protected List<AbstractText> abstractTexts;
    @XmlElement(name = "CopyrightInformation")
    protected String copyrightInformation;

    /**
     * Gets the value of the abstractTexts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractTexts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractTexts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractText }
     * 
     * 
     */
    public List<AbstractText> getAbstractTexts() {
        if (abstractTexts == null) {
            abstractTexts = new ArrayList<AbstractText>();
        }
        return this.abstractTexts;
    }

    /**
     * Gets the value of the copyrightInformation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyrightInformation() {
        return copyrightInformation;
    }

    /**
     * Sets the value of the copyrightInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyrightInformation(String value) {
        this.copyrightInformation = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Abstract that = ((Abstract) object);
        {
            List<AbstractText> leftAbstractTexts;
            leftAbstractTexts = (((this.abstractTexts!= null)&&(!this.abstractTexts.isEmpty()))?this.getAbstractTexts():null);
            List<AbstractText> rightAbstractTexts;
            rightAbstractTexts = (((that.abstractTexts!= null)&&(!that.abstractTexts.isEmpty()))?that.getAbstractTexts():null);
            if ((this.abstractTexts!= null)&&(!this.abstractTexts.isEmpty())) {
                if ((that.abstractTexts!= null)&&(!that.abstractTexts.isEmpty())) {
                    if (!leftAbstractTexts.equals(rightAbstractTexts)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.abstractTexts!= null)&&(!that.abstractTexts.isEmpty())) {
                    return false;
                }
            }
        }
        {
            String leftCopyrightInformation;
            leftCopyrightInformation = this.getCopyrightInformation();
            String rightCopyrightInformation;
            rightCopyrightInformation = that.getCopyrightInformation();
            if (this.copyrightInformation!= null) {
                if (that.copyrightInformation!= null) {
                    if (!leftCopyrightInformation.equals(rightCopyrightInformation)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.copyrightInformation!= null) {
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
            List<AbstractText> theAbstractTexts;
            theAbstractTexts = (((this.abstractTexts!= null)&&(!this.abstractTexts.isEmpty()))?this.getAbstractTexts():null);
            if ((this.abstractTexts!= null)&&(!this.abstractTexts.isEmpty())) {
                currentHashCode += theAbstractTexts.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theCopyrightInformation;
            theCopyrightInformation = this.getCopyrightInformation();
            if (this.copyrightInformation!= null) {
                currentHashCode += theCopyrightInformation.hashCode();
            }
        }
        return currentHashCode;
    }

}
