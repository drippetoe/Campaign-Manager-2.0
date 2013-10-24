package com.proximus.util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author eric
 */
public class ImageUtil
{

    public static final int IMAGE_UNKNOWN = -1;
    public static final int IMAGE_JPEG = 0;
    public static final int IMAGE_PNG = 1;
    public static final int IMAGE_GIF = 2;

    public static void main(String[] args)
    {
        File temp = new File("/home/eric/Desktop/Offer1.png");
        File img = new File("/home/eric/Desktop/img.png");
        if (template(temp, img, "20% Off Any Purchase", "Entire Purchase Including Sale Item", "Valid: Monday, April 9, 2012 through Saturday, April 14, 2012. Valid US only. We accept cupons on smartphones."))
        {
            System.out.println("Successfully wrote image: " + img.getName());
        }
    }

    private static float calculatePadding(int imageWidth, int stringWidth)
    {
        float padding = 0;
        padding = (imageWidth - stringWidth) / 2;
        if (padding < 0)
        {
            padding = 0;
        }
        return padding;
    }

    public static boolean template(File background, File outputFile, String title, String message, String disclaimer)
    {

        List<TextLayout> layouts = new ArrayList<TextLayout>();
        //Font font = new Font("SansSerif", Font.BOLD, 42);

        BufferedImage templateImage = null;
        try
        {
            templateImage = ImageIO.read(background);
        } catch (IOException e)
        {
            return false;
        }

        try
        {
            float lastY = 0;
            BufferedImage bi = templateImage;
            int width = templateImage.getWidth();
            int height = templateImage.getHeight();
            Graphics2D g2d = bi.createGraphics();
            FontRenderContext fontRenderContext = g2d.getFontRenderContext();
            AttributedString as;
            FontMetrics metrics;

            /**
             * Title
             */
            Font courierNew = new Font("Courier New", Font.BOLD, 48);
            Font timesNewRoman = new Font("Times New Roman", Font.BOLD, 20);
            Font helvetica = new Font("Helvetica", Font.BOLD, 10);
            as = new AttributedString(title);
            as.addAttribute(TextAttribute.FONT, courierNew);
            g2d.setPaint(Color.black);
            metrics = g2d.getFontMetrics(courierNew);
            float titlePadding = calculatePadding(width, metrics.stringWidth(title));

            lastY += metrics.getAscent();
            lastY = printWrapText(g2d, titlePadding, lastY, (width - titlePadding), as);
            /**
             * Subtitle
             */
            as = new AttributedString(message);
            as.addAttribute(TextAttribute.FONT, timesNewRoman);
            g2d.setPaint(Color.black);
            metrics = g2d.getFontMetrics(timesNewRoman);
            float messagePadding = calculatePadding(width, metrics.stringWidth(message));
            lastY += metrics.getAscent();
            lastY = printWrapText(g2d, messagePadding, lastY, (width - messagePadding), as);
            /**
             * Disclaimer
             */
            as = new AttributedString(disclaimer);
            as.addAttribute(TextAttribute.FONT, helvetica);
            g2d.setPaint(Color.black);
            metrics = g2d.getFontMetrics(helvetica);
            float disclaimerPadding = calculatePadding(width, metrics.stringWidth(disclaimer));
            lastY += metrics.getAscent();
            lastY = printWrapText(g2d, disclaimerPadding, lastY, (width - disclaimerPadding), as);

            /*
             * Save file
             */
            ImageIO.write(bi, "PNG", outputFile);

            return true;

        } catch (IOException ie)
        {
            //ie.printStackTrace();
            return false;
        }
    }

    private static float printWrapText(Graphics2D g2d, float x, float y, float width, AttributedString as)
    {
        AttributedCharacterIterator characterIterator = as.getIterator();
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
        while (measurer.getPosition() < characterIterator.getEndIndex())
        {
            TextLayout textLayout = measurer.nextLayout(width);
            y += textLayout.getAscent();
            textLayout.draw(g2d, x, y);
            y += textLayout.getDescent() + textLayout.getLeading();
        }
        return y;
    }
}
