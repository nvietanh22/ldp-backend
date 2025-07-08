package vn.lottefinance.landingpage.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class CommonUtil {

    /**
     * Check empty
     *
     * @param h
     * @return
     */
    public static boolean isEmpty(Hashtable<?, ?> h) {
        if (h == null || h.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Object[] o) {
        if (o == null || o.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(byte[] o) {
        if (o == null || o.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Vector<?> v) {
        if (v == null || v.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.trim().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(List<?> l) {
        if (l == null || l.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Set<?> s) {
        if (s == null || s.size() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Map<?, ?> m) {
        if (m == null || m.size() == 0) {
            return true;
        }
        return false;
    }

    public static <T> T getRandomItem(List<T> list)
    {
        Random random = new Random();
        int listSize = list.size();
        int randomIndex = random.nextInt(listSize);
        return list.get(randomIndex);
    }

}
