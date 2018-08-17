package timing.template;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/***
 * @author 王强 Email : wangqiang@hushijie.com.cn
 * @version 创建时间：2017/12/5
 * DateToX
 */
@Data
public class DateToX{
    private long milliSecond;

    public DateToX(TimeUnit timeUnit, long duration){
        this.milliSecond = timeUnit.toMillis(duration);
    }

    public DateToX(){}
}
