package bsu.fpmi.chat.util;

import bsu.fpmi.chat.model.MessageInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class MessageUtil {
    public static final String TOKEN = "token";
    public static final String MESSAGES = "messages";
    private static final String TN = "TN";
    private static final String EN = "EN";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String MESTEXT = "messageText";

    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index * 8 + 11;
        return TN + number + EN;
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }

    private static String generateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return dateFormat.format(new Date());
    }

    public static MessageInfo jsonToMessage(JSONObject json) {
        Object id = json.get(ID);
        Object user = json.get(USER);
        Object message = json.get(MESTEXT);

        if (id != null && user != null && message != null) {
            return new MessageInfo((String) id, (String) user, (String) message, Boolean.FALSE, Boolean.FALSE, generateDate());
        }
        return null;
    }

}