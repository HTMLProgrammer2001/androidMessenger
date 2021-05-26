package htmlprogrammer.labs.messanger.constants;

public enum WSEvents {
    NEW_MESSAGE("newMessage"),
    DELETE_MESSAGE("deleteMessage"),
    UPDATE_MESSAGE("updateMessage"),
    NEW_DIALOG("newDialog"),
    ONLINE("online"),
    OFFLINE("offline");

    private String value;

    WSEvents(String val){
        value = val;
    }

    public String getValue(){
        return value;
    }
}