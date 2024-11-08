package UEA;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.convolution.FGaussianConvolve;
import org.openimaj.image.typography.hershey.HersheyFont;

/**
 * OpenIMAJ Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
    	//Create an image
        //MBFImage image = new MBFImage(320,70, ColourSpace.RGB);

        //Fill the image with white
        //image.fill(RGBColour.WHITE);
        		        
        //Render some test into the image
        //image.drawText("Hello World", 10, 60, HersheyFont.CURSIVE, 50, RGBColour.BLACK);

        //Apply a Gaussian blur
        //image.processInplace(new FGaussianConvolve(2f));
        
        //Display the image
        //DisplayUtilities.display(image);
        
        VideoSender send = new VideoSender();
        VideoReceiver receive = new VideoReceiver();
        AudioSender asend = new AudioSender();
        AudioReceiver areceive = new AudioReceiver();
        Thread thread1 = new Thread(send);
        Thread thread2 =  new Thread(receive);
        Thread thread3 = new Thread(asend);
        Thread thread4 = new Thread(areceive);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        /*VideoIO test = new VideoIO();
        test.SetSender();
        test.SetReceiver();
        test.Continuous();*/
    }
}
