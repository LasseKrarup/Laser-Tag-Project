package lasertag3000;

public class Player {
    private int _id;
    private String _name;
    private Kit _kit;

    public Player(int id, String name, Kit kit){
        _id = id;
        _name = name;
        _kit = kit;
    }

    public String getName(){
        return _name;
    }

    public int getID(){
        return _id;
    }

    public Kit getKit(){
        return _kit;
    }

}