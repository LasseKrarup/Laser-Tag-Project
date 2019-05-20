package lasertag3000;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

    // player variables
    private int _id;
    private List<Player> _players = new ArrayList<Player>();
    private boolean _active = false;

    // default constructor
    public Game() {
        _id = SQLConn.getInstance().addGame();
        // TODO: Log
    }

    public Game(List<Player> players){
        _id = SQLConn.getInstance().addGame();
        _players = players;
    }

    public boolean addPlayer(String player, int kit) {

        // check if the kit exists
        if (App._kits[kit - 1] != null && App._kits[kit - 1].isConnected()) {

            // check if kit is in use
            if (!_players.isEmpty()) {
                Iterator<Player> itr = _players.iterator();
                while (itr.hasNext()) {
                    if (itr.next().getKit().getID() == kit) {
                        // TODO player with kit already exists
                        return false;
                    }
                }
            }

            // add player to database and playerlist
            int id = SQLConn.getInstance().addPlayer(player, kit, _id);
            _players.add(new Player(id, player, App._kits[kit - 1]));
        } else {
            // log kit not online
            System.out.println("Couldnt add player becouse kit doesnt exist or isnt connected");
            return false;
        }
        return true;
        // TODO: Log
    }

    public void removePlayer(int kit) {

        // find player in list
        Iterator<Player> itr = _players.iterator();
        while (itr.hasNext()) {
            Player p;
            if ((p = itr.next()).getKit().getID() == kit) {

                // remove player from database and list
                SQLConn.getInstance().removePlayer(p.getID());
                itr.remove();
                // TODO log player removed
                return;
            }
        }
        // TODO: Log error while removing player
    }

    public void startGame(int time) {

        Iterator<Player> itr = _players.iterator();

        itr = _players.iterator();

        // enable kits active in game
        while (itr.hasNext()) {
            Player player = itr.next();
            player.getKit().enable();
        }
        _active = true;
        // Start new thread that waits until time is up or interrupted
        new Thread(() -> stopGame(time)).start();
    }

    public void stopGame() {
        if (_active) {
            synchronized (this) {
                this.notifyAll();
            }
        } else {
            App.game = new Game();
        }

    }

    public void stopGame(int delay) {

        // Start timer
        LocalDateTime startTime = LocalDateTime.now();
        synchronized (this) {
            try {
                wait(60 * 1000 * delay);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Write game duration to database
        Duration time = Duration.between(startTime, LocalDateTime.now());
        SQLConn.getInstance().StopGame(_id, (int) time.toMinutes());
        Iterator<Player> itr = _players.iterator();

        // Set kits to inactive
        while (itr.hasNext()) {
            itr.next().getKit().disable();
        }

        App.game = new Game(_players);
    }

    public void shot(int id) {
        if (_active) {
            id++;
            Iterator<Player> itr = _players.iterator();
            while (itr.hasNext()) {
                Player p;
                if ((p = itr.next()).getKit().getID() == id) {

                    int score = SQLConn.getInstance().PlayerShot(p.getID());
                    GUICom.getInstance().updateHighscore(id, score);
                    return;
                }
            }
        }

    }
}