/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.myanmartools.TransliterateZ2U;
import com.google.myanmartools.ZawgyiDetector;
import core.acc.api.model.DateModel;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author WSwe
 */
@Slf4j
public class Util1 {
    public static HashMap<String, String> hmSysProp = new HashMap<>();
    private static final DecimalFormat df0 = new DecimalFormat("0");
    public static String SYNC_DATE;
    public static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();

    public static boolean getBoolean(Object obj) {
        return obj != null && (obj.toString().equals("1") || obj.toString().equalsIgnoreCase("true"));

    }
    public static Date toDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String strDate = f2.format(date) + " " + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
        try {
            date = formatter.parse(strDate);
        } catch (ParseException ex) {
            log.error(String.format("toDateTime: %s", ex.getMessage()));
        }
        return date;
    }
    public static String minusDay(String sqlFormat, int minusDay) {
        LocalDate date = LocalDate.parse(sqlFormat);
        LocalDate minusDays = date.minusDays(minusDay);
        return minusDays.toString();
    }

    public static String getPercent(Double d) {
        DecimalFormat format = new DecimalFormat("#,##0.00%");
        return format.format(d);
    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }


    public static Date toDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            if (objDate != null) {
                date = formatter.parse(objDate.toString());
            }
        } catch (ParseException ex) {
            log.info("toDateStr Error : " + ex.getMessage());
        }

        return date;
    }

    public static boolean isDate(String str) {

        return str.length() == 10;
    }

    public static boolean isSameDate(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }

    public static String toDateStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static Date toDate(Object objDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;

        try {
            date = formatter.parse(objDate.toString());
        } catch (ParseException ex) {
            try {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                date = formatter.parse(objDate.toString());
            } catch (ParseException ex1) {
                log.info("toDateStr Error : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String strDate = null;

        try {
            strDate = formatter.format(date);
        } catch (Exception ex) {
            System.out.println("toDateStr Error : " + ex.getMessage());
        }

        return strDate;
    }

    public static String toDateStr(String strDate, String inFormat, String outFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(outFormat);
        String date = null;

        try {
            date = formatter.format(toDate(strDate, inFormat));
        } catch (Exception ex) {
            try {
                date = formatter.format(toDate(strDate, outFormat));
            } catch (Exception ex1) {
                log.info("toDateStr : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static Double toNull(double value) {
        return value == 0 ? null : value;
    }

    public static Date getTodayDate() {
        return Calendar.getInstance().getTime();
    }

    public static String toDateStrMYSQL(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, format));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static Date addDateTo(Date date, int ttlDay) {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date tmp = null;

        try {
            //c.setTime(toDate(date, "yyyy-MM-dd")); // Now use today date.
            c.setTime(date);
            c.add(Calendar.DATE, ttlDay);
            tmp = c.getTime();
        } catch (Exception ex) {
            log.info("addDateTo : " + ex.getMessage());
        }

        return tmp;
    }

    public static String isAll(String value) {
        if (value != null) {
            if (value.equals("All")) {
                return "-";
            }
        }
        return Util1.isNull(value, "-");
    }

    public static String isNull(String strValue, String value) {
        if (strValue == null) {
            return value;
        } else if (strValue.isEmpty()) {
            return value;
        } else {
            return strValue;
        }
    }

    public static boolean isNull(String value) {
        boolean status = false;
        if (value == null) {
            status = true;
        } else if (value.isBlank()) {
            status = true;
        }
        return status;
    }

    public static Double getDouble(Object number) {
        double value = 0.0;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Double.parseDouble(number.toString());
            }
        }
        return value;
    }

    public static String getString(Object value) {
        return value == null ? null : value.toString();
    }

    public static int getInteger(Object number) {
        int value = 0;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Integer.parseInt(number.toString());
            }
        }
        return value;
    }

    public static JDialog getLoading(JDialog owner, ImageIcon icon) {
        JDialog dialog = new JDialog(owner, false);
        dialog.getContentPane().setBackground(Color.white);
        dialog.setSize(70, 70);
        dialog.getContentPane().setLayout(new BorderLayout());
        JLabel lblImg = new JLabel(icon);
        lblImg.setLocation(70, 0);
        dialog.add(lblImg);
        dialog.getContentPane().add(lblImg, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.validate();
        return dialog;
    }

    public static JDialog getLoading(JFrame owner, ImageIcon icon) {
        JDialog dialog = new JDialog(owner, false);
        dialog.getContentPane().setBackground(Color.white);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(70, 70);
        dialog.getContentPane().setLayout(new BorderLayout());
        JLabel lblImg = new JLabel(icon);
        lblImg.setLocation(70, 0);
        dialog.add(lblImg);
        dialog.getContentPane().add(lblImg, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.validate();
        return dialog;
    }


    public static void writeJsonFile(Object data, String exportPath) throws IOException {
        try (Writer writer = new FileWriter(exportPath, StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }

    public static void extractZipToJson(byte[] zipData, String exportPath) {
        try {
            File file = new File(exportPath.concat(".zip"));
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(zipData);
            }
            try (ZipFile zf = new ZipFile(exportPath.concat(".zip"))) {
                zf.extractAll("temp");
            }
        } catch (IOException ex) {
            log.error("extractZipToJson : " + ex.getMessage());
        }
    }

    public static byte[] zipJsonFile(String exportPath) throws IOException {
        String zipPath = exportPath.replace(".json", ".zip");
        File file = new File(exportPath);
        try (ZipFile fr = new ZipFile(zipPath)) {
            fr.addFile(file);
        }
        FileInputStream stream = new FileInputStream(zipPath);
        byte[] data = stream.readAllBytes();
        stream.close();
        return data;
    }

    public static boolean isMultiCur() {
        return Util1.getBoolean(hmSysProp.get("system.multi.currency.flag"));
    }

    public static String getProperty(String key) {
        return hmSysProp.get(key);
    }

    public static Date getSyncDate() {
        return Util1.toDate(SYNC_DATE);
    }

    public static Date getOldDate() {
        return Util1.toDate("1998-10-07");
    }

    public static boolean isZGText(String str) {
        if (Util1.isNullOrEmpty(str)) return false;
        ZawgyiDetector zd = new ZawgyiDetector();
        Double score = zd.getZawgyiProbability(str);
        return getBoolean(df0.format(score));
    }

    public static String convertToUniCode(String str) {
        if (isZGText(str)) {
            TransliterateZ2U z2U = new TransliterateZ2U("Zawgyi to Unicode");
            return z2U.convert(str);
        }
        return str;
    }

    public static String cleanStr(String str) {
        return str.strip();
    }

    public static java.util.List<DateModel> generateDate(String opDate,String fromDate) {
        java.util.List<DateModel> list = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(fromDate);
        LocalDate todayDate = LocalDate.now();
        while (!startDate.isAfter(todayDate.plusMonths(3))) {
            String monthName = startDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = startDate.withDayOfMonth(startDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int month = startDate.getMonthValue();
            int year = startDate.getYear();
            DateModel m = new DateModel();
            m.setMonthName(monthName);
            m.setStartDate(startDateStr);
            m.setEndDate(endDateStr);
            m.setMonth(month);
            m.setYear(year);
            m.setDescription(monthName + "/" + year);
            list.add(m);
            startDate = startDate.plusMonths(1);
        }
        //all
        DateModel all = new DateModel();
        String todayDateStr = todayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        all.setDescription("All");
        all.setStartDate(opDate);
        all.setEndDate(todayDateStr);
        list.add(0, all);
        //today
        DateModel today = new DateModel();
        today.setDescription("Today");
        today.setMonth(todayDate.getMonthValue());
        today.setYear(todayDate.getYear());
        today.setStartDate(todayDateStr);
        today.setEndDate(todayDateStr);
        list.add(1, today);
        //yesterday
        DateModel yesterday = new DateModel();
        LocalDate yesDate = LocalDate.now().minusDays(1);
        String yesterdayStr = yesDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        yesterday.setDescription("Yesterday");
        yesterday.setMonth(yesDate.getMonthValue());
        yesterday.setYear(yesDate.getYear());
        yesterday.setStartDate(yesterdayStr);
        yesterday.setEndDate(yesterdayStr);
        list.add(2, yesterday);
        DateModel custom = new DateModel();
        custom.setDescription("Custom");
        list.add(3, custom);
        return list;
    }

    private static String getMonthShortName(Month month) {
        String strMonth = month.toString();
        if (strMonth.length() >= 4) {
            strMonth = strMonth.substring(0, 3);
        }
        return strMonth;
    }

}
