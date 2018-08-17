package config;

import org.apache.log4j.Logger;
import timing.ringlist.TimeRound;
import utils.BaseUtils;
import utils.PropertiesUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/5
 * TimerConfig 定时任务轮配置
 */
public class TimerConfig{
    private static Logger logger = Logger.getLogger(TimerConfig.class);
    private static final TimerConfig timerConfig = new TimerConfig();

    /***
     * 线程数量
     */
    private int corePoolSize;

    /***
     * 请求编码格式
     */
    private String encode;

    /***
     * 校验token(哈希值)
     */
    private String token;

    /***
     * 测试环境ip地址
     */
    private String testIp;

    /***
     * 执行器生产消费者地址(执行队列)
     */
    private String channel;

    /***
     * 执行器生产消费者地址(备份队列,如果执行队列执行失败,则从备份队列中重新获取,并添加到执行队列)
     */
    private String channelTmp;

    /***
     * 槽的数量
     */
    private int grooveNumber = 10;

    /***
     * 在容器中存储的key值前缀
     */
    private String keyPrefix;

    /***
     * 线程最大数量
     */
    private int ringCorePoolSize;

    /***
     * 循环时距离上次执行开始时的间隔
     */
    private int initialDelay;

    /***
     * 指针锁的key值
     */
    private String lockKey;

    /***
     * 指针当前所指向的地址,从redis中获取(移动指针时必须为原子操作),还需要在项目启动时定位上次指针的位置
     */
    private String pointer;

    /***
     * 遍历环形结构的时间间隔
     */
    private int period;

    /***
     * 锁的时间
     */
    private int lockOutTime;

    /***
     * 发布任务消费消费者地址
     */
    private String hashedWheelProducerTask;

    /***
     * 发布任务消费消费者备用队列地址
     */
    private String hashedWheelProducerTaskTmp;

    /***
     * 前缀
     */
    private String hashedWheelLogPrefix;

    /****
     * 判断任务是否执行成功的字段的名称
     */
    private String resultCodeName;

    /***
     * 时间单位:秒
     */
    public static final TimeUnit timeUnit = TimeUnit.SECONDS;

    /***
     * post请求
     */
    public static final String POST = "POST";

    /***
     * get请求
     */
    public static final String GET = "GET";

    public static TimerConfig getTimerConfig(){
        return TimerConfig.timerConfig;
    }

    private TimerConfig(){
        PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfig.icuProperties);
        this.corePoolSize = Integer.parseInt(propertiesUtil.getPropery("timer.core.pool.size"));
        this.encode = propertiesUtil.getPropery("timer.encode");
        this.token = propertiesUtil.getPropery("timer.token");
        this.testIp = propertiesUtil.getPropery("timer.test.ip");
        this.channel = propertiesUtil.getPropery("timer.channel");
        this.channelTmp = propertiesUtil.getPropery("timer.channel.tmp");
        this.grooveNumber = Integer.parseInt(propertiesUtil.getPropery("timer.groove.number"));
        this.keyPrefix = propertiesUtil.getPropery("timer.key.prefix");
        this.ringCorePoolSize = Integer.parseInt(propertiesUtil.getPropery("timer.ring.core.pool.size"));
        this.initialDelay = Integer.parseInt(propertiesUtil.getPropery("timer.initial.delay"));
        this.lockKey = propertiesUtil.getPropery("timer.lock.key");
        this.pointer = propertiesUtil.getPropery("timer.pointer");
        this.lockOutTime = Integer.parseInt(propertiesUtil.getPropery("timer.lock.out.time"));
        this.period = Integer.parseInt(propertiesUtil.getPropery("timer.period"));
        this.hashedWheelProducerTask = propertiesUtil.getPropery("timer.hashed.wheel.producer.task");
        this.hashedWheelProducerTaskTmp = propertiesUtil.getPropery("timer.hashed.wheel.producer.task.tmp");
        this.hashedWheelLogPrefix = propertiesUtil.getPropery("timer.hashed.wheel.log.prefix");
        this.resultCodeName = propertiesUtil.getPropery("timer.result.code.name");
        this.isIp();
    }

    private void isIp(){
        String testIp = this.testIp;
        //判断是否为测试环境或者本地
        if(BaseUtils.isBlank(testIp)){
            return;
        }
        //判断是否在本地运行
        String localIp = this.getLocalIp();
        logger.info("localIp = " + localIp);
        if(localIp != null){//判定为本地的话则生成隔离环境地址
            this.channel = localIp + this.channel;
            this.channelTmp = localIp + this.channelTmp;
            this.keyPrefix = localIp + this.keyPrefix;
            this.lockKey = localIp + this.lockKey;
            this.pointer = localIp + this.pointer;
            this.hashedWheelProducerTask = localIp + this.hashedWheelProducerTask;
        }
    }

    private String getLocalIp(){
        Enumeration<NetworkInterface> n = null;
        try{
            n = NetworkInterface.getNetworkInterfaces();
        }catch(SocketException e){
            e.printStackTrace();
        }
        for(; n.hasMoreElements(); ){
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            if(!e.getName().equals("en1")){
                continue;
            }
            for(; a.hasMoreElements(); ){
                String ip = a.nextElement().getHostAddress();
                String[] ips = ip.split("\\.");
                if(ips.length == 4){
                    return ip;
                }
            }
        }
        return null;
    }

    public static void main(String[] args){
        String ip = TimerConfig.getTimerConfig().getLocalIp();
        System.out.println(ip);
    }

    public String getHashedWheelLogPrefix(){
        return this.hashedWheelLogPrefix;
    }

    public int getCorePoolSize(){
        return this.corePoolSize;
    }

    public String getEncode(){
        return this.encode;
    }

    public String getToken(){
        return this.token;
    }

    public String getTestIp(){
        return this.testIp;
    }

    public String getChannel(){
        return this.channel;
    }

    public String getChannelTmp(){
        return this.channelTmp;
    }

    public int getGrooveNumber(){
        return this.grooveNumber;
    }

    public String getKeyPrefix(){
        return this.keyPrefix;
    }

    public int getRingCorePoolSize(){
        return this.ringCorePoolSize;
    }

    public int getInitialDelay(){
        return this.initialDelay;
    }

    public String getLockKey(){
        return this.lockKey;
    }

    public String getPointer(){
        return this.pointer;
    }

    public int getPeriod(){
        return this.period;
    }

    public int getLockOutTime(){
        return this.lockOutTime;
    }

    public String getHashedWheelProducerTask(){
        return this.hashedWheelProducerTask;
    }

    public String getResultCodeName(){
        return this.resultCodeName;
    }

    public String getHashedWheelProducerTaskTmp(){
        return this.hashedWheelProducerTaskTmp;
    }
}
