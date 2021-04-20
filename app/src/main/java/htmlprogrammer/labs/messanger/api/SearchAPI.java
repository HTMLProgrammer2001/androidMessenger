package htmlprogrammer.labs.messanger.api;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.Request;

public class SearchAPI extends ApiClass {
    public static void getDialogsByName(String token, String name, int page, int pageSize, APICallback callback){
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/dialogs/name?name" + name + "&pageSize=" + pageSize + "&page=" + page)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        makeCall(request, callback);
    }
}
