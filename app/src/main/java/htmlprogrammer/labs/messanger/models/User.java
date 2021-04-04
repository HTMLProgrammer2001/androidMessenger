package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

public class User {
    private String avatar;
    private String fullName = "";
    private String phone = "";
    private String nick = "";

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
        this.avatar = avatar;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public static User fromJSON(JSONObject obj){
         User user = new User();

         try {
             user.setFullName(obj.getString("name"));
             user.setNick(obj.getString("nickname"));
             user.setPhone(obj.getString("phone"));
             user.setAvatar(obj.getString("avatar"));
         }
         catch (Exception e){}

         return user;
    }
}
