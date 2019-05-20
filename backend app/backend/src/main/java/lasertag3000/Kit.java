package lasertag3000;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Kit {
    private int _id;
    private InetAddress _ip;
    private volatile Socket _socket;
    private volatile boolean _active;
    private volatile Boolean connected;
    private volatile OutputStream _out;

    public Kit(int id, InetAddress ip) {
        _id = id;
        _ip = ip;
        connected = false;
        new Thread(() -> this.connSocket()).start();
        new Thread(() -> this.messageReciever()).start();
    }

    private void connSocket() {
        System.out.println("Trying to connect to kit " + _id);
        while (true) {
            try {
                //if socket is not connected to kit, try to connect
                if ((_socket == null || _socket.isClosed()) || (!connected && _ip.isReachable(500))) {
                    try {
                        if(_out != null){
                            _out.close();
                        }
                        if(_socket != null){
                            _socket.close();
                        }
                        _socket = new Socket(_ip, Config.getInstance().KitPort());
                        connected = true;
                        System.out.println("Connected to kit " + _id);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        // System.out.println("couldnt connect to socket " + e.getMessage());
                        if (_socket != null) {
                            _socket.close();
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                connected = false;
            }
        } 
        // TODO check if connection got interrupted and set socket to null
    }

    public boolean sendMessage(char message) {
        //If socket is connected to kit
        if (_socket != null && !_socket.isClosed()) {
            try {
                //check if output stream exists
                if (_out == null) {
                    _out = _socket.getOutputStream();
                }

                //Convert char to int
                int asci = message;

                //Send int
                _out.write(asci);
                _out.flush();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                //Set connection as not connected on error
                connected = false;
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }

    }

    public void messageReciever() {
        InputStream istream = null;
        BufferedReader receiveRead = null;
        while (true) {
            try {
                if (_socket != null && !_socket.isClosed()) {
                    //Create inputstream and bufferreader if null
                    if (istream == null) {
                        istream = _socket.getInputStream();
                    }
                    if (receiveRead == null) {
                        receiveRead = new BufferedReader(new InputStreamReader(istream));
                    }
                    int message = 0;

                    //Wait for new messages while kit is connected
                    while(connected){
                        //Test for new data in buffer
                        if (istream.available() > 0) {
                            //Read from buffer
                            message = receiveRead.read();

                            //Evaluate recived data
                            if (message >= 0 & message < 10) {
                                if (App.game != null & _active) {
                                    App.game.shot(message);
                                }
                            } else if (message == 'a') {
                                GUICom.getInstance().startPractice();
                            } else {
                                System.out.println("Message from kit " + _id + " not understood:");
                                System.out.println(message);
                            }

                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                //Set connection as not connected on error
                connected = false;
                // TODO Handle exception

            } finally {
                //Close open connections
                try {
                    if (istream != null) {
                        istream.close();
                    }
                    if (receiveRead != null) {
                        receiveRead.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    //Return private values
    public int getID() {
        return _id;
    }

    public InetAddress getIP() {
        return _ip;
    }

    public void enable() {
        _active = true;
    }

    public void disable() {
        _active = false;
    }

    public boolean isConnected() {
        return connected;
    }
}