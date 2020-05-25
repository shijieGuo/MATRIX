package Events;

/**
 * Created by zhihuafeng on 4/10/2018.
 */

public class MatrixRecall {
    int cmd;
    int channel;
    public MatrixRecall(int cmd,int channel){
        this.cmd=cmd;
        this.channel = channel;
    }

    public MatrixRecall(int channel) {
        this.channel = channel;
    }

    public int getCmd() {
        return cmd;
    }

    public int getChannel() {
        return channel;
    }
}
