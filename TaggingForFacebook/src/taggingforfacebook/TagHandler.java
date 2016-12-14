package taggingforfacebook;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

/**
 * @author Matthew
 */
public class TagHandler 
{
    private BufferedImage baseImage;
    private BufferedImage taggedImage;
    double THRESHOLD;
    double LOWER_THRESHOLD;
    Color brightnessColorArray[][];
    Color colorArrayBase[][];
    Color colorArrayTagged[][];
    int tagArray[][];
    int dark = 0;
    int light = 0;
    int none = 0;
    int totalPossibleTags = 0;
    private int ADJUSTMENT_AMOUNT = 4;
    String strConstruct = "";
    int spaces = 0;
    
    /* Class that calculates where tags should've been placed, and 
     * checks the likeliness that there is actually a tag there */
    TagHandler(BufferedImage base, BufferedImage tagged)
    {
        THRESHOLD = 20;
        LOWER_THRESHOLD = 10;
        baseImage = base;
        taggedImage = tagged;
        brightnessColorArray = new Color[baseImage.getWidth()][baseImage.getHeight()];
        colorArrayBase = new Color[baseImage.getWidth()][baseImage.getHeight()];
        colorArrayTagged = new Color[baseImage.getWidth()][baseImage.getHeight()];
        tagArray = new int[baseImage.getWidth()][baseImage.getHeight()];
        addColorsToArray();
    }
    
    private void addColorsToArray()
    {
        for (int i = 0; i < baseImage.getWidth(); i++) 
        {
            for (int j = 0; j < baseImage.getHeight(); j++) 
            {
                  tagArray[i][j] = 0;   //Not tagged
                  colorArrayBase[i][j] = new Color(baseImage.getRGB(i,j));
                  colorArrayTagged[i][j] = new Color(taggedImage.getRGB(i,j));
                  brightnessColorArray[i][j] = new Color(taggedImage.getRGB(i,j));
            }
        }
    }
    
