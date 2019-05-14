package lasertag3000;

import java.io.*;
import java.util.Properties;

//TODO
//Handle exceptions

public final class Config {

    private static Config instance = new Config();

    private File config = null;

    private Config(){
        config = new File("config.properties");
        boolean createFile = true;
        if(config.exists()){
            InputStream input = null;
            try{
                input = new FileInputStream(config);
                Properties prop = new Properties();
                prop.load(input);
                createFile = false;
                if(prop.getProperty("db.url") == null){
                    createFile = true;
                }
                else if(prop.getProperty("db.user") == null){
                    createFile = true;
                }
                else if(prop.getProperty("db.password") == null){
                    createFile = true;
                }
                else if(prop.getProperty("db.port") == null){
                    createFile = true;
                }
                else if(prop.getProperty("Kit.port") == null){
                    createFile = true;
                }
                
            } catch (Exception e) {

            }
            finally{
                try {
                    input.close();
                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

            
        }
        if(createFile){createProperties();}
    }

    public static Config getInstance(){
        return instance;
    }

    private void createProperties(){
        OutputStream output = null;
        try{
            output = new FileOutputStream(config); 
            Properties prop = new Properties();

            prop.setProperty("db.url", "127.0.0.1");
            prop.setProperty("db.user", "admin");
            prop.setProperty("db.password", "password");
            prop.setProperty("db.port", "3306");
            prop.setProperty("Kit.port", "2222");
            prop.setProperty("TCP.port", "9000");
            prop.store(output, null);

        } catch (Exception e) {
            //log error
        }
        finally {
            try {
                output.close();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
    }

    private Properties getProperties(){
        InputStream input = null;
        Properties prop = new Properties();
        try{
            input = new FileInputStream(config);
            prop.load(input);
            
        } catch (Exception e) {

        }
        finally{
            try {
                input.close();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        return prop;
    }

    public String DBURL(){
        Properties prop = getProperties();
        return prop.getProperty("db.url");
    }

    public String DBUSER(){
        Properties prop = getProperties();
        return prop.getProperty("db.user");
    }

    public String DBPASS(){
        Properties prop = getProperties();
        return prop.getProperty("db.password");
    }

    public int DBPORT(){
        Properties prop = getProperties();
        int port = 0;
        try {
            port = Integer.parseInt(prop.getProperty("db.port"));
        } catch (Exception e) {
            //TODO: handle exception
        }
        return port;
    }

    public int KitPort(){
        Properties prop = getProperties();
        int port = 0;
        try {
            port = Integer.parseInt(prop.getProperty("Kit.port"));
        } catch (Exception e) {
            //TODO: handle exception
        }
        return port;
    }

    public int ServerPort(){
        Properties prop = getProperties();
        int port = 0;
        try {
            port = Integer.parseInt(prop.getProperty("TCP.port"));
        } catch (Exception e) {
            //TODO: handle exception
        }
        return port;
    }
}