package message;

import config.TimerConfig;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import timing.template.BusinessTask;
import utils.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/25
 * TaskRecycleBinList,(失败队列) 此队列中的任务会定期执行一遍
 */
public class TaskRecycleBinList extends Thread{
    private static Logger logger = Logger.getLogger(TaskRecycleBinList.class);

    /***
     * redis连接对象
     */
    private Jedis jedis;

    /***
     * 收回频道
     */
    private String taskRecucle = "hashed-wheel-task-recucle";

    /***
     * 执行频道间隔锁
     */
    private String taskRecucleLock = "hashed-wheel-task-recucle-lock";

    /***
     * 失败任务执行循环间隔单位:秒
     * 12小时
     */
    private int cycleInterval = (int)TimeUnit.SECONDS.toMillis(43200);
//    private int cycleInterval = (int)TimeUnit.SECONDS.toMillis(3600);

    /***
     * 失败任务获取最大长度
     */
    private long taskLenth = 500;

    public TaskRecycleBinList(JedisPool jedisPool){
        this.jedis = jedisPool.getResource();
    }

    @Override
    public void run(){
        while(true){
            try{
                String ok = jedis.set(this.taskRecucleLock, this.taskRecucleLock, "NX", "PX", cycleInterval);
                if("OK".equals(ok)){
                    boolean bool = true;
                    while(bool){
                        logger.info("开始执行失败队列");
                        List<String> taskList = this.getTasks();
                        List<String> failureTaskList = new ArrayList<>();
                        for(String task : taskList){
                            boolean taskBool = TaskUtil.execTaskAnsy(MessageUtil.getObjectFromString(task, BusinessTask.class));
                            if(!taskBool){//如果没成功则追加到队列后面
                                failureTaskList.add(task);
                            }
                        }
                        int failureTaskListSize = failureTaskList.size();
                        int taskListSize = taskList.size();
                        this.removeTasks(failureTaskList.toArray(new String[failureTaskListSize]), taskListSize);
                        logger.info(String.format("失败队列执行完毕:共执行%d个任务,其中失败了%d个任务", taskList.size(), failureTaskListSize));
                        bool = false;
                    }
                }else{
                    sleep(1000);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /***
     * 获取当前失败任务队列中的所有任务
     */
    public List<String> getTasks(){
        long length = this.jedis.llen(this.taskRecucle);
        if(length > this.taskLenth){//只取前规定的长度的失败任务
            length = this.taskLenth;
        }
        return this.jedis.lrange(this.taskRecucle, 0, length);
    }

    /***
     * 装载又执行失败的任务
     * @param task 又执行失败的任务
     * @param count 获取对象的范围
     * @return
     */
    public void removeTasks(String[] task, long count){
        Transaction transaction = jedis.multi();
        if(task.length != 0){
            transaction.rpush(this.taskRecucle, task);
        }
        transaction.ltrim(this.taskRecucle, count, -1);
        transaction.exec();
    }

    /***
     * 单个装载失败任务
     */
    public void setFailureTask(){
        jedis.rpoplpush(TimerConfig.getTimerConfig().getChannelTmp(), this.taskRecucle);
    }

    public static void main(String[] args){
        System.out.println(TimeUnit.HOURS.toSeconds(1));
    }
}
