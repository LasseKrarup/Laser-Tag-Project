package lasertag3000;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private int _id;
    private List<Player> _players = new ArrayList<Player>();
    private int _duration;

    public Game() {
    }

    public void addPlayer(Player player) {

    }

    public void removePlayer(Player player) {

    }

    public void startGame(int time) {
        Iterator<Player> itr = _players.iterator();
        int[] playerID = new int[_players.size()];

        int i = 0;
        while (itr.hasNext()) {
            Player player = itr.next();
            playerID[i] = player.getKit().getID();
            i++;
        }

        _id = SQLConn.getInstance().addGame(playerID, _duration);

        itr = _players.iterator();

        while (itr.hasNext()) {
            Player player = itr.next();
            player.getKit().enable(_id);
            player.getKit().notify();
        }
        new Thread(() -> stopGame(time)).start();
    }

    public void stopGame() {
        this.notifyAll();
    }

    public void stopGame(int delay) {
        try {
            wait(60 * 1000 * delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Iterator<Player> itr = _players.iterator();
        while(itr.hasNext()){
            itr.next().getKit().disable();
        }
        App.game = null;
    }
}