package me.rioeyu.enhancedGuard.utils;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class UpdateChecker {
    private static final String PROJECT_ID = "EWPavdEt";
    private static final String MODRINTH_API = "https://api.modrinth.com/v2/project/" + PROJECT_ID + "/version";
    private final String currentVersion;
    private final Logger logger;

    public UpdateChecker(String currentVersion, Logger logger) {
        this.currentVersion = currentVersion;
        this.logger = logger;
    }

    public void checkForUpdate() {
        CompletableFuture.supplyAsync(this::getLatestVersion).thenAccept(latestVersion -> {
            if (latestVersion == null) {
                logger.warning("Không thể kiểm tra phiên bản mới. Vui lòng kiểm tra kết nối mạng hoặc thử lại sau.");
                return;
            }

            int comparison = compareVersions(currentVersion, latestVersion);
            if (comparison < 0) {
                logger.info(String.format("\u001B[33mĐã có phiên bản mới: %s (Bạn đang dùng: %s).", latestVersion, currentVersion));
                logger.info("\u001B[33mTải về tại: https://modrinth.com/project/enhancedguard/versions\u001B[0m");
            } else if (comparison > 0) {
                logger.info("\u001B[35mBạn đang sử dụng phiên bản thử nghiệm của EnhancedGuard!\u001B[0m");
            } else {
                logger.info("\u001B[32mBạn đang sử dụng phiên bản mới nhất của EnhancedGuard!\u001B[0m");
            }
        });
    }

    private String getLatestVersion() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(MODRINTH_API).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                logger.warning("API trả về mã lỗi: " + connection.getResponseCode());
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Extract version number from JSON response
                // Example: "version_number":"1.0.0"
                String json = response.toString();
                int versionIndex = json.indexOf("\"version_number\":\"");
                if (versionIndex != -1) {
                    int startIndex = versionIndex + 17;
                    int endIndex = json.indexOf("\"", startIndex);
                    return json.substring(startIndex, endIndex);
                }
            }
        } catch (IOException e) {
            logger.warning("Lỗi khi kiểm tra cập nhật: " + e.getMessage());
        }
        return null;
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }
}