package com.example.dogubby;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@Controller
public class DogubbyApplication {
    private final LinkedList<ChatMessage> messages = new LinkedList<>();
    private static final Gson gson = new Gson();
    private static final String DOGUBBY_COOKIE_NAME = "DOGUBBYCOOKIE";
    public static final String LOGIN_SUCCESS_RESPONSE_VALUE = "LOGIN_SUCCESS";
    public static final String LOGOUT_SUCCESS_RESPONSE_VALUE = "LOGOUT_SUCCESS";
    private final Map<String, DogubbyState> sessions = new HashMap<>(); // cookieValue, DogubbyAppState
    private static final Random rand = new Random();


    public DogubbyApplication() throws Exception {

    }

    public static void main(String[] args) {
        SpringApplication.run(DogubbyApplication.class, args);
    }

    @GetMapping("/")
    public synchronized String index(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        DogubbyState state = getOrCreateSession(req, resp);
        if (!state.isLoggedIn()) {
            return "redirect:/login";
        }
        return "index";
    }

    @ResponseBody
    @GetMapping("/allmessages")
    public synchronized String getAllMessages() throws Exception {
        return gson.toJson(messages);
    }

    // Handles messages from /app/chat. (Note the Spring adds the /app prefix for us).
    @MessageMapping("/chat")
    @SendTo("/listen/messages")
    public synchronized ChatMessage receiveMessage(String messageBody) throws Exception {
        MessageBody message = gson.fromJson(messageBody, MessageBody.class);

        String user = sessions.get(message.getCookie()).userName;
        ChatMessage chatMessage = new ChatMessage(user, System.currentTimeMillis(), message.getMessage());
        messages.add(chatMessage);
        return chatMessage;
    }

    @GetMapping("/login")
    public synchronized String getLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        DogubbyState state = getOrCreateSession(req, resp);
        if (state.isLoggedIn()) {
            return "redirect:/";
        }
        return "login";
    }

    @ResponseBody
    @PostMapping("/login")
    public synchronized String login(@RequestBody String username, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            DogubbyState state = getOrCreateSession(req, resp);
            state.setUserName(username.substring(1, username.length() - 1));
            state.setUserId(sessions.size());
            return gson.toJson(LOGIN_SUCCESS_RESPONSE_VALUE);
        } catch (IllegalStateException e) {
            return gson.toJson(e.getMessage());
        }
    }

    @Nonnull
    private DogubbyState getOrCreateSession(HttpServletRequest req, HttpServletResponse resp) {
        if (!Thread.holdsLock(this)) {
            throw new IllegalStateException("Should hold DogubbyApplication lock to call getOrCreateSession");
        }

        // First, get the Cookie from the request.
        Cookie cookie = findOrSetSessionCookie(req, resp);

        // Use the cookie value as the session ID.
        String sessionId = cookie.getValue();

        // Then, look up the corresponding session for this Cookie ID.
        DogubbyState state = sessions.get(sessionId);

        if (state == null) {
            // Create a new session (findOrSetSessionCookie probably just created the Cookie, so there is not yet a
            // corresponding session).
            state = new DogubbyState();
            sessions.put(sessionId, state);
        }

        return state;
    }

    @Nonnull
    private static Cookie findOrSetSessionCookie(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (DOGUBBY_COOKIE_NAME.equals(c.getName())) {
                    // Found our cookie.
                    return c;
                }
            }
        }

        // No cookie. Set a new one.
        Cookie cookie = new Cookie(DOGUBBY_COOKIE_NAME, String.format("%x%xdog", rand.nextLong(), rand.nextLong()));
        resp.addCookie(cookie);
        return cookie;
    }

    @RequestMapping(value="/logout")
    public synchronized @ResponseBody String logout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Cookie c = findOrSetSessionCookie(req, resp);
        c.setMaxAge(0);
        resp.addCookie(c);
        HttpSession session = req.getSession();
        session.invalidate();
        req.logout();
        return gson.toJson(LOGOUT_SUCCESS_RESPONSE_VALUE);
    }

    private static final class DogubbyState {
        private long userId = -1;
        @Nullable  // If logged out, this is null.
        private String userName;

        boolean isLoggedIn() {
            return this.userId > 0;
        }

        @Nullable
        public String getUserName() {
            return this.userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public long getUserId() {
            return this.userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "id: " + this.getUserId() + ", user name: " + this.getUserName();
        }
    }

}
