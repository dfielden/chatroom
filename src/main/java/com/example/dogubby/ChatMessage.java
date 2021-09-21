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

    public MessageBody(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }


}
