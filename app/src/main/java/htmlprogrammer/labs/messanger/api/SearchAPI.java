package htmlprogrammer.labs.messanger.api;

import htmlprogrammer.labs.messanger.BuildConfig;
import htmlprogrammer.labs.messanger.interfaces.APICallback;
import htmlprogrammer.labs.messanger.interfaces.ApiClass;
import okhttp3.Request;

public class SearchAPI extends ApiClass {
    public static void getDialogsByName(String token, String name, APICallback callback){
        Request request = new Request.Builder()
                .url(BuildConfig.API_URL + "/dialogs/name?name" + name + "&pageSize=" + 15)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        makeCall(request, callback);
    }
}
