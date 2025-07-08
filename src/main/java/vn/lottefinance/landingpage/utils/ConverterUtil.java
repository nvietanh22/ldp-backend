package vn.lottefinance.landingpage.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConverterUtil {

    private static final SimpleDateFormat dateSdf = new SimpleDateFormat(
            "hh:mm:ss dd/MM/yyyy");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static final DecimalFormat numberDf = new DecimalFormat("######");
    private static final DecimalFormat doubleDf = new DecimalFormat(
            "######.#########");
    private static final DecimalFormat numberCommaDf = new DecimalFormat(
            "###,###");
    private static final DecimalFormat doubleCommaDf = new DecimalFormat(
            "###,###.#########");

    private static final DecimalFormat percentDf = new DecimalFormat("#%");

    /**
     * Convert Object to JSON
     *
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj) {
        String json = "";
        if (obj == null)
            return json;
        return gson.toJson(obj);
    }

    /**
     * Convert List to JSON
     *
     * @param objs
     * @return
     */
    public static String toJsonString(List<Object> objs) {
        String json = "";
        if (objs == null)
            return json;
        return gson.toJson(objs);
    }


    /**
     * Convert string to object
     *
     * @param c
     * @param json
     * @return
     */
    public static <T> T fromJSON(Class<T> c, String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        return gson.fromJson(reader, c);
    }

    /**
     * Convert string date to date
     *
     * @param s
     * @return
     */
    public static Date toDate(String s) {
        try {
            return dateSdf.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert date to string
     *
     * @param date
     * @return
     */
    public static String toStringDate(Date date) {
        if (date == null) {
            return "";
        }
        return dateSdf.format(date);
    }

    /**
     * Convert date to string
     *
     * @param date
     * @return
     */
    public static String toString(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(
                pattern);
        return df.format(date);
    }

    /**
     *
     * @param d
     * @return
     */
    public static String formatNumber(Long d) {
        if (d == null) {
            return "";
        } else {
            return numberDf.format(d);
        }
    }

    /**
     *
     * @param d
     * @return
     */
    public static String formatNumber(Double d) {
        if (d == null) {
            return "";
        } else {
            return doubleDf.format(d);
        }
    }

    /**
     *
     * @param d
     * @return
     */
    public static String formatCommaNumber(Long d) {
        if (d == null) {
            return "";
        } else {
            return numberCommaDf.format(d);
        }
    }

    /**
     *
     * @param d
     * @return
     */
    public static String formatCommaNumber(Double d) {
        if (d == null) {
            return "";
        } else {
            return doubleCommaDf.format(d);
        }
    }

    /**
     *
     * @param d
     * @param format
     * @return
     */
    public static String formatCommaNumber(Double d, String format) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat decimalFormat = new DecimalFormat(format);
            return decimalFormat.format(d);
        }
    }

    /**
     *
     * @param d
     * @return
     */
    public static String formatPercent(Double d) {
        if (d == null) {
            return "";
        } else {
            return percentDf.format(d / 100);
        }
    }

    public static void main(String[] args) {

    }
}