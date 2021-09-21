package com.example.dogubby;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;

@SpringBootApplication
@Controller
public class DogubbyApplication {
    private final LinkedList<ChatMessage> messages = new LinkedList<>();
    private static final Gson gson = new Gson();

    public DogubbyApplication() throws Exception {

        //TODO: remove this adding of fake chat messages
        for (int i = 1; i < 21; i++) {
            messages.add(new ChatMessage("Dogubby",
                    System.currentTimeMillis(),
                    "hello little worm!" ));
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DogubbyApplication.class, args);
    }

    @GetMapping("/")
    public String index() throws Exception {
        return "index";
    }

    @ResponseBody
    @GetMapping("/allmessages")
    public String getAllMessages() throws Exception {
        return gson.toJson(messages);

    }

    // Handles messages from /app/chat. (Note the Spring adds the /app prefix for us).
    @MessageMapping("/chat")
    @SendTo("/listen/messages")
    public ChatMessage receiveMessage(String messageBody) throws Exception {
        System.out.println(messageBody);
        MessageBody message = gson.fromJson(messageBody, MessageBody.class);
        messages.add(new ChatMessage("Worm", System.currentTimeMillis(), message.getMessage()));
        return new ChatMessage("Worm", System.currentTimeMillis(), message.getMessage());
    }



}
