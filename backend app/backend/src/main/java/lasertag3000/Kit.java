package lasertag3000;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Kit {
    private int _id;
    private InetAddress _ip;
    private Socket _socket;
    private boolean _active;
    private int _gameid;
    private Boolean connected;
    private OutputStream _out;

    public Kit(int id, InetAddress ip) {
        _id = id;
        _ip = ip;
        _gameid = 0;
        connected = false;
        new Thread(() -> this.connSocket()).start();
        new Thread(() -> this.messageReciever()).start();
    }

    private void connSocket() {
        while (true) {
            try {
                if ((_socket == null || _socket.isClosed()) || !connected && _ip.isReachable(500)) {
                    try {
                        _socket = new Socket(_ip, Config.getInstance().KitPort());
                        connected = true;
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("couldnt connect to socket " + e.getMessage());
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
        while (true) {
                InputStream istream = null;
                BufferedReader receiveRead = null;
                try {
                    while (_socket != null && !_socket.isClosed()) {
                        if (istream == null) {
                            istream = _socket.getInputStream();
                        }
                        if (receiveRead == null) {
                            receiveRead = new BufferedReader(new InputStreamReader(istream));
                        }

                        while (connected) {
                            int message;
                            if ((message = receiveRead.read()) != -1) {
                                System.out.println(String.valueOf(message));
                            } else {
                                connected = false;
                            }

                            // SQLConn.getInstance().PlayerShot(Character.valueOf(message[0]));

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

    public void enable(int gameid) {
        _gameid = gameid;
        _active = true;
        sendMessage('A');

    }

    public void disable() {
        _active = false;
    }

    public boolean isConnected() {
        return connected;
    }
}