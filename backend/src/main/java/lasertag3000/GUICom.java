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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class GUICom extends Thread {

    private int port;
    private BufferedOutputStream bout;
    ServerSocket socket;
    Socket sock;
    InputStream in;
    OutputStream out;
    Scanner s;

    public void run() {
        while(true){
            try {
                socket = new ServerSocket(port);
                System.out.println("Server  ready for chatting");
    
                sock = socket.accept();
                in = sock.getInputStream();
                out = sock.getOutputStream();
                s = new Scanner(in);
                BufferedInputStream bin = new BufferedInputStream(in);
                out.write(handshake(s));
                bout = new BufferedOutputStream(out);
                while(!sock.isClosed()){
                    if(bin.available() > 0){
                        byte[] message = new byte[bin.available()];
                        bin.read(message);
                        messageInterpreter(decode(message));
                    }
                    //sock.setTrafficClass(arg0);
                }
            } catch (IOException e) {
                System.out.print(e.getMessage());
            } finally {
                try {
                    if (s != null){
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

    public GUICom() {
        port = Config.getInstance().ServerPort();
    }

    public void Send(String message){
        try {
            bout.write(encode("testmessage"));
            bout.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }

    private byte[] handshake(Scanner s) {
        String data = s.useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("GET").matcher(data);
        byte[] response = null;
        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            try {
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
            
        }
        System.out.println("Made handshake");
        String str = new String(response, StandardCharsets.UTF_8);
        System.out.print(str);
        return response;
    }

    private String decode(byte[] data){
        int datalength = (data[1] ^ 0x80) & 0xFF;
        int position = 2;
        byte[] mask = new byte[4];
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
        for(int i = 0; i < 4; i++){
            mask[i] = data[position];
            position++;
        }
        byte[] decoded = new byte[datalength];
        for(int i = 0; i < datalength; i++){
            decoded[i] = (byte) (data[i + position] ^ mask[i % 4]);
        }
        return new String(decoded, StandardCharsets.UTF_8);

        
    }

    public byte[] encode(String message) {
        int datalength = message.getBytes().length;
        ArrayList<Byte> list = new ArrayList<Byte>();
        list.add((byte) (0x81));
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
        byte[] bytemsg = StandardCharsets.UTF_8.encode(message).array();
        for(int i = 0; i < bytemsg.length; i++){
            list.add(bytemsg[i]);
        }

        byte[] returnArray = new byte[list.size()];
        for(int i = 0; i < list.size(); i++){
            returnArray[i] = list.get(i).byteValue();
        }
        return returnArray;
    }

    private void messageInterpreter(String message){
        Gson gson = new Gson();
        LinkedTreeMap map = gson.fromJson(message, LinkedTreeMap.class);
        String action = (String) map.get("action");
        switch(action) {
            case "addPlayer":
                if(App.game != null){

                }
                else{
                    App.game = new Game();
                }
                App.game.addPlayer((String) map.get("name"), (Integer) map.get("id"));
            break;
            case "removePlayer":
                App.game.removePlayer((String) map.get("name"));
            break;
            case "stopGame":
                App.game.stopGame();
            break;
        }

    }

}