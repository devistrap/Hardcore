package nl.devistrap.hardcore;

import net.luckperms.api.model.user.User;
import nl.devistrap.hardcore.objects.playerFromDb;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.List;


public class DatabaseManager {

    private Connection connection;
    private Hardcore plugin;

    public DatabaseManager(Hardcore plugin) {
        this.plugin = plugin;
    }


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
        String createGracePeriodsTableSQL = "CREATE TABLE IF NOT EXISTS grace_periods (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL UNIQUE, " +
                "grace_time LONG NOT NULL" +
                ");";



        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            statement.execute(createGracePeriodsTableSQL);
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
            utils.addPermission(utils.lpapi.getUserManager().getUser(player.getUniqueId()), "hardcore.deathbanned");
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
                    utils.removePermission(utils.lpapi.getUserManager().getUser(player.getName()), "hardcore.deathbanned");
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
        utils.removePermission((User) Bukkit.getOfflinePlayer(playerName), "hardcore.deathbanned");
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

    public boolean addGracePeriod(String playerName, Timestamp graceTime) {
        String insertSQL = "INSERT INTO grace_periods (player_name, grace_time) VALUES (?, ?) " +
                "ON CONFLICT(player_name) DO UPDATE SET grace_time = excluded.grace_time;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, playerName);
            pstmt.setTimestamp(2, graceTime);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long getGracePeriod(String playerName) {
        String query = "SELECT grace_time FROM grace_periods WHERE player_name = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                System.out.println(rs.getLong("grace_time"));
                return rs.getLong("grace_time");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean removeGracePeriod(String playerName) {
        String deleteSQL = "UPDATE grace_periods SET grace_time = 0 WHERE player_name = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setString(1, playerName);
            int affectedRows = pstmt.executeUpdate();
            utils.removePermission(utils.lpapi.getUserManager().getUser(playerName), "hardcore.deathbanned");
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}