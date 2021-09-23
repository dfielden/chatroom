import {ChatMessage} from "./ChatMessage.js";
import {AJAX} from "./ajax.js";

const messages = document.querySelector('.messages');
const sendMessageBtn = document.querySelector('#send-msg-btn');
const leaveChatBtn = document.querySelector('#leave-chat-btn');
const scrollBottomBtn = document.querySelector('#scroll-bottom-btn');
const textArea = document.querySelector('.text-area');

const LOGOUT_SUCCESS_VALUE = 'LOGOUT_SUCCESS';


window.addEventListener('load', async(e) => {
    //messages.innerHTML = '';
    const prevMessages = await AJAX("/allmessages");
    prevMessages.forEach(chatMessage => displayMessage(chatMessage));
});


// Try to set up WebSocket connection with the handshake at "http://localhost:8080/stomp"
let sock = new SockJS("/stomp");

// Create a new StompClient object with the WebSocket endpoint
let client = Stomp.over(sock);

// Start the STOMP communications, provide a callback for when the CONNECT frame arrives.
client.connect({}, frame => {
    // Subscribe to "/list/messages". Whenever a message arrives add to the messages div.
    client.subscribe("/listen/messages", payload => {
        console.log(JSON.parse(payload.body));
        displayMessage(JSON.parse(payload.body));
    });
});

// Take the value in the ‘text-area’ text field and send it to the server with empty headers.
function sendMessage() {
    const messageContent = document.querySelector('.text-area').value.trim();
    if (messageContent) {
        client.send('/app/chat', {}, JSON.stringify({message: messageContent, cookie: document.cookie}));
        textArea.value = "";
        textArea.setSelectionRange(0,0);
    }
}

textArea.addEventListener('keydown', (e) => {
    if (e.keyCode === 13 && !e.shiftKey) {
        sendMessage();
        textArea.setSelectionRange(0,0);
    }
})

sendMessageBtn.addEventListener('click', async (e) => {
    e.preventDefault();
    sendMessage();
});

scrollBottomBtn.addEventListener('click', async (e) => {
    messages.scrollTop = messages.scrollHeight;
});

const createMessageHtml = (chatMessage) => {
    const date = createDateText(new Date(chatMessage.timeSentMillis));
    return `
        <div class="message-container">
            <div class="message-header">
                <div class="message-user heading-4">${chatMessage.userName}</div>
                <div class="message-time">${date}</div>
            </div>
            <div class="message-body">${chatMessage.message}</div>
        </div>
    `;
};

const displayMessage = (chatMessage) => {
    const html = createMessageHtml(chatMessage);
    messages.insertAdjacentHTML('beforeend', html);
}

leaveChatBtn.addEventListener('click', async function() {
    const data = await AJAX("/logout");
    if (data === LOGOUT_SUCCESS_VALUE) {
        window.location.href = "/login";
    }
})

const createDateText = (date) => {
    const time = `${date.getHours() < 10 ?'0':''}${date.getHours()}:${date.getMinutes() < 10 ?'0':''}${date.getMinutes()}`;
    if (isToday(date)) {
        return `Today ${time}`;
    }
    return `${date.getDay() < 10 ?'0':''}${date.getDay()} ${date.toLocaleString('default', {month: 'short'})} ` + time;
}

const isToday = (date) => {
    const today = new Date();
    return date.getDate() === today.getDate() && date.getMonth() === today.getMonth() && date.getFullYear() === today.getFullYear();
}






