package htmlprogrammer.labs.messanger.interfaces;

import java.io.FileNotFoundException;

import okhttp3.Response;

public interface APICallback {
    void run(Exception e, Response response);
}
