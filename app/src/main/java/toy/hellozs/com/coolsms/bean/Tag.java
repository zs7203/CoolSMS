package toy.hellozs.com.coolsms.bean;

/**
 * Created by Administrator on 2016/2/20.
 */
public class Tag {

    private String title;
    private String href;

    public Tag(String title, String href) {
        this.title = title;
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
