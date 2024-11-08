/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UEA;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author OWNER
 */
public class Security
{
    PublicKey myPublicKey;
    PublicKey theirPublicKey;
    PrivateKey privateKey;
    SecretKey myAESKey;
    SecretKey theirAESKey;
    
    public void GenerateKeys()
    {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            myPublicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void GenerateAES()
    {
        try
        {
            KeyGenerator AESGen = KeyGenerator.getInstance("AES");
            AESGen.init(128);
            myAESKey = AESGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Security.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] Encrypt(byte[] data)//Encrypts data using RSA
    {
        try
        {
            //Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");//Create a cipher instance of encryption
            /*Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, theirPublicKey);
            
            int blockSize = rsa.getOutputSize(data.length);//Find how big each chunk of data will be
            int encryptedLength = 0;
            int maxDataPerBlock = blockSize - 11;
            int numberofBlocks = (data.length + maxDataPerBlock - 1) / maxDataPerBlock;
            int realLength = numberofBlocks * blockSize;
            byte[] encrypted = new byte[realLength];
            //Calculate the number of complete blocks that can be formed, and adds 1 to the calculated number if length is not a perfect multiple
            //byte[] encrypted = new byte[blockSize * ((data.length / blockSize) + (data.length % blockSize == 0 ? 0 : 1))];
            for (int i = 0; i < data.length; i+= blockSize)
            {
                int chunkSize = Math.min(maxDataPerBlock, data.length - i);
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(data, i, chunk, 0, chunkSize);//Take a chunk size from the original data
                byte[] encryptedChunk = rsa.doFinal(chunk);//Apply RSA encryption to it
                System.arraycopy(encryptedChunk, 0, encrypted, i / maxDataPerBlock * blockSize, encryptedChunk.length);
                //System.arraycopy(encryptedChunk, 0, encrypted, encryptedLength, encryptedChunk.length);
                encryptedLength += encryptedChunk.length;
            }
            return encrypted;
            //return Arrays.copyOf(encrypted, encryptedLength);//Remove any excess padding bytes
            /*Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, theirPublicKey);
            byte[] x = rsa.doFinal(data);
            return x;*/
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, myAESKey);
            byte[] encrypted = aes.doFinal(data);
            //Adding length of encrypted data to the packet
            ByteBuffer together = ByteBuffer.allocate(encrypted.length + 4);
            together.putInt(encrypted.length);
            together.put(encrypted);
            return together.array();
            
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public byte[] Decrypt(byte[] encryptedData)
    {
        try
        {
            /*Cipher rsa = Cipher.getInstance("RSA/ECB/NoPadding");
            rsa.init(Cipher.DECRYPT_MODE, privateKey);
            
            int blockSize = rsa.getOutputSize(0);
            int decryptedLength = 0;
            int numBlocks = encryptedData.length / blockSize;
            if (encryptedData.length % blockSize != 0 ){
                numBlocks++;
            }
            //byte[] decrypted = new byte[blockSize * ((encryptedData.length / blockSize) + (encryptedData.length % blockSize == 0 ? 0 : 1))];
            byte[] decrypted = new byte[numBlocks * blockSize];
            /*for (int i = 0; i < encryptedData.length; i+= blockSize)
            {
                int chunkSize = Math.min(blockSize, encryptedData.length - i);
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(encryptedData, i, chunk, 0, chunkSize);
                byte[] decryptedChunk = rsa.doFinal(chunk);
                System.arraycopy(decryptedChunk, 0, decrypted, decryptedLength, decryptedChunk.length);//Copy the encrypted chunk into the array
                decryptedLength += decryptedChunk.length;
            }*/
            /*for (int i = 0; i < numBlocks; i++)
            {
                int offset = i * blockSize;
                int chunkLength = Math.min(blockSize, encryptedData.length - offset);
                byte[] encryptedChunk = new byte[chunkLength];
                System.arraycopy(encryptedData, offset, encryptedChunk, 0, chunkLength);
                byte[] decryptedChunk = rsa.doFinal(encryptedChunk);
                System.arraycopy(decryptedChunk, 0, decrypted, offset, decryptedChunk.length);
            }*/
            //return Arrays.copyOf(decrypted, decryptedLength);
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, theirAESKey);
            byte[] decrypted = aes.doFinal(encryptedData);
            return decrypted;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public byte[] GetMyPublic()
    {
        return myPublicKey.getEncoded();
    }
    
    public byte[] GetEncryptedmyAESKey()//Encrypts my AES key using RSA
    {
        try{
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            rsa.init(Cipher.ENCRYPT_MODE, theirPublicKey);
            return rsa.doFinal(myAESKey.getEncoded());
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public void SetTheirPublic(byte[] key)
    {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
            theirPublicKey = keyFactory.generatePublic(spec);
        } catch (Exception e) {
            e.getMessage();
        }
    }
    
    public void SetTheirAES(byte[] encryptedKey)//Decrypts their AES key using RSA
    {
        try{
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            rsa.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedKey = rsa.doFinal(encryptedKey);
            theirAESKey = new SecretKeySpec(decryptedKey, "AES");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
