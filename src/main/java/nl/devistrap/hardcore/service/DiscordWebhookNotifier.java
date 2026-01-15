package nl.devistrap.hardcore.service;

import nl.devistrap.hardcore.Hardcore;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.lang.System.getLogger;


public class DiscordWebhookNotifier {

    private static Hardcore plugin = null;
    private static String serverName ="Hardcore Minecraft Server";
    private static String webhookUrl = "https://discord.com/api/webhooks/your_webhook_url";
    private static String pingRoleId="123456789012345678";

    public DiscordWebhookNotifier(Hardcore plugin) {
        this.plugin = plugin;
        this.serverName = plugin.getConfig().getString("discord-webhook.server-name");
        this.webhookUrl = plugin.getConfig().getString("discord-webhook.url");
        this.pingRoleId = plugin.getConfig().getString("discord-webhook.ping-role-id");
    }

    public static void sendWebhookNotification(String message, String playerName, boolean ping) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String roleId = "1448046210358181999";
            String avatarUrl = "https://mc-heads.net/avatar/" + playerName;

            if(ping == false) {
                String JsonPayload = String.format(
                        "{"
                                + "\"embeds\": [{"
                                + "\"description\": \"%s\","
                                + "\"footer\": {"
                                + "\"text\": \"%s\""
                                + "},"
                                + "\"thumbnail\": {"
                                + "\"url\": \"%s\""
                                + "}"
                                + "}]"
                                + "}",
                        message, serverName, avatarUrl
                );
                plugin.getLogger().info("JSON Payload: " + JsonPayload);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(JsonPayload.getBytes());
                    outputStream.flush();
                }
            }
            else{
                String jsonPayload = String.format(
                        "{"
                                + "\"content\": \"<@&%s>\","
                                + "\"embeds\": [{"
                                + "\"description\": \"%s\","
                                + "\"footer\": {"
                                + "\"text\": \"%s\""
                                + "},"
                                + "\"thumbnail\": {"
                                + "\"url\": \"%s\""
                                + "}"
                                + "}]"
                                + "}",
                        roleId, message, serverName, avatarUrl
                );

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(jsonPayload.getBytes());
                    outputStream.flush();
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
               plugin.getLogger().warning("Failed to send Discord webhook notification. Response code: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}