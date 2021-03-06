package htmlprogrammer.labs.messanger.interfaces;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class ApiClass {
    protected static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build();

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    protected static void makeCall(Request req, APICallback callback){
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.run(e, null);
            }

            @Override
            public void onResponse(Call call, Response response){
                callback.run(null, response);
            }
        });
    }
}
