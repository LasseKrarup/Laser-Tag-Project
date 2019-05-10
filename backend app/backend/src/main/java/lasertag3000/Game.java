package lasertag3000;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

    //player variables
    private int _id;
    private List<Player> _players = new ArrayList<Player>();
    private int _duration;

    //default constructor
    public Game() {
        _id = SQLConn.getInstance().addGame();
        //TODO: Log
    }

    public boolean addPlayer(String player, int kit) {

        //check if the kit exists
        if(App._kits[kit-1] != null && App._kits[kit-1].isConnected()){

            //check if kit is in use
            if(!_players.isEmpty()){
                Iterator<Player> itr = _players.iterator();
                while(itr.hasNext()){
                    if(itr.next().getKit().getID() == kit){
                        //TODO player with kit already exists
                        return false;
                    }
                }
            }

            //add player to database and playerlist
            int id = SQLConn.getInstance().addPlayer(player, kit, _id);
            _players.add(new Player(id, player, App._kits[kit-1]));
        }
        else{
            //log kit not online
            return false;
        }
        return true;
        //TODO: Log
    }

    public void removePlayer(String name) {

        //find player in list
        Iterator<Player> itr = _players.iterator();
        while(itr.hasNext()){
            Player p;
            if((p = itr.next()).getName() == name){

                //remove player from database and list
                SQLConn.getInstance().removePlayer(p.getID());
                itr.remove();
                //TODO log player removed
                return;
            }
        }
        //TODO: Log error while removing player
    }

    public void startGame(int time) {

        Iterator<Player> itr = _players.iterator();

        itr = _players.iterator();

        //enable kits active in game
        while (itr.hasNext()) {
            Player player = itr.next();
            player.getKit().enable(_id);
            player.getKit().notify();
        }

        //Start new thread that waits until time is up or interrupted
        new Thread(() -> stopGame(time)).start();
    }

    public void stopGame() {
        this.notifyAll();
    }

    public void stopGame(int delay) {

        //Start timer
        LocalDateTime startTime = LocalDateTime.now();
        try {
            wait(60 * 1000 * delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Write game duration to database
        Duration time = Duration.between(startTime, LocalDateTime.now());
        SQLConn.getInstance().StopGame(_id, (int) time.toMinutes());
        Iterator<Player> itr = _players.iterator();

        //Set kits to inactive
        while(itr.hasNext()){
            itr.next().getKit().disable();
        }

        App.game = null;
    }
}