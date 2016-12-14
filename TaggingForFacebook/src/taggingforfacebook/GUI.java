package taggingforfacebook;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Matthew
 */
public class GUI extends javax.swing.JFrame 
{
    int tagSize = 12;    //default of 12x12 pixels to tag
    BufferedImage img;
    BufferedImage baseImg;
    double maxWidth = 750;
    double maxHeight = 750;
    ImageParser ip;
    public GUI() {
        
        initComponents();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        initVariables();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem5 = new javax.swing.JMenuItem();
        lblImage = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        miSavePNG = new javax.swing.JMenuItem();
        miSaveJPG = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        miAddTags = new javax.swing.JMenuItem();
        miScanForTags = new javax.swing.JMenuItem();
        miSetOriginal = new javax.swing.JMenuItem();

        jMenuItem5.setText("jMenuItem5");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tagging For Facebook");
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.FlowLayout());

        lblImage.setBackground(new java.awt.Color(204, 0, 153));
        lblImage.setForeground(new java.awt.Color(255, 102, 102));
        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(lblImage);

        jMenu1.setText("File");

        jMenuItem1.setText("Open Image");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        miSavePNG.setText("Save Image (.png)");
        miSavePNG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSavePNGActionPerformed(evt);
            }
        });
        jMenu1.add(miSavePNG);

        miSaveJPG.setText("Save Image (.jpeg)");
        miSaveJPG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveJPGActionPerformed(evt);
            }
        });
        jMenu1.add(miSaveJPG);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem2.setText("Tag Size");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("Tag Brightness");
        jMenu2.add(jMenuItem3);

        jMenuItem7.setText("Threshold");
        jMenu2.add(jMenuItem7);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Tagging");

        jMenuItem4.setText("Add Message");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        miAddTags.setText("Add Tags");
        miAddTags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddTagsActionPerformed(evt);
            }
        });
        jMenu3.add(miAddTags);

        miScanForTags.setText("Scan for Tags");
        miScanForTags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miScanForTagsActionPerformed(evt);
            }
        });
        jMenu3.add(miScanForTags);

        miSetOriginal.setText("Set Original Image");
        miSetOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSetOriginalActionPerformed(evt);
            }
        });
        jMenu3.add(miSetOriginal);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int w, int h) {
        BufferedImage resizedImage = new BufferedImage(w, h, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        g.dispose();
        return resizedImage;
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.showOpenDialog(rootPane);
        
        File file = chooser.getSelectedFile();
        try {
            img = ImageIO.read(file);
            //baseImg = img;
            BufferedImage resizedImg = null;
            int cushioning = 40;
            if (img.getWidth() > maxWidth || img.getHeight() > maxHeight) {
                if (img.getWidth() < img.getHeight()) 
                {
                    resizedImg = resizeImage(img, 1, (int) ((double) img.getWidth() / (double) img.getHeight() * maxWidth), (int) maxHeight);
                    this.setMinimumSize(new Dimension((int) ((double) img.getWidth() / (double) img.getHeight() * maxWidth) + cushioning, (int) maxHeight + cushioning + 41));
                    this.setPreferredSize(new Dimension((int) ((double) img.getWidth() / (double) img.getHeight() * maxWidth) + cushioning, (int) maxHeight + cushioning + 41));
                    this.setSize(new Dimension((int) ((double) img.getWidth() / (double) img.getHeight() * maxWidth) + cushioning, (int) maxHeight + cushioning + 41));
                } else 
                {
                    System.out.println("here");
                    resizedImg = resizeImage(img, 1, (int) maxWidth, (int) ((double) img.getHeight() / (double) img.getWidth() * maxHeight));
                    this.setMinimumSize(new Dimension((int) maxWidth + cushioning, (int) ((double) img.getHeight() / (double) img.getWidth() * maxHeight + cushioning) + 41));
                    this.setPreferredSize(new Dimension((int) maxWidth + cushioning, (int) ((double) img.getHeight() / (double) img.getWidth() * maxHeight + cushioning) + 41));
                    this.setSize(new Dimension((int) maxWidth + cushioning, (int) ((double) img.getHeight() / (double) img.getWidth() * maxHeight + cushioning) + 41));
                }
            }else
            {
                resizedImg = img;
            }

            ImageIcon icon = new ImageIcon(resizedImg);
            lblImage.setIcon(icon);

            Dimension imageSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
            lblImage.setPreferredSize(imageSize);

            lblImage.revalidate();
            lblImage.repaint();
        } catch (Exception e1) {
            System.out.println(e1.toString());
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        int oldTagSize = tagSize;
        try{
        tagSize = Integer.parseInt(JOptionPane.showInputDialog(rootPane,"Please set the tag size (currently "+tagSize+")"));
        }catch (Exception e1)
        {
            tagSize = oldTagSize;
            JOptionPane.showMessageDialog(rootPane, "Please enter an appropriate numeric value");
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    String getMessage()
    {
        String str = JOptionPane.showInputDialog("Enter your message");
        str+="#";
        byte[] bytes = str.getBytes();
        StringBuilder binary = new StringBuilder();
        StringBuilder embed = new StringBuilder();
        for (byte b : bytes)
        {
           int val = b;
           for (int i = 0; i < 8; i++)
           {
              binary.append((val & 128) == 0 ? 0 : 1);
              embed.append((val & 128) == 0 ? 0 : 1);
              val <<= 1;
           }
           binary.append(' ');
        }
        System.out.println("'" + str + "' to binary: " + binary);
        return embed.toString();
    }
    private void miAddTagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddTagsActionPerformed
           String message = getMessage();
        //try 
        //{
            ip = new ImageParser(img, tagSize, message);
            ip.calculateTagLocations(tagSize);
            ip.printTaggingStats(); 
            img = ip.getImage();
            BufferedImage resizedImg = resizeImage(img, 1, (int) ((double) img.getWidth() / (double) img.getHeight() * maxWidth), (int) maxHeight);
            ImageIcon icon = new ImageIcon(resizedImg);
            lblImage.setIcon(icon);
            ip.saveBrightTags();
            System.out.println("tag done");
            //ip.loopImage();
        //} catch (Exception e) 
        //{
          //  JOptionPane.showMessageDialog(rootPane, "No image has been selected");
        //}
    }//GEN-LAST:event_miAddTagsActionPerformed

    private void miSavePNGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSavePNGActionPerformed
        File outputfile = new File("taggedImage.png");
        try {
            ImageIO.write(img, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_miSavePNGActionPerformed

    private void miSetOriginalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSetOriginalActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.showOpenDialog(null);
        File file = chooser.getSelectedFile();
        try {
            baseImg = ImageIO.read(file);
            System.out.println(baseImg.getHeight()+ " " +baseImg.getWidth());
        } catch (Exception e1) {
            System.out.println(e1.toString());
        }
    }//GEN-LAST:event_miSetOriginalActionPerformed

    private void miScanForTagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miScanForTagsActionPerformed
        if (baseImg == null)
        {
            JOptionPane.showMessageDialog(rootPane, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat");
            return;
        }else if (img == null)
        {
            JOptionPane.showMessageDialog(rootPane, "No image available to scan");
            return;
        }else if (baseImg.getWidth() != img.getWidth() || baseImg.getHeight() != img.getHeight())
        {
            JOptionPane.showMessageDialog(rootPane, "Image dimensions don't match, please pre-process them");
            return;
        }
        TagHandler th = new TagHandler(baseImg, img);
        th.calculateOriginalLocations(tagSize);
        th.printTaggingStats(rootPane);
        th.saveBrightTags();
        /*BufferedImage test = th.getImage();
        File outputfile = new File("imageTest2.png");
        try {
            ImageIO.write(test, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
    }//GEN-LAST:event_miScanForTagsActionPerformed

    private void miSaveJPGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveJPGActionPerformed
        File outputfile = new File("taggedImage.jpeg");
        try {
            ImageIO.write(img, "jpeg", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_miSaveJPGActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    public void initVariables() {
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JLabel lblImage;
    private javax.swing.JMenuItem miAddTags;
    private javax.swing.JMenuItem miSaveJPG;
    private javax.swing.JMenuItem miSavePNG;
    private javax.swing.JMenuItem miScanForTags;
    private javax.swing.JMenuItem miSetOriginal;
    // End of variables declaration//GEN-END:variables
}
