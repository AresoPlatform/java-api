

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
    public static final SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat ymr = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");


    private static final Object lockObj = new Object();

    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();


    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date getDate_(String dateStr){
        try {
            return getSdf("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Date();
    }


    public static String formatDateByType(Date date, SimpleDateFormat format){
        return format(date,format.toPattern());
    }



    public static String parseDate(Date date){
        return sdf1.format(date);
    }

    public static boolean isArrival(Date date, int s){
        long time = date.getTime()+(s*1000);
        if (time>new Date().getTime()){
            return false;
        }
        return true;
    }

    public static Date addUserDefined(Date date,int field, int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field,amount);
        return calendar.getTime();
    }

    public static Date getSecondDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day + 1);
        String temp = format(cal.getTime(), "yyyy-MM-dd 00:00:00");
        try {
            return sdf_.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDate(Long timeStamp){
        return new Date(timeStamp);
    }

}
