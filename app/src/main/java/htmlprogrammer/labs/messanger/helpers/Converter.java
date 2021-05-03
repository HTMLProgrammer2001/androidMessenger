package htmlprogrammer.labs.messanger.helpers;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Converter {
    public static String toTimeStr(int millis){
        int minutes = millis / 1000 / 60;
        int seconds = millis / 1000 - minutes * 60;

        return minutes + ":" + seconds;
    }

    public static String toSizeStr(long bytes){
        long absBytes = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);

        if(absBytes < 1024)
            return bytes + "B";

        long value = absBytes;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");

        for (int i = 40; i >= 0 && absBytes > 0xfffccccccccccccL >> i; i -= 10){
            value >>= 10;
            ci.next();
        }

        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
