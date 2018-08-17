package utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 王强 wangqiang@hushijie.com.cn
 * @version 创建时间：2017/07/27 15:04
 * 工具类
 **/
public final class BaseUtils{

    //private static final BeanUtilsBean bean = BeanUtilsBean.getInstance();


///////////////////数学运算函数集合//////////////////////

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double mathAdd(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double mathAdd(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }


    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double mathSub(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double mathSub(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mathMul(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mathMul(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double mathDiv(double v1, double v2, int scale){
        if(scale < 0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double mathDiv(String v1, String v2, int scale){
        if(scale < 0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    ///////////////////Bean函数集合//////////////////////

    /**
     * 获取当前类声明的private/protected变量
     *
     * @param object       将被执行的对象
     * @param propertyName 属性名
     * @return 属性对应的object value
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static Object getPrivateProperty(Object object, String propertyName)
            throws IllegalAccessException, NoSuchFieldException{
        Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        return field.get(object);
    }


    /**
     * 设置当前类声明的private/protected变量
     *
     * @param object       将被执行的对象
     * @param propertyName 属性名
     * @param newValue     属性对应的object value
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static void setPrivateProperty(Object object, String propertyName,
                                          Object newValue) throws IllegalAccessException,
            NoSuchFieldException{
        Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        field.set(object, newValue);
    }

    /**
     * 调用当前类声明的private/protected函数
     *
     * @param object     将被执行的对象
     * @param methodName 方法名
     * @param params     方法参数对应值数组
     * @return 返回返回值
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokePrivateMethod(Object object, String methodName,
                                             Object[] params) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException{
        Class[] types = new Class[params.length];
        for(int i = 0; i < params.length; i++){
            types[i] = params[i].getClass();
        }
        Method method = object.getClass().getDeclaredMethod(methodName, types);
        method.setAccessible(true);
        return method.invoke(object, params);
    }

    /**
     * 调用当前类声明的private/protected函数
     *
     * @param object     将被执行的对象
     * @param methodName 方法名
     * @param param      方法参数一个值时，方法参数对应值。
     * @return 返回返回值
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokePrivateMethod(Object object, String methodName,
                                             Object param) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException{
        return invokePrivateMethod(object, methodName, new Object[]{param});
    }


    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args){
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if(method == null){
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }
        try{
            return method.invoke(obj, args);
        }catch(Exception e){
            if(((e instanceof IllegalAccessException)) || ((e instanceof IllegalArgumentException)) ||
                    ((e instanceof NoSuchMethodException)))
                return new IllegalArgumentException("Reflection Exception.", e);
            if((e instanceof InvocationTargetException))
                return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
            if((e instanceof RuntimeException)){
                return (RuntimeException) e;
            }
            return new RuntimeException("Unexpected Checked Exception.", e);
        }

    }

    public static Method getAccessibleMethod(Object obj, String methodName, Class<?>[] parameterTypes){
        for(Class superClass = obj.getClass(); superClass != Object.class; ){
            try{
                Method method = superClass.getDeclaredMethod(methodName, parameterTypes);

                method.setAccessible(true);

                return method;
            }catch(NoSuchMethodException localNoSuchMethodException){
                superClass = superClass.getSuperclass();
            }

        }

        return null;
    }



    public static void copyMap(Map dest, Object orig){
        copyMap(dest, orig, true, null);
    }

    public static void copyNonNullMap(Map dest, Object orig){
        copyMap(dest, orig, false, null);
    }

    public static void copyMap(Map dest, Object orig, String[] ext){
        copyMap(dest, orig, true, ext);
    }

    public static void copyNonNullMap(Map dest, Object orig, String[] ext){
        copyMap(dest, orig, false, ext);
    }


    private static void copyMap(Map dest, Object orig, boolean copyNulls, String[] ext){

        if(dest == null)
            throw new IllegalArgumentException("No destination bean specified");
        if(orig == null)
            throw new IllegalArgumentException("No origin bean specified");

        JSONObject ojson = (JSONObject) JSON.toJSON(orig);
        for(String key : ojson.keySet()){
            if(((ojson.get(key) == null) && (!copyNulls)) || searchInArray(key, ext)){
            }else{
                dest.put(key, ojson.get(key));
            }
        }
    }


    public static void copyJSONObject(JSONObject dest, Object orig){
        copyJSONObject(dest, orig, true, null);
    }

    public static void copyNonNullJSONObject(JSONObject dest, Object orig){
        copyJSONObject(dest, orig, false, null);
    }

    public static void copyJSONObject(JSONObject dest, Object orig, String[] ext){
        copyJSONObject(dest, orig, true, ext);
    }

    public static void copyNonNullJSONObject(JSONObject dest, Object orig, String[] ext){
        copyJSONObject(dest, orig, false, ext);
    }


    private static void copyJSONObject(JSONObject dest, Object orig, boolean copyNulls, String[] ext){
        if(dest == null)
            throw new IllegalArgumentException("No destination bean specified");
        if(orig == null)
            throw new IllegalArgumentException("No origin bean specified");

        JSONObject ojson = (JSONObject) JSON.toJSON(orig);
        for(String key : ojson.keySet()){
            if(((ojson.get(key) == null) && (!copyNulls)) || searchInArray(key, ext)){
            }else{
                dest.put(key, ojson.get(key));
            }
        }
    }




    private static boolean searchInArray(String key, String[] arry){
        if(arry != null && arry.length > 0){
            for(String s : arry){
                if(key.equals(s)){
                    return true;
                }
            }
        }
        return false;
    }

    private static String getName(String name, String split){
        if(StringUtils.isBlank(split)){
            return name;
        }
        String[] names = name.split(split);
        String result = name;
        if(names.length > 1){
            result = names[0];
            for(int i = 1; i < names.length; i++){
                if(result.length() == 1 && i == 1){
                    result += names[i];
                }else{
                    result += names[i].substring(0, 1).toUpperCase() + names[i].substring(1, names[i].length());
                }
            }
        }
        return result;
    }


    ////////byte数据操作////////////

    /**
     * int转化为byte[]数组
     *
     * @param num
     * @return
     */
    public static byte[] int2bytes(int num){
        byte[] b = new byte[4];
        for(int i = 0; i < 4; i++){
            b[i] = (byte) (num >>> (24 - i * 8));
        }
        return b;
    }

    /**
     * byte[]数字转化为int型
     *
     * @param b
     * @return
     */
    public static int bytes2int(byte[] b){
        int mask = 0xff;
        int temp = 0;
        int res = 0;
        for(int i = 0; i < 4; i++){
            res <<= 8;
            temp = b[i] & mask;
            res |= temp;
        }
        return res;
    }

    /**
     * java字节码转十六进制字符串
     *
     * @param source
     * @return
     */
    public static String byte2hex(byte[] source){
        StringBuffer result = new StringBuffer();
        String item = "";
        for(int i = 0; i < source.length; i++){
            // 整数转成十六进制表示
            item = (Integer.toHexString(source[i] & 0XFF));
            if(item.length() == 1){
                result.append("0");
                result.append(item);
            }else{
                result.append(item);
            }
        }
        return result.toString().toUpperCase(); // 转成大写
    }

    /**
     * 十六进制字符串转换字节数组
     *
     * @param hex String 十六进制
     * @return String 转换后的字符串
     */
    public static byte[] hex2bin(String hex){
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for(int i = 0; i < bytes.length; i++){
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }


    /////////////////////////////////////////String数组组合//////////////////////////////


    /**
     * 对字符串进行UTF-8编码;
     *
     * @param source 原始输入
     * @return 返回编码后的字符串;
     */
    public static String encodeSourceByUTF8(String source){
        if(source == null || source.length() <= 0){
            return "";
        }
        try{
            return URLEncoder.encode(source, "UTF-8");
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }


    /**
     * 截取字符串的前targetCount个字符,格式化字符串长度，区分汉字跟字母。 注意，该方法前提认为：汉字2个字节，字母、数字一个字节。
     *
     * @param source      被处理字符串
     * @param targetCount 截取长度
     * @param more        后缀字符串
     * @return String
     */
    public static String StringSubContent(String source, int targetCount, String more){
        String result = "";
        try{
            // 不能使用getBytes(),因为它会使用平台默认的字符集来判断长度，比如utf-8会认为汉字是三个字节
            if(source.getBytes("GBK").length <= targetCount){// 如果长度比需要的长度n小,返回原字符串
                return source;
            }else{
                int t = 0;
                char[] tempChar = source.toCharArray();
                for(int i = 0; i < tempChar.length && t < targetCount; i++){
                    // if ((int) tempChar[i] >= 0x4E00
                    // && (int) tempChar[i] <= 0x9FA5)// 是否汉字
                    if((int) tempChar[i] > 256){ // 不是英文字母以及数字
                        result += tempChar[i];
                        t += 2;
                    }else{
                        result += tempChar[i];
                        t++;
                    }
                }
                result += more;
                return result;
            }
        }catch(UnsupportedEncodingException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 全角转半角 说明：该类实现把字符中的全角转化为半角，包括全角字符，全角空格. 在windows中，中文和全角字符都占两个字节，并且使用了ascii
     * chart 2 (codes 128?C255)。 我们可以凭这一点来一个个检测用户输入的是否是中文和全角字符。
     * 实际上，全角字符的第一个字节总是被置为163，而第二个字节则是相同半角字符码加上128（包 * 括空格）。
     * 如半角a为65，则全角a则是163（第一个字节）、193（第二个字节，128+65）。 而对于中文来讲，*它的第一个字节被置为大于163，
     * （如'阿'为:176 162）,我们可以在检测到中文时不进行转换。
     *
     * @param sbcContent
     * @return
     */
    public static String sbc2dbc(String sbcContent){

        if(sbcContent == null)
            return null;
        String outStr = "";
        String Tstr = "";
        byte[] b = null;

        for(int i = 0; i < sbcContent.length(); i++){
            try{
                Tstr = sbcContent.substring(i, i + 1);
                b = Tstr.getBytes("unicode");
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
            if(b[3] == -1){
                b[2] = (byte) (b[2] + 32);
                b[3] = 0;
                try{
                    outStr = outStr + new String(b, "unicode");
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }else
                outStr = outStr + Tstr;
        }
        return outStr;
    }

    /**
     * 半角转全角
     *
     * @param dbcContent
     * @return
     */
    public static String dbc2sbc(String dbcContent){
        if(dbcContent == null)
            return null;
        String outStr = "";
        String Tstr = "";
        byte[] b = null;

        for(int i = 0; i < dbcContent.length(); i++){
            try{
                Tstr = dbcContent.substring(i, i + 1);
                b = Tstr.getBytes("unicode");
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
            if(b[3] != -1){
                b[2] = (byte) (b[2] - 32);
                b[3] = -1;
                try{
                    outStr = outStr + new String(b, "unicode");
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }else
                outStr = outStr + Tstr;
        }
        return outStr;
    }

    /**
     * 根据传入的数组转化成以逗号分隔的字符串
     *
     * @param arrays
     * @return String 例如"1,2,3,4,5,6" 默认使用逗号
     */
    public static String array2String(String[] arrays){
        return array2String(arrays, ",");
    }

    /**
     * 根据传入的数组转化成以逗号分隔的字符串
     *
     * @param arrays string数组
     * @return String 例如"1,2,3,4,5,6"
     */
    public static String array2String(String[] arrays, String splitChar){
        String resultString = "";
        if(arrays != null && arrays.length != 0){
            StringBuffer tmpstring = new StringBuffer();
            boolean flag = false;
            for(String tmps : arrays){
                if(flag)
                    tmpstring.append(splitChar);
                tmpstring.append(tmps.trim());
                flag = true;
            }
            resultString = tmpstring.toString();
        }
        return resultString;
    }

    /**
     * 根据传入的list转化成以逗号分隔的字符串
     *
     * @param stringlist
     * @return 默认使用逗号
     */
    public static String list2String(List<String> stringlist){
        return list2String(stringlist, ",");
    }

    /**
     * 根据传入的list转化成以逗号分隔的字符串
     *
     * @param stringlist
     * @param splitChar
     * @return
     */
    public static String list2String(List<String> stringlist, String splitChar){
        String resultString = "";
        if(stringlist != null && stringlist.size() != 0){
            StringBuffer tmpstring = new StringBuffer();
            boolean flag = false;
            for(String tmps : stringlist){
                if(flag)
                    tmpstring.append(splitChar);
                tmpstring.append(tmps.trim());
                flag = true;
            }
            resultString = tmpstring.toString();
        }
        return resultString;
    }

    /**
     * 根据以分割的字符串参数转化为数组 默认使用逗号
     *
     * @param string
     * @return
     */
    public static String[] string2Array(String string){
        return string2Array(string, ",");
    }

    /**
     * 根据以分割的字符串参数转化为数组
     *
     * @param string
     * @param splitChar
     * @return
     */
    public static String[] string2Array(String string, String splitChar){
        String[] tmpArray = null;
        if(string != null && !"".equals(string.trim())){
            tmpArray = StringUtils.splitByWholeSeparator(string, splitChar);
        }
        return tmpArray;
    }

    /**
     * 根据以分割的字符串参数转化为list 默认使用逗号
     *
     * @param string
     * @return
     */
    public static List<String> string2List(String string){
        return string2List(string, ",");
    }

    /**
     * 根据以分割的字符串参数转化为list
     *
     * @param string
     * @return
     */
    public static List<String> string2List(String string, String splitChar){
        List<String> tmpList = null;
        if(string != null && !"".equals(string.trim())){
            tmpList = Arrays.asList(StringUtils.splitByWholeSeparator(string,
                    splitChar));
        }
        return tmpList;
    }

    /**
     * 将source中凡是出现 字符串数组中的字符串 的位置 替换成 target
     *
     * @param source
     * @param keywords
     * @param target
     * @return
     */
    public static String replace(String source, String[] keywords, String target){
        if(StringUtils.isBlank(source) || StringUtils.isBlank(target)
                || keywords == null || keywords.length == 0)
            return null;
        String result = source;
        for(String keyword : keywords){
            if(StringUtils.isBlank(keyword))
                continue;
            result = StringUtils.replace(result, keyword, target);
        }
        return result;
    }

    /**
     * null2Empty
     *
     * @param source
     * @return
     */
    public static String null2Empty(String source){
        if(source == null || StringUtils.equalsIgnoreCase(source, "null")){
            return "";
        }else{
            return source;
        }
    }

    /**
     * 连接两个字符串,后一个字符串使用[]括起来
     *
     * @param source1
     * @param source2
     * @return
     */
    public static String StringConcat(String source1, String source2){
        StringBuffer result = new StringBuffer("");
        if(StringUtils.isNotBlank(source1)){
            result.append(source1);
            if(StringUtils.isNotBlank(source2)){
                result.append(source2);
            }
        }
        return result.toString();
    }

    public static boolean isNotBlank(String str){
        return StringUtils.isNotBlank(str);
    }

    public static boolean isBlank(String str){
        return StringUtils.isBlank(str);
    }


    /////////////////////////////////Date函数集合////////////////////////////////

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2){
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try{
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        }catch(Exception e){
            return "";
        }
        return day + "";
    }

    public static long getSecond(String sj1, String sj2){
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        try{
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime());
        }catch(Exception e){
            return 0;
        }
        return day;
    }

    /**
     * 获取给定月有多少天
     */
    public static String getMonthDays(String str){
        String nm = formatDate(addMonth(strToDate(str)), "yyyy-MM-dd");
        return getTwoDay(nm, str);
    }


    public static String formatDate(Date date, String format){
        if(date == null){
            date = new Date();
        }
        if(format == null || "".equalsIgnoreCase(format)){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


    /**
     * 根据�?个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate){
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范�? 1~7
        // 1=星期�? 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    /**
     * 根据当前时间，返回是1,2等字符串
     *
     * @return
     */
    public static String getWeekNow(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("星期一", "1");
        map.put("星期二", "2");
        map.put("星期三", "3");
        map.put("星期四", "4");
        map.put("星期五", "5");
        map.put("星期六", "6");
        map.put("星期日", "7");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(new Date());
        return map.get(week);
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date strToDate(String strDate, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    public static Date addMonth(Date date){
        if(date == null){
            date = new Date();
        }
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(Calendar.MONTH, 1);
        return cd.getTime();
    }

    /**
     * 两个时间之间的天�?
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2){
        if(date1 == null || date1.equals(""))
            return 0;
        if(date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时�?
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try{
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        }catch(Exception e){
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    //获取加上i天后的日期
    public static String getAddDay(String date, int i){
        Date tempDate = strToDate(date);
        Calendar cd = Calendar.getInstance();
        cd.setTime(tempDate);
        cd.add(Calendar.DAY_OF_MONTH, i);
        Date tempDate1 = cd.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(tempDate1);
    }


    // 计算当月�?后一�?,返回字符�?
    public static String getDefaultDay(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//设为当前月的1�?
        lastDate.add(Calendar.MONTH, 1);//加一个月，变为下月的1�?
        lastDate.add(Calendar.DATE, -1);//减去�?天，变为当月�?后一�?

        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 上月第一�?
    public String getPreviousMonthFirst(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//设为当前月的1�?
        lastDate.add(Calendar.MONTH, -1);//减一个月，变为下月的1�?
        //lastDate.add(Calendar.DATE,-1);//减去�?天，变为当月�?后一�?

        str = sdf.format(lastDate.getTime());
        return str;
    }

    //获取当月第一�?
    public String getFirstDayOfMonth(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//设为当前月的1�?
        str = sdf.format(lastDate.getTime());
        return str;
    }

    // 获得本周星期日的日期
    public String getCurrentWeekday(){
        int mondayPlus = this.getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();

        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得本周星期一的日期
    public static String getCurrentMonday(){
        int mondayPlus = getMondayPlus();
        //mondayPlus=mondayPlus-7;
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();

        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得下周星期一的日期
    public static String getNextWeekday(){
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6 + 1);
        Date monday = currentDate.getTime();

        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String preMonday = df1.format(monday);
        return preMonday;
    }


    //获取当天时间
    public static String getNowTime(String dateformat){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);//可以方便地修改日期格�?
        String hehe = dateFormat.format(now);
        return hehe;
    }

    // 获得当前日期与本周日相差的天�?
    private static int getMondayPlus(){
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二�?......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1;         //因为按中国礼拜一作为第一天所以这里减1
        if(dayOfWeek == 1){
            return 0;
        }else{
            return 1 - dayOfWeek;
        }
    }

    //获得本周�?的日�?
    public static String getMondayOFWeek(){
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();

        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }


    // 获得下周星期�?的日�?
    public static String getNextMonday(){
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得下周星期日的日期
    public static String getNextSunday(){

        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }


    //获得上月�?后一天的日期
    public static String getPreviousMonthEnd(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, -1);//减一个月
        lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一�?
        lastDate.roll(Calendar.DATE, -1);//日期回滚�?天，也就是本月最后一�?
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //获得下个月第�?天的日期
    public static String getNextMonthFirst(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);//减一个月
        lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一�?
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //获得下个月最后一天的日期
    public static String getNextMonthEnd(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);//加一个月
        lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一�?
        lastDate.roll(Calendar.DATE, -1);//日期回滚�?天，也就是本月最后一�?
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //获得明年�?后一天的日期
    public static String getNextYearEnd(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.YEAR, 1);//加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        lastDate.roll(Calendar.DAY_OF_YEAR, -1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    //获得明年第一天的日期
    public static String getNextYearFirst(){
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.YEAR, 1);//加一个年
        lastDate.set(Calendar.DAY_OF_YEAR, 1);
        str = sdf.format(lastDate.getTime());
        return str;

    }

    //获得本年有多少天
    private static int getMaxYear(){
        Calendar cd = Calendar.getInstance();
        cd.set(Calendar.DAY_OF_YEAR, 1);//把日期设为当年第�?�?
        cd.roll(Calendar.DAY_OF_YEAR, -1);//把日期回滚一天�??
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        return MaxYear;
    }

    private static int getYearPlus(){
        Calendar cd = Calendar.getInstance();
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//获得当天是一年中的第几天
        cd.set(Calendar.DAY_OF_YEAR, 1);//把日期设为当年第�?�?
        cd.roll(Calendar.DAY_OF_YEAR, -1);//把日期回滚一天�??
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);
        if(yearOfNumber == 1){
            return -MaxYear;
        }else{
            return 1 - yearOfNumber;
        }
    }

    //获得本年第一天的日期
    public static String getCurrentYearFirst(){
        int yearPlus = getYearPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, yearPlus);
        Date yearDay = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preYearDay = df.format(yearDay);
        return preYearDay;
    }


    //获得本年�?后一天的日期 *
    public static String getCurrentYearEnd(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");//可以方便地修改日期格�?
        String years = dateFormat.format(date);
        return years + "-12-31";
    }


    //获得上年第一天的日期 *
    public static String getPreviousYearFirst(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");//可以方便地修改日期格�?
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);
        years_value--;
        return years_value + "-1-1";
    }


    //获得本季�?
    public static String getThisSeasonTime(int month){
        int array[][] = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        int season = 1;
        if(month >= 1 && month <= 3){
            season = 1;
        }
        if(month >= 4 && month <= 6){
            season = 2;
        }
        if(month >= 7 && month <= 9){
            season = 3;
        }
        if(month >= 10 && month <= 12){
            season = 4;
        }
        int start_month = array[season - 1][0];
        int end_month = array[season - 1][2];

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");//可以方便地修改日期格�?
        String years = dateFormat.format(date);
        int years_value = Integer.parseInt(years);

        int start_days = 1;//years+"-"+String.valueOf(start_month)+"-1";//getLastDayOfMonth(years_value,start_month);
        int end_days = getLastDayOfMonth(years_value, end_month);
        String seasonDate = years_value + "-" + start_month + "-" + start_days + ";" + years_value + "-" + end_month + "-" + end_days;
        return seasonDate;

    }

    /**
     * 获取某年某月的最后一�?
     *
     * @param year  �?
     * @param month �?
     * @return �?后一�?
     */
    private static int getLastDayOfMonth(int year, int month){
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12){
            return 31;
        }
        if(month == 4 || month == 6 || month == 9 || month == 11){
            return 30;
        }
        if(month == 2){
            if(isLeapYear(year)){
                return 29;
            }else{
                return 28;
            }
        }
        return 0;
    }

    /**
     * 是否闰年
     *
     * @param year �?
     * @return
     */
    public static boolean isLeapYear(int year){
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /*
     * 比较两个月日期大小
     * */
    public static boolean compareMonth(int year1, int month1, int year, int month){
        if(year1 > year){
            return true;
        }else if(year1 == year){
            if(month1 >= month){
                return true;
            }
        }
        return false;
    }

    public static boolean compareDate(String d1, String d2) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date1;

        date1 = df.parse(d1);
        Date date2 = df.parse(d2);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        return c2.before(c1);
    }

    public static String decreaseMonth(String d1) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date1;
        date1 = df.parse(d1);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        c1.add(Calendar.MONTH, -1);
        return df.format(c1.getTime());
    }

    public static String addDay(String d1, int day){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date1;
        try{
            date1 = df.parse(d1);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            c1.add(Calendar.DATE, day);
            return df.format(c1.getTime());
        }catch(ParseException e){
            return "";
        }
    }

    public static String addHour(String d1, int hour){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1;
        try{
            date1 = df.parse(d1);
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            c1.add(Calendar.HOUR_OF_DAY, hour);
            return df.format(c1.getTime());
        }catch(ParseException e){
            return "";
        }
    }

    public static String addMin(String d1, int min) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1;
        date1 = df.parse(d1);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        c1.add(Calendar.MINUTE, min);
        return df.format(c1.getTime());
    }

    /*
     * 获取上月25日到当前系统时间的日期列表
     * */
    public static List getDateList(int year, int month) throws Exception{
        List list = new ArrayList();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String strDate = df.format(date);
        String thisDate = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(26);
        String thisDate1 = decreaseMonth(thisDate);
        String thisDate2 = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(25);
        if(compareDate(thisDate1, strDate)){
            return null;
        }else if(compareDate(strDate, thisDate2)){
            long days = getDays(thisDate2, thisDate1);
            for(int i = 0; i < days - 1; i++){
                list.add(addDay(thisDate1, i));
            }
        }else if(compareDate(strDate, thisDate1)){
            long days = getDays(strDate, thisDate1);
            for(int i = 0; i < days; i++){
                list.add(addDay(thisDate1, i));
            }
        }
        return list;
    }


    /**
     * 从给定日期序列中获取连续系列
     **/
    public static List getDateListInDate(List list, String sdate){
        if(list == null || list.size() == 0){
            return null;
        }
        List upList = new ArrayList();
        List downList = new ArrayList();
        List aList = new ArrayList();
        String tstr = sdate;
        while(true){
            tstr = getSubDateUp(list, tstr);
            if("".equalsIgnoreCase(tstr)){
                break;
            }
            upList.add(tstr);
        }
        tstr = sdate;
        while(true){
            tstr = getSubDateDown(list, tstr);
            if("".equalsIgnoreCase(tstr)){
                break;
            }
            downList.add(tstr);
        }

        for(int i = upList.size() - 1; i >= 0; i--){
            String tempStr = (String) upList.get(i);
            aList.add(tempStr);
        }
        aList.add(sdate);
        for(int i = 0; i < downList.size(); i++){
            String tempStr = (String) downList.get(i);
            aList.add(tempStr);
        }

        return aList;
    }

    public static String getSubDateDown(List list, String sdate){
        if(list == null || list.size() == 0){
            return "";
        }
        String sdate1 = addDay(sdate, 1);
        for(int i = 0; i < list.size(); i++){
            String tempStr = (String) list.get(i);
            if(sdate1.equalsIgnoreCase(tempStr)){
                return tempStr;
            }
        }
        return "";
    }

    public static String getSubDateUp(List list, String sdate){
        if(list == null || list.size() == 0){
            return "";
        }
        String sdate1 = addDay(sdate, -1);
        for(int i = 0; i < list.size(); i++){
            String tempStr = (String) list.get(i);
            if(sdate1.equalsIgnoreCase(tempStr)){
                return tempStr;
            }
        }
        return "";
    }

    //获取本年度所有周末数据
    public static List getWeekInYear(String year){
        List list = new ArrayList();
        int iyear = Integer.parseInt(year);
        int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if(iyear % 4 == 0 && iyear % 100 != 0 || iyear % 400 == 0){
            days[2] = 29;
        }
        for(int i = 1; i < days.length; i++){
            for(int j = 1; j <= days[i]; j++){
                Date date = new Date(iyear - 1900, i - 1, j);
                int week = date.getDay();
                if(week == 0 || week == 6){
                    list.add(formatDate(date, "yyyy-MM-dd"));
                }
            }
        }
        return list;
    }

    //
    public static int getMaxDay(int year, int month){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year); //指定年
        cal.set(Calendar.MONTH, month - 1);//指定月，应该是指定月-1
        int maxDate = cal.getActualMaximum(Calendar.DATE);
        return maxDate;
    }


    ///////////////////////验证函数集合//////////////////////////////

    /**
     * 验证手机号码是否合法,包括移动，联通，小灵通
     *
     * @param mobileNo
     */
    public static boolean isMobileNo(String mobileNo){

        String reg = "(^13[0-9]\\d{8}$)|(^17[0,1,6,7,8]\\d{8}$)|(^15[0,1,2，5，6,7,8,9]\\d{8}$)|(^18[0-9]\\d{8}$)|(^14[5,7]\\d{8}$)|(^[1-9]{2}\\d{6}$)";

        Pattern p = Pattern.compile(reg);

        Matcher m = p.matcher(mobileNo);

        return m.find();
    }

    /**
     * 验证身份证号是否有效
     *
     * @param strIdCard
     */
    public static boolean isIdentityCard(String strIdCard){

        boolean result = false;

        Map<String, String> mapArea = new HashMap<String, String>();

        mapArea.put("11", "北京");
        mapArea.put("12", "天津");
        mapArea.put("13", "河北");
        mapArea.put("14", "山西");
        mapArea.put("15", "内蒙古");
        mapArea.put("21", "辽宁");
        mapArea.put("22", "吉林");
        mapArea.put("23", "黑龙江");
        mapArea.put("31", "上海");
        mapArea.put("32", "江苏");

        mapArea.put("33", "浙江");
        mapArea.put("34", "安徽");
        mapArea.put("35", "福建");
        mapArea.put("36", "江西");
        mapArea.put("37", "山东");
        mapArea.put("41", "河南");
        mapArea.put("42", "湖北");
        mapArea.put("43", "湖南");
        mapArea.put("44", "广东");
        mapArea.put("45", "广西");

        mapArea.put("46", "海南");
        mapArea.put("50", "重庆");
        mapArea.put("51", "四川");
        mapArea.put("52", "贵州");
        mapArea.put("53", "云南");
        mapArea.put("54", "西藏");
        mapArea.put("61", "陕西");
        mapArea.put("62", "甘肃");
        mapArea.put("63", "青海");
        mapArea.put("64", "宁夏");

        mapArea.put("65", "新疆");
        mapArea.put("71", "台湾");
        mapArea.put("81", "香港");
        mapArea.put("82", "澳门");
        mapArea.put("91", "国外");

        if(!mapArea.containsKey(StringUtils.substring(strIdCard, 0, 2))){

        }else{

            // 闰年出生日期正则 15位
            String regRYear15 = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";
            // 平年出生日期正则 15位
            String regPYear15 = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$";

            // 闰年出生日期正则 18位
            String regRYear18 = "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";

            // 平年出生日期正则 18位
            String regPYear18 = "^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$";

            String reqYear = "";

            int intYear = 0;

            if(strIdCard.length() == 15){

                intYear = Integer.valueOf(
                        StringUtils.substring(strIdCard, 6, 8)).intValue() + 1900;

                if(((intYear) % 4 == 0)
                        || (((intYear) % 100 == 0) && ((intYear) % 4 == 0))) // 闰年
                {
                    reqYear = regRYear15;
                }else// 平年
                {
                    reqYear = regPYear15;
                }

                Pattern p = Pattern.compile(reqYear);

                Matcher m = p.matcher(strIdCard);

                result = m.find();

                if(!result){
                }

            }else if(strIdCard.length() == 18){

                intYear = Integer.valueOf(
                        StringUtils.substring(strIdCard, 6, 10)).intValue() + 1900;

                if(((intYear) % 4 == 0)
                        || (((intYear) % 100 == 0) && ((intYear) % 4 == 0))) // 闰年
                {
                    reqYear = regRYear18;
                }else// 平年
                {
                    reqYear = regPYear18;
                }

                Pattern p = Pattern.compile(reqYear);

                Matcher m = p.matcher(strIdCard);

                result = m.find();

                if(!result){


                }else{

                    String[] tmpSplit = strIdCard.split("");

                    int calcText = (Integer.valueOf(tmpSplit[1]).intValue() + Integer
                            .valueOf(tmpSplit[11]).intValue())
                            * 7
                            + (Integer.valueOf(tmpSplit[2]).intValue() + Integer
                            .valueOf(tmpSplit[12]).intValue())
                            * 9
                            + (Integer.valueOf(tmpSplit[3]).intValue() + Integer
                            .valueOf(tmpSplit[13]).intValue())
                            * 10
                            + (Integer.valueOf(tmpSplit[4]).intValue() + Integer
                            .valueOf(tmpSplit[14]).intValue())
                            * 5
                            + (Integer.valueOf(tmpSplit[5]).intValue() + Integer
                            .valueOf(tmpSplit[15]).intValue())
                            * 8
                            + (Integer.valueOf(tmpSplit[6]).intValue() + Integer
                            .valueOf(tmpSplit[16]).intValue())
                            * 4
                            + (Integer.valueOf(tmpSplit[7]).intValue() + Integer
                            .valueOf(tmpSplit[17]).intValue())
                            * 2
                            + Integer.valueOf(tmpSplit[8]).intValue()
                            * 1
                            + Integer.valueOf(tmpSplit[9]).intValue()
                            * 6
                            + Integer.valueOf(tmpSplit[10]).intValue() * 3;

                    int x = calcText % 11;

                    String JYM = "10X98765432";

                    String src = StringUtils.substring(JYM, x, x + 1);

                    result = StringUtils.equalsIgnoreCase(tmpSplit[18], src);

                }

            }
        }

        return result;

    }

    /**
     * 验证身份证准确性，并判断是否在 开始年龄-结束年龄 周岁的范围内
     *
     * @param strIdCard 身份证号码
     * @param begin     开始年龄
     * @param end       结束年龄
     * @return
     */
    public static boolean isIdentityCardAndInRange(String strIdCard, int begin,
                                                   int end){

        // 先判断身份证号码的有效性
        boolean result = BaseUtils.isIdentityCard(strIdCard);

        if(result){

            String birthYear = "";
            String birthDate = "";

            if(strIdCard.length() == 15){
                birthYear = "19" + strIdCard.substring(6, 8);
                birthDate = strIdCard.substring(8, 12);
            }else if(strIdCard.length() == 18){
                birthYear = strIdCard.substring(6, 10);
                birthDate = strIdCard.substring(10, 14);
            }

            Date date = new Date();

            int age = Integer.parseInt(BaseUtils.formatDate(date, "yyyy"))
                    - Integer.valueOf(birthYear).intValue();

            String currentDate = BaseUtils.formatDate(date, "MMdd");

            if(age < begin || age > end)// 首先判断年份
            {
                result = false;

            }
            if((age == begin) && (currentDate.compareTo(birthDate) < 0))// 如果刚好等于begin，还得判断日期是否到了
            {

                result = false;

            }
            if((age == end) && (currentDate.compareTo(birthDate) > 0))// 如果刚好等于end，还得判断日期是否超过
            {

                result = false;

            }
        }

        return result;
    }


    public static String hexEncode(byte[] input){
        return Hex.encodeHexString(input);
    }

    public static byte[] hexDecode(String input){
        try{
            return Hex.decodeHex(input.toCharArray());
        }catch(DecoderException e){
            throw new IllegalStateException("Hex Decoder exception", e);
        }

    }

    public static String base64Encode(byte[] input){
        return new String(Base64.encodeBase64(input));
    }


    public static String base64UrlSafeEncode(byte[] input){
        return Base64.encodeBase64URLSafeString(input);
    }

    public static byte[] base64Decode(String input){
        return Base64.decodeBase64(input);
    }

    public static String urlEncode(String input){
        try{
            return URLEncoder.encode(input, "UTF-8");
        }catch(UnsupportedEncodingException e){
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }

    }

    public static String urlDecode(String input){
        try{
            return URLDecoder.decode(input, "UTF-8");
        }catch(UnsupportedEncodingException e){
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }

    }

    public static String getHashStr(String str, String type){
        try{
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] input = str.getBytes();
            byte[] buff = md.digest(input);
            return bytesToHex(buff);
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    public static String htmlEscape(String html){
        return StringEscapeUtils.escapeHtml(html);
    }

    public static String htmlUnescape(String htmlEscaped){
        return StringEscapeUtils.unescapeHtml(htmlEscaped);
    }

    public static String xmlEscape(String xml){
        return StringEscapeUtils.escapeXml(xml);
    }

    public static String xmlUnescape(String xmlEscaped){
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }

    public static String replaceStringNull(String str){
        if(str == null){
            str = "";
        }
        return str;
    }

    public static Timestamp getSysDate(){//获得系统当前时间
        return new Timestamp((new GregorianCalendar()).getTimeInMillis());
    }


    public static List<Map<String, Object>> checknull(List<Map<String, Object>> topic, List<Map<String, Object>> details) throws Exception{

        for(Map<String, Object> tmap : topic){
            for(Map<String, Object> dmap : details){

                if(dmap.get(tmap.get("Ename").toString()) == null){
                    dmap.put(tmap.get("Ename").toString(), "");
                }
            }
        }
        return details;
    }


    public static List<Map<String, Object>> changeTime(List<Map<String, Object>> list) throws Exception{
        for(Map<String, Object> map : list){
            if(map.get("jc_conn") == null){
                map.put("jc_conn", "");
            }
            for(String key : map.keySet()){
                if(map.get(key) instanceof Date){
                    Date date = (Date) map.get(key);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String str = sdf.format(date);
                    map.put(key, str);
                }
            }
        }
        return list;
    }

    public static List<HashMap<String, Object>> changeHashTime(List<HashMap<String, Object>> list) throws Exception{
        for(Map<String, Object> map : list){
            for(String key : map.keySet()){
                if(map.get(key) == null){
                    map.put(key, "");
                }

                if(map.get(key) instanceof Date){
                    Date date = (Date) map.get(key);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String str = sdf.format(date);
                    map.put(key, str);
                }
            }
        }
        return list;
    }


    /***
     * 队列相关工具
     */
    public static class ListUtils{




        public static void main(String[] args){
            String str = BaseUtils.getHashStr("234", "SHA-256");
            System.out.println(str);
//            logger.info(String.format("用户"));
//            List<CourseWareDO> list = new ArrayList<>();
//            List<CourseWareDO> list2 = new ArrayList<>();

//            CourseWareDO courseWareDO1 = new CourseWareDO();
//            courseWareDO1.setId(1);
//            courseWareDO1.setName("demo1");
//            CourseWareDO courseWareDO2 = new CourseWareDO();
//            courseWareDO2.setId(2);
//            courseWareDO2.setName("demo2");
//            CourseWareDO courseWareDO3 = new CourseWareDO();
//            courseWareDO3.setId(1);
//            courseWareDO3.setName("demo3");

//            list.add(courseWareDO1);
//            list.add(courseWareDO2);
//            list.add(courseWareDO3);

//            List<CourseWareDO> repeatCoursewareList = utils.BaseUtils.ListUtils.getRepeatElements(list, "id");
//            list.removeAll(repeatCoursewareList);
//            System.out.println(list);
        }

        /**
         * 将list根据fieldName将元素装载到map中
         *
         * @param list            数据源
         * @param map             数据容器
         * @param keyFieldName    map中的key在list中元素的字段的名称,如果为空则使用uuid
         * @param prefixFieldName 字段前缀名称
         */
        public static void listToMap(List list, Map<String, Object> map, String keyFieldName, String prefixFieldName){
            try{
                Object key;
                Object prefix = null;
                String getT = "get";
                for(Object object : list){
                    if(keyFieldName == null){
                        key = UUID.randomUUID().toString();
                    }else{
                        Class c = object.getClass();
                        Method keyMethod = c.getMethod(getT + StringUtilsSon.firstLetterUpperCase(keyFieldName));
                        key = keyMethod.invoke(object);
                        if(prefixFieldName != null){
                            Method prefixMethod = c.getMethod(getT + StringUtilsSon.firstLetterUpperCase(prefixFieldName));
                            prefix = prefixMethod.invoke(object);
                        }
                    }
                    map.put(prefix == null ? JSONObject.toJSONString(key).replaceAll("\"", "") : (JSONObject.toJSONString(prefix) + JSONObject.toJSONString(key)).replaceAll("\"", ""), object);
                }
            }catch(NoSuchMethodException e){
                e.printStackTrace();
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }catch(InvocationTargetException e){
                e.printStackTrace();
            }
        }

        /**
         * 将list根据fieldName将元素装载到map中
         *
         * @param list            数据源
         * @param map             数据容器
         * @param keyFieldName    map中的key在list中元素的字段的名称,如果为空则使用uuid
         * @param prefixFieldName 字段前缀名称
         */
        public static void listToMapAndPrefix(List list, Map<String, Object> map, String keyFieldName, String prefixFieldName){
            try{
                Object key;
                String getT = "get";
                for(Object object : list){
                    if(keyFieldName == null){
                        key = UUID.randomUUID().toString();
                    }else{
                        Class c = object.getClass();
                        Method keyMethod = c.getMethod(getT + StringUtilsSon.firstLetterUpperCase(keyFieldName));
                        key = keyMethod.invoke(object);
                    }
                    map.put(prefixFieldName == null ? key + "" : prefixFieldName + key, object);
                }
            }catch(NoSuchMethodException e){
                e.printStackTrace();
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }catch(InvocationTargetException e){
                e.printStackTrace();
            }
        }


        /**
         * 从一个list中获取某个元素的某个值等于key的元素实例对象,(默认返回list中第一个符合条件的对象实例)，元素实例的变量必须为object类型，比如说整型的话Integer可以，int就不行。
         *
         * @param list  数据源
         * @param key   元素中的变量名称
         * @param value 要查询的具体值
         * @param <T>
         * @return
         */
        public static <T> T getListToValue(List<T> list, String key, Object value){
            String get = "get";
            String methodName = get + StringUtilsSon.firstLetterUpperCase(key);
            for(T t : list){
                Object tValue = ObjectUtils.invokeMethod(t, methodName);
                if(value == null){
                    if(value == tValue){
                        return t;
                    }
                }else{
                    if(value.equals(tValue)){
                        return t;
                    }
                }
            }
            return null;
        }

        /**
         * 自动生成uuid为map的key,然后装载到map中
         *
         * @param list 数据源
         * @param map  数据容器
         */
        public static void listToMap(List list, Map<String, Object> map){
            ListUtils.listToMap(list, map, null, null);
        }





    }

    /**
     * String工具类
     */
    public static class StringUtilsSon extends StringUtils{
        /**
         * 将一个字符串的首字母变成大写
         *
         * @param str
         * @return
         */
        public static String firstLetterUpperCase(String str){
            return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
        }

        /***
         * 根据一个标记转换为驼峰
         * @param str
         * @return
         */
        public static String markToHump(String str, String mark, Integer start){
            String[] strs = str.split(mark);
            String returnStr = "";
            if(start == null){
                start = 0;
            }
            for(int i = 0; i < strs.length; i++){
                returnStr += start == null ? StringUtilsSon.firstLetterUpperCase(strs[i]) : start == i ? strs[i] : StringUtilsSon.firstLetterUpperCase(strs[i]);
            }
            return returnStr;
        }

        /***
         * 将一个标识符在驼峰的字符串中加入进去，并将大写字母转为小写字母
         * @param str 原始数据
         * @param mark 标识
         * @return
         */
        public static String addMarkToString(String str, String mark){
            char[] chs = str.toCharArray();
            int start = 65;
            int end = 90;
            str = "";
            int i = -1;
            for(char ch : chs){
                i++;
                int chInt = (int) ch;
                if(start <= chInt && end >= chInt){
                    if(i != 0){
                        str += (mark + (ch + "").toLowerCase());
                    }else{
                        str += (ch + "").toLowerCase();
                    }
                    continue;
                }
                str += ch;
            }
            return str;
        }


        private static String replaceRange(String source, String oldChar, String newChar, String start, String end, int index){
            int startInt = source.indexOf(start, index);
            int endInt = source.indexOf(end, index);
            if(endInt == -1)
                return source;
            return StringUtilsSon.replaceRange(
                    source.substring(0, startInt) + source.substring(startInt, endInt).replace(oldChar, newChar) + source.substring(endInt, source.length()),
                    oldChar,
                    newChar,
                    start,
                    end,
                    endInt + 1
            );
        }

        /***
         * 讲一个字符串中两个字符之间的值替换为其他值
         * @param source 数据源
         * @param oldChar 要替换的字符串原来的值
         * @param newChar 要替换的字符串最新的值
         * @param start    范围开始的值
         * @param end 范围结束的值
         * @return
         */
        public static String replaceRange(String source, String oldChar, String newChar, String start, String end){
            return StringUtilsSon.replaceRange(source, oldChar, newChar, start, end, 0);
        }
    }

    /***
     * 枚举相关工具
     */
    public static class EnumUtils{



//        public static <T> T getValue(Object[] enmuObjs, String keyName, String valueName, String byKey, Class<T> type){
//            String methodKeyName = "";
//            String methodValueName = "";
//            for(Object enmuObj : enmuObjs){
//                Field[] fields = enmuObj.getClass().getDeclaredFields();
//                String get = "get";
//                //取到key name 和 value name
//                if(methodKeyName.equals("") || methodKeyName.equals(null)){
//                    for(Field field : fields){
//                        field.setAccessible(true);
//                        String fieldName = field.getName();
//                        //判断是否为key name
//                        if(keyName.equals(fieldName))
//                            methodKeyName = get + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());//如果为key name或者value name则存起来
//                        if(valueName.equals(fieldName))
//                            methodValueName = get + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
//                    }
//                }
//                try{
//                    Method methodValueMth = enmuObj.getClass().getMethod(methodValueName);
//                    Method methodKeyMth = enmuObj.getClass().getMethod(methodKeyName);
//                    T value = (T) methodValueMth.invoke(enmuObj);
//                    String key = (String) methodKeyMth.invoke(enmuObj);
//                    if(key.equals(byKey)){
//                        return value;
//                    }
//                }catch(NoSuchMethodException e){
//                    String msg = e.getMessage();
//                    String msgStr = "() 此方法不存在";
//                    if(msg.contains(methodValueName)){
//                        try{
//                            throw new Exception(methodValueName + msgStr);
//                        }catch(Exception e1){
//                            e1.printStackTrace();
//                        }
//                    }else if(msg.contains(methodKeyName)){
//                        try{
//                            throw new Exception(methodKeyName + msgStr);
//                        }catch(Exception e1){
//                            e1.printStackTrace();
//                        }
//                    }else{
//                        e.printStackTrace();
//                    }
//                }catch(IllegalAccessException e){
//                    e.printStackTrace();
//                }catch(InvocationTargetException e){
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }

        /***
         * 根据keyName得值获取对应valueName的值
         * @param enums 要查的枚举类数组对象
         * @param keyName 相对key的名称（此名称也可指定枚举中的value或别的字段的变量名）
         * @param valueName 相对value的名称（keyName多对应的value的值）（此名称也可指定枚举中的value或别的字段的变量名）
         * @param byKey keyName对应的key值
         * @return
         */
        public static <T> T getValue(Enum[] enums, String keyName, String valueName, String byKey, Class<T> type){
            try{
                for(Enum enumObj : enums){
                    Field keyField = enumObj.getClass().getDeclaredField(keyName);
                    keyField.setAccessible(true);
                    Object o = keyField.get(enumObj);
                    if(o.equals(byKey)){
                        Field valueField = enumObj.getClass().getDeclaredField(valueName);
                        valueField.setAccessible(true);
                        return (T) valueField.get(enumObj);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class ObjectUtils{
        /**
         * 执行一个对象中的某个方法
         *
         * @param obj        对象
         * @param methodName 方法名称
         * @param parameters 将要执行的方法的参数
         * @return 方法的返回值
         */
        public static Object invokeMethod(Object obj, String methodName, Object... parameters){
            try{
                Class c = obj.getClass();
                Method[] mds = c.getMethods();
                for(Method method : mds){
                    if(method.getName().equals(methodName)){
                        return method.invoke(obj, parameters);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }


    }

    /***
     * map相关工具
     */
    public static class MapUtils{
        /**
         * 判断一个key值在map中是否存在
         *
         * @param key map中的key值
         * @param map map集合
         * @return [true:存在], [false:不存在]
         */
        public static boolean isKeyToMapBlank(Object key, Map<? extends Object, Object> map){
            return map.get(key) == null ? false : true;
        }

        public Map<Object, Object> putMap(){
            return null;
        }
    }

    /**
     * @author 王强 wangqiang@hushijie.com.cn
     * @version 创建时间：2017/08/11 11:16
     * RegularlyCheckUtils
     **/
    public static class RegularlyUtils{

        private final static String ERROR_MSG = "第%d个元素出错, 错误值:%s, 原因: ";

        public enum RegularlyEnum{

            LETTER_CHINESE_NUMBER_OTHER(
                    "%s只能输入字母，汉字，数字, -, _, [],%s",
                    "^[\\u4e00-\\u9fa5_\\-\\[\\]\\u2160-\\u216ba-zA-Z0-9]{%s,%s}$"
            ),
            LETTER_NUMBER(
                    "%s只能输入字母和数字组合成的字符串,%s",
                    "^[0-9a-zA-Z]{%s,%s}$"
            ),
            NUMBER_AND_ONE_DECIMAL(
                    "%s只能输入大于0且最多一位小数的数字字符串,%s",
                    "^[0-9]+([.][0-9]{1}){%s,%s}$"
            ),
            PHONE(
                    "手机号码格式不正确",
                    "^1[3|4|5|8|7][0-9]\\d{8}$"
            ),
            IS_BLANK(
                    "%s不能为空或者空字符串%s",
                    "[^\\s]{1,}"
            ),
            NUMBER(
                    "%s只支持数字",
                    "^\\d{1,}$"
            ),
            LETTER_CHINESE_NUMBER(
                    "%s只能输入字母，汉字，数字,%s",
                    "^\\s*$|^[\\u4e00-\\u9fa5a-zA-Z0-9]{%s,%s}$"
            ),
            LETTER_CHINESE_NUMBER_SYMBOL_GREEK(
                    "%s只能输入字母，汉字，数字, -, _, []",
                    "^[(Ⅰ)(Ⅱ)(Ⅲ)(Ⅳ)(Ⅴ)(Ⅵ)(Ⅶ)(Ⅷ)(Ⅸ)(Ⅹ)(Ⅺ)(Ⅻ)\\u3002\\uff1f\\uff01\\uff0c\\u3001\\uff1b\\uff1a" +
                            "\\u201c\\u201d\\u2018\\u2019\\uff08\\uff09\\u300a\\u300b\\u3008\\u3009\\u3010\\u3011" +
                            "\\u300e\\u300f\\u300c\\u300d\\ufe43\\ufe44\\u3014\\u3015\\u2026\\u2014\\uff5e\\ufe4f" +
                            "\\uffe5a-zA-Z\\u2460-\\u2473\\u4e00-\\u9fa5\\u000A\\u000D\\d\\-\\_\\[\\]\\(\\)=\\/\\," +
                            "\\.\\;\\:\\'\\\"]{%s,%s}$"
            ),
            TEXT_AND_LETTERS(
                    "只能输入文字和字母",
                    "^\\s*$|^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,10000}$"
            ),
            TEXT_AND_LETTERS_UNDERLINE(
                    "%s只能输入字母,数字或下划线",
                    "^\\s*$|^[\\u4e00-\\u9fa5a-zA-Z0-9_]{1,10000}$"
            );

            private String errorMsg;

            private String regex;

            private String[] params;

            private String[] msgParams;

            RegularlyEnum(String errorMsg, String regex){
                if(BaseUtils.isBlank(errorMsg)){
                    try{
                        throw new Exception("参数errorMsg值不能为null或空字符串");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                this.errorMsg = errorMsg;
                this.regex = regex;
            }

            /***
             *	初始化msg和regex中的占位符字段值
             * @param msgParams msg的占位符
             * @param regexParams 正则的参数
             * @return
             */
            public RegularlyEnum init(String[] msgParams, String[] regexParams){
                this.params = regexParams;
                this.msgParams = msgParams;
                return this;
            }

            private String getErrorMsg(){
                if(this.msgParams == null){
                    return this.errorMsg;
                }
                if(this.msgParams.length == 0){
                    return this.errorMsg;
                }
                return String.format(this.errorMsg, this.msgParams);
            }

            /***
             * 获取正则字符串，如果有占位符则输入占位符。
             * @return
             */
            private String getRegex(){
                if(this.params == null){
                    return this.regex;
                }
                if(this.params.length == 0){
                    return this.regex;
                }
                return String.format(this.regex, this.params);
            }
        }

        /**
         * 根据某个正则枚举校验一个字符串
         *
         * @param str           将要校验的字符串
         * @param regularlyEnum 正则规则枚举
         * @return 是否校验成功
         */
        public static boolean checkRegex(String str, RegularlyEnum regularlyEnum){
            return Pattern.matches(regularlyEnum.getRegex(), str == null ? "" : str);
        }

        /***
         * 校验后输出错误信息，如果正确则返回null
         * @param str
         * @param regularlyEnum
         * @return
         */
        public static String checkRegexReturnMsg(String str, RegularlyEnum regularlyEnum){
            if(!checkRegex(str, regularlyEnum)){
                return regularlyEnum.getErrorMsg();
            }
            return null;
        }

        /***
         * 校验某个正则枚举队列
         * @param list 将要校验的枚举队列
         * @param regularlyEnum 正则枚举对象
         * @param errorTemplate 发生错误后的提示模板,必须含有%d（错误下标）%s（错误值）字符。否则会抛出异常
         * @return 不符合正则的队列下标集合
         */
        public static List<String> checkRegexToList(List<String> list, RegularlyEnum regularlyEnum, String errorTemplate){
            List<String> errorList = new ArrayList<>();
            String errorMsg = RegularlyUtils.getErrorMsgTemplate(errorTemplate);
            int i = 1;
            for(String str : list){
                boolean checkResult = RegularlyUtils.checkRegex(str, regularlyEnum);
                if(!checkResult){
                    errorList.add(String.format(errorMsg, i, str) + regularlyEnum.getErrorMsg());
                }
                i++;
            }
            return errorList;
        }

        /***
         * 校验某个正则枚举队列
         * @param list 将要校验的枚举队列
         * @param regularlyEnum 正则枚举对象
         * @return 不符合正则的队列下标集合
         */
        public static List<String> checkRegexToList(List<String> list, RegularlyEnum regularlyEnum){
            return RegularlyUtils.checkRegexToList(list, regularlyEnum, null);
        }

        /***
         * 校验错误信息模板参数是否正确（ 发生错误后的提数模板,必须含有%d（错误下标）%s（错误值）字符。否则会抛出异常）
         * @param errorTemplate
         */
        private static void cacheErrorTemplate(String errorTemplate){
            String placeholderCheck = "参数errorTemplate中必须含有%s占位符";
            if(!errorTemplate.contains("%d")){
                try{
                    throw new Exception(String.format(placeholderCheck, "%d"));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            if(!errorTemplate.contains("%s")){
                try{
                    throw new Exception(String.format(placeholderCheck, "%s"));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        }

        private static String getErrorMsgTemplate(String errorTemplate){
            if(BaseUtils.isBlank(errorTemplate)){
                return RegularlyUtils.ERROR_MSG;
            }else{
                RegularlyUtils.cacheErrorTemplate(errorTemplate);
                return errorTemplate;
            }
        }






//		/**
//		 * 正则校验单个字符串数据
//		 * @param str
//		 * @param regularlyEnum
//		 * @param params
//		 * @return
//		 */
//		public static String checkRegexSingleString(String str, RegularlyEnum regularlyEnum,String... params) {
//
//			String resultMsg = null;
//			boolean checkItemScore = utils.BaseUtils.RegularlyUtils.checkRegex(str, regularlyEnum, params);
//			if (!checkItemScore) {
//				resultMsg = regularlyEnum.getErrorMsg();
//			}
//
//			return resultMsg;
//		}
    }

    public static void main(String[] args){
        JSONObject json = JSONObject.parseObject("{\"a20\": \"一般项目：上述车辆一般项目检验合格\", \"a21\": \"安全性能：上述车辆安全性能检验合格\", \"a22\": \"日期：\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t\\t2017年04月10日\", \"b4\": \"制造国:美国\", \"b5\": \"生产厂名:戴姆勒股份公司图斯卡鲁萨工厂\", \"b6\": \"生产日期（年/月）\"}");
        String a20 = (String) json.get("a20");
        System.out.println(a20);

//		String str = utils.BaseUtils.StringUtilsSon.addMarkToString("wangQiang", "_");
//		System.out.println(str);


//		String a = "abc_def_ghi_";
//		a = StringUtilsSon.markToHump(a, "_", 0);
//		System.out.println(a);
//		boolean bool = utils.BaseUtils.RegularlyUtils.checkRegex("hhjkhjkhjkhukhuihuihuihuihuihuihuihuihuihkhkhkjghjfghghdghgjkhklhjkhjkhu", RegularlyUtils.RegularlyEnum.LETTER_CHINESE_NUMBER.init(null, new String[]{"0", "30"}));
//		System.out.println(bool);
//			Account account = new Account();
//			account.setPhone("123123123");
//			AccountDTO accountDTO = new AccountDTO();
//			utils.BaseUtils.fastCopy(accountDTO, account);
//			System.out.println(accountDTO.getPhone());
        //\u4e00-\u9fa5_\-\[\]a-zA-Z0-9

        //boolean bool = utils.BaseUtils.RegularlyUtils.checkRegex("ⅦⅠ副书记啦俯拾地芥客服哈时间开好房间卡上的话费卡技术的尽快发货3789", RegularlyEnum.LETTER_CHINESE_NUMBER.init(new String[]{"", ""}, new String[]{"0", "30"}));
//			System.out.println(bool);
//		RegularlyUtils.RegularlyEnum[] regularlyEnums = {
//				RegularlyUtils.RegularlyEnum.LETTER_NUMBER.init(new String[]{"手机号", "长度为6"}, new String[]{"0", "6"}),
//				RegularlyUtils.RegularlyEnum.LETTER_CHINESE_NUMBER.init(new String[]{"科室名称", "长度为30"}, new String[]{"0", "30"})
//		};
//		List<String[]> list = new ArrayList<>();
//		list.add(new String[]{"42344a0", "fjklsajldjf"});
//		list.add(new String[]{"1233a", "3213123"});
//		List<String> msgs = checkRegexListToList(
//				list,
//				new RegularlyUtils.RegularlyEnum[]{
//						RegularlyUtils.RegularlyEnum.LETTER_NUMBER.init(new String[]{"手机号", "长度为6"},new String[]{"0", "6"}),
//						RegularlyUtils.RegularlyEnum.LETTER_CHINESE_NUMBER.init(new String[]{"科室名称", "长度为30"}, new String[]{"0", "30"})
//				},
//				new Integer[]{0, 1}
//		);
//		for (String msg : msgs) System.out.println(msg);
//
//
//		String errorMsg = utils.BaseUtils.RegularlyUtils.checkRegexReturnMsg(
//				null,
//				RegularlyUtils.RegularlyEnum.IS_BLANK.init(
//						new String[]{"科室名称", ""},
//						new String[]{"1", "30"}
//				)
//		);
//		System.out.println(errorMsg);
//		System.out.println(String.format("sdjklf%s", "%s"));
//
//		List<String> list2 = new ArrayList<>();
//		list2.add("3297489");
//		list2.add("7897");
//		list2.add("jkl343");
//		List<String> errorMsgList = utils.BaseUtils.RegularlyUtils.checkRegexToList(list2, RegularlyUtils.RegularlyEnum.PHONE);
//		for (String errorMsg2 : errorMsgList)
//			System.out.println(errorMsg2);
    }

    public static class JsonUtils{
        public static Map<String, Object> updateMapKeyToHump(Map<String, Object> map, String mark){
            Map<String, Object> returnMap = new HashMap<>();
            for(Map.Entry<String, Object> entry : map.entrySet()){
                String keyName = entry.getKey();
                keyName = StringUtilsSon.markToHump(keyName, mark, 0);
                returnMap.put(keyName, entry.getValue());
            }
            return returnMap;
        }

        /***
         * 将map中的key的名称修改为根据某个标记编辑成驼峰的形式，并通过jsonUtil返回成class对象
         * @param map map集合
         * @param mark 标记
         * @param type 返回值类型
         * @param <T>
         * @return
         */
        public static <T> T jsonToHumpToObject(Map<String, Object> map, String mark, Class<T> type){
            return JsonUtils.jsonObjectToObject(JsonUtils.updateMapKeyToHump(map, mark), type);
        }

        /***
         * 将map中的key的名称修改为根据某个标记编辑成驼峰的形式，并通过jsonUtil返回成class对象
         * @param list list集合
         * @param mark 标记
         * @param type 返回值类型
         * @param <T>
         * @return
         */
        public static <T> List<T> jsonToHumpToList(List<Map<String, Object>> list, String mark, Class<T> type){
            List<T> returnList = new ArrayList<>();
            for(Map<String, Object> map : list){
                returnList.add(JsonUtils.jsonToHumpToObject(map, mark, type));
            }
            return returnList;
        }


        /***
         * 将一个List转为
         * @param list
         * @param type
         * @param <T>
         * @return
         */
        public static <T> List<T> jsonArrayToList(List<? extends Object> list, Class<T> type){
            return JSONArray.parseArray(JSONArray.toJSONString(list), type);
        }

        public static <T> T jsonObjectToObject(Object object, Class<T> type){
            return JSONObject.parseObject(JSONObject.toJSONString(object), type);
        }


    }

}