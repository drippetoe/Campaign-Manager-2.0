package com.proximus.contentgenerator.bean;

/**
 *
 * @author eric
 */
public class ImageUploader extends HtmlBlock {

    public ImageUploader(String id) {
        super(id, ImageUploader.class);
    }

    private String getRealValue() {
        if (this.value.lastIndexOf("/") > 0) {
            String result = this.value.substring(this.value.lastIndexOf("/")+1, this.value.length());
            return result;
        } else {
            return this.value;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("<img src=\"").append(getRealValue()).append("\" id=\"").append(this.id).append("\"/>");
        return sb.toString();
    }

    @Override
    public String getEditor() {
        String result = "";
        result += "<!--" + this.tag + "-->\n";
        result += "<div id=\"" + this.id + "_container\" class=\"" + this.getClass().getSimpleName() + "\">\n";
        result+="\t<input type=\"file\" id=\"" + this.id + "\" name=\"" + this.id + "\"/>\n";
        result+="\t<img src=\"" + this.value + "\"/>\n";
        result+="</div>";
        return result;
    }
    
    public static void main(String[] args) {
        String value = "this/is/just/a/test/cgtemp/atlanta_skyline.png";
        String result;
         if (value.lastIndexOf("/") > 0) {
            result = value.substring(value.lastIndexOf("/")+1, value.length());
             
        } else {
            result = value;
        }
         
         System.out.println("result: " + result);
    }
}
