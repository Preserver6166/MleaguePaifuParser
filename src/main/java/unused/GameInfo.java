package unused;

public class GameInfo {

    private int id;
    private int seasonId;
    private String seasonName;
    private String time;
    private ProGameInfo[] pros;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ProGameInfo[] getPros() {
        return pros;
    }

    public void setPros(ProGameInfo[] pros) {
        this.pros = pros;
    }
}

