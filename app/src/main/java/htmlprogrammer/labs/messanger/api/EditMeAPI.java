package htmlprogrammer.labs.messanger.api;

import android.webkit.MimeTypeMap;

import org.json.JSONObject;

import java.io.File;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class EditMeAPI extends ApiClass {
    private static void changeMe(String token, String fieldName, String value, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put(fieldName, value);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/me")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void editAvatar(String token, File file, APICallback callback){
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        String name = file.getName();

        //create body
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("avatar", name, RequestBody.create(MediaType.parse(type), file))
                .build();

        //create request
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/me")
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();

        makeCall(request, callback);
    }

    public static void editNick(String token, String newNick, APICallback callback){
        changeMe(token, "nickname", newNick, callback);
    }

    public static void editName(String token, String newName, APICallback callback){
        changeMe(token, "name", newName, callback);
    }

    public static void editDesc(String token, String newDesc, APICallback callback){
        changeMe(token, "description", newDesc, callback);
    }
}
