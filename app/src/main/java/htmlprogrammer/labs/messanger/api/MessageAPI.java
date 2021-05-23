package htmlprogrammer.labs.messanger.api;

import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MessageAPI extends ApiClass {
    public static void sendTextMessage(String token, String to, String message, String id, APICallback callback) {
        JSONObject data = new JSONObject();

        try {
            data.put("dialog", to);
            data.put("message", message);
            data.put("type", MessageTypes.TEXT.getValue());

            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/messages")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .tag("send:" + id)
                    .build();

            makeCall(request, callback);
        } catch (Exception e) {}
    }

    public static void sendFileMessage(String token, String to, MessageTypes msgType, File file, String id, APICallback callback) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        String name = file.getName();

        //create body
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", name, RequestBody.create(MediaType.parse(type), file))
                .addFormDataPart("dialog", to)
                .addFormDataPart("message", name)
                .addFormDataPart("type", String.valueOf(msgType.getValue()))
                .build();

        //create request
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/messages")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .tag("send:" + id)
                .build();

        makeCall(request, callback);
    }

    public static void cancelSend(String id) {
        for (Call call : client.dispatcher().runningCalls())
            if (call.request().tag().equals("send:" + id))
                call.cancel();
    }

    public static void getMessages(String token, String dialog, int page, int pageSize, APICallback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/messages/chat/" + dialog).newBuilder();
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        urlBuilder.addQueryParameter("page", Integer.toString(page));

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token)
                .get().build();

        makeCall(request, callback);
    }

    public static void deleteMessages(String token, boolean forOther, String[] messages, APICallback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/messages").newBuilder();

        for(int i = 0; i < messages.length; i++){
            urlBuilder.addEncodedQueryParameter("messages[]", messages[i]);
        }

        urlBuilder.addQueryParameter("forOthers", Boolean.valueOf(forOther).toString());

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Authorization", "Bearer " + token)
                .delete().build();

        makeCall(request, callback);
    }

    public static void editTextMessage(String token, String messageID, String message, String id, APICallback callback){
        JSONObject data = new JSONObject();

        try {
            data.put("message", message);
            data.put("type", MessageTypes.TEXT.getValue());

            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/messages/" + messageID)
                    .addHeader("Authorization", "Bearer " + token)
                    .put(body)
                    .tag("send:" + id)
                    .build();

            makeCall(request, callback);
        } catch (Exception e) {}
    }

    public static void editFileMessage(String token, String messageID, MessageTypes msgType, File file, String id, APICallback callback) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        String name = file.getName();

        //create body
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", name, RequestBody.create(MediaType.parse(type), file))
                .addFormDataPart("message", name)
                .addFormDataPart("type", String.valueOf(msgType.getValue()))
                .build();

        //create request
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/messages/" + messageID)
                .addHeader("Authorization", "Bearer " + token)
                .put(body)
                .tag("send:" + id)
                .build();

        makeCall(request, callback);
    }

    public static void resend(String token, String[] to, String[] messages, APICallback callback){
        JSONObject data = new JSONObject();

        try {
            data.put("to", new JSONArray(to));
            data.put("messages", new JSONArray(messages));

            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/messages/resend")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            makeCall(request, callback);
        } catch (Exception e) {}
    }
}
