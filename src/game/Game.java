package game;

import java.util.*;     // required for ArrayList
import gameobjects.Actor;
import gameobjects.Room;
import gameobjects.Thing;
import gameobjects.ThingList;
import gameobjects.Treasure;
import globals.Direction;

public class Game implements java.io.Serializable {

    private ArrayList<Room> map; // an ArrayList of Rooms
    private Actor player;  // provides 'first person perspective'

    List<String> commands = new ArrayList<>(Arrays.asList(
            "take", "drop", "look", "l", "i", "inventory",
            "n", "s", "w", "e",
            "save", "load"));
    List<String> objects = new ArrayList<>(Arrays.asList("carrot", "sausage",
            "paper", "pencil", "ring", "wombat"));

    public Game() {
        this.map = new ArrayList<Room>(); // TODO: Make map a Generic list of Room
        // --- construct a new adventure ---

        ThingList trollRoomList = new ThingList();
        trollRoomList.add(new Treasure("carrot", "It is a very crunchy carrot", 1));

        ThingList forestList = new ThingList();
        forestList.add(new Treasure("sausage", "It is a plump pork sausage", 10));

        ThingList caveList = new ThingList();
        caveList.add(new Treasure("paper", "Someone has written a message on the scrap of paper using a blunt pencil. It says 'This space is intentionally left blank'", 1));
        caveList.add(new Treasure("pencil", "This pencil is so blunt that it can no longer be used to write.", 1));

        ThingList dungeonList = new ThingList();
        dungeonList.add(new Treasure("ring", "It is a ring of great power.", 500));
        dungeonList.add(new Treasure("wombat", "It's a cuddly little wombat. It is squeaking gently to itself.", 700));

        ThingList playerlist = new ThingList();
        // Add Rooms to the map
        // Room(name, description, N, S, W, E)
        map.add(new Room("Troll Room", "A dank room that smells of troll", Direction.NOEXIT, 2, Direction.NOEXIT, 1, trollRoomList));
        map.add(new Room("Forest", "A leafy woodland", Direction.NOEXIT, Direction.NOEXIT, 0, Direction.NOEXIT, forestList));
        map.add(new Room("Cave", "A dismal cave with walls covered in luminous moss", 0, Direction.NOEXIT, Direction.NOEXIT, 3, caveList));
        map.add(new Room("Dungeon", "A nasty, dark cell", Direction.NOEXIT, Direction.NOEXIT, 2, Direction.NOEXIT, dungeonList));

        // create player and place in Room 0 (i.e. the Room at 0 index of map)
        player = new Actor("player", "a loveable game-player", playerlist, map.get(0));
    }

    // access methods

    // map
    ArrayList getMap() {
        return map;
    }

    void setMap(ArrayList aMap) {
        map = aMap;
    }

    // player
    public Actor getPlayer() {
        return player;
    }

    public void setPlayer(Actor aPlayer) {
        player = aPlayer;
    }

    // take and drop
    private void transferOb(Thing t, ThingList fromlist, ThingList tolist) {
        fromlist.remove(t);
        tolist.add(t);
    }

    public String takeOb(String obname) {
        String retStr = "";
        Thing t = player.getRoom().getThings().thisOb(obname);
        if (obname.equals("")) {
            obname = "nameless object"; // if no object specified
        }
        if (t == null) {
            retStr = "There is no " + obname + " here!";
        } else {
            transferOb(t, player.getRoom().getThings(), player.getThings());
            retStr = obname + " taken!";
        }
        return retStr;
    }

    public String dropOb(String obname) {
        String retStr = "";
        Thing t = player.getThings().thisOb(obname);
        if (obname.equals("")) {
            retStr = "You'll have to tell me which object you want to drop!"; // if no object specified
        } else if (t == null) {
            retStr = "You haven't got one of those!";
        } else {
            transferOb(t, player.getThings(), player.getRoom().getThings());
            retStr = obname + " dropped!";
        }
        return retStr;
    }

    // move a Person to a Room
    void moveActorTo(Actor p, Room aRoom) {
        p.setRoom(aRoom);
    }

