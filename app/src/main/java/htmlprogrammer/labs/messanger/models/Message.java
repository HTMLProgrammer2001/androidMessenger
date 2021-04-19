package htmlprogrammer.labs.messanger.models;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import htmlprogrammer.labs.messanger.constants.MessageTypes;

public class Message {
    private String id;
    private String message;
    private MessageTypes type;
    private Date time;
    private boolean readed;
    private int size = 0;
    private String url = null;

    public static Message fromJSON(JSONObject obj){
        Message message = new Message();
        Date time = null;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            time = format.parse(obj.getString("time"));
        }
        catch (Exception e){ }

        try{
            message.setId(obj.getString("_id"));
            message.setMessage(obj.getString("message"));
            message.setTime(time);
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

    public Date getTime() {
        return time;
    }

    public String getTimeString(){
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        if(time != null)
            return outputFormat.format(time);

        return "";
    }

    public void setTime(Date time) {
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
