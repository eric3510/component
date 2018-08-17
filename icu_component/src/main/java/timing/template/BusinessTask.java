package timing.template;

import lombok.Data;

import java.util.Map;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/6
 * BusinessTask
 */
@Data
public class BusinessTask{
    /***
     * 任务唯一id
     */
    private String id;


    /***
     * 请求地址
     */
    private String url;

    /***
     * 参数
     */
    private Map<String, String> params;

    /***
     * 请求方式
     */
    private String requestMethod;

    /***
     * 任务延迟执行的时长
     */
    private DateToX initialDelay;

    /***
     * 此任务应该放在的槽位
     */
    private int slot;

    /***
     * 指针扫描几圈后执行(可以理解为该任务的执行触发点),当小于等于0的时候执行
     */
    private int carriedTurns;

    /***
     * 是否为循环任务,默认为不是
     */
    private boolean isCycle = false;
}
