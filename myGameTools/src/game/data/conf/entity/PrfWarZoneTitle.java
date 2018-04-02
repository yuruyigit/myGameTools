package game.data.conf.entity;

import java.io.Serializable;

public class PrfWarZoneTitle implements Serializable {
    /*** zzb  */
	private static final long serialVersionUID = 1L;

	private int id;
 
    private int rankStart;

    private int rankEnd;

    private String title;

    private String frameSource;
 
    private String medalSource;

    private String reward;
	
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRankStart() {
        return rankStart;
    }

    public void setRankStart(int rankStart) {
        this.rankStart = rankStart;
    }

    public int getRankEnd() {
        return rankEnd;
    }

    public void setRankEnd(int rankEnd) {
        this.rankEnd = rankEnd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getFrameSource() {
        return frameSource;
    }

    public void setFrameSource(String frameSource) {
        this.frameSource = frameSource == null ? null : frameSource.trim();
    }

    public String getMedalSource() {
        return medalSource;
    }

    public void setMedalSource(String medalSource) {
        this.medalSource = medalSource == null ? null : medalSource.trim();
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward == null ? null : reward.trim();
    }
}