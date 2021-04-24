package htmlprogrammer.labs.messanger.api;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class SearchAPI extends ApiClass {
    public static void getDialogsByName(String token, String name, int page, int pageSize, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/dialogs/name").newBuilder();
        urlBuilder.addQueryParameter("name", name);
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        urlBuilder.addQueryParameter("page", Integer.toString(page));

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token)
                .get().build();

        makeCall(request, callback);
    }

    public static void getDialogsByNick(String token, String nick, int page, int pageSize, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/dialogs/nickname").newBuilder();
        urlBuilder.addQueryParameter("nickname", nick);
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        urlBuilder.addQueryParameter("page", Integer.toString(page));

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token).get().build();

        makeCall(request, callback);
    }

    public static void getMessagesByName(String token, String text, int page, int pageSize, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/messages/text").newBuilder();
        urlBuilder.addQueryParameter("text", text);
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        urlBuilder.addQueryParameter("page", Integer.toString(page));

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token).get().build();

        makeCall(request, callback);
    }

    public static void getUserByNickname(String token, String nick, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/users/nickname/" + nick).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token).get().build();

        makeCall(request, callback);
    }

    public static void getDialogByNickname(String token, String nick, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/dialogs/nickname/" + nick).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token).get().build();

        makeCall(request, callback);
    }
}
