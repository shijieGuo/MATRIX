package Events;

/**
 * Created by zhihuafeng on 3/26/2018.
 */

public class showChannelPgaeEvent {
    int page;
    int type;//右往左0，左往右1
    public showChannelPgaeEvent(int page,int type){
        this.page = page;
        this.type = type;
    }

    public int getPage() {
        return page;
    }


    public int getType(){
        return type;
    }
}
