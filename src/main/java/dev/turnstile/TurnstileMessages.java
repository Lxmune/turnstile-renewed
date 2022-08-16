package dev.turnstile;

public class TurnstileMessages {
    public static String getMessage(String value) {
        String message = TurnstileRenewed.messagesConfig.getString("messages." + value);
        return message;
    }
}
