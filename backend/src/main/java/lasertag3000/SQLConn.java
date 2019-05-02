package lasertag3000;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        URL = "jdbc:mysql://" + server + ":" + port + "/lasertag?allowMultiQueries=true&serverTimezone=UTC";

        getConnection();
        String createtables = null;
        try {
            createtables = new String(Files.readAllBytes(Paths.get("createTables.sql")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        update(createtables);
    }

    public static SQLConn getInstance() {
        return instance;
    }

    private Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, user, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
                e.printStackTrace();
            }/* finally {
                try {
                    if(tmp != null){
                        tmp.close();
                    }
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }*/
        }
        return res;
    }
    
    //Kit functions
    public void addKit(InetAddress ip, int id) {
        String query = "INSERT INTO `kits` (`id`, `ipaddress`) VALUES ('" + id + "', '" + ip.getHostAddress() + "')";
        if(!update(query)){
            //TODO error 
        }
    }

    public void removeKit(int id) {
        String query = "DELETE FROM `kits` WHERE `kits`.`id` = " + id;
        if(!update(query)){
            //TODO error
        }
    }

    public String[][] getKits(){
        String query = "SELECT * FROM `kits`";
        Connection con = getConnection();
        ResultSet res = query(query, con);
        String[][] kits = null;
        try{
            int rows = 0;
            if (res.last()) {
                rows = res.getRow();
                res.beforeFirst();
            }
            kits = new String[rows][2];
            int i = 0;
            while(res.next()){
                kits[i][0] = res.getString("id");
                kits[i][1] = res.getString("ipaddress");
                i++;
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            //TODO error
        }
        finally{
            try {
                if(res != null){
                    res.close();
                }
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                //TODO: handle exception
            }

        }
        return kits;
    }

    //Game functions
    public int addGame(){
        String query = "INSERT INTO `game` (`id`, `players`, `starttime`, `gametime`) VALUES (NULL, NULL, NULL, NULL);";
        String query2 = "SELECT LAST_INSERT_ID()";
        update(query);
        Connection con = getConnection();
        ResultSet res = query(query2, con);
        int id = 0;
        try{
            if(res.next()){
                id = res.getInt("id");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            //TODO error
        }
        finally{
            try {
                if(res != null){
                    res.close();
                }
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                //TODO: handle exception
            }

        }
        return id;

    }

    public int addPlayer(String name, int kit, int game){
        String query = "INSERT INTO `player` (`id`, `username`, `kit`, `game`) VALUES (NULL, '" + name + "', '"+ kit +"', '" + game + "'); SELECT LAST_INSERT_ID()";
        Connection con = getConnection();
        ResultSet res = query(query, con);
        int id = 0;
        try{
            res.next();
            id = res.getInt("id");
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            //TODO error
        }
        finally{
            try {
                if(res != null){
                    res.close();
                }
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                //TODO: handle exception
            }

        }

        String query2 = "INSERT INTO `game_score` (`game`, `score`, `player`) VALUES ('"+game+"', '0', '"+id+"');";
        update(query2);
        return id;
    }

    public void removePlayer(int player){
        String query = "DELETE FROM `player` WHERE `player`.`id` = " + player + "; DELETE FROM `game_score` WHERE `game_score`.`player` = "+player+";";
        update(query);
    }

    public void PlayerShot(int id){
        String query = "SELECT `game_score`.`score` FROM `game_score` WHERE `game_score`.`player` = "+id;
        Connection con = getConnection();
        ResultSet res = query(query, con);
        try{
            res.next();
            res.updateInt("score", res.getInt("score")+1);
            res.updateRow();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            //TODO error
        }
        finally{
            try {
                if(res != null){
                    res.close();
                }
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                //TODO: handle exception
            }

        }
    }

    public void StopGame(int game, int time){
        String query = "SELECT * FROM `game` WHERE `id` = " + game;
        Connection con = getConnection();
        ResultSet res = query(query, con);
        try{
            res.next();
            res.updateString("gametime", String.valueOf(time));
            res.updateRow();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            //TODO error
        }
        finally{
            try {
                if(res != null){
                    res.close();
                }
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                //TODO: handle exception
            }

        }
    }
/*
    public boolean clearHighScore(){

    }

    public boolean addPractice(){

    } */

}