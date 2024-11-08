/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UEA;

import java.net.*;
import java.io.*;
/**
 *
 * @author OWNER
 */
public class VideoReceiver implements Runnable
{
    static DatagramSocket sending_socket;
    static DatagramSocket receiving_socket;
    static DatagramSocket receiving_socket2;//For receiving public key
    
    @Override
    public void run()
    {
        int PORT = 55555;
        InetAddress clientIP = null;
        try{
            //clientIP = InetAddress.getByName("139.222.230.1");
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: NetworkReceiver: Could not find client IP");
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
            System.out.println("ERROR: NetworkReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            receiving_socket2 = new DatagramSocket(5552);//Making a receiving socket
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        
        VideoIO receiver = new VideoIO();
        receiver.SetReceiver();
        
        Security manager = new Security();
        manager.GenerateKeys(); //Generate RSA keys
        byte[] myPublic = manager.GetMyPublic();
        DatagramPacket myPublicKey = new DatagramPacket(myPublic, myPublic.length, clientIP, 5558);
        SendPacket(myPublicKey);//Send public key to sender
        
        byte[] theirKey = new byte[256];
        DatagramPacket receiveKey = new DatagramPacket(theirKey, 0, theirKey.length);
        ReceivePacket2(receiveKey);//Receive their public key
        manager.SetTheirAES(theirKey);
        
        int x = 1;
        while(true)//Keep receiving video
        {
            byte[] buffer = new byte[60000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            ReceivePacket(packet);
            /*int size = buffer.length;
            for (int i = buffer.length - 1; i > 0; i--)
            {
                if (buffer[i] == 0 )
                {
                    size--;
                }
                else
                {
                    break;
                }
            }
            byte[] frame = new byte[size];
            System.arraycopy(buffer, 0, frame, 0, size);*/
            int numOfBytes = ExtractInt(buffer);//Extract the number of bytes in the frame
            byte[] video = new byte[numOfBytes];
            System.arraycopy(buffer, 4, video, 0, numOfBytes);
            byte[] decrypted = manager.Decrypt(video);
            if (x == 1)
            {
                System.out.println("Received frame size: " + decrypted.length);
                x++;
            }
            receiver.DisplayFrame(video);
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
    
    private int ExtractInt(byte[] array)
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            value = (value << 8) + (array[i] & 0xFF);
        }
        return value;
    }
}
