package htmlprogrammer.labs.messanger.constants;

public enum WSEvents {
    NEW_MESSAGE("newMessage"),
    DELETE_MESSAGE("deleteMessage"),
    UPDATE_MESSAGE("updateMessage"),
    NEW_DIALOG("newDialog"),
    ONLINE("online"),
    OFFLINE("offline"),
    TOGGLE_BAN("toggleBan"),
    VIEW_MESSAGES("viewMessages"),
    SET_STATUS("setStatus"),
    CHANGE_DIALOG_STATUS("changeDialogStatus");

    private String value;

    WSEvents(String val){
        value = val;
    }

    public String getValue(){
        return value;
    }
}
