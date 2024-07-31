package gameobjects;

public class Actor extends ThingHolder implements java.io.Serializable {

    private Room room; // the Room where the Person is at present

    public Actor(String aName, String aDescription, ThingList tl, Room aRoom) {
        super(aName, aDescription, tl); // init super class
        this.room = aRoom;
    }

    public void setRoom(Room aRoom) {
        this.room = aRoom;
    }

    public Room getRoom() {
        return this.room;
    }
}
