/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.passbook;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dshaw
 */
@XmlRootElement(name = "barcode")
public class PassbookBarcode {

    private String message = "123456789";
    private String altText = message; // below the barcode, if it didnt scan

    // supports PDF417, Aztec, QR, all 2D formats
    // because you can't laser scan a device screen (1D scanner)
    // PKBarcodeFormatQR, PKBarcodeFormatPDF417, PKBarcodeFormatAztec, PKBarcodeFormatText.
    private String format = "PKBarcodeFormatPDF417"; 
    private String messageEncoding = "iso-8859-1"; // LATIN1

    public PassbookBarcode(String message)
    {
        this.setMessage(message);
        this.setAltText(message);
    }

    public String getMessage() {
        return message;
    }

    public void setBarcodeFormat(String format)
    {
        this.format = format;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public String getAltText() {
        return altText;
    }

    public final void setAltText(String altText) {
        this.altText = altText;
    }

    public void setMessageEncoding(String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

    public static void main(String[] args)
    {
        PassbookBarcode bc = new PassbookBarcode("12345");
        
        


    }
}
