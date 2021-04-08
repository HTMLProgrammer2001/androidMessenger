package htmlprogrammer.labs.messanger.api;

import org.json.JSONObject;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserActionsAPI extends ApiClass {
    public static void getMe(String token, APICallback callback){
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/me")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        makeCall(request, callback);
    }

    public static void login(String phone, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("phone", phone);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/login").post(body).build();
            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void confirmLogin(String code, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("code", code);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder()
                    .url(BuildConfig.API_URL + "/confirm/login")
                    .post(body)
                    .build();

            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void signIn(String name, String phone, String nick, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("name", name);
            data.put("nickname", nick);
            data.put("phone", phone);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/sign").post(body).build();
            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void confirmSignIn(String code, APICallback callback){
        JSONObject data = new JSONObject();

        try{
            data.put("code", code);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder()
                    .url(BuildConfig.API_URL + "/confirm/sign")
                    .post(body)
                    .build();

            makeCall(request, callback);
        }
        catch (Exception e){ }
    }

    public static void resend(String phone, int type, APICallback callback){
        JSONObject data = new JSONObject();

        try {
            data.put("phone", phone);
            data.put("type", type);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/resend").post(body).build();
            makeCall(request, callback);
        }
        catch (Exception e){}
    }

    public static void logout(String token, APICallback callback){
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/logout")
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(JSON, ""))
                .build();

        makeCall(request, callback);
    }

    public static void changePhone(String oldPhone, String newPhone, APICallback callback){
        JSONObject data = new JSONObject();

        try {
            data.put("oldPhone", oldPhone);
            data.put("newPhone", newPhone);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/changePhone")
                    .post(body).build();

            makeCall(request, callback);
        }
        catch (Exception e){}
    }

    public static void confirmChangePhone(String oldCode, String newCode, APICallback callback){
        JSONObject data = new JSONObject();

        try {
            data.put("oldCode", oldCode);
            data.put("newCode", newCode);
            RequestBody body = RequestBody.create(JSON, data.toString());

            Request request = new Request.Builder().url(BuildConfig.API_URL + "/confirm/changePhone")
                    .post(body).build();

            makeCall(request, callback);
        }
        catch (Exception e){}
    }
}
