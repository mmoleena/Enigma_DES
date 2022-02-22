package final_proj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

//import javax.crypto.*;
//import javax.crypto.spec.*;
//import javax.crypto.spec.DESKeySpec;
//import javax.crypto.spec.IvParameterSpec;

public class DES
{
    public static void encryptDecrypt(String key, int cipherMode, File in, File out)
    throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException
    {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        
        //byte[] keyBytes = "2b7e151628aed2a6abf71589".getBytes("UTF-8");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = skf.generateSecret(desKeySpec);
        
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        
        byte[] ivBytes = new byte[8];
        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        
        if(cipherMode == Cipher.ENCRYPT_MODE)
        {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv, SecureRandom.getInstance("SHA1PRNG"));
            CipherInputStream cis = new CipherInputStream(fis,cipher);
            write(cis, fos);
        }
        else if(cipherMode == Cipher.DECRYPT_MODE)
        {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv, SecureRandom.getInstance("SHA1PRNG"));
            CipherOutputStream cos = new CipherOutputStream(fos,cipher);
            write(fis, cos);
        }
    }
    
    public static void write(InputStream in, OutputStream out) throws IOException 
    {
        byte[] buffer = new byte[64];
        int numOfBytesRead;
        while((numOfBytesRead = in.read(buffer)) != -1) 
        {
            out.write(buffer, 0 , numOfBytesRead);
        }
        out.close();
        in.close();
    }

    public static void des_en(int choice)
    {
        File plain = new File("C:\\Desktop\\textfiles\\plain.txt");
        File encrypted = new File("C:\\Desktop\\textfiles\\encrypted.txt");
        File decrypted = new File("C:\\Desktop\\textfiles\\decrypted.txt");
        
        //System.out.println("Choose : ");
        //System.out.println("1) Encryption");
        //System.out.println("2) Decryption");
        
        //Scanner sc = new Scanner(System.in);
        
        //int ch = sc.nextInt();
        int ch=choice;
        try
        {
            if(ch == 1)
            {
                encryptDecrypt("12345678", Cipher.ENCRYPT_MODE, plain, encrypted);
                //System.out.println("Encryption complete");
            }
            else if(ch == 2)
            {
                encryptDecrypt("12345678", Cipher.DECRYPT_MODE, encrypted, decrypted);
                //System.out.println("Decryption complete");
            }
            else
            {
                System.out.println("Invalid");
            }
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IOException | InvalidAlgorithmParameterException e) 
        {
            e.printStackTrace();
        }
    }
}