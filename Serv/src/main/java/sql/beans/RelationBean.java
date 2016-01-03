package sql.beans;
import java.io.Serializable;

public final class RelationBean implements Serializable {

    private String message;
    private String link;
    private int imageIndex;

    public RelationBean() {}

    public RelationBean(String message, String link, int imageIndex) {
        init(message, link, imageIndex);
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public void init(String message, String link, int imageIndex) {
        this.message = message;
        this.link = link;
        this.imageIndex = imageIndex;
    }

}