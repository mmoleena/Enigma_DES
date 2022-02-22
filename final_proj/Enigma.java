package final_proj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Enigma
{
  private Rotor[] rotors;
  private Reflector reflector;
  private Plugboard plugboard;

  // Characters showing through window at top of rotor
  private char[] tops;
  private int[] ringSettings;

  // Size of blocks of output text
  private int blockSize = 4;

  // All the components are set after initialization
  public Enigma(int numRotors)
  {
    rotors = new Rotor[numRotors];
    tops = new char[numRotors];
    ringSettings = new int[numRotors];

    plugboard = new Plugboard();
  }

  public Enigma()
  {
    this(3);
  }

  public void setRotor(int index, Rotor rotor)
  {
    rotors[index] = rotor;
  }

  public void setReflector(Reflector reflector)
  {
    this.reflector = reflector;
  }

  public void setRings(int[] ringSettings)
  {
    this.ringSettings = ringSettings;
  }

  public void setTops(char[] tops)
  {
    this.tops = tops;
  }

  // Carries out rotor turnover with double-stepping
  public void step()
  {
    int rotorIndex = rotors.length-1;
    // Traverse right to left through rotors
    while (rotorIndex >= 0)
    {
      // Implement a step of the current rotor
      if (tops[rotorIndex] != 'Z')
        tops[rotorIndex] ++;
      else
        tops[rotorIndex] = 'A';
      // If current rotor is passing a turnover notch, move left and keep going
      if (rotors[rotorIndex].isNotch(tops[rotorIndex]))
        rotorIndex --;
      // If not, check for double-stepping
      else
      {
        if (rotorIndex > 0)
        {
          // If rotor to the left is at its turnover notch,
          // then turn it over and keep going
          if (rotors[rotorIndex-1].isNotch(Rotor.offset(tops[rotorIndex-1], 1)))
            rotorIndex --;
          else
            break;
        }
        else
          break;
      }
    }
  }

  // Returns the output after a forward pass through the rotors (right to left)
  public char forwardRotors(char input)
  {
    int rotorIndex = rotors.length-1;
    char current = input;
    while (rotorIndex >= 0)
    {
      // Shift given current rotor position
      current = Rotor.offset(current, (int)(tops[rotorIndex]-65));
      // Now calclate output given ringsetting
      current = rotors[rotorIndex].output(current, ringSettings[rotorIndex]);
      // Shift back (for moving to the next rotor)
      current = Rotor.offset(current, (int)(65-tops[rotorIndex]));
      rotorIndex--;
    }
    return current;
  }

  // Returns the output after a backward pass through the rotors (left to right)
  public char backwardRotors(char input)
  {
    int rotorIndex = 0;
    char current = input;
    while (rotorIndex < rotors.length)
    {
      current = Rotor.offset(current, (int)(tops[rotorIndex]-65));
      current = rotors[rotorIndex].revOutput(current, ringSettings[rotorIndex]);
      current = Rotor.offset(current, (int)(65-tops[rotorIndex]));
      rotorIndex++;
    }
    return current;
  }

  // Encrypts a single character
  public char encrypt(char input)
  {
    // Steps first
    step();
    char current = input;
    // Passes through all these components
    current = plugboard.output(current);
    current = forwardRotors(current);
    current = reflector.output(current);
    current = backwardRotors(current);
    current = plugboard.output(current);
    return current;
  }

  // Encrypts a whole string
  public String encrypt(String message)
  {
    String cyphertext = "";
    int count = 0;
    for (int i = 0; i < message.length(); i++)
    {
      char next = message.charAt(i);
      // Accounts for lower case
      if (next >= 97 && next <= 122)
        next -= 32;
      // Only takes in letter chars
      if (next >= 65 && next <= 90)
      {
        cyphertext += String.valueOf(encrypt(next));
        count++;
        // Inserts spaces every blockSize for readability
        if (count % blockSize == 0)
          cyphertext += " ";
      }
    }
    return cyphertext;
  }

  // Parses command line input for setup parameters, then encrypts or decrypts your message
  public static void main(String[] args)
  {
        Scanner kb = new Scanner(System.in);
        //System.out.println("Enter the function to perform");
        //System.out.println("\n(1) For encryption enter 'E'");
        //System.out.println("(2) For decryption enter 'D'");
        //System.out.print("\nEnter your choice : ");
        //String choice=kb.nextLine();
        
        String gui_choice = JOptionPane.showInputDialog("Enter the function to perform\n(1) For encryption enter 'E'\n(2) For decryption enter 'D'\n\nEnter your choice : \n");
        String choice=gui_choice;
        
        if (choice.equals("E") || choice.equals("e"))
        {
            //System.out.println("\nEnter rotors in order, left to right (i.e. I V II): ");
            //System.out.println();
            
            String gui_rotorsString = JOptionPane.showInputDialog("Enter rotors in order, left to right (i.e. I V II) : \n");
            String rotorsString=gui_rotorsString;
            
            //String rotorsString = kb.nextLine();
            rotorsString = rotorsString.toUpperCase();
            rotorsString = rotorsString.trim();
    
            String nextRotor = "";
            ArrayList<Rotor> rotorsToAdd = new ArrayList<Rotor>();
            for (int i = 0; i < rotorsString.length(); i++)
            {
                if (rotorsString.charAt(i) == ' ')
                {
                    if (nextRotor.equals("I"))
                        rotorsToAdd.add(Rotor.I);
                    else if (nextRotor.equals("II"))
                        rotorsToAdd.add(Rotor.II);
                    else if (nextRotor.equals("III"))
                        rotorsToAdd.add(Rotor.III);
                    else if (nextRotor.equals("IV"))
                        rotorsToAdd.add(Rotor.IV);
                    else if (nextRotor.equals("V"))
                        rotorsToAdd.add(Rotor.V);
                    else if (nextRotor.equals("VI"))
                        rotorsToAdd.add(Rotor.VI);
                    else if (nextRotor.equals("VII"))
                        rotorsToAdd.add(Rotor.VII);
                    else if (nextRotor.equals("VIII"))
                        rotorsToAdd.add(Rotor.VIII);
    
                    nextRotor = "";
                }
                else
                    nextRotor += String.valueOf(rotorsString.charAt(i));
            }
            
            if (!nextRotor.equals(""))
            {
                if (nextRotor.equals("I"))
                    rotorsToAdd.add(Rotor.I);
                else if (nextRotor.equals("II"))
                    rotorsToAdd.add(Rotor.II);
                else if (nextRotor.equals("III"))
                    rotorsToAdd.add(Rotor.III);
                else if (nextRotor.equals("IV"))
                    rotorsToAdd.add(Rotor.IV);
                else if (nextRotor.equals("V"))
                    rotorsToAdd.add(Rotor.V);
                else if (nextRotor.equals("VI"))
                    rotorsToAdd.add(Rotor.VI);
                else if (nextRotor.equals("VII"))
                    rotorsToAdd.add(Rotor.VII);
                else if (nextRotor.equals("VIII"))
                    rotorsToAdd.add(Rotor.VIII);
            }
    
            Enigma enigma = new Enigma(rotorsToAdd.size());
    
            int rotorIndex = 0;
            for (Rotor r : rotorsToAdd)
            {
                enigma.setRotor(rotorIndex, r);
                rotorIndex++;
            }
    
            //System.out.println();
            //System.out.println("Enter reflector (i.e. B): ");
            //System.out.println();
            
            String gui_ref = JOptionPane.showInputDialog("Enter reflector (i.e. B) : \n");
            String ref=gui_ref;
            
            //String ref = kb.nextLine();
            ref = ref.toUpperCase();
            ref = ref.trim();
            if (ref.equals("A"))
                enigma.setReflector(Reflector.A);
            else if (ref.equals("B"))
                enigma.setReflector(Reflector.B);
            else if (ref.equals("C"))
                enigma.setReflector(Reflector.C);
    
                //System.out.println();
                //System.out.println("Enter ring settings (i.e. 01 02 21): ");
                //System.out.println();
                
                String gui_ringSets = JOptionPane.showInputDialog("Enter ring settings (i.e. 01 02 21) : \n");
                String ringSets=gui_ringSets;
                
                //String ringSets = kb.nextLine();
                ringSets = ringSets.trim();
    
                int[] rsets = new int[enigma.rotors.length];
                int ringIndex = 0;
                String currRing = "";
                for (int i = 0; i < ringSets.length(); i++)
                {
                    if (ringSets.charAt(i) == ' ')
                    {
                        if (currRing.charAt(0) == '0')
                        {
                            currRing = currRing.substring(1);
                        }
                        rsets[ringIndex] = Integer.parseInt(currRing);
                        ringIndex++;
                        currRing = "";
                    }
                    else
                    {
                        currRing += String.valueOf(ringSets.charAt(i));
                    }
                }
                if (!currRing.equals(""))
                {
                    if (currRing.charAt(0) == '0')
                    {
                        currRing = currRing.substring(1);
                    }
                    rsets[ringIndex] = Integer.parseInt(currRing);
                }
                enigma.setRings(rsets);
    
                //System.out.println();
                //System.out.println("Enter ring start positions (i.e. A W V): ");
                //System.out.println();
                
                String gui_ringPosStr = JOptionPane.showInputDialog("Enter ring start positions (i.e. A W V) : \n");
                String ringPosStr=gui_ringPosStr;
                
                //String ringPosStr = kb.nextLine();
                ringPosStr = ringPosStr.toUpperCase();
                ringPosStr = ringPosStr.trim();
    
                int topsIndex = 0;
                char[] topsToAdd = new char[enigma.rotors.length];
                for (int i = 0; i < ringPosStr.length(); i++)
                {
                    if (ringPosStr.charAt(i) == ' ')
                    {
                        topsIndex++;
                    }
                    else
                    {
                        topsToAdd[topsIndex] = ringPosStr.charAt(i);
                    }
                }
                enigma.setTops(topsToAdd);
    
                //System.out.println();
                //System.out.println("Enter plugboard pairs (i.e. AQ BZ LM ...): ");
                //System.out.println();
    
                String gui_plugString = JOptionPane.showInputDialog("Enter plugboard pairs (i.e. AQ BZ LM ...) : \n");
                String plugString=gui_plugString;
                
                //String plugString = kb.nextLine();
                plugString = plugString.toUpperCase();
                plugString = plugString.trim();
    
                String currPair = "";
                for (int i = 0; i < plugString.length(); i++)
                {
                    if (plugString.charAt(i) == ' ')
                    {
                        currPair = "";
                    }
                    else
                    {
                        currPair += String.valueOf(plugString.charAt(i));
                    }
                    if (currPair.length() == 2)
                    {
                        enigma.plugboard.connect(currPair.charAt(0), currPair.charAt(1));
                    }
                }
                
                String dkey = "(" + rotorsString + ")" + "-" + "(" + ref + ")" + "-" + "(" + ringSets + ")" + "-" + "(" + ringPosStr + ")" + "-" + "(" + plugString + ")";
                //System.out.println();
                //System.out.println("Enter your message to encrypt: ");
                //System.out.println();
                
                String gui_text = JOptionPane.showInputDialog("Enter your message to encrypt : \n");
                String text=gui_text;
                
                //String text = kb.nextLine();
                //System.out.println();
                //System.out.println("Enter your E-mail address : ");
                //System.out.println();
                
                String gui_recpt = JOptionPane.showInputDialog("Enter your E-mail address : \n");
                String recpt=gui_recpt;
                
                //String recpt =  kb.nextLine();
                recpt = recpt.trim();
                //System.out.println();
                //System.out.println("Your enigma's output message is: ");
                //System.out.println();
                String txt_output = enigma.encrypt(text);
                //System.out.println(txt_output);
                
                JOptionPane.showMessageDialog(null, "Message :\n" + txt_output, "Encrypted Message", JOptionPane.PLAIN_MESSAGE);
                
                try
                {
                    FileWriter witerobj1 = new FileWriter("C:\\Desktop\\textfiles\\enmsg.txt");
                    witerobj1.write(txt_output);
                    witerobj1.close();
                }
                catch(IOException e)
                {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                
                mailingtest obj1 = new mailingtest();
                //obj1.Mailer(recpt);
                
                if (!dkey.equals("()-()-()-()-()"))
                {
                    try
                    {
                        FileWriter witerobj2 = new FileWriter("C:\\Desktop\\textfiles\\plain.txt");
                        witerobj2.write(dkey);
                        witerobj2.close();
                    }
                    catch(IOException e)
                    {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    //System.out.println("Decryption key : " + dkey);
                    //obj1.keyMailer(recpt, dkey);
                    obj1.msgMailer(recpt, txt_output);
                    //System.out.println("Encrypted Message and Decryption Key has been sent to " + (recpt.toLowerCase()));
                    
                }
                
                DES.des_en(1);
                
                obj1.keyMailer(recpt);
                
                JOptionPane.showMessageDialog(null, "Encrypted Message and Decryption Key has been sent to " + (recpt.toLowerCase()), "Alert", JOptionPane.PLAIN_MESSAGE);
        }
        else if (choice.equals("D") || choice.equals("d"))
        {
            //System.out.println("\nEnter Decryption Key : ");
            
            String gui_str1 = JOptionPane.showInputDialog("Enter Decryption Key : \n");
            String str1=gui_str1;
            
            int i=0;
            //String str1 = kb.nextLine();
            
            try
            {
                FileWriter witerobj2 = new FileWriter("C:\\Desktop\\textfiles\\plain.txt");
                witerobj2.write(str1);
                witerobj2.close();
            }
            catch(IOException e)
            {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            
            
            DES.des_en(2);
            String data="";
            try 
            {
                File myObj = new File("C:\\Desktop\\textfiles\\decrypted.txt");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) 
                {
                    data = myReader.nextLine();
                    //System.out.println(data);
                }
                myReader.close();
            } 
            catch (FileNotFoundException e) 
            {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            
            String str = data;
            
            //Splitting the key into configuration componenets
            String keys[] = str.split("-");
            
            //Initialization        
            String rotorsString= (keys[0]).substring(1, keys[0].length() - 1);
            String ref= (keys[1]).substring(1, keys[1].length() - 1);
            String ringSets= (keys[2]).substring(1, keys[2].length() - 1);
            String ringPosStr= (keys[3]).substring(1, keys[3].length() - 1);
            String plugString= (keys[4]).substring(1, keys[4].length() - 1);
            
            //OUTPUT
            /*System.out.println("Rotor Setting  :  " + rotorsString);
            System.out.println("Reflector  :  " + ref);
            System.out.println("Ring Setting  :  " + ringSets);
            System.out.println("Rotor Start Position  :  " + ringPosStr);
            System.out.println("Plugboard  :  " + plugString);*/
            
                
            String nextRotor = "";
            ArrayList<Rotor> rotorsToAdd = new ArrayList<Rotor>();
            for (i = 0; i < rotorsString.length(); i++)
            {
                if (rotorsString.charAt(i) == ' ')
                {
                    if (nextRotor.equals("I"))
                        rotorsToAdd.add(Rotor.I);
                    else if (nextRotor.equals("II"))
                        rotorsToAdd.add(Rotor.II);
                    else if (nextRotor.equals("III"))
                        rotorsToAdd.add(Rotor.III);
                    else if (nextRotor.equals("IV"))
                        rotorsToAdd.add(Rotor.IV);
                    else if (nextRotor.equals("V"))
                        rotorsToAdd.add(Rotor.V);
                    else if (nextRotor.equals("VI"))
                        rotorsToAdd.add(Rotor.VI);
                    else if (nextRotor.equals("VII"))
                        rotorsToAdd.add(Rotor.VII);
                    else if (nextRotor.equals("VIII"))
                        rotorsToAdd.add(Rotor.VIII);
    
                    nextRotor = "";
                }
                else
                    nextRotor += String.valueOf(rotorsString.charAt(i));
            }
            
            if (!nextRotor.equals(""))
            {
                if (nextRotor.equals("I"))
                    rotorsToAdd.add(Rotor.I);
                else if (nextRotor.equals("II"))
                    rotorsToAdd.add(Rotor.II);
                else if (nextRotor.equals("III"))
                    rotorsToAdd.add(Rotor.III);
                else if (nextRotor.equals("IV"))
                    rotorsToAdd.add(Rotor.IV);
                else if (nextRotor.equals("V"))
                    rotorsToAdd.add(Rotor.V);
                else if (nextRotor.equals("VI"))
                    rotorsToAdd.add(Rotor.VI);
                else if (nextRotor.equals("VII"))
                    rotorsToAdd.add(Rotor.VII);
                else if (nextRotor.equals("VIII"))
                    rotorsToAdd.add(Rotor.VIII);
            }
    
            Enigma enigma = new Enigma(rotorsToAdd.size());
    
            int rotorIndex = 0;
            for (Rotor r : rotorsToAdd)
            {
                enigma.setRotor(rotorIndex, r);
                rotorIndex++;
            }
    
            System.out.println();
            if (ref.equals("A"))
                enigma.setReflector(Reflector.A);
            else if (ref.equals("B"))
                enigma.setReflector(Reflector.B);
            else if (ref.equals("C"))
                enigma.setReflector(Reflector.C);
    
              
    
                int[] rsets = new int[enigma.rotors.length];
                int ringIndex = 0;
                String currRing = "";
                for (i = 0; i < ringSets.length(); i++)
                {
                    if (ringSets.charAt(i) == ' ')
                    {
                        if (currRing.charAt(0) == '0')
                        {
                            currRing = currRing.substring(1);
                        }
                        rsets[ringIndex] = Integer.parseInt(currRing);
                        ringIndex++;
                        currRing = "";
                    }
                    else
                    {
                        currRing += String.valueOf(ringSets.charAt(i));
                    }
                }
                if (!currRing.equals(""))
                {
                    if (currRing.charAt(0) == '0')
                    {
                        currRing = currRing.substring(1);
                    }
                    rsets[ringIndex] = Integer.parseInt(currRing);
                }
                enigma.setRings(rsets);
    
                
    
                int topsIndex = 0;
                char[] topsToAdd = new char[enigma.rotors.length];
                for (i = 0; i < ringPosStr.length(); i++)
                {
                    if (ringPosStr.charAt(i) == ' ')
                    {
                        topsIndex++;
                    }
                    else
                    {
                        topsToAdd[topsIndex] = ringPosStr.charAt(i);
                    }
                }
                enigma.setTops(topsToAdd);
    
                
    
                String currPair = "";
                for (i = 0; i < plugString.length(); i++)
                {
                    if (plugString.charAt(i) == ' ')
                    {
                        currPair = "";
                    }
                    else
                    {
                        currPair += String.valueOf(plugString.charAt(i));
                    }
                    if (currPair.length() == 2)
                    {
                        enigma.plugboard.connect(currPair.charAt(0), currPair.charAt(1));
                    }
                }
    
                //System.out.println();
                //System.out.println("Enter your message to decrypt: ");
                //System.out.println();
                
                String gui_text = JOptionPane.showInputDialog("Enter your message to decrypt : \n");
                String text=gui_text;
                
                //String text = kb.nextLine();
                //System.out.println();
                //System.out.println("Your enigma's output message is: ");
                System.out.println();
                String final_output = enigma.encrypt(text);
                System.out.println(final_output);
                
                JOptionPane.showMessageDialog(null, "Your decrypted output message is : \n" + final_output, "Alert", JOptionPane.PLAIN_MESSAGE);
        }
        else
        {
            System.out.println("Invalid Choice");
        }
  }
}