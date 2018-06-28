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
 *         &lt;element ref="{}LocationLabel" minOccurs="0"/&gt;
 *         &lt;element ref="{}SectionTitle"/&gt;
 *         &lt;element ref="{}Section" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "locationLabel",
    "sectionTitle",
    "sections"
})
@XmlRootElement(name = "Section")
public class Section implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "LocationLabel")
    protected LocationLabel locationLabel;
    @XmlElement(name = "SectionTitle", required = true)
    protected SectionTitle sectionTitle;
    @XmlElement(name = "Section")
    protected List<Section> sections;

    /**
     * Gets the value of the locationLabel property.
     * 
     * @return
     *     possible object is
     *     {@link LocationLabel }
     *     
     */
    public LocationLabel getLocationLabel() {
        return locationLabel;
    }

    /**
     * Sets the value of the locationLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationLabel }
     *     
     */
    public void setLocationLabel(LocationLabel value) {
        this.locationLabel = value;
    }

    /**
     * Gets the value of the sectionTitle property.
     * 
     * @return
     *     possible object is
     *     {@link SectionTitle }
     *     
     */
    public SectionTitle getSectionTitle() {
        return sectionTitle;
    }

    /**
     * Sets the value of the sectionTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link SectionTitle }
     *     
     */
    public void setSectionTitle(SectionTitle value) {
        this.sectionTitle = value;
    }

    /**
     * Gets the value of the sections property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sections property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSections().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Section }
     * 
     * 
     */
    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<Section>();
        }
        return this.sections;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Section that = ((Section) object);
        {
            LocationLabel leftLocationLabel;
            leftLocationLabel = this.getLocationLabel();
            LocationLabel rightLocationLabel;
            rightLocationLabel = that.getLocationLabel();
            if (this.locationLabel!= null) {
                if (that.locationLabel!= null) {
                    if (!leftLocationLabel.equals(rightLocationLabel)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.locationLabel!= null) {
                    return false;
                }
            }
        }
        {
            SectionTitle leftSectionTitle;
            leftSectionTitle = this.getSectionTitle();
            SectionTitle rightSectionTitle;
            rightSectionTitle = that.getSectionTitle();
            if (this.sectionTitle!= null) {
                if (that.sectionTitle!= null) {
                    if (!leftSectionTitle.equals(rightSectionTitle)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.sectionTitle!= null) {
                    return false;
                }
            }
        }
        {
            List<Section> leftSections;
            leftSections = (((this.sections!= null)&&(!this.sections.isEmpty()))?this.getSections():null);
            List<Section> rightSections;
            rightSections = (((that.sections!= null)&&(!that.sections.isEmpty()))?that.getSections():null);
            if ((this.sections!= null)&&(!this.sections.isEmpty())) {
                if ((that.sections!= null)&&(!that.sections.isEmpty())) {
                    if (!leftSections.equals(rightSections)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.sections!= null)&&(!that.sections.isEmpty())) {
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
            LocationLabel theLocationLabel;
            theLocationLabel = this.getLocationLabel();
            if (this.locationLabel!= null) {
                currentHashCode += theLocationLabel.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            SectionTitle theSectionTitle;
            theSectionTitle = this.getSectionTitle();
            if (this.sectionTitle!= null) {
                currentHashCode += theSectionTitle.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            List<Section> theSections;
            theSections = (((this.sections!= null)&&(!this.sections.isEmpty()))?this.getSections():null);
            if ((this.sections!= null)&&(!this.sections.isEmpty())) {
                currentHashCode += theSections.hashCode();
            }
        }
        return currentHashCode;
    }

}
