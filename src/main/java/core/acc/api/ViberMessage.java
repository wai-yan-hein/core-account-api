package core.acc.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ViberMessage {
    public static void main(String[] args) {
        String apiKey = "510ca6289c67dc45-67dc00c9252b4aa9-6f5e8091b7c5e6ed";
        String phoneNumber = "+959420667006";
        String message = "Hello, Viber!";

        try {
            // Construct the API endpoint URL
            URL url = new URL("https://chatapi.viber.com/pa/send_message");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Viber-Auth-Token", apiKey);
            connection.setDoOutput(true);

            // Construct the JSON payload
            String payload = String.format(
                    "{\"receiver\":\"%s\",\"type\":\"text\",\"text\":\"%s\"}",
                    phoneNumber,
                    message
            );

            // Send the request
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(payload.getBytes());
            outputStream.flush();
            outputStream.close();

            // Read the response
            int responseCode = connection.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Handle the response
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
