/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatappp.networking;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import mychatappp.gui.MainScreen;

/**
 *
 * @author manthanhd
 */
public class MessageListener extends Thread {

    public ServerSocket server;
    int port, key, triggerForReceivingFile = 0;

    WritableGUI gui;
    String FileName = "";

    public MessageListener(WritableGUI gui, int port, int key) {

        this.port = port;
        this.gui = gui;
        this.key = key;
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public MessageListener() {
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

        Socket clientSocket;
        //server established
        try {
            while ((clientSocket = server.accept()) != null) {

                InputStream is = clientSocket.getInputStream(); // All streamed data has been received

                // Do not see this part first . Cause this will make confusion. This part will be activated only 
                //when the sender wants to send a file. Otherwise ignore this part. Normal chat receiving starts afte line number 100
                if (triggerForReceivingFile == 1/* if file getting is activated*/) {

                    try {
                        FileOutputStream fos = new FileOutputStream(".\\" + FileName); // Creates the file

                        byte[] buffer = new byte[4096];

                        int filesize = 20000000; // File size upto acceptable . You can change this to send larger files now only 20MB accpted
                        int read = 0;
                        int totalRead = 0;
                        int remaining = filesize;
                        while ((read = is.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                            totalRead += read;
                            remaining -= read;
                            fos.write(buffer, 0, read);
                        }
                        //File data written
                        is = null;
                        fos.close();
                        triggerForReceivingFile = 0;
                        FileName = "";
                        gui.write("You have received a file");
                    } catch (Exception e) {
                        gui.write("Unable to open file");
                    }
                }
                //This is line number 100 . So do not look upto this side until the triggerForReceivingFile == 1
                
                 //File getting ends.
                if (triggerForReceivingFile == 0 && is != null) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = br.readLine();

                    if (line != null) {

                        char[] ch = line.toCharArray();

                        


                        //File receiving code is manufactured here
                        if (ch[0] == '.' && ch[1] == '.' && ch[2] == '.' && ch[3] == '.' && ch[4] == '.'
                                && ch[5] == '.' && ch[6] == '.' && ch[7] == '1') {

                            triggerForReceivingFile = 1; // This .......1 will activate  file receiving means triggerForReceivingFile = 1

                            //Filename Finding
                            for (int x = 8; x < line.length(); x++) {
                                Character d;
                                d = ch[x];
                                FileName += d.toString();
                            }

                            line = "";

                        }

                        //Filename Finding ends
                        
                        //CHAT message is processed here
                        //decryption done here
                        if (triggerForReceivingFile == 0 && line != "") {

                            for (int i = 0; i < ch.length; i++) {
                                ch[i] = (char) (ch[i] - key);

                            }
                            //decryption done ends here        
                            line = "";

                            for (int x = 0; x < ch.length; x++) {
                                Character d;
                                d = ch[x];
                                line += d.toString();

                            }
                            //writing the message on chat box
                            gui.write(line);
                        }

                    }
                }
            }
        } catch (Exception ex) {
            
        }
    }

}
