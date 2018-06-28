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
 *         &lt;element ref="{}BookDocument" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}DeleteDocument" minOccurs="0"/&gt;
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
    "bookDocuments",
    "deleteDocument"
})
@XmlRootElement(name = "BookDocumentSet")
public class BookDocumentSet
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BookDocument")
    protected List<BookDocument> bookDocuments;
    @XmlElement(name = "DeleteDocument")
    protected DeleteDocument deleteDocument;

    /**
     * Gets the value of the bookDocuments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bookDocuments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBookDocuments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BookDocument }
     * 
     * 
     */
    public List<BookDocument> getBookDocuments() {
        if (bookDocuments == null) {
            bookDocuments = new ArrayList<BookDocument>();
        }
        return this.bookDocuments;
    }

    /**
     * Gets the value of the deleteDocument property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteDocument }
     *     
     */
    public DeleteDocument getDeleteDocument() {
        return deleteDocument;
    }

    /**
     * Sets the value of the deleteDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteDocument }
     *     
     */
    public void setDeleteDocument(DeleteDocument value) {
        this.deleteDocument = value;
    }

    public boolean equals(java.lang.Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final BookDocumentSet that = ((BookDocumentSet) object);
        {
            List<BookDocument> leftBookDocuments;
            leftBookDocuments = (((this.bookDocuments!= null)&&(!this.bookDocuments.isEmpty()))?this.getBookDocuments():null);
            List<BookDocument> rightBookDocuments;
            rightBookDocuments = (((that.bookDocuments!= null)&&(!that.bookDocuments.isEmpty()))?that.getBookDocuments():null);
            if ((this.bookDocuments!= null)&&(!this.bookDocuments.isEmpty())) {
                if ((that.bookDocuments!= null)&&(!that.bookDocuments.isEmpty())) {
                    if (!leftBookDocuments.equals(rightBookDocuments)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if ((that.bookDocuments!= null)&&(!that.bookDocuments.isEmpty())) {
                    return false;
                }
            }
        }
        {
            DeleteDocument leftDeleteDocument;
            leftDeleteDocument = this.getDeleteDocument();
            DeleteDocument rightDeleteDocument;
            rightDeleteDocument = that.getDeleteDocument();
            if (this.deleteDocument!= null) {
                if (that.deleteDocument!= null) {
                    if (!leftDeleteDocument.equals(rightDeleteDocument)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.deleteDocument!= null) {
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
            List<BookDocument> theBookDocuments;
            theBookDocuments = (((this.bookDocuments!= null)&&(!this.bookDocuments.isEmpty()))?this.getBookDocuments():null);
            if ((this.bookDocuments!= null)&&(!this.bookDocuments.isEmpty())) {
                currentHashCode += theBookDocuments.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            DeleteDocument theDeleteDocument;
            theDeleteDocument = this.getDeleteDocument();
            if (this.deleteDocument!= null) {
                currentHashCode += theDeleteDocument.hashCode();
            }
        }
        return currentHashCode;
    }

}
