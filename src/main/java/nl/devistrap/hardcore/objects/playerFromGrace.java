package nl.devistrap.hardcore.objects;

public class playerFromGrace {

    private String playerName;
    private String graceTime;

    public playerFromGrace(String playerName, String graceTime) {
        this.playerName = playerName;
        this.graceTime = graceTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getGraceTime() {
        return graceTime;
    }

    public void setGraceTime(String graceTime) {
        this.graceTime = graceTime;
    }
}
