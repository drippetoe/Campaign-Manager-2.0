/**'
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.contentgenerator.bean;

/**
 *
 * @author Gilberto Gaxiola
 */
public class FacebookButton extends HtmlBlock {

    
    public boolean isFacebookOnly = false;
    
    public FacebookButton(String id) {
        super(id, FacebookButton.class);
    }

    public String convert(String html) {
        /*
         * 1. Search for pattern "#"+this.getClass().getSimpleName()
         */
        return html.replace(this.tag, this.toString());
    }

    @Override
    public String toString() {
        String result = "";
        if(!isFacebookOnly) {
            result += "<div class=\"accessInternet\"><a href=\"facebook.php?companyName=" + this.value +"\" title=\"Like Us on Facebook\">ACCESS THE INTERNET</a></div>";
            return result;
        } else {
            result += "<?php\n";
            result += "$company = \""+ this.value + "\";\n";
            result += "\n?>";
            return result;
        }
    }

    /**
     * Example
        <div id="internet_container" class="FacebookButton">
			<div class="accessInternet">
				<a class="accessInternet" href="#" title="Like Us on Facebook">ACCESS THE INTERNET</a>
			</div>
			<input type="hidden" id="internet" name="internet" value=""/>
			<div class="editMeFace">
				<label for="internet">Your Facebook Page:<br/><br/>http://www.facebook.com/</label>
				<input type="text" id="internet" value=""/>
			</div>
        </div>
     * @return 
     */
    @Override
    public String getEditor() {
        String result = "";
        result += "<!--" + this.tag + "-->\n";
        result += "<div id=\"" + this.id + "_container\" class=\"" + this.getClass().getSimpleName() + "\">";
        result += "\n\t<div class=\"accessInternet\">";
        result += "\n\t\t<a class=\"accessInternet\" href=\"#\" title=\"Like Us on Facebook\">ACCESS THE INTERNET</a>";
        result += "\n\t</div>";
        result += "\n\t<input type=\"hidden\" id=\"" + this.id + "\" name=\"" + this.id + "\" value=\"" + this.value + "\"/>";
        result += "\n\t<div class=\"editMeFace\">";
        result += "\n\t\t<label for=\""+ this.id+"\">Your Facebook Page:<br/><br/>http://www.facebook.com/</label>";
        result += "\n\t\t<input type=\"text\" id=\"" + this.id + "\" value=\""+ ((this.value==null||this.value.isEmpty())?"yourFacebookPage":this.value) + "\"/>";
        result += "\n\t</div>";
        result += "\n</div>";
        return result;
    }
    
    public static void main(String[] args) {
        HtmlBlock block = new FacebookButton("internet");
        System.out.println(block.getEditor());
    }

    public boolean isIsFacebookOnly() {
        return isFacebookOnly;
    }

    public void setIsFacebookOnly(boolean isFacebookOnly) {
        this.isFacebookOnly = isFacebookOnly;
    }
    
    
}
