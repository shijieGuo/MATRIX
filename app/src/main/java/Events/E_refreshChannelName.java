package Events;

/**
 * Created by williamXia on 2018/4/30.
 */

public class E_refreshChannelName {

    private final int MAXCH_PerPage = 6;
    public int chindex;

    public E_refreshChannelName(int chindex) {
        this.chindex = chindex;
    }

    private int pageindex;

    public int getPageindex() {
        pageindex = chindex / MAXCH_PerPage;
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }
}
