package lasertag3000;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GUICom extends Thread {
    private int port;
    public void run(){
        ServerSocket socket = null;
        Socket sock = null;
        InputStream istream = null;
        BufferedReader receiveRead = null;
        try{
        socket = new ServerSocket(port);
        System.out.println("Server  ready for chatting");
        
        sock = socket.accept();  
        istream = sock.getInputStream();
        receiveRead = new BufferedReader(new InputStreamReader(istream));
   
        String receiveMessage;               
        while(true)
        {
            
          while(receiveRead.ready())  
          {
            receiveMessage = receiveRead.readLine();
             System.out.println(receiveMessage);         
          }
          receiveRead = new BufferedReader(new InputStreamReader(istream));
        } 
        }   
        catch(IOException e){
            System.out.print(e.getMessage());
        }
        finally{
            try{ 
               if(receiveRead != null){
                    receiveRead.close();
                }
                if(istream != null){
                    istream.close();
                }
                if(sock != null){
                    sock.close();
                }
                if(socket != null){
                    socket.close();
                }
            } catch(IOException e){

            }
        }
    }

    public GUICom(){
        port = Config.getInstance().ServerPort();    
    }


}