/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UEA;

import java.net.*;
//import java.nio.ByteBuffer;
import java.io.*;

/**
 *
 * @author OWNER
 */
public class VideoSender implements Runnable
{
    
    static DatagramSocket sending_socket;
    static DatagramSocket receiving_socket;
    
    @Override
    public void run()
    {
        int PORT = 55555;
        InetAddress clientIP = null;
        try{
            //clientIP = InetAddress.getByName("139.222.230.1");
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
        System.out.println("ERROR: NetworkSender: Could not find client IP");
                e.printStackTrace();
        System.exit(0);
        }
        
        try{
            sending_socket = new DatagramSocket();//Making a sending socket
        } catch (SocketException e){
            System.out.println("ERROR: NetworkSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            receiving_socket = new DatagramSocket(5558);//Making a receiving socket
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        
        VideoIO sender = new VideoIO();
        sender.SetSender();
        sender.Twotest();
        
        Security manager = new Security();
        byte[] theirPublicKey = new byte[294];
        DatagramPacket theirs = new DatagramPacket(theirPublicKey, 0, theirPublicKey.length);
        
        ReceivePacket(theirs);
        manager.SetTheirPublic(theirPublicKey);
        manager.GenerateAES();
        
        byte[] encryptedAES = manager.GetEncryptedmyAESKey();
        DatagramPacket myKey = new DatagramPacket(encryptedAES, encryptedAES.length, clientIP, 5552);
        SendPacket(myKey);
        
        while (true)//Keep sending video
        {
            byte[] frame = sender.GetNextFrame();
            byte[] encrypted = manager.Encrypt(frame);
            //byte[] x = test.compress(frame);
            //byte[] y = test.compress(x);
            //byte[] xy = test.decompress(y);
            //byte[] xyz = test.decompress(xy);
            //test.DisplayFrame(frame);
            //System.out.println(encrypted.length);
            //DatagramPacket z = new DatagramPacket(frame, frame.length, clientIP, PORT);
            DatagramPacket z = new DatagramPacket(encrypted, encrypted.length, clientIP, PORT);
            SendPacket(z);
        }
    }
    
    private void SendPacket(DatagramPacket packet)
    {
        try{
            sending_socket.send(packet);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    private void ReceivePacket(DatagramPacket packet)
    {
        try{
            receiving_socket.receive(packet);
        } catch (IOException e) {
            System.out.println("ERROR: Receiver: IO error occured!");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {
        
    }
}
