package nl.devistrap.hardcore.objects;


import lombok.Getter;
import lombok.Setter;

public class playerFromDb {


    @Getter
    @Setter
    public String playerName;

    @Getter
    @Setter
    public String banTime;


    public playerFromDb(String playerName, String banTime) {
        this.playerName = playerName;
        this.banTime = banTime;
    }



}
