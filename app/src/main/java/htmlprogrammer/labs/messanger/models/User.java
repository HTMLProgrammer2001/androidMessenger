package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

public class User {
    private String id;
    private String avatar;
    private String fullName = "";
    private String phone = "";
    private String nick = "";
    private String description = "";

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

    public static User fromJSON(JSONObject obj){
         User user = new User();

         try {
             user.setId(obj.getString("_id"));
             user.setFullName(obj.getString("name"));
             user.setNick(obj.getString("nickname"));
             user.setPhone(obj.getString("phone"));
             user.setAvatar(obj.getString("avatar"));
             user.setDescription(obj.getString("description"));
         }
         catch (Exception e){}

         return user;
    }
}