    // move an Actor in direction 'dir'
    int moveTo(Actor anActor, Direction dir) {
        // return: Constant representing the room number moved to
        // or NOEXIT
        //
        // try to move any Person (typically but not necessarily player)
        // in direction indicated by dir
        Room r = anActor.getRoom();
        int exit;

        switch (dir) {
            case NORTH:
                exit = r.getN();
                break;
            case SOUTH:
                exit = r.getS();
                break;
            case EAST:
                exit = r.getE();
                break;
            case WEST:
                exit = r.getW();
                break;
            default:
                exit = Direction.NOEXIT; // noexit - stay in same room
                break;
        }
        if (exit != Direction.NOEXIT) {
            moveActorTo(anActor, map.get(exit));
        }
        return exit;
    }

    public int movePlayerTo(Direction dir) {
        // return: Constant representing the room number moved to
        // or NOEXIT (see moveTo())
        return moveTo(player, dir);
    }

    private void goN() {
        showRoomDescription(movePlayerTo(Direction.NORTH));
    }

    private void goS() {
        showRoomDescription(movePlayerTo(Direction.SOUTH));
    }

    private void goW() {
        showRoomDescription(movePlayerTo(Direction.WEST));
    }

    private void goE() {
        showRoomDescription(movePlayerTo(Direction.EAST));
    }

    private void look() {
        showStr("You are in the " + getPlayer().getRoom().describe());
    }

    private void showStr(String s) {
        System.out.println(s);
    }

    private void showRoomDescription(int roomNumber) {
        // if roomNumber = NOEXIT, display a special message, otherwise
        // display text (e.g. name and description of room)        
        String s;
        if (roomNumber == Direction.NOEXIT) {
            s = "No Exit!";
        } else {
            Room r = getPlayer().getRoom();
            s = "You are in the " + r.describe();
        }
        showStr(s);
    }

    private void showInventory() {
        showStr("You have " + getPlayer().getThings());
    }
   
    public String processVerb(List<String> wordlist) {
        String verb;
        String msg = "";
        verb = wordlist.get(0);
        if (!commands.contains(verb)) {
            msg = verb + " is not a known verb! ";
        } else {
            switch (verb) {
                case "n":
                    goN();
                    break;
                case "s":
                    goS();
                    break;
                case "w":
                    goW();
                    break;
                case "e":
                    goE();
                    break;
                case "l":
                case "look":
                    look();
                    break;
                case "inventory":
                case "i":
                    showInventory();
                    break;               
                default:
                    msg = verb + " (not yet implemented)";
                    break;
            }
        }
        return msg;
    }

    public String processVerbNoun(List<String> wordlist) {
        String verb;
        String noun;
        String msg = "";
        boolean error = false;
        verb = wordlist.get(0);
        noun = wordlist.get(1);
        if (!commands.contains(verb)) {
            msg = verb + " is not a known verb! ";
            error = true;
        }
        if (!objects.contains(noun)) {
            msg += (noun + " is not a known noun!");
            error = true;
        }
        if (!error) {
            switch (verb) {
                case "take":
                    msg = takeOb(noun);
                    break;
                case "drop":
                    msg = dropOb(noun);
                    break;
                default:
                    msg += " (not yet implemented)";
                    break;
            }
        }
        return msg;
    }

    public String parseCommand(List<String> wordlist) {
        String msg;
        if (wordlist.size() == 1) {
            msg = processVerb(wordlist);
        } else if (wordlist.size() == 2) {
            msg = processVerbNoun(wordlist);
        } else {
            msg = "Only 2 word commands allowed!";
        }
        return msg;
    }

    public List<String> wordList(String input) {
        String delims = " \t,.:;?!\"'";
        List<String> strlist = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, delims);
        String t;

        while (tokenizer.hasMoreTokens()) {
            t = tokenizer.nextToken();
            strlist.add(t);
        }
        return strlist;
    }

    public void showIntro() {
        String s;
        s = "You have fallen down a rabbit hole and arrived in\n"
                + "an underground cavern that smells strongly of troll.\n"
                + "Where do you want to go? [Enter n, s, w or e]?\n"
                + "(or enter q to quit)";
        showStr(s);
        showRoomDescription(0);
    }

    public String runCommand(String inputstr) {
        List<String> wordlist;
        String s = "ok";
        String lowstr = inputstr.trim().toLowerCase();
        if (!lowstr.equals("q")) {
            if (lowstr.equals("")) {
                s = "You must enter a command";
            } else {
                wordlist = wordList(lowstr);
                s = parseCommand(wordlist);
            }
        }
        return s;
    }

}
