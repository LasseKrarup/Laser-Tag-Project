package lasertag3000;

import java.net.InetAddress;
import java.net.UnknownHostException;

//log error

public final class App {

    public static Kit[] _kits;
    public static Game game;

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Config.getInstance();
        SQLConn.getInstance();
        //getKits();
        GUICom guiCom = new GUICom();
        guiCom.start();
    }

    private static void getKits() {
        String[][] kits = SQLConn.getInstance().getKits();
        _kits = new Kit[kits.length];
        for (int i = 0; i < kits.length; i++) {
            int id = Integer.parseInt(kits[i][0]);
            InetAddress ip = null;
            try {
                ip = InetAddress.getByName(kits[i][1]);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int type = Integer.parseInt(kits[i][2]);
            getKitObject(i, id, type, ip);
        }
    }

    private static void getKitObject(int i, int id, int type, InetAddress ip){
        _kits[i] = new Kit(id, type, ip);
        new Thread(() -> _kits[i] =  new Kit(id, type, ip)).start();
    }

}
