package searchengine.dto.search;

public class SearchRank {
    private int idPage;
    private int idLemma;
    private float absRank;

    public SearchRank(int idPage, int idLemma, float absRank) {
        this.idPage = idPage;
        this.idLemma = idLemma;
        this.absRank = absRank;
    }

    public int getIdLemma() {
        return idLemma;
    }

    public void setIdLemma(int idLemma) {
        this.idLemma = idLemma;
    }
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

}
