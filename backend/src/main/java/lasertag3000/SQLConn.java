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

        Connection con = getConnection();
        String createtables = null;
        try {
            createtables = new String(Files.readAllBytes(Paths.get("createTables.sql")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
        PreparedStatement statement = null;
        try {     
            statement = con.prepareStatement(createtables);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
            
        }
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
    
    //Kit functions
    public void addKit(InetAddress ip, int id) {
        String query = "INSERT INTO `kits` (`id`, `ipaddress`) VALUES (?, ?)";
        PreparedStatement statement = null;
        Connection con = getConnection();
        try {     
            statement = con.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, ip.getHostAddress());
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
            
        }
    }

    public void removeKit(int id) {
        String query = "DELETE FROM `kits` WHERE `kits`.`id` = ?";
        PreparedStatement statement = null;
        Connection con = getConnection();
        try {
            
            statement = con.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
    }

    public String[][] getKits(){
        String query = "SELECT * FROM `kits`";
        Connection con = getConnection();
        ResultSet res = null;
        PreparedStatement statement = null;
        String[][] kits = null;
        try {
            statement = con.prepareStatement(query);
            res = statement.executeQuery();
            int rows = 0;
            if (res.last()) {
                rows = res.getRow();
                res.beforeFirst();
            }
            kits = new String[rows][2];
            for(int i = 0; res.next(); i++){
                kits[i][0] = res.getString("id");
                kits[i][1] = res.getString("ipaddress");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
            
        }
        return kits;
    }

    public int addGame(){
        String query = "INSERT INTO `game` (`id`, `gametime`) VALUES (NULL, NULL);";
        PreparedStatement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        int id = 0;
        try {
            statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            res = statement.getGeneratedKeys();
            if(res.next()){
                id = res.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
        return id;

    }

    public int addPlayer(String name, int kit, int game){
        String query = "INSERT INTO `player` (`id`, `username`, `kit`, `game`) VALUES (NULL, ?, ?, ?)"; 
        String query2 = "INSERT INTO `game_score` (`game`, `score`, `player`) VALUES (?, '0', ?);";
        PreparedStatement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        int id = 0;
        try {
            statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setInt(2, kit);
            statement.setInt(3, game);
            statement.executeUpdate();
            res = statement.getGeneratedKeys();
            if(res.next()){
                id = res.getInt(1);
            }
            statement.close();
            statement = con.prepareStatement(query2);
            statement.setInt(1, game);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
        return id;
    }

    public void removePlayer(int player){
        String query = "DELETE FROM `game_score` WHERE `game_score`.`player` = "+player;
        String query2 = "DELETE FROM `player` WHERE `player`.`id` = "+player;
        Statement statement = null;
        Connection con = getConnection();
        try {
            statement = con.createStatement();
            statement.addBatch(query);
            statement.addBatch(query2);
            statement.executeBatch();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
    }

    public void PlayerShot(int id){
        String query = "SELECT * FROM `game_score` WHERE `game_score`.`player` = "+id;
        Statement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        try {
            statement = con.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);
            res = statement.executeQuery(query);
            if(res.next()){
                res.updateInt("score", res.getInt("score")+1);
            }
            res.updateRow();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
    }

    public void StopGame(int game, int time){
        String query = "SELECT * FROM `game` WHERE `id` = " + game;
        Statement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        try {
            statement = con.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);
            res = statement.executeQuery(query);
            if(res.next()){
                res.updateInt("gametime", time);
            }
            res.updateRow();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
    }
/*
    public boolean clearHighScore(){

    }

    public boolean addPractice(){

    } */

}