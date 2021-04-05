package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

public class Dialog {
    private String name;
    private String nick;
    private String avatar;
    private Message message;

    public static Dialog fromJSON(JSONObject obj){
        Dialog dialog = new Dialog();

        try {
            dialog.setName(obj.getString("name"));
            dialog.setAvatar(obj.getString("avatar"));
            dialog.setNick(obj.getString("nickname"));
        }
        catch (Exception e){}

        return dialog;
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
}
