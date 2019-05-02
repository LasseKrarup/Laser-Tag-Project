package lasertag3000;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
    private int _id;
    private List<Player> _players = new ArrayList<Player>();
    private int _duration;

    public Game() {
        _id = SQLConn.getInstance().addGame();
        //TODO: Log
    }

    public boolean addPlayer(String player, int kit) {
        if(App._kits[kit] != null){
            if(!_players.isEmpty()){
                Iterator<Player> itr = _players.iterator();
                while(itr.hasNext()){
                    if(itr.next().getKit().getID() == kit){
                        //TODO player with kit already exists
                        return false;
                    }
                }
            }
            int id = SQLConn.getInstance().addPlayer(player, kit, _id);
            _players.add(new Player(id, player, App._kits[kit]));
        }
        else{
            //log kit not online
            return false;
        }
        return true;
        //TODO: Log
    }

    public void removePlayer(String name) {
        Iterator<Player> itr = _players.iterator();
        while(itr.hasNext()){
            Player p;
            if((p = itr.next()).getName() == name){
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
        LocalDateTime startTime = LocalDateTime.now();
        try {
            wait(60 * 1000 * delay);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Duration time = Duration.between(startTime, LocalDateTime.now());
        SQLConn.getInstance().StopGame(_id, (int) time.toMinutes());
        Iterator<Player> itr = _players.iterator();
        while(itr.hasNext()){
            itr.next().getKit().disable();
        }

        App.game = null;
    }
}