    public void tag(int x, int y, int tagSize)
    {
        //System.out.println("Checking "+x+", "+y);
        int sum = 0;
        int count = 0;
        
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                    ++count;
                    sum += colorArrayTagged[i][j].getRed()-colorArrayBase[i][j].getRed();
                    sum += colorArrayTagged[i][j].getGreen()-colorArrayBase[i][j].getGreen();
                    sum += colorArrayTagged[i][j].getBlue()-colorArrayBase[i][j].getBlue();
                    tagArray[i][j] = 1;
                    
                    
                    /*int r = colorArray[i][j].getRed()+255;
                    int g =colorArray[i][j].getGreen()+255;
                    int b =colorArray[i][j].getBlue()+255;
                    if (r > 255)    r = 255;
                    if (b > 255)    b = 255;
                    if (g > 255)    g = 255;
                    colorArray[i][j] = new Color(r,g,b); */
                    
            }
        }
        double avg = ((double)sum/(double)count/3.0);
        System.out.println(avg+"\n");
        ++totalPossibleTags;
        
        //Calculate how to split
        double temp = ((ADJUSTMENT_AMOUNT*2+1)/3);
        int whichColor = -1;
        
        if (avg >= (-1*ADJUSTMENT_AMOUNT+temp) && avg <= ADJUSTMENT_AMOUNT-temp)
        {     
            ++none; 
            whichColor = 3;
            strConstruct+="0";
            if ((strConstruct.length()-spaces)%8==0){
                strConstruct+=" ";
                spaces++;
            }
        }else if (avg > ADJUSTMENT_AMOUNT-temp){
            ++light;
            whichColor = 1;
            strConstruct+="1";
            if ((strConstruct.length()-spaces)%8==0){
                strConstruct+=" ";
                spaces++;
            }
        }
        else{
            ++dark;
            whichColor = 2;
        }
        
        //Color the brightness array for visual comparison
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                    if (whichColor == 1)
                    {       
                            //Tag a 1
                            brightnessColorArray[i][j] = new Color(255,0,0);   //Bright red 1
                            
                        }else if (whichColor == 2)
                        {
                            //Tag a 0
                            brightnessColorArray[i][j] = new Color(0,255,0);   //Bright green 0
                            
                        }else 
                        {
                            //Dont tag a location
                            brightnessColorArray[i][j] = new Color(0,0,255); //Bright blue nothing
                        }
                    }
            }
    }
    
    public static String int2str(String s) 
    { 
        String[] ss = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < ss.length; i++ ) 
        { 
            sb.append((char)Integer.parseInt(ss[i], 2));                                                                                                                                                        
        }   
        return sb.toString();
    } 
    
    public void printTaggingStats(JRootPane root)
    {
        String mess = int2str(strConstruct);
        if (mess.contains("#"))
        {
            String [] messy = mess.split("#");
            JOptionPane.showMessageDialog(root, "Message found:\n"+messy[0]);
        }else  
            JOptionPane.showMessageDialog(root, "No Message found");
        System.out.println("Binary String is: "+strConstruct);
        System.out.println("This image had:");
        System.out.println(totalPossibleTags+ " suitable locations");
        System.out.println(light+" locations made lighter");
        System.out.println(dark+" locations made darker");
        System.out.println(none+" locations not adjusted");
    }
        
    public void calculateOriginalLocations(int tagSize)
    {
        for (int i = 0; i < baseImage.getWidth(); i++) 
        {
            for (int j = 0; j < baseImage.getHeight(); j++) 
            {
                if (validLocation(i,j,tagSize))
                {
                    double variance = calculateBlockVariance(i,j,tagSize);
                    if (variance < THRESHOLD && variance > LOWER_THRESHOLD)
                    {
                        tag(i,j,tagSize);
                    }
                }
            }
        }
    }
    
    boolean validLocation(int x, int y, int tagS)
    {
        if (x < tagS || y < tagS || x+tagS >= baseImage.getWidth() || y+tagS >= baseImage.getHeight()) //check image borders
            return false;
        
        for (int i = x-tagS; i < x+tagS+tagS; i++) 
        {
            for (int j = y-tagS; j < y+tagS+tagS; j++) 
            {
                if (i >= baseImage.getWidth() || j >= baseImage.getHeight() || tagArray[i][j] == 1)
                    return false;
            }
        }
        return true;
    }  
    
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
        double total = 0;
        for (int i = x; i < x+tagSize; i++)     
        {
            for (int j = y; j < y+tagSize; j++) 
            {
                if (suitableColor(colorArrayBase[i][j]))
                    total += getBrightness(colorArrayBase[i][j].getRed(),colorArrayBase[i][j].getGreen(),colorArrayBase[i][j].getBlue());
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
                distanceFromMean += Math.pow(getBrightness(colorArrayBase[i][j].getRed(),colorArrayBase[i][j].getGreen(),colorArrayBase[i][j].getBlue()) - total, 2);
            }
        }
        return (distanceFromMean/total); 
    }
    
    private double getBrightness(int r, int g, int b)
    {
        return (0.2126*r + 0.7152*g + 0.0722*b);
    }
    
    public BufferedImage getImage()
    {
        BufferedImage bufferedImage = new BufferedImage(colorArrayBase.length, colorArrayBase[0].length,BufferedImage.TYPE_INT_RGB);
        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < colorArrayBase.length; x++) {
            for (int y = 0; y < colorArrayBase[x].length; y++) {
                bufferedImage.setRGB(x, y, colorArrayBase[x][y].getRGB());
            }
        }
        return bufferedImage;
    }
    
    public void saveBrightTags()
    {
        BufferedImage bufferedImage = new BufferedImage(colorArrayTagged.length, colorArrayTagged[0].length,BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < colorArrayTagged.length; x++) {
            for (int y = 0; y < colorArrayTagged[x].length; y++) {
                bufferedImage.setRGB(x, y, brightnessColorArray[x][y].getRGB());
            }
        }
        File outputfile = new File("newBrightTags.png");
        try {
            ImageIO.write(bufferedImage, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
