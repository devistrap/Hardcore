package nl.devistrap.hardcore;

import nl.devistrap.hardcore.objects.playerFromDb;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.List;

public class DatabaseManager {

    private Connection connection;


    public void connect() {
        try {
            String dbPath = "plugins/Hardcore/hardcore.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS deathbans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "ban_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<playerFromDb> getAllBannedPlayers() {
        String query = "SELECT player_name, ban_time FROM deathbans;";
        List<playerFromDb> bannedPlayers = new java.util.ArrayList<playerFromDb>();
        try (Connection conn = getConnection();

             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String playerName = rs.getString("player_name");
                String banTime = rs.getString("ban_time");
                new playerFromDb(playerName, banTime);
                bannedPlayers.add(new playerFromDb(playerName, banTime));

            }
            return bannedPlayers;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bannedPlayers;
    }

    public boolean deathBanPlayer(Player player, Timestamp duration) {
        String insertSQL = "INSERT INTO deathbans (player_name, ban_time) VALUES (?, ?);";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, player.getName());
            pstmt.setTimestamp(2,duration);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPlayerBanned(Player player) {
        String query = "SELECT * FROM deathbans WHERE player_name = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp banTime = rs.getTimestamp("ban_time");
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                if(banTime == null){
                    return true;
                }
                if (banTime.after(currentTime)) {
                    return true;
                } else {
                    String deleteSQL = "DELETE FROM deathbans WHERE player_name = ?;";
                    try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSQL)) {
                        deletePstmt.setString(1, player.getName());
                        deletePstmt.executeUpdate();
                    }
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean revivePlayer(String playerName) {
        String deleteSQL = "DELETE FROM deathbans WHERE player_name = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setString(1, playerName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}