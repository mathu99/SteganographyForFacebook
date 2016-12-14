/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taggingforfacebook;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/* Takes an image in, identifies blocks, modulates them as needed */
public class ImageParser 
{
    BufferedImage image;
    Color colorArray[][];
    Color brightnessColorArray[][];
    int tagArray[][];
    double THRESHOLD;
    double LOWER_THRESHOLD;
    int dark = 0;
    int light = 0;
    int none = 0;
    int totalPossibleTags = 0;
    private int ADJUSTMENT_AMOUNT = 4;
    String message = "";
    int taggingPos = 0;
    
    public ImageParser(BufferedImage im, int ts, String msg)
    {
        THRESHOLD = 20;
        LOWER_THRESHOLD = 10;
        message = msg;
        image = im;
        colorArray = new Color[im.getWidth()][im.getHeight()];
        brightnessColorArray = new Color[im.getWidth()][im.getHeight()];
        tagArray = new int[im.getWidth()][im.getHeight()];
        addColorsToArray();
        System.out.println("Image parser created");
    }
    
    public void setThreshold(int t)
    {
        THRESHOLD = t;
    }
    
    private void addColorsToArray()
    {
        for (int i = 0; i < image.getWidth(); i++) 
        {
            for (int j = 0; j < image.getHeight(); j++) 
            {
                  tagArray[i][j] = 0;   //Not tagged
                  colorArray[i][j] = new Color(image.getRGB(i,j));
                  brightnessColorArray[i][j] = new Color(image.getRGB(i,j));
            }
        }
        System.out.println("Colors added to arrays...");
    }
    
    private double getBrightness(int r, int g, int b)
    {
        return (0.2126*r + 0.7152*g + 0.0722*b);
    }
    
    /*private boolean validLocation(int x, int y, int tagSize)
    {
        if (x < tagSize || y < tagSize || x+tagSize >= image.getWidth() || y+tagSize >= image.getHeight()) //check image borders
            return false;
        
        for (int i = x-tagSize; i < x+tagSize+1; i++) 
        {
            for (int j = y-tagSize; j < y+tagSize+1; j++) 
            {
                if (i >= image.getWidth() || j >= image.getHeight() || tagArray[i][j] == 1)
                    return false;
            }
        }
        return true;
    }*/
    
    //Avoid if adjust out of range
    private boolean suitableColor(Color col)
    {
        int errorCount = 0;
        if (col.getRed() - ADJUSTMENT_AMOUNT < 0)
            errorCount++;
        if (col.getGreen() - ADJUSTMENT_AMOUNT < 0)
            errorCount++;
        if (col.getBlue() - ADJUSTMENT_AMOUNT < 0)
            errorCount++;
        if (col.getRed() + ADJUSTMENT_AMOUNT > 255)
            errorCount++;
        if (col.getGreen() + ADJUSTMENT_AMOUNT > 255)
            errorCount++;
        if (col.getBlue() + ADJUSTMENT_AMOUNT > 255)
            errorCount++;
        
        /*if (col.getRed() - ADJUSTMENT_AMOUNT >= 0 && col.getBlue() - ADJUSTMENT_AMOUNT && col.getGreen() - ADJUSTMENT_AMOUNT >= 0 &&
             col.getRed() - ADJUSTMENT_AMOUNT >= 0 && col.getBlue() - ADJUSTMENT_AMOUNT && col.getGreen() - ADJUSTMENT_AMOUNT >= 0)
            return true;*/
       if (errorCount > 1)
            return false;
       else 
           return true;
    }
    
