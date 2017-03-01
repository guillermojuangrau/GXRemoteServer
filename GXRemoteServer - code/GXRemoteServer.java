package GXRemoteServer;

import java.net.*;
import java.io.*;
import java.awt.Robot;  
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.Font;
import java.awt.AWTException;

public class GXRemoteServer implements Runnable
{  private Socket       socket = null;
   private ServerSocket server = null;
   private Thread       thread = null;
   private static int portOfServer = 15032;
   private DataInputStream  streamIn  =  null;
   public GXRemoteServer(int port)
   {  
      
   startFrame(getAndExtractIpAddress()); 
   
      try
        {
           
         System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         start();
      }
      catch(Exception e)
      {  System.out.println(e); 
      }
   }
   
   public void startFrame(String ipaddress){
   JFrame frame = new JFrame();
   JPanel panel = new JPanel();
   JLabel label = new JLabel("The code for this computer is: ");
   label.setFont(new Font("Arial", Font.PLAIN, 14));
   JLabel label2 = new JLabel(ipaddress);
   label2.setFont(new Font("Arial", Font.BOLD, 20));
   panel.add(label);
   panel.add(label2);
   frame.add(panel);
   frame.setSize(300, 80);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.setVisible(true);
   }
   
   public String getAndExtractIpAddress(){
   String ip = null;
   try{ip = Inet4Address.getLocalHost().getHostAddress();}catch(Exception e){System.out.println("Exception");} 
   String[] splitip = ip.split("\\.");
   String code = splitip[3];
   return code;   }
   
   public void run()
   {
   while (thread != null)
      {   try
         {  System.out.println("Waiting for a client ..."); 
            socket = server.accept();
            System.out.println("Client accepted: " + socket);
            open();
            boolean done = false;
            while (!done)
            {  try  
               {  String line = streamIn.readUTF();//TODO
               
                  System.out.println(line);
                  if(line.equals("volup")){volup();}
                  if(line.equals("voldown")){voldown();}
                  if(line.equals("mute")){toggle_mute();}
                  if(line.equals("shutdown")){shutdown();};
                  if(line.equals("pause")){pause();}
                  done = line.equals(".bye");
               }
               catch(IOException ioe)
               {  done = true; ioe.printStackTrace();  }
            }
            close();
         }
         catch(IOException ie)
         {  System.out.println("Acceptance Error: " + ie);  }
      }
   }
   public void start()
   {  if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  thread.stop(); 
         thread = null;
      }
   }
   public void open() throws IOException
   {  streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
   }
   public void close() throws IOException
   {  if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
   }
   
   public static void shutdown() throws RuntimeException, IOException {
    Runtime runtime = Runtime.getRuntime();
	Process proc = runtime.exec("shutdown -s -t 0");
	System.exit(0);
}

public static void volup() throws RuntimeException, IOException {
    Runtime runtime = Runtime.getRuntime();
	Process proc = runtime.exec("audioApp.exe volup");
}
public static void voldown() throws RuntimeException, IOException {
    Runtime runtime = Runtime.getRuntime();
	Process proc = runtime.exec("audioApp.exe voldn");
}
public static void toggle_mute() throws RuntimeException, IOException {
    Runtime runtime = Runtime.getRuntime();
	Process proc = runtime.exec("audioApp.exe");
}

public  void pause(){
    try {
             
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_SPACE);
        } catch (AWTException e) {
            e.printStackTrace();
        }
}
   public static void main(String args[])
   {  GXRemoteServer server = null;
      
         server = new GXRemoteServer(portOfServer);
 }  
 }