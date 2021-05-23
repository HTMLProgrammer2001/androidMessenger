package htmlprogrammer.labs.messanger.api;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class FriendAPI extends ApiClass {
    public static void getFriendsByName(String token, String name, int page, int pageSize, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/users/friends/name").newBuilder();
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        urlBuilder.addQueryParameter("page", Integer.toString(page));
        urlBuilder.addQueryParameter("name", name);

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token)
                .get().build();

        makeCall(request, callback);
    }

    public static void getFriendsByNick(String token, String nick, int page, int pageSize, APICallback callback){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.API_URL + "/users/friends/nick").newBuilder();
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        urlBuilder.addQueryParameter("page", Integer.toString(page));
        urlBuilder.addQueryParameter("nick", nick);

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .addHeader("Authorization", "Bearer " + token)
                .get().build();

        makeCall(request, callback);
    }
}
