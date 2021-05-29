package htmlprogrammer.labs.messanger.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.helpers.DateHelper;

public class Message implements Comparable<Message>, Serializable {
    private String id;
    private String message;
    private MessageTypes type;
    private Date time;
    private boolean readed;
    private boolean isSending = false;
    private Dialog dialog;
    private User author;
    private long size = 0;
    private String url = null;
    private ArrayList<Message> resend = new ArrayList<>();

    @Override
    public int compareTo(Message otherMessage) {
        if (getId().equals(otherMessage.getId()))
            return 0;

        int comp = -getTime().compareTo(otherMessage.getTime());
        return comp == 0 ? getId().compareTo(otherMessage.getId()) : comp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message otherMessage = (Message) obj;
            return getId().equals(otherMessage.getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public static Message fromJSON(JSONObject obj){
        Message message = new Message();

        try{
            message.setId(obj.getString("_id"));
            message.setMessage(obj.getString("message"));
            message.setType(MessageTypes.fromInt(obj.getInt("type")));
            message.setTime(DateHelper.parseDate(obj.getString("time")));
            message.setReaded(obj.optInt("readed", 1) == 1);
            message.setSize(obj.optInt("size", 0));
            message.setUrl(obj.optString("url", null));
            message.setAuthor(User.fromJSON(obj.getJSONObject("author")));

            if(obj.optJSONObject("dialog") != null)
                message.setDialog(Dialog.fromJSON(obj.optJSONObject("dialog"), false));

            if(obj.optJSONArray("resend") != null){
                ArrayList<Message> resend = new ArrayList<>();
                JSONArray resendObj = obj.getJSONArray("resend");

                for(int i = 0; i < resendObj.length(); i++)
                    resend.add(Message.fromJSON(resendObj.getJSONObject(i)));

                message.setResend(resend);
            }
        }
        catch (Exception e){}

        return message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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

    public String getDateTimeString(){
        return DateHelper.toDateTimeStr(time);
    }

    public String getTimeString(){
        return DateHelper.toTimeStr(time);
    }

    public String getDateString(){
        return DateHelper.toDateStr(time);
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

    public boolean isSending(){
        return isSending;
    }

    public void setSending(boolean sending){
        this.isSending = sending;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public ArrayList<Message> getResend(){
        return resend;
    }

    public void setResend(ArrayList<Message> resend){
        this.resend = resend;
    }
}
