package start;

import db.RedisUtil;
import message.ConsumerCarriedOutTask;
import message.ConsumerProducerTask;
import message.TaskRecycleBinList;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import timing.ringlist.TimeRound;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/14
 * start.TimerStart
 */
public class TimerStart{
    private static Logger logger = Logger.getLogger(TimerStart.class);
    public static void main(String[] args){
        RedisUtil redisUtil = RedisUtil.getRedisUtil();
        JedisPool jedisPool = redisUtil.getJedisPool();
        int count = 1;
        if(args.length > 0){
            try{
                count = Integer.parseInt(args[0]);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        ConsumerCarriedOutTask[] consumerCarriedOutTasks = new ConsumerCarriedOutTask[count];
        for(int i = 0; i < count; i++){
            consumerCarriedOutTasks[i] = new ConsumerCarriedOutTask(jedisPool, i);
            consumerCarriedOutTasks[i].start();
        }

        ConsumerProducerTask consumerProducerTask = new ConsumerProducerTask(jedisPool);
        consumerProducerTask.start();

        TaskRecycleBinList taskRecycleBinList = new TaskRecycleBinList(jedisPool);
        taskRecycleBinList.start();

        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        TimeRound timeRound = new TimeRound(jedisPool);
        timeRound.traverse();
    }
}
