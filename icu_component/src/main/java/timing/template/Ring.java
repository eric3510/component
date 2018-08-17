package timing.template;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/6
 * Ring
 */
@Data
public class Ring implements Serializable{
    /***
     * 当前节点地址
     */
    private String node;

    /***
     * 下一个节点地址
     */
    private String nextNode;

    /***
     * 节点任务集合
     */
    private List<BusinessTask> taskList;
}
