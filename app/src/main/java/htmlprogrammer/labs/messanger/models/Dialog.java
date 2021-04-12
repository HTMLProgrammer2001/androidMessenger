package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.constants.DialogTypes;

public class Dialog {
    private String id;
    private String name;
    private String nick;
    private String avatar;
    private Message message = null;
    private int unread = 0;
    private int partCount;
    private DialogTypes type;

    public static Dialog fromJSON(JSONObject obj){
        Dialog dialog = new Dialog();

        try {
            dialog.setId(obj.getString("_id"));
            dialog.setName(obj.getString("name"));
            dialog.setNick(obj.getString("nick"));
            dialog.setUnread(obj.getInt("unread"));
            dialog.setPartCount(obj.getInt("partCount"));
            dialog.setType(DialogTypes.fromInt(obj.getInt("type")));
            dialog.setAvatar(obj.optString("avatar", null));

            if(obj.optJSONObject("lastMessage") != null){
                dialog.setMessage(Message.fromJSON(obj.getJSONObject("lastMessage")));
            }
        }
        catch (Exception e){}

        return dialog;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getUnread(){
        return unread;
    }

    public void setUnread(int unread){
        this.unread = unread;
    }

    public DialogTypes getType(){
        return type;
    }

    public void setType(DialogTypes type){
        this.type = type;
    }

    public int getPartCount(){
        return partCount;
    }

    public void setPartCount(int partCount){
        this.partCount = partCount;
    }
}
