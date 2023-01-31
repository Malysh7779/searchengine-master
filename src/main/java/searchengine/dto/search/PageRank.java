package searchengine.dto.search;

public class PageRank {

    private int idPage;
    private float absRank;
    private float relRank;
    private float maxRank;

    public int getIdPage() {
        return idPage;
    }

    public void setIdPage(int idPage) {
        this.idPage = idPage;
    }
    public float getAbsRank() {
        return absRank;
    }

    public void setAbsRank(float absRank) {
        this.absRank = absRank;
    }

    public float getRelRank() {
        return relRank;
    }

    public void setRelRank(float relRank) {
        this.relRank = relRank;
    }
    public float getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(float maxRank) {
        this.maxRank = maxRank;
    }

    public PageRank(int idPage) {
        this.idPage = idPage;
    }
}
