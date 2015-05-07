package bsu.fpmi.chat.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessagesStorage {
    private static final List<MessageInfo> INSTANSE = Collections.synchronizedList(new ArrayList<MessageInfo>());

    private MessagesStorage() {
    }

    public static List<MessageInfo> getStorage() {
        return INSTANSE;
    }

    public static void addMessage(MessageInfo messageInfo) {
        INSTANSE.add(messageInfo);
    }

    public static void addAll(MessageInfo[] messages) {
        INSTANSE.addAll(Arrays.asList(messages));
    }

    public static void addAll(List<MessageInfo> messages) {
        INSTANSE.addAll(messages);
    }

    public static int getSize() {
        return INSTANSE.size();
    }

    public static List<MessageInfo> getSubMessagesByIndex(int index) {
        return INSTANSE.subList(index, INSTANSE.size());
    }

    public static MessageInfo getMessageById(String id) {
        for (MessageInfo message : INSTANSE) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    public static String getFormattedView() {
        StringBuffer sb = new StringBuffer();
        for (MessageInfo message : INSTANSE) {
            sb.append(message.getFormat() + '\n');
        }
        return sb.toString().trim();
    }

}
