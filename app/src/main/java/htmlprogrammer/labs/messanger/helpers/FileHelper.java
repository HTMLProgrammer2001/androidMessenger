package htmlprogrammer.labs.messanger.helpers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileHelper {
    public static String getRealPathFromUri(Activity activity, Uri contentUri) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);

        if(cursor == null)
            result = contentUri.getPath();
        else{
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }
}
