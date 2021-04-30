package htmlprogrammer.labs.messanger.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static Date parseDate(String date){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return format.parse(date);
        }
        catch (Exception err){
            return null;
        }
    }

    public static String toDateTimeStr(Date date){
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        if(date != null)
            return outputFormat.format(date);

        return "";
    }

    public static String toDateStr(Date date){
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        if(date != null)
            return outputFormat.format(date);

        return "";
    }

    public static String toTimeStr(Date date){
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

        if(date != null)
            return outputFormat.format(date);

        return "";
    }
}
