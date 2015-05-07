
package bsu.fpmi.chat.model;

import org.json.simple.JSONObject;

public class MessageInfo {
    private String user;
    private String message;
    private String id;
    private boolean deleted;
    private boolean edited;
    private String date;

    public MessageInfo(String id, String user, String message, boolean deleted, boolean edited, String date) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.deleted = deleted;
        this.edited = edited;
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public String getMessageText() {
        return message;
    }

    public String getId() {
        return id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getFormat() {
        return "" + date + " " + user + " : " + message;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("user", user);
        jsonObject.put("messageText", message);
        jsonObject.put("deleted", deleted);
        jsonObject.put("edited", edited);
        jsonObject.put("date", date);
        return jsonObject.toJSONString();
    }
}
