package lasertag3000;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Kit {
    private int _id;
    private int _type;
    private InetAddress _ip;
    private Socket _socket;
    private boolean _active;
    private int _gameid;

    public Kit(int id, int type, InetAddress ip) {
        _id = id;
        _type = type;
        _ip = ip;
    }

    public void connSocket() {
        if (_socket == null) {
            Socket socket = null;
            while (!socket.isConnected()) {
                try {
                    if (_ip.isReachable(500)) {
                        try {
                            socket = new Socket(_ip, Config.getInstance().KitPort());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        _socket = socket;
                    }
                    Thread.sleep(10000);
                } catch (IOException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(String message) {
        PrintWriter pwrite = null;
        OutputStream ostream = null;
        try {
            ostream = _socket.getOutputStream();
            pwrite = new PrintWriter(ostream, true);
            pwrite.println(message);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        finally {
            if(pwrite != null){
                pwrite.close();
            }
            if(ostream != null){
                try {
                    ostream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        }
    }

    public void messageReciever(){
        InputStream istream = null;
        BufferedReader receiveRead = null;
        try{
            istream = _socket.getInputStream();
            receiveRead = new BufferedReader(new InputStreamReader(istream));
            
            while(true){
                while(_active){
                    String receiveMessage;
                    if((receiveMessage = receiveRead.readLine()) != null) //receive from server
                    {
                        int id = 0;
                        //get user id and send to database
                        SQLConn.getInstance().PlayerShot(id,_gameid);
                    }   
                }
                wait();
            }
        }
        catch(IOException | InterruptedException e) {
            //catch exception
        }
        finally{
            try {
                if(istream != null){    istream.close();    }
                if(receiveRead != null){    receiveRead.close();    }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public int getID(){
        return _id;
    }

    public int getType(){
        return _type;
    }

    public InetAddress getIP(){
        return _ip;
    }

    public void enable(int gameid){
        _gameid = gameid;
        _active = true;
    }

    public void disable(){
        _active = false;
    }
    // implement online check
}