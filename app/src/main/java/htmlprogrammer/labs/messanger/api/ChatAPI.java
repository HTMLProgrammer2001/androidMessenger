package htmlprogrammer.labs.messanger.api;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChatAPI extends ApiClass {
    public static void ban(String token, String userID, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("id", userID);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/users/ban")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void clear(String token, String dialogID, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("dialog", dialogID);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/dialogs/clear")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            makeCall(request, callback);
        }
        catch (Exception e){ }
    }
}
