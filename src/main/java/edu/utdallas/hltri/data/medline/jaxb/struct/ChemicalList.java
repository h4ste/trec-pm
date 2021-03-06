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
 *         &lt;element ref="{}Chemical" maxOccurs="unbounded"/&gt;
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
    "chemicals"
})
@XmlRootElement(name = "ChemicalList")
public class ChemicalList implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Chemical", required = true)
    protected List<Chemical> chemicals;

    /**
     * Gets the value of the chemicals property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chemicals property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChemicals().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Chemical }
     * 
     * 
     */
    public List<Chemical> getChemicals() {
        if (chemicals == null) {
            chemicals = new ArrayList<Chemical>();
        }
        return this.chemicals;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final ChemicalList that = ((ChemicalList) object);
        {
            List<Chemical> leftChemicals;
            leftChemicals = (((this.chemicals!= null)&&(!this.chemicals.isEmpty()))?this.getChemicals():null);
            List<Chemical> rightChemicals;
            rightChemicals = (((that.chemicals!= null)&&(!that.chemicals.isEmpty()))?that.getChemicals():null);
            if ((this.chemicals!= null)&&(!this.chemicals.isEmpty())) {
                if ((that.chemicals!= null)&&(!that.chemicals.isEmpty())) {
                    if (!leftChemicals.equals(rightChemicals)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.chemicals!= null)&&(!that.chemicals.isEmpty())) {
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
            List<Chemical> theChemicals;
            theChemicals = (((this.chemicals!= null)&&(!this.chemicals.isEmpty()))?this.getChemicals():null);
            if ((this.chemicals!= null)&&(!this.chemicals.isEmpty())) {
                currentHashCode += theChemicals.hashCode();
            }
        }
        return currentHashCode;
    }

}
