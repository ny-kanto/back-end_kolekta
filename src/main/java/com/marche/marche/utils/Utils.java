package com.marche.marche.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;

public class Utils {
    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String getExtension(String str) {
        String extension = "";
        int lastDotIndex = str.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = str.substring(lastDotIndex + 1);
        }
        return extension;
    }

    public static Method getGetterMethod(Class<?> clazz, String fieldName) throws NoSuchMethodException {
        String getterMethodName = "get" + Utils.capitalizeFirstLetter(fieldName);
        return clazz.getMethod(getterMethodName);
    }

    public static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    public static String getNombreFormat(double nombre) {
        if (nombre == (int) nombre) {
            return String.format(Locale.FRANCE, "%,d", (int) nombre);
        } else {
            return String.format(Locale.FRANCE, "%,.2f", nombre);
        }
    }

    public static boolean isDecimal(String text) {
        String regex = "\\d*\\.?\\d+";

        return text.matches(regex);
    }

    public static String guessMimeType(byte[] imageBytes) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(imageBytes);
        String mimeType = URLConnection.guessContentTypeFromStream(is);
        return mimeType != null ? mimeType : "application/octet-stream"; // Default to a generic binary type if unknown
    }
}
