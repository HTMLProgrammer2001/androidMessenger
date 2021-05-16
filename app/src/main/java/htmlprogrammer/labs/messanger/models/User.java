package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import htmlprogrammer.labs.messanger.helpers.DateHelper;

public class User implements Serializable {
    private String id;
    private String avatar;
    private String fullName = "";
    private String phone = "";
    private String nick = "";
    private String description = "";
    private boolean isBanned = false;
    private boolean isOnline = false;
    private Date lastSeen = new Date();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description.equals("") || description.equals("null"))
            this.description = null;
        else
            this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        if(avatar.equals("") || avatar.equals("null"))
            this.avatar = null;
        else
            this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void parseLastSeen(String date){
        lastSeen = DateHelper.parseDate(date);
    }

    public String getDateTimeString(){
        return DateHelper.toDateTimeStr(lastSeen);
    }

    public static User fromJSON(JSONObject obj){
         User user = new User();

         try {
             user.setId(obj.getString("_id"));
             user.setFullName(obj.getString("name"));
             user.setNick(obj.getString("nickname"));
             user.setPhone(obj.getString("phone"));
             user.setBanned(obj.getBoolean("isBanned"));
             user.setOnline(obj.getBoolean("isOnline"));
             user.setAvatar(obj.optString("avatar", ""));
             user.setDescription(obj.optString("description", ""));
             user.parseLastSeen(obj.getString("lastSeen"));
         }
         catch (Exception e){}

         return user;
    }
}
