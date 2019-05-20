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
    private OutputStream _out;

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
                if ((_socket == null || _socket.isClosed()) || !connected && _ip.isReachable(500)) {
                    try {
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
            }
        } /*
           * else if (!_socket.isConnected() | _socket.isClosed()) { try {
           * _socket.close(); } catch (IOException e) { // TODO Auto-generated catch block
           * e.printStackTrace(); } _socket = null; }
           */
        // TODO check if connection got interrupted and set socket to null
    }

    public boolean sendMessage(char message) {
        if (_socket != null && !_socket.isClosed()) {
            try {
                if (_out == null) {
                    _out = _socket.getOutputStream();
                }
                int asci = message;

                _out.write(asci);
                _out.flush();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                connected = false;
                e.printStackTrace();
            } finally {
                /*
                 * if (pwrite != null) { pwrite.close(); } if (ostream != null) { try {
                 * ostream.close(); } catch (IOException e) { // TODO Auto-generated catch block
                 * e.printStackTrace(); } }
                 */

            }
            return true;
        } else {
            return false;
        }

    }

    public void messageReciever() {
        InputStream istream = null;
        ;
        BufferedReader receiveRead = null;
        while (true) {
            try {
                while (_socket != null && !_socket.isClosed()) {
                    if (istream == null) {
                        istream = _socket.getInputStream();
                    }
                    if (receiveRead == null) {
                        receiveRead = new BufferedReader(new InputStreamReader(istream));
                    }
                    int message = 0;
                    while (message != -1) {
                        if (istream.available() > 0) {
                            message = receiveRead.read();
                            System.out.println(message);
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
                // TODO Handle exception

            } finally {
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