export class ChatMessage {
    userName;
    timeSentMillis;
    message;

    constructor(userName, timeSentMillis, message) {
        this.userName = userName;
        this.timeSentMillis = timeSentMillis;
        this.message = message;
    }


}