package lasertag3000;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public final class SQLConn {

    //instance for Singleton Class
    private static volatile SQLConn instance = new SQLConn();

    private String URL;
    private String user;
    private String password;

    //default constructor
    private SQLConn() {

        //Get values from config files
        password = Config.getInstance().DBPASS();
        user = Config.getInstance().DBUSER();
        String server = Config.getInstance().DBURL();
        int port = Config.getInstance().DBPORT();

        //Connection string
        URL = "jdbc:mysql://" + server + ":" + port + "/lasertag?allowMultiQueries=true&serverTimezone=UTC";

        Connection con = getConnection();
        String createtables = null;

        //Get table creation querys from file
        try {
            
            createtables = new String(Files.readAllBytes(Paths.get("createTables.sql")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        PreparedStatement statement = null;

        //execut table creation 
        try {     
            statement = con.prepareStatement(createtables);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            //TODO handle exception
        }
        finally{

            //close connection to DB
            try{
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
        }
    }

    //Return instance, part of singleton class
    public static SQLConn getInstance() {
        return instance;
    }

    //Create connection to database
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

        //prepare query and connection
        String query = "INSERT INTO `kits` (`id`, `ipaddress`) VALUES (?, ?)";
        PreparedStatement statement = null;
        Connection con = getConnection();
        try {     
            statement = con.prepareStatement(query);

            //Insert data into query
            statement.setInt(1, id);
            statement.setString(2, ip.getHostAddress());

            //Send query to database
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //Close open connections
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

        //prepare query and connection
        String query = "DELETE FROM `kits` WHERE `kits`.`id` = ?";
        PreparedStatement statement = null;
        Connection con = getConnection();
        try {
            statement = con.prepareStatement(query);

            //Insert data into query
            statement.setInt(1, id);

            //Send query to database
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //Close open connections
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

        //prepare query and connection
        String query = "SELECT * FROM `kits`";
        Connection con = getConnection();
        ResultSet res = null;
        PreparedStatement statement = null;
        String[][] kits = null;
        try {
            statement = con.prepareStatement(query);

            //Send query to database
            res = statement.executeQuery();

            //Check the amount of kits in DB
            int rows = 0;
            if (res.last()) {
                rows = res.getRow();
                res.beforeFirst();
            }

            if(rows > 0){
                //Put values in return array
                kits = new String[rows][2];
                for(int i = 0; res.next(); i++){
                    kits[i][0] = res.getString("id");
                    kits[i][1] = res.getString("ipaddress");
                }
            }
            else{
                return null;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //Close open connection
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

        //prepare query and connection
        String query = "INSERT INTO `game` (`id`, `gametime`) VALUES (NULL, NULL);";
        PreparedStatement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        int id = 0;
        try {

            //Execute query
            statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            res = statement.getGeneratedKeys();

            //Get assigned ID
            if(res.next()){
                id = res.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            //TODO handle exception
        }
        finally{

            //Close open connections
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return id;

    }

    public int addPlayer(String name, int kit, int game){

        //Prepare querys and connection
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

            //execute first query
            res = statement.getGeneratedKeys();

            //get assigned id
            if(res.next()){
                id = res.getInt(1);
            }
            statement.close();

            //prepare and execute second query
            statement = con.prepareStatement(query2);
            statement.setInt(1, game);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //Close open connections
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

        //Prepare querys and connection
        String query = "DELETE FROM `game_score` WHERE `game_score`.`player` = "+player;
        String query2 = "DELETE FROM `player` WHERE `player`.`id` = "+player;
        Statement statement = null;
        Connection con = getConnection();
        try {
            statement = con.createStatement();
            statement.addBatch(query);
            statement.addBatch(query2);

            //execute querys
            statement.executeBatch();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //close open connections
            try{
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
    }

    public int PlayerShot(int id){

        //Prepare query and connection
        int points = 0;
        String query = "SELECT * FROM `game_score` WHERE `game_score`.`player` = "+id;
        Statement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        try {
            statement = con.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);

            //execute query
            res = statement.executeQuery(query);

            //update score field in database
            if(res.next()){
                points = res.getInt("score")+1;
                res.updateInt("score", points);
            }
            res.updateRow();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //close open connections
            try{
                if(res != null){res.close();}
                if(statement != null){statement.close();}
                if(con != null){con.close();}
            } catch (Exception e){
                //TODO handle exception
                System.out.println(e.getMessage());
            }
        }
        return points;
    }

    public void StopGame(int game, int time){

        //prepare query and connection
        String query = "SELECT * FROM `game` WHERE `id` = " + game;
        Statement statement = null;
        Connection con = getConnection();
        ResultSet res = null;
        try {
            statement = con.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_UPDATABLE);

            //execute query
            res = statement.executeQuery(query);

            //update timefield in results
            if(res.next()){
                res.updateInt("gametime", time);
            }
            res.updateRow();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            //TODO handle exception
        }
        finally{

            //close open connections
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

}