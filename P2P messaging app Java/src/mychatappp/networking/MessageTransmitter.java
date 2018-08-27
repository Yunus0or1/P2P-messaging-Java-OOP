/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatappp.networking;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static javax.imageio.ImageIO.read;
import mychatappp.gui.MainScreen;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;

/**
 *
 * @author manthanhd
 */
public class MessageTransmitter extends Thread {

    String message, hostname, user,firstMessage;
    int targetport, receivePort, key, triggerForSendingFile;
    WritableGUI gui;


    

    public MessageTransmitter(WritableGUI gui,String message, String hostname, int targetport, int receivePort, String user, int key, int trigger) {
        this.gui  = gui ;
        this.message = message;
        this.hostname = hostname;
        this.targetport = targetport;
        this.user = user;
        this.receivePort = receivePort;
        this.key = key;
        this.triggerForSendingFile = trigger;

    }
    
    

    @Override
    public void run() {
        
        
        //File sending done here
        if (triggerForSendingFile == 1) {

            Socket s1;
            String filePath = message;
            String[] tokens = filePath.split("[\\\\|/]");
            String filename = tokens[tokens.length - 1];

            String line = null, line2 = ".......1" + filename;

            //sending the code .......1 so that it will ready for file getting and file name
            try {
                s1 = new Socket(hostname, targetport);
                s1.getOutputStream().write(line2.getBytes());
                s1.close();
                line2 = "";
            } catch (IOException ex) {
                Logger.getLogger(MessageTransmitter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //end
            
            //sending the full file data
            try {
                Socket socket;
                socket = new Socket(hostname, targetport);

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                FileInputStream fis = new FileInputStream(filePath);

                byte[] buffer = new byte[1024];

                while (fis.read(buffer) > 0) {
                    dos.write(buffer);
                    dos.flush();
                }

                fis.close();
                dos.close();
                gui.write("You have sent a file");
            } catch (Exception ex) {
                gui.write("Unable to open file");

            }
        }
        //File data sending ends here
        
        
        // The chat messages are transmitted here
        if (triggerForSendingFile == 0) {
            String line1, line2 = "", line3 = "";

            int i;
            char[] a = new char[1000];

            line1 = user + " : " + message; // This will store all chat data
            line3 = line1;
            
            
            //encryption is done here with specified key
            for (i = 0; i < line1.length(); i++) {
                int ascii = line1.charAt(i);
                int temp = ascii;

                ascii = ascii + key;

                a[i] = (char) (ascii);

            }
            //encryption ends
            
            //Copying all char to make a string
            for (int x = 0; x < i; x++) {
                Character d;
                d = a[x];
                line2 += d.toString();
            }

            line1 = line2;

            try {
                
                //Creating socket with opponent
                Socket s1 = new Socket(hostname, targetport);
                //Creating socket with mYSELF
                Socket s2 = new Socket("localhost", receivePort);
                //sending data to hostname on target port
                s1.getOutputStream().write(line1.getBytes());
                //sending data to myself so it can be seen on my chat board
                s2.getOutputStream().write(line1.getBytes());

                s1.close();
                s2.close();

            } catch (Exception ex) {
                gui.write("The other side is not connected");

            }

        }
    }
}
