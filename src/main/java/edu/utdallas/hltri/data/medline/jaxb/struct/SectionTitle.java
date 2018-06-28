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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
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
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{}b"/&gt;
 *         &lt;element ref="{}i"/&gt;
 *         &lt;element ref="{}sup"/&gt;
 *         &lt;element ref="{}sub"/&gt;
 *         &lt;element ref="{}u"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="book" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="part" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sec" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "SectionTitle")
public class SectionTitle implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElementRefs({
        @XmlElementRef(name = "i", type = I.class, required = false),
        @XmlElementRef(name = "sup", type = Sup.class, required = false),
        @XmlElementRef(name = "u", type = U.class, required = false),
        @XmlElementRef(name = "b", type = B.class, required = false),
        @XmlElementRef(name = "sub", type = Sub.class, required = false)
    })
    @XmlMixed
    protected List<Serializable> content;
    @XmlAttribute(name = "book")
    protected String book;
    @XmlAttribute(name = "part")
    protected String part;
    @XmlAttribute(name = "sec")
    protected String sec;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link I }
     * {@link Sup }
     * {@link U }
     * {@link Sub }
     * {@link String }
     * {@link B }
     * 
     * 
     */
    public List<Serializable> getContent() {
        if (content == null) {
            content = new ArrayList<Serializable>();
        }
        return this.content;
    }

    /**
     * Gets the value of the book property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBook() {
        return book;
    }

    /**
     * Sets the value of the book property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBook(String value) {
        this.book = value;
    }

    /**
     * Gets the value of the part property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPart() {
        return part;
    }

    /**
     * Sets the value of the part property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPart(String value) {
        this.part = value;
    }

    /**
     * Gets the value of the sec property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSec() {
        return sec;
    }

    /**
     * Sets the value of the sec property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSec(String value) {
        this.sec = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final SectionTitle that = ((SectionTitle) object);
        {
            List<Serializable> leftContent;
            leftContent = (((this.content!= null)&&(!this.content.isEmpty()))?this.getContent():null);
            List<Serializable> rightContent;
            rightContent = (((that.content!= null)&&(!that.content.isEmpty()))?that.getContent():null);
            if ((this.content!= null)&&(!this.content.isEmpty())) {
                if ((that.content!= null)&&(!that.content.isEmpty())) {
                    if (!leftContent.equals(rightContent)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.content!= null)&&(!that.content.isEmpty())) {
                    return false;
                }
            }
        }
        {
            String leftBook;
            leftBook = this.getBook();
            String rightBook;
            rightBook = that.getBook();
            if (this.book!= null) {
                if (that.book!= null) {
                    if (!leftBook.equals(rightBook)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.book!= null) {
                    return false;
                }
            }
        }
        {
            String leftPart;
            leftPart = this.getPart();
            String rightPart;
            rightPart = that.getPart();
            if (this.part!= null) {
                if (that.part!= null) {
                    if (!leftPart.equals(rightPart)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.part!= null) {
                    return false;
                }
            }
        }
        {
            String leftSec;
            leftSec = this.getSec();
            String rightSec;
            rightSec = that.getSec();
            if (this.sec!= null) {
                if (that.sec!= null) {
                    if (!leftSec.equals(rightSec)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.sec!= null) {
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
            List<Serializable> theContent;
            theContent = (((this.content!= null)&&(!this.content.isEmpty()))?this.getContent():null);
            if ((this.content!= null)&&(!this.content.isEmpty())) {
                currentHashCode += theContent.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theBook;
            theBook = this.getBook();
            if (this.book!= null) {
                currentHashCode += theBook.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String thePart;
            thePart = this.getPart();
            if (this.part!= null) {
                currentHashCode += thePart.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            String theSec;
            theSec = this.getSec();
            if (this.sec!= null) {
                currentHashCode += theSec.hashCode();
            }
        }
        return currentHashCode;
    }

}
