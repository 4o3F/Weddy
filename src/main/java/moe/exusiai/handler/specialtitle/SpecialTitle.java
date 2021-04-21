package moe.exusiai.handler.specialtitle;

import moe.exusiai.data.SpecialTitleData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SpecialTitle {
    public static String GetSpecialTitle(Boolean bedrock) {
        Integer lastnumber = SpecialTitleData.specialrank.get("lastnumber");
        lastnumber++;
        SpecialTitleData.specialrank.put("lastnumber", lastnumber);
        String titlenumber = String.format("%03d", lastnumber);
        String titleletter = String.valueOf((char)(97+getMonthDiffer())).toUpperCase();
        if (titleletter.equals("R")) {
            titleletter = String.valueOf((char)(97+getMonthDiffer()+1)).toUpperCase();
        }
        if (bedrock) {
            titleletter = "BE";
        }
        return titleletter+titlenumber;
    }

    private static int getMonthDiffer() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar startcalendar = Calendar.getInstance();
        try {
            startcalendar.setTime(dateFormat.parse("2021-04-01 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar presentcalendar = Calendar.getInstance();
        presentcalendar.setTime(new Date());

        return presentcalendar.get(Calendar.MONTH) - startcalendar.get(Calendar.MONTH);
    }
}
