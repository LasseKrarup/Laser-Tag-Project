package lasertag3000;

import java.net.InetAddress;
import java.sql.*;

public final class SQLConn {

    private static volatile SQLConn instance = new SQLConn();

    private String URL;
    private String user;
    private String password;

    private SQLConn() {

        password = Config.getInstance().DBPASS();
        user = Config.getInstance().DBUSER();
        String server = Config.getInstance().DBURL();
        int port = Config.getInstance().DBPORT();

        URL = "jdbc:mysql://" + server + ":" + port;

    }

    public static SQLConn getInstance() {
        return instance;
    }

    private Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, user, password);
        } catch (Exception e) {
            // log no connection could be etablished
        }
        return con;
    }

    private boolean update(String statement) {
        synchronized(this){
            Connection con = getConnection();
            Statement tmp = null;
            try {
                tmp = con.createStatement();
                tmp.executeUpdate(statement);
            } catch (Exception e) {
                return false;
                // TODO: handle exception
            } finally {
                try {
                    if(tmp != null){
                        tmp.close();
                    }
                    if(con!= null){
                        con.close();
                    }
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private ResultSet query(String statement, Connection con) {
        ResultSet res = null;
        Statement tmp = null;
        synchronized(this){
            con = getConnection();
            try {
                tmp = con.createStatement();
                res = tmp.executeQuery(statement);
            } catch (Exception e) {

            } finally {
                try {
                    if(tmp != null){
                        tmp.close();
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
    
    //Kit functions
    public int addKit(InetAddress ip, int type) {
        return 0;
    }

    public void removeKit(int id) {
        return;
    }

    public String[][] getKits(){
        return null;
    }

    //Game functions
    public int addGame(int[] players, int gametime){
        return 0;
    }

    public int addPlayer(String username, String email){
        return 0;
    }

    public void removePlayer(int player_id, int game_id){
        return;
    }

    public void PlayerShot(int player_id, int game_id){
        return;
    }
/*
    public boolean clearHighScore(){

    }

    public boolean addPractice(){

    } */

}