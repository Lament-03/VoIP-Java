/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UEA;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.*;
import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.Encoder;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.openimaj.image.DisplayUtilities;
import java.util.List;
import org.openimaj.image.MBFImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.hadoop.hdfs.server.namenode.INodeFile;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.capture.*;
import org.tukaani.xz.UnsupportedOptionsException;
/**
 *
 * @author OWNER
 */
public class VideoIO
{
    private VideoCapture cap;
    private int width = 854;
    private int height = 480;
    JFrame vid = new JFrame();
    
    /*public VideoIO(int w, int h)
    {
        try
        {
            cap = new VideoCapture(w, h);
            vid.setSize(w, h);
            vid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            vid.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }*/
    
    public void SetSender()
    {
        try
        {
            //Open webcam to start recording
            cap = new VideoCapture(width, height);
        } catch (VideoCaptureException ex) {
            Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SetReceiver()//Set up the JFrame for displaying the video
    {
        vid.setSize(width, height);
        vid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vid.setVisible(true);
    }
    
    public byte[] GetNextFrame()
    {
        if (IsOpen())
        {
            MBFImage x = cap.getNextFrame();
            //byte[] s = x.toByteImage();
            //Convert the frame into a bufferedimage
            BufferedImage image = ImageUtilities.createBufferedImageForDisplay(x);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //Compress frame 
            try {
                ImageIO.write(image, "jpg", out);
                out.flush();
                out.close();
                byte[] compressed = out.toByteArray();
                return compressed;
            } catch (IOException ex) {
                Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        else
        {
            System.out.println("Video Capture device has disconnected");
            System.exit(-1);
            return null;
        }
    }
    
    public void DisplayFrame(byte[] frame)
    {
        /*int dstLength = frame.length >>> 2;
        int[]dst = new int[dstLength];
        
        for (int i=0; i<dstLength; i++) {
            int j = i << 2;
            int x = 0;
            x += (frame[j++] & 0xff) << 0;
            x += (frame[j++] & 0xff) << 8;
            x += (frame[j++] & 0xff) << 16;
            x += (frame[j++] & 0xff) << 24;
            dst[i] = x;
        }*/
        /*MBFImage x = new MBFImage(width, height);
        FImage y = new FImage(width, height);
        x.internalAssign(frame, width, height);
        //MBFImage x = y.toRGB();
        DisplayUtilities.displayName(x, "video");*/
        
        try {
            BufferedImage newFrame = ImageIO.read(new ByteArrayInputStream(frame));
            Graphics g = vid.getGraphics();
            g.drawImage(newFrame, 0, 0, null);
            //JLabel label = new JLabel(new ImageIcon(newFrame));
            //vid.add(label);
            //vid.setVisible(true);
        } catch (Exception e) {
            //Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
            e.printStackTrace();
        }
        
    }
    
    public void framerate()
    {
        System.out.println(cap.getFPS());
    }
    
    public void list()
    {
        List<Device> e = VideoCapture.getVideoDevices();
    }
    
    private boolean IsOpen()
    {
        return cap.hasNextFrame();
    }
    
    //Unused methods
    public byte[] compress(byte[] input)
    {
        /*Deflater compressor = new Deflater(9);
        compressor.setInput(input);
        compressor.finish();
        
        ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
        byte[] outputBuffer = new byte[512]; // Adjust buffer size as needed
        int x = 0;
        while (!compressor.finished())
        {
            int compressedDataSize = compressor.deflate(outputBuffer);
            compressedData.write(outputBuffer, 0, compressedDataSize);
            /*x++;
            if (x == 66000){
                System.out.println(x);
            }
        }
        return compressedData.toByteArray();*/
        /*Brotli4jLoader.ensureAvailability();
        try {
            byte[] compressed = Encoder.compress(input);
            return compressed;
        } catch (IOException ex) {
            Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }*/
        
        /*try {
            LZMA2Options options = new LZMA2Options(9);
            ByteArrayInputStream rawData = new ByteArrayInputStream(input);
            ByteArrayOutputStream compressedData = new ByteArrayOutputStream();
            XZOutputStream out = new XZOutputStream(compressedData, options);
            byte[] buffer = new byte[1000000];
            int size;
            while ((size = rawData.read(buffer)) != -1)
            {
                out.write(buffer, 0, size);
            }
            out.finish();
            System.out.println("UEA.VideoIO.compress()");
            return compressedData.toByteArray();
        } catch (Exception ex) {
            Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }*/
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            //BufferedImage image = ImageUtilities
            ByteArrayInputStream in = new ByteArrayInputStream(input);
            BufferedImage image = ImageIO.read(in);
            ImageIO.write(image, "jpg", out);
            out.flush();
            out.close();
            byte[] compressed = out.toByteArray();
            return compressed;
            //ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            //ImageOutputStream stream = ImageIO.createImageOutputStream(out);
            //writer.setOutput(stream);
            //writer.write(null, new IIOImage(image, null, null));*/
        } catch (IOException ex) {
            Logger.getLogger(VideoIO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public byte[] decompress(byte[] input)
    {
        Inflater inflater = new Inflater();
        inflater.setInput(input);
        
        ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
        byte[] buffer = new byte[1000000];
        
        while (!inflater.finished())
        {
            try{
                int decompressedSize = inflater.inflate(buffer);
                decompressedData.write(buffer, 0, decompressedSize);
            } catch (DataFormatException e){
                System.out.println(e.getMessage());
            }
        }
        return decompressedData.toByteArray();
    }
    
    public void Twotest()//Test method for displaying video
    {
        JFrame vid2 = new JFrame("No compression");
        VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(cap, vid2);
        System.out.println(cap.getFPS());
    }
    
    public static void main(String args[])
    {
        /*VideoIO x = new VideoIO();
        x.framerate();
        while (true)
        {
            byte[] frame = x.GetNextFrame();
            x.DisplayFrame(frame);
        }*/
        
    }
}