    public double calculateBlockVariance(int x, int y, int tagSize)
    {
        /*if (x+tagSize >= image.getWidth() || y+tagSize >= image.getHeight())
        {
            return 10000000;
        }*/
        double total = 0;
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                if (suitableColor(colorArray[i][j]))
                    total += getBrightness(colorArray[i][j].getRed(),colorArray[i][j].getGreen(),colorArray[i][j].getBlue());
                else 
                    return 1000000;
            }
        }
        total /= (tagSize * tagSize); //Arithmetic Mean
        
        double distanceFromMean = 0;
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                distanceFromMean += Math.pow(getBrightness(colorArray[i][j].getRed(),colorArray[i][j].getGreen(),colorArray[i][j].getBlue()) - total, 2);
            }
        }
        
        return (distanceFromMean/total); 
    }
    
    public void printTaggingStats()
    {
        System.out.println("This image had:");
        System.out.println(totalPossibleTags+ " suitable locations");
        System.out.println(light+" locations made lighter");
        System.out.println(dark+" locations made darker");
        System.out.println(none+" locations not adjusted");
    }
    public void tag(int x, int y, int tagSize)
    {

        ++totalPossibleTags;
        int tagDecider = -1;
        System.out.println("tag at "+x+ " ,"+y);
        
        if (message.charAt(taggingPos) == '1')
        {
            tagDecider = 1;
            light++;
        }else if (message.charAt(taggingPos) == '0')
        {
            tagDecider = 0;
            dark++;
        }
        taggingPos++;
        
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                    if (tagDecider == 1)
                    {       
                            //Tag a 1
                            tagArray[i][j] = 1;
                            int r = colorArray[i][j].getRed()+ADJUSTMENT_AMOUNT;
                            int g =colorArray[i][j].getGreen()+ADJUSTMENT_AMOUNT;
                            int b =colorArray[i][j].getBlue()+ADJUSTMENT_AMOUNT;
                            if (r > 255)    r = 255;
                            if (b > 255)    b = 255;
                            if (g > 255)    g = 255;
                            colorArray[i][j] = new Color(r,g,b);
                            brightnessColorArray[i][j] = new Color(255,0,0);   //Bright red 1
                            
                        }else if (tagDecider == 2)
                        {
                            //Tag a 0
                            tagArray[i][j] = 1;
                            int r = colorArray[i][j].getRed()-ADJUSTMENT_AMOUNT;
                            int g =colorArray[i][j].getGreen()-ADJUSTMENT_AMOUNT;
                            int b =colorArray[i][j].getBlue()-ADJUSTMENT_AMOUNT;
                            if (r < 0)    r = 0;
                            if (b < 0)    b = 0;
                            if (g < 0)    g = 0;
                            colorArray[i][j] = new Color(r,g,b);
                            brightnessColorArray[i][j] = new Color(0,255,0);   //Bright green 0
                            
                        }else 
                        {
                            //Dont tag a location
                            tagArray[i][j] = 1;
                            brightnessColorArray[i][j] = new Color(0,0,255); //Bright blue 0
                        }
                    }
            }
        
        /*Add some random 1's and 0's
         *       
        int random = (int) (Math.random()*3);
        switch (random)
                    {
                        case 0:
                        {
                            none++;
                        }break;
                        case 1:
                        {
                            light++;
                        }break;
                        case 2:
                        {
                            dark++;
                        }break;
                    }
        
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                    if (random == 1)
                    {       
                            //Tag a 1
                            tagArray[i][j] = 1;
                            int r = colorArray[i][j].getRed()+ADJUSTMENT_AMOUNT;
                            int g =colorArray[i][j].getGreen()+ADJUSTMENT_AMOUNT;
                            int b =colorArray[i][j].getBlue()+ADJUSTMENT_AMOUNT;
                            if (r > 255)    r = 255;
                            if (b > 255)    b = 255;
                            if (g > 255)    g = 255;
                            colorArray[i][j] = new Color(r,g,b);
                            brightnessColorArray[i][j] = new Color(255,0,0);   //Bright red 1
                            
                        }else if (random == 2)
                        {
                            //Tag a 0
                            tagArray[i][j] = 1;
                            int r = colorArray[i][j].getRed()-ADJUSTMENT_AMOUNT;
                            int g =colorArray[i][j].getGreen()-ADJUSTMENT_AMOUNT;
                            int b =colorArray[i][j].getBlue()-ADJUSTMENT_AMOUNT;
                            if (r < 0)    r = 0;
                            if (b < 0)    b = 0;
                            if (g < 0)    g = 0;
                            colorArray[i][j] = new Color(r,g,b);
                            brightnessColorArray[i][j] = new Color(0,255,0);   //Bright green 0
                            
                        }else 
                        {
                            //Dont tag a location
                            tagArray[i][j] = 1;
                            brightnessColorArray[i][j] = new Color(0,0,255); //Bright blue 0
                        }
                    }
            }*/
    }
    
    
    boolean validLocation(int x, int y, int tagS)
    {
        if (x < tagS || y < tagS || x+tagS >= image.getWidth() || y+tagS >= image.getHeight()) //check image borders
            return false;
        
        for (int i = x-tagS; i < x+tagS+tagS; i++) 
        {
            for (int j = y-tagS; j < y+tagS+tagS; j++) 
            {
                if (i >= image.getWidth() || j >= image.getHeight() || tagArray[i][j] == 1)
                    return false;
            }
        }
        return true;
    }        
            
    
    public void calculateTagLocations(int tagSize)
    {
        for (int i = 0; i < image.getWidth(); i++) 
        {
            for (int j = 0; j < image.getHeight(); j++) 
            {
                if (validLocation(i,j,tagSize))
                {
                    double variance = calculateBlockVariance(i,j,tagSize);
                    if (variance < THRESHOLD && variance > LOWER_THRESHOLD)
                    {
                        if (taggingPos <  message.length())
                            tag(i,j,tagSize);
                        else
                        { 
                            System.out.println(taggingPos+" tags embedded. Tag Done");
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public BufferedImage getImage()
    {
        BufferedImage bufferedImage = new BufferedImage(colorArray.length, colorArray[0].length,BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < colorArray.length; x++) {
            for (int y = 0; y < colorArray[x].length; y++) {
                bufferedImage.setRGB(x, y, colorArray[x][y].getRGB());
            }
        }
        return bufferedImage;
    }
    
    public void saveBrightTags()
    {
        BufferedImage bufferedImage = new BufferedImage(colorArray.length, colorArray[0].length,BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < colorArray.length; x++) {
            for (int y = 0; y < colorArray[x].length; y++) {
                bufferedImage.setRGB(x, y, brightnessColorArray[x][y].getRGB());
            }
        }
        File outputfile = new File("originalBrightTags.png");
        try {
            ImageIO.write(bufferedImage, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
