package utils;

import com.alibaba.fastjson.JSONObject;
import config.TimerConfig;
import message.ConsumerProducerTask;
import org.apache.log4j.Logger;
import timing.template.BusinessTask;

import java.util.Map;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/27
 * TaskUtil
 */
public class TaskUtil{
    private static Logger logger = Logger.getLogger(TaskUtil.class);

    /***
     * 执行任务,校验是否执行成功
     * @param businessTask 任务内容[1:成功][0:失败]
     * @return
     */
    private static boolean execTask(final BusinessTask businessTask){
        //如果为空认为是无效任务自动认为执行成功
        if(businessTask == null){
            return true;
        }
        //如果url为空则任务是无效任务,自动认为执行成功
        if(BaseUtils.isBlank(businessTask.getUrl())){
            return true;
        }
        JSONObject result = execTaskToJson(businessTask);
        if(result == null){
            return false;
        }
        String ret = result.getString(TimerConfig.getTimerConfig().getResultCodeName());
        logger.info("ret == " + ret);
        if(BaseUtils.isBlank(ret)){
            return false;
        }
        if(!ret.equals("1")){
            logger.info(JSONObject.toJSONString(result));
            return false;
        }
        return true;
    }

    /***
     * 执行任务
     * @param businessTask 任务
     */
    public static boolean execTaskAnsy(final BusinessTask businessTask){
        return execTask(businessTask);
    }



    public static JSONObject execTaskToJson(BusinessTask businessTask){
        JSONObject jsonObject;
        try{
            jsonObject = (JSONObject) JSONObject.parse(
                    httpDoService(
                            businessTask.getUrl(),
                            businessTask.getParams(),
                            businessTask.getRequestMethod()
                    )
            );
            jsonObject.put("id", businessTask.getId());
        }catch(Exception e){
            return null;
        }
        return jsonObject;
    }

    private static String httpDoService(String url, Map<String, String> params, String requestMethod){
        TimerConfig timerConfig = TimerConfig.getTimerConfig();
        params.put("timerToken", timerConfig.getToken());
        String result = "";
        if(requestMethod.equals(TimerConfig.GET)){
            result = HttpClientUtil.doGet(url, params, timerConfig.getEncode());
            logger.info("result = " + result);
            return result;
        }else if(requestMethod.equals(TimerConfig.POST)){
            result = HttpClientUtil.doPost(url, params, timerConfig.getEncode());
            logger.info("result = " + result);
            return result;
        }else{
            logger.info("result = " + result);
            return null;
        }
    }
}
