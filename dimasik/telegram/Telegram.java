package dimasik.telegram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Telegram {
    private final String token;
    private final List<String> chatId;

    public Telegram(String token, String ... chatId) {
        this.token = token;
        this.chatId = Arrays.asList(chatId);
    }

    public void send(String message, String text, String data) {
        for (String id : this.chatId) {
            try {
                String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
                URL url = new URL("https://api.telegram.org/bot" + this.token + "/sendMessage");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String keyboard = "{\n                            \"inline_keyboard\": [\n                                [\n                                    {\n                                        \"text\":\"" + text + "\",\n                                        \"callback_data\":\"" + data + "\"\n                                    }\n                                ]\n                            ]\n                        }";
                String params = "chat_id=" + id + "&text=" + encodedMessage + "&reply_markup=" + keyboard;
                try (OutputStream os = connection.getOutputStream();){
                    os.write(params.getBytes());
                    os.flush();
                }
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    throw new IOException("Failed to send message. Response: " + String.valueOf(response));
                }
                connection.disconnect();
            }
            catch (IOException iOException) {}
        }
    }

    public void send(String message) {
        for (String id : this.chatId) {
            try {
                String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
                URL url = new URL("https://api.telegram.org/bot" + this.token + "/sendMessage");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String params = "chat_id=" + id + "&text=" + encodedMessage;
                try (OutputStream os = connection.getOutputStream();){
                    os.write(params.getBytes());
                    os.flush();
                }
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    throw new IOException("Failed to send message. Response: " + String.valueOf(response));
                }
                connection.disconnect();
            }
            catch (IOException iOException) {}
        }
    }
}
