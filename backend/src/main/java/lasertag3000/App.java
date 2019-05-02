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
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Hello World!");
        Config.getInstance();
        SQLConn.getInstance();
        //getKits();
        GUICom guiCom = new GUICom();
        guiCom.start();
        int game = SQLConn.getInstance().addGame();
        SQLConn.getInstance().addKit(InetAddress.getLocalHost(), 1);
        int player = SQLConn.getInstance().addPlayer("Leo", 1, game);
        SQLConn.getInstance().PlayerShot(player);
        SQLConn.getInstance().removePlayer(player);
        SQLConn.getInstance().getKits();
        SQLConn.getInstance().removeKit(1);
        SQLConn.getInstance().StopGame(game, 20);
    }

    private static void getKits() {
        String[][] kits = SQLConn.getInstance().getKits();
        if(_kits == null){
            _kits = new Kit[kits.length];
        }
        for (int i = 0; i < kits.length; i++) {
            int id = Integer.parseInt(kits[i][0]);
            InetAddress ip = null;
            try {
                ip = InetAddress.getByName(kits[i][1]);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            getKitObject(i, id, ip);
        }
    }

    private static void getKitObject(int i, int id, InetAddress ip){
        _kits[i] =  new Kit(id, ip);
    }

}
