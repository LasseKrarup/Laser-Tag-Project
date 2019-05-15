package lasertag3000;

import java.net.InetAddress;
import java.net.UnknownHostException;

//log error

public final class App {

    public volatile static Kit[] _kits;
    public volatile static Game game;

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("Lasertag backend app starting");
        getKits();
        GUICom.getInstance().start();
        //getKits();
    }

    //get kits from database
    private static void getKits() {

        //get kit options in array
        String[][] kits = SQLConn.getInstance().getKits();
        if (_kits == null) {
            _kits = new Kit[10];
        }
        
        if(kits != null){

            //create kit object for every kit in database
            for (int i = 0; i < kits.length; i++) {
                int id = Integer.parseInt(kits[i][0]);
                InetAddress ip = null;
                try {
                    ip = InetAddress.getByName(kits[i][1]);
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                _kits[id-1] = new Kit(id, ip);
            }
        }

    }

}
