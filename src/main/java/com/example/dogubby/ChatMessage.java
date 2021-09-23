package com.example.dogubby;

public class ChatMessage {
    private final String userName;
    private final long timeSentMillis;
    private final String message;

    public ChatMessage(String userName, long timeMillisSent, String message) {
        this.userName = userName;
        this.timeSentMillis = timeMillisSent;
        this.message = message;
    }

    public String getUserName() {
        return this.userName;
    }

    public long getTimeSentMillis() {
        return this.timeSentMillis;
    }

    public String getMessage() {
        return this.message;
    }



    @Override
    public String toString() {
        return "ChatMessage{" +
                "userName='" + userName + '\'' +
                ", timeSentMillis=" + timeSentMillis +
                ", message='" + message + '\'' +
                '}';
    }
}

class MessageBody {
    private final String message;
    private final String cookie;

    public MessageBody(String message, String cookie) {
        this.message = message;
        this.cookie = cookie;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCookie() {
        // Remove the "DOGUBBYCOOKIE=" component
        return this.cookie.substring(14);
    }


}
