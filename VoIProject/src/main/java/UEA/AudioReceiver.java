/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UEA;

import java.net.*;
import java.io.*;
import CMPC3M06.AudioPlayer;
import java.nio.ByteBuffer;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author OWNER
 */
public class AudioReceiver implements Runnable
{
    static DatagramSocket receiving_socket;//For receiving audio
    static DatagramSocket receiving_socket2;//For receiving public key
    static DatagramSocket sending_socket;
    
    public void run()
    {
        AudioPlayer player;
        try{//Initialise audio player
            player = new AudioPlayer();
        } catch (LineUnavailableException e){
            throw new RuntimeException(e);
        }
        int PORT = 55556;
        InetAddress clientIP = null;
        try {
            //clientIP = InetAddress.getByName("139.222.230.1");
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            sending_socket = new DatagramSocket();//Making a sending socket
        } catch (SocketException e){
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            receiving_socket = new DatagramSocket(PORT);//Making a receiving socket
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            receiving_socket2 = new DatagramSocket(5551);//Making a receiving socket
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        
        Security manager = new Security();
        manager.GenerateKeys();
        byte[] myPublic = manager.GetMyPublic();
        DatagramPacket myPublicKey = new DatagramPacket(myPublic, myPublic.length, clientIP, 5557);
        //DatagramPacket myPublicKey = new DatagramPacket(myPublic, myPublic.length, clientIP, PORT);
        SendPacket(myPublicKey);//Send public key to sender
        
        byte[] theirKey = new byte[256];//Receive their public key
        DatagramPacket receiveKey = new DatagramPacket(theirKey, 0, theirKey.length);
        ReceivePacket2(receiveKey);
        manager.SetTheirAES(theirKey);
        
        while (true)//Receive audio and play it
        {
            try{
                byte[] buffer = new byte[600];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                ReceivePacket(packet);

                int numOfBytes = ExtractInt(buffer);
                byte[] audio = new byte[numOfBytes];
                System.arraycopy(buffer, 4, audio, 0, numOfBytes);//Extract audio from buffer
                byte[] decrypted = manager.Decrypt(audio);
                //player.playBlock(audio);
                player.playBlock(decrypted);
            
            } catch (IOException e)
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
    
    private void ReceivePacket2(DatagramPacket packet)
    {
        try{
            receiving_socket2.receive(packet);
        } catch (IOException e) {
            System.out.println("ERROR: Receiver: IO error occured!");
            e.printStackTrace();
        }
    }
    
    private int ExtractInt(byte[] array)//Extract an integer from a byte array
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            value = (value << 8) + (array[i] & 0xFF);
        }
        return value;
    }
}
