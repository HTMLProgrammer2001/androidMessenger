package htmlprogrammer.labs.messanger.constants;

public enum WSEvents {
    NEW_MESSAGE("newMessage"),
    ONLINE("online");

    private String value;

    WSEvents(String val){
        value = val;
    }

    public String getValue(){
        return value;
    }
}
