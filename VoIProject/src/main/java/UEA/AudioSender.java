/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UEA;

import CMPC3M06.AudioRecorder;
import java.net.*;
import java.io.*;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author OWNER
 */
public class AudioSender implements Runnable
{
    static DatagramSocket sending_socket;
    static DatagramSocket receiving_socket;
    
    public void run()
    {
        int PORT = 55556;
        InetAddress clientIP = null;
        try {
            //clientIP = InetAddress.getByName("139.222.230.1");
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Sender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            sending_socket = new DatagramSocket();//Making a sending socket
        } catch (SocketException e){
            System.out.println("ERROR: Sender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            receiving_socket = new DatagramSocket(5557);//Making a receiving socket
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        
        //Key exchange
        Security manager = new Security();
        byte[] theirPublicKey = new byte[294];
        DatagramPacket theirs = new DatagramPacket(theirPublicKey, 0, theirPublicKey.length);
        
        ReceivePacket(theirs);
        manager.SetTheirPublic(theirPublicKey);
        manager.GenerateAES();
        
        byte[] encryptedAES = manager.GetEncryptedmyAESKey();
        DatagramPacket myKey = new DatagramPacket(encryptedAES, encryptedAES.length, clientIP, 5551);
        SendPacket(myKey);
        AudioRecorder recorder = null;
        try{
        recorder = new AudioRecorder();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        while (true)//Keep sending audio
        {
            try{
                //AudioRecorder recorder = new AudioRecorder();
                for (int i = 0; i < Math.ceil(10 / 0.032); i++)
                {
                    byte[] block = recorder.getBlock();
                    byte[] encrypted = manager.Encrypt(block);
                    //DatagramPacket packet = new DatagramPacket(block, block.length, clientIP, PORT);
                    DatagramPacket packet = new DatagramPacket(encrypted, encrypted.length, clientIP, PORT);
                    SendPacket(packet);
                }
                
                //recorder.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
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
}
