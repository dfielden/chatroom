# dogubby
Aim of project: create a simple chat room MVP within a week.
Technology: Java, Spring Boot, HTML, CSS, JavaScript
Before entering the chat room, users hare redirected to an entry screen where they must create a username. Doing this starts a session and users can now enter the chat.
If the user leaves and re-enters the chat, their login details are remembered. 
Messages do not persist beyond the running of the application and are temporarily stored within a List.
When a user first enters the chat, all messages that had been sent before they entered are displayed in the chat window, in the order that they were originally sent, along with the time sent and the user who sent the message.
Two-way communication between client and server achieved through use of a Web Socket. When a user sends a message, it automatically appears in the chat window for all users who are part of the chat.

