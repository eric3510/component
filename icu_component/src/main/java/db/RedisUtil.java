package db;

import config.PropertiesConfig;
import config.TimerConfig;
import message.TaskRecycleBinList;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utils.PropertiesUtil;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/15
 * RedisUtil
 */
public class RedisUtil{
    private static final RedisUtil redisUtil = new RedisUtil();

    public static RedisUtil getRedisUtil(){
        return RedisUtil.redisUtil;
    }
    /***
     * Redis服务器IP
     */
    private String addr;

    /***
     * Redis的端口号
     */
    private int port;

    /***
     * 访问密码
     */
    private String auth;

    /***
     * 可用连接实例的最大数目，默认值为8；
     */
    /***
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
     */
    private int maxActive;

    /***
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
     */
    private int maxIdle;

    /***
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
     */
    private int maxWait;

    private int timeOut;

    /***
     * 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
     */
    private boolean testOnBorrow;

    /***
     * 非切片连接池
     */
    private JedisPool jedisPool;

    private RedisUtil(){
        PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfig.redisProperties);
        this.addr = propertiesUtil.getPropery("redis.addr");
        this.port = Integer.parseInt(propertiesUtil.getPropery("redis.port"));
        this.auth = propertiesUtil.getPropery("redis.auth");
        this.maxIdle = Integer.parseInt(propertiesUtil.getPropery("redis.maxIdle"));
        this.maxActive = Integer.parseInt(propertiesUtil.getPropery("redis.maxActive"));
        this.maxWait = Integer.parseInt(propertiesUtil.getPropery("redis.maxWait"));
        this.timeOut = Integer.parseInt(propertiesUtil.getPropery("redis.timeOut"));
        this.testOnBorrow = Boolean.parseBoolean(propertiesUtil.getPropery("redis.testOnBorrow"));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWait);
        config.setTestOnBorrow(testOnBorrow);
        this.jedisPool = new JedisPool(config, addr, port, maxWait, auth);
    }

    public String getAddr(){
        return this.addr;
    }

    public int getPort(){
        return this.port;
    }

    public String getAuth(){
        return this.auth;
    }

    public int getMaxActive(){
        return this.maxActive;
    }

    public int getMaxIdle(){
        return this.maxIdle;
    }

    public int getMaxWait(){
        return this.maxWait;
    }

    public int getTimeOut(){
        return this.timeOut;
    }

    public boolean isTestOnBorrow(){
        return this.testOnBorrow;
    }

    public JedisPool getJedisPool(){
        return this.jedisPool;
    }

    public static void main(String[] args){
        RedisUtil redisUtil = new RedisUtil();
        Jedis jedis = redisUtil.getJedisPool().getResource();
        System.out.println(TimerConfig.getTimerConfig().getChannel());
        long len = jedis.llen(TimerConfig.getTimerConfig().getChannel());
        System.out.println(len);
    }
}
