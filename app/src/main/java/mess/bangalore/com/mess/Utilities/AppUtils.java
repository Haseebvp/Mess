package mess.bangalore.com.mess.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AppUtils {

    public static String getTime(long mills) {
        String time = "";
        if (mills != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, ''yy", Locale.getDefault());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("K:mm a", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);
            time = sdf.format(calendar.getTime());
            time += " at ";
            time += simpleDateFormat.format(calendar.getTime());
        }
        return time;
    }

}
