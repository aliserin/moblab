public class Opinion {

    private int id;
    private String date;
    private String hrefUrl;
    private String shownUrl;
    private int overallRating;
    private String npsRating;
    private String comment;
    private String npsComment;
    private String detailsUrl;
    private String npsRatingType;
    private String tags;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTags() {
        return tags;
    }

    public String getShownUrl() {
        return shownUrl;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public String getComment() {

        return comment;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public String getNpsRatingType() {
        return npsRatingType;
    }

    public String getNpsRating() {
        return npsRating;
    }

    public String getNpsComment() {
        return npsComment;
    }

    public String getHrefUrl() {
        return hrefUrl;
    }
}
