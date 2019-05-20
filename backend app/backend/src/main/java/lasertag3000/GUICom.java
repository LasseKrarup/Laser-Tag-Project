package lasertag3000;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

//Class responsible for commonication with GUI
public final class GUICom extends Thread {

    private static volatile GUICom instance = new GUICom();

    //Variables for GUICom
    private int port;
    private volatile BufferedOutputStream bout;
    private ServerSocket socket;
    private Socket sock;
    private InputStream in;
    private OutputStream out;
    private Scanner s;

    //Implementation of run function from Thread interface
    public void run() {
        while (true) {
            try {
                //Open socket on defined port
                socket = new ServerSocket(port);

                System.out.println("Ready for gui to connect");

                //Block while waiting for client(GUI) to connect to socket
                sock = socket.accept();

                //Get input and output stream after connection etablished
                in = sock.getInputStream();
                out = sock.getOutputStream();
                bout = new BufferedOutputStream(out);
                s = new Scanner(in);

                //Make websocket handshake to finish connection
                BufferedInputStream bin = new BufferedInputStream(in);
                out.write(handshake(s));
                
                //Run while socket is not closed
                while (!sock.isClosed()) {
                    //Check if input is aviable in input stream
                    if (bin.available() > 0) {
                        //Read from input stream
                        byte[] message = new byte[bin.available()];
                        bin.read(message);
                        //Decode websocket frame and interpret the decoded json data
                        messageInterpreter(decode(message));
                    }
                }

            } 
            catch (IOException e) {
                System.out.print(e.getMessage());
            } finally {
                //Close all open streams and connections
                try {
                    if (s != null) {
                        s.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (sock != null) {
                        sock.close();
                    }
                    if (socket != null) {
                        
                        socket.close();
                    }
                } catch (IOException e) {

                }
            }
        }
    }

    public static GUICom getInstance() {
        return instance;
    }

    private GUICom() {
        //Get port for GUI connection from config
        port = Config.getInstance().ServerPort();
    }

    private void Send(String message){
        //Check if socket is created and not closed
        if(sock != null && !sock.isClosed()){
            try {
                //Write encoded websocket frame to output stream
                bout.write(encode(message));
                bout.flush();
            } catch (IOException e) {
                System.out.println("Error sending message to GUI");
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }

    private byte[] handshake(Scanner s) {
        //Check if recived data contain "GET" keyword
        String data = s.useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("GET").matcher(data);
        byte[] response = null;
        //if data contain "GET" keyword (its a websocket handshake request)
        if (get.find()) {
            //find key in request
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            try {
                //create response with new key, for deeper understanding read up on websocket handshakes
                response = ("HTTP/1.1 101 Switching Protocols\r\n" + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n" + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder()
                                .encodeToString(MessageDigest.getInstance("SHA-1").digest(
                                        (match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                        + "\r\n\r\n").getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Made handshake");
        }
        return response;
    }

    private String decode(byte[] data){
        //TODO implement ping pong feature
        //Find amount the length of data as unsigned int
        int datalength = (data[1] ^ 0x80) & 0xFF;

        //set position of next unread byte
        int position = 2;
        byte[] mask = new byte[4];

        //check if length is longer than 125 and get actual size if so
        if(datalength == 126){
            datalength = 0;
            datalength = (data[2] & 0xFF) << 8 | (data[3]) & 0xFF;
            position = 4;
        }
        else if(datalength == 127){
            datalength = (data[2] & 0xFF) << 56 
            | (data[3] & 0xFF) << 48 
            | (data[4] & 0xFF) << 40 
            | (data[5] & 0xFF) << 32 
            | (data[6] & 0xFF) << 24 
            | (data[7] & 0xFF) << 16 
            | (data[8] & 0xFF) << 8 
            | (data[9] & 0xFF);
            position = 10;
        }
        //get mask for decoding
        for(int i = 0; i < 4; i++){
            mask[i] = data[position];
            position++;
        }

        //Create empty array to store decoded data
        byte[] decoded = new byte[datalength];

        //decode data using the mask from websocket frame
        for(int i = 0; i < datalength; i++){
            decoded[i] = (byte) (data[i + position] ^ mask[i % 4]);
        }

        //Return data as string
        return new String(decoded, StandardCharsets.UTF_8);

        
    }

    //encode string to websocket message
    private byte[] encode(String message) {
        int datalength = message.getBytes().length;
        ArrayList<Byte> list = new ArrayList<Byte>();

        //set bits, part of protocol
        list.add((byte) (0x81));

        //set datalength, depending on lenth a different amount of bytes is used
        if(datalength > 125){
            int extlength;
            if(datalength > 0xFFFF){
                list.add((byte) (0x7E & 0xFF));
                extlength = 2;
            }
            else{
                list.add((byte) (0x7F & 0xFF));
                extlength = 8;
            }
            for(int i = 0; i < extlength; i++){
                int shift = (extlength-1-i)*8;
                Byte b = (byte) ((datalength >> shift) & 0xFF);
                list.add(b);
            }
        }
        else{
            list.add(((byte) datalength));
        }

        //add message to websocket frame
        byte[] bytemsg = StandardCharsets.UTF_8.encode(message).array();
        for(int i = 0; i < datalength; i++){
            list.add(bytemsg[i]);
        }

        //Convert Byte list to byte array
        byte[] returnArray = new byte[list.size()];
        for(int i = 0; i < list.size(); i++){
            returnArray[i] = list.get(i).byteValue();
        }
        return returnArray;
    }

    //Interpret json message
    private void messageInterpreter(String message){

        //Create new gson object for json conversion
        Gson gson = new Gson();

        //Create map from json data
        LinkedTreeMap map = gson.fromJson(message, LinkedTreeMap.class);

        //get action option from json object
        String action = (String) map.get("action");

        //find action 
        switch(action) {
            case "addPlayer":

            //Create game if it doenst exist before adding player
                if(App.game == null){
                    App.game = new Game();
                }

                App.game.addPlayer((String) map.get("name"),  Integer.valueOf((String) map.get("id")));
            break;
            case "removePlayer":
                App.game.removePlayer(Integer.valueOf((String) map.get("id")));
            break;
            case "startGame":
                App.game.startGame(Integer.valueOf((String) map.get("time")));
                break;
            case "stopGame":
                if(App.game != null){
                    App.game.stopGame();
                }
            break;
        }

    }

    //Send highscore update to gui
    public void updateHighscore(int kitid, int score){

        //Create new map with json options
        Gson gson = new Gson();
        HashMap<String, String> jsonmap = new HashMap<>();
        jsonmap.put("action", "highscoreUpdate");
        jsonmap.put("id", String.valueOf(kitid));
        jsonmap.put("score", String.valueOf(score));

        //Convert map to json formatted string and send it to gui
        Send(gson.toJson(jsonmap));
    }

    //Send message from practice kit to gui to start practice
    public void startPractice(){

        //Create new map with json options
        Gson gson = new Gson();
        HashMap<String, String> jsonmap = new HashMap<>();
        jsonmap.put("action", "startPractice");

        //Convert map to json formatted string and send it to gui
        Send(gson.toJson(jsonmap));

    }

}