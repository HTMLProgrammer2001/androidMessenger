package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.constants.MessageTypes;

public class Message {
    private String id;
    private String message;
    private MessageTypes type;
    private String time;
    private boolean readed;
    private int size = 0;
    private String url = null;

    public static Message fromJSON(JSONObject obj){
        Message message = new Message();

        try{
            message.setId(obj.getString("_id"));
            message.setMessage(obj.getString("message"));
            message.setTime(obj.getString("time"));
            message.setType(MessageTypes.fromInt(obj.getInt("type")));
            message.setReaded(obj.optBoolean("readed", false));
            message.setSize(obj.optInt("size", 0));
            message.setUrl(obj.optString("url", null));
        }
        catch (Exception e){}

        return message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageTypes getType() {
        return type;
    }

    public void setType(MessageTypes type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
