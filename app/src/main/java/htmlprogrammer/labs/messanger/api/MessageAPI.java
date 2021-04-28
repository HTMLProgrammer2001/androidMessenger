package htmlprogrammer.labs.messanger.api;

import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.File;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.constants.MessageTypes;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MessageAPI extends ApiClass {
    public static void sendTextMessage(String token, String to, String message, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("dialog", to);
            data.put("message", message);
            data.put("type", MessageTypes.TEXT.getValue());

            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/messages")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void sendFileMessage(String token, String to, MessageTypes msgType, File file, APICallback callback){
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
                .build();

        makeCall(request, callback);
    }
}
