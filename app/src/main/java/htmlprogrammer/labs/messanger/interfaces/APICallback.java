package htmlprogrammer.labs.messanger.interfaces;

import okhttp3.Response;

public interface APICallback {
    void run(Exception e, Response response);
}
