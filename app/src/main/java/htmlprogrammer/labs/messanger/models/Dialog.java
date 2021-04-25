package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.constants.DialogTypes;

public class Dialog implements Comparable<Dialog> {
    private String id;
    private String name;
    private String nick;
    private String avatar;
    private Message message = null;
    private int unread = 0;
    private int partCount;
    private DialogTypes type;
    private boolean isActive = true;
    private String user;

    @Override
    public int compareTo(Dialog otherDialog) {
        Message otherMessage = otherDialog.getMessage();

        if (getId().equals(otherDialog.getId()))
            return 0;

        if (message == null && otherMessage == null)
            return getId().compareTo(otherDialog.getId());

        if (message != null && otherMessage == null)
            return -1;

        if (message == null && otherMessage != null)
            return 1;

        return message.getTime().compareTo(otherMessage.getTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dialog) {
            Dialog otherDialog = (Dialog) obj;
            return getId().equals(otherDialog.getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public static Dialog fromJSON(JSONObject obj){
        return fromJSON(obj, true);
    }

    public static Dialog fromJSON(JSONObject obj, boolean withMessage) {
        Dialog dialog = new Dialog();

        try {
            dialog.setId(obj.getString("_id"));
            dialog.setName(obj.getString("name"));
            dialog.setNick(obj.getString("nick"));
            dialog.setUnread(obj.getInt("unread"));
            dialog.setPartCount(obj.getInt("partCount"));
            dialog.setType(DialogTypes.fromInt(obj.getInt("type")));
            dialog.setAvatar(obj.optString("avatar", null));
            dialog.setActive(obj.getBoolean("isActive"));

            if (obj.optJSONObject("lastMessage") != null && withMessage) {
                dialog.setMessage(Message.fromJSON(obj.getJSONObject("lastMessage")));
            }

            dialog.setUser(obj.getString("user"));
        } catch (Exception e) {}

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

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public DialogTypes getType() {
        return type;
    }

    public void setType(DialogTypes type) {
        this.type = type;
    }

    public int getPartCount() {
        return partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
