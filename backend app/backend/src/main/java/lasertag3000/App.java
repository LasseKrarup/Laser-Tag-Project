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
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Lasertag backend app starting");
        GUICom.getInstance().start();
        boolean firstrun = true;
        while (true) {
            getKits(firstrun);
            firstrun = false;
            Thread.sleep(10000);
        }
        //getKits();
    }

    //get kits from database
    private static void getKits(boolean firstrun) {

        //get kit options in array
        String[][] kits = SQLConn.getInstance().getKits();
        if (_kits == null) {
            _kits = new Kit[11];
        }
        
        if(kits != null){

            //create kit object for every kit in database
            for (int i = 0; i < kits.length; i++) {
                int id = Integer.parseInt(kits[i][0]);
                InetAddress ip = null;
                if(_kits[id-1] == null){
                    try {
                        ip = InetAddress.getByName(kits[i][1]);
                        _kits[id-1] = new Kit(id, ip);
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                        if(firstrun){
                            System.out.println("Couldnt resolve IP of kit " + id);
                        }
                    }
                }

            }
        }

    }

}
