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

        // Start gui thread
        GUICom.getInstance().start();

        boolean firstrun = true;

        // Start thread for each kit if it is online
        while (true) {
            getKits(firstrun);
            firstrun = false;
            Thread.sleep(10000);
        }
        // getKits();
    }

    // get kits from database
    private static void getKits(boolean firstrun) {

        // get kit options in array from database
        String[][] kits = SQLConn.getInstance().getKits();

        // Create empty array if it doesnt exist
        if (_kits == null) {
            _kits = new Kit[11];
        }

        // create kit object for every kit in database
        for (int i = 0; i < kits.length; i++) {
            int id = Integer.parseInt(kits[i][0]);
            InetAddress ip = null;
            if (_kits[id - 1] == null) {

                try {
                    // Try to resolve hostname to IP address
                    ip = InetAddress.getByName(kits[i][1]);

                    // Create new kit object at add it to array
                    _kits[id - 1] = new Kit(id, ip);
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                    if (firstrun) {
                        System.out.println("Couldnt resolve IP of kit " + id);
                    }
                }
            }

        }

    }

}
