package timing.ringlist;

import config.TimerConfig;
import lombok.Data;
import message.ConsumerCarriedOutTask;
import message.MessageUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import timing.template.BusinessTask;
import timing.template.DateToX;
import timing.template.Ring;
import utils.BaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/8
 * TimeRound
 */
@Data
public class TimeRound{
    private static Logger logger = Logger.getLogger(TimeRound.class);
    private Jedis jedis;

    /***
     * 遍历环形结构跳动频率
     */
    private DateToX ringBeatFrequency;

    /***
     * 指针锁的有效时间
     */
    private DateToX lockKeyTimeout;

    /***
     * 在容器中存储的初始节点地址
     */
    private String initPointer;

    /***
     * 调度线程池
     */
    private ScheduledExecutorService scheduledExecutorService;

    /***
     * 线程池
     */
    private ExecutorService executorService;

    private static final TimerConfig timerConfig = TimerConfig.getTimerConfig();

    public TimeRound(JedisPool jedisPool){
        this.lockKeyTimeout = new DateToX(timerConfig.timeUnit, timerConfig.getLockOutTime());
        this.scheduledExecutorService = Executors.newScheduledThreadPool(timerConfig.getRingCorePoolSize());
        this.ringBeatFrequency = new DateToX(TimerConfig.timeUnit, timerConfig.getPeriod());
        this.initPointer = timerConfig.getKeyPrefix() + 0;
        this.executorService = Executors.newFixedThreadPool(timerConfig.getRingCorePoolSize());
        this.jedis = jedisPool.getResource();
    }

    /***
     * 遍历环形结构
     */
    public void traverse(){
        scheduledExecutorService.scheduleAtFixedRate(
                new Runnable(){
                    @Override
                    public void run(){
                        try{
                            //获取指针当前所指向的地址
                            String pointerAddress = getPointerToSlot(jedis);
                            if(!BaseUtils.isBlank(pointerAddress)){
                                logger.info("当前指针位置:" + pointerAddress);
                            }
                            //如果没抢到锁就直接返回,直到抢到后在执行其他操作
                            //当上一个操作队列释放锁后,下一个(也就是当前指向的指针地址)才能操作,发布任务等.
                            if(BaseUtils.isBlank(pointerAddress)){//判断是否抢到分布式锁
                                return;
                            }
                            Ring currentRing = getCurrentRing(pointerAddress, jedis);
                            List<BusinessTask> produceList = new ArrayList<>();
                            analysisRing(currentRing, produceList);
                            // 将需要发布的任务发布到执行队列
                            if(produceList.size() != 0){
                                //执行队列需要注意冪等
                                TimeRound.producerTask(jedis, produceList.toArray(new BusinessTask[produceList.size()]));
                            }
                            Transaction transaction = jedis.multi();
                            // 将需要修改的任务队列修改
                            transaction.set(currentRing.getNode(), MessageUtil.object2String(currentRing));
                            // 将指针指向下一个节点的地址
                            transaction.set(timerConfig.getPointer(), currentRing.getNextNode());
                            transaction.exec();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }, timerConfig.getInitialDelay(), ringBeatFrequency.getMilliSecond(), TimeUnit.MILLISECONDS
        );
    }

    public static Long producerTask(Jedis jedisPool, BusinessTask... businessTasks){
        List<String> strList = new ArrayList<>();
        for(BusinessTask businessTask : businessTasks){
            strList.add(MessageUtil.object2String(businessTask));
        }
        return jedisPool.lpush(timerConfig.getChannel(), strList.toArray(new String[businessTasks.length]));
    }

    /***
     * 从容器中获取当前指针(添加了锁的校验)(需要考虑线程安全问题版,用作操作遍历槽格时)
     * @return
     */
    private String getPointerToSlot(Jedis jedis){
        String nx = "NX";
        String px = "PX";
        //获取锁，判断此时是否存在其他任务轮正在操作指针以及指针指向的槽格
        String uuid = UUID.randomUUID().toString();
        //当获取到锁后设定过期时间为时间轮跳动频率的时间,就是说只有此线程释放后才能获取节点
        String bool = jedis.set(timerConfig.getLockKey(), uuid, nx, px, lockKeyTimeout.getMilliSecond());
        if("OK".equals(bool)){//如果等于ok则说明获取锁成功
            //获取指针当前指向的地址
            String pointerAddress = jedis.get(timerConfig.getPointer());
            if(BaseUtils.isBlank(pointerAddress)){//如果为空的话则认为时间轮是第一次启动, 则把队列中第一个节点(按规则)的地址返回
                return this.initPointer;
            }
            return pointerAddress;
        }
        //如果返回空,则说明锁正在被其他线程使用
        return null;
    }

    /***
     * 根据地址获取槽格
     * @param pointerAddress 指针指向的地址
     * @return
     */
    public static Ring getCurrentRing(String pointerAddress, Jedis jedis){
        String ringString = jedis.get(pointerAddress);
        if(BaseUtils.isBlank(ringString)){//如果为空的话就初始化一个
            Ring ring = new Ring();
            ring.setNode(pointerAddress);
            int temp;
            String nextNode = timerConfig.getKeyPrefix() + ((temp = Integer.parseInt(pointerAddress.substring(timerConfig.getKeyPrefix().length(), pointerAddress.length()))) == timerConfig.getGrooveNumber() - 1 ? 0 : temp + 1);
            ring.setNextNode(nextNode);
            ring.setTaskList(new ArrayList());
            return ring;
        }else{
            return MessageUtil.getObjectFromString(ringString, Ring.class);
        }
    }

    /***
     * 解析槽格
     * @param ringNode 将要解析的槽格
     * @param produceList 需要发布的队列
     */
    private void analysisRing(final Ring ringNode, final List<BusinessTask> produceList){
        List<? extends BusinessTask> baseTaskList = ringNode.getTaskList();
        List<BusinessTask> insertTaskList = new ArrayList<>();
        for(BusinessTask baseTask : baseTaskList){
            //当carriedTurns小于等于0的时候说明此任务到了或者超时了需要执行
            int carriedTurns = baseTask.getCarriedTurns();
            if(carriedTurns <= 0){//将任务发布到执行队列
                produceList.add(baseTask);
            }else{//carriedTurns减一后再放回存储容器
                baseTask.setCarriedTurns(carriedTurns - 1);
                insertTaskList.add(baseTask);
            }
        }
        ringNode.setTaskList(insertTaskList);
    }
}
