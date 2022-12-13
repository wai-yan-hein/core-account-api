/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.acc.api.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author WSwe
 */
@Slf4j
public class Util1 {
    public static HashMap<String, String> hmSysProp = new HashMap<>();
    public static String SYNC_DATE;
    public static final Gson gson = new GsonBuilder()
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create();

    public static boolean getBoolean(String obj) {
        boolean status = false;
        if (!Util1.isNull(obj)) {
            status = obj.equals("1") || obj.equalsIgnoreCase("true");
        }
        return status;

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


}
