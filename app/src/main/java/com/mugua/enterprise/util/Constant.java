package com.mugua.enterprise.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.mugua.enterprise.bean.HomeBean;
import com.mugua.enterprise.bean.LoginBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/29.
 */

public class Constant {
    public static String accessKeyId = "";//子accessKeyId
    public static String accessKeySecret = "";//子accessKeySecret
    public static String securityToken = "";//STS 的securityToken
    public static String expriedTime = "";//STS的过期时间
    public static boolean open;
    public static boolean open2;
    public static boolean open4;
    public static boolean open5;
    public static String home_text = "";
    public static String me_text = "";

    public static String jgusername;
    public static String jgnickname;
    public static String jgappKey;

    public static String path;
    public static String apkUrl;

    //appid 微信分配的公众账号ID
    public static String APP_ID = "";

    //商户号 微信分配的公众账号ID
    public static String MCH_ID = "";

    //  API密钥，在商户平台设置
    public static final  String API_KEY= "352f2e89b908c576b5d6af79b0fae9dd";

//    public static final String URL = "https://api.ikamang.com/";
//    public static final String URL = "http://39.106.27.65:8081/";
    public static final String URL = "http://192.168.1.111:9010/";
//    public static final String URL = "http://39.106.27.65:8083/";

    public static LoginBean user = null;

    public static final String URL_login= URL + "app/user/login";//登录
    public static final String URL_regist= URL + "app/user/regist";//注册
    public static final String URL_forgetPassword= URL + "app/user/forgetPassword";//修改密码
    public static final String URL_home= URL + "app/index";//首页数据
    public static final String URL_yzm= URL + "app/sendMessage/";//注册发送验证码

    public static final String URL_search= URL + "app/qichacha/searchIndex";//企查查
    public static final String URL_addGoods= URL + "app/goods/addGoods";//商品上传
    public static final String URL_upload= URL + "upload";//图片上传
    public static final String URL_bulidCard= URL + "app/card/bulidCard";//名片上传

    public static final String URL_getCardShow= URL + "app/card/getCardShow";//用户名片数据

    public static final String URL_getList= URL + "app/sayone/getList";//一言
    public static final String URL_getDetail= URL + "app/sayone/getDetail";//一言详情

    public static final String URL_brokenlist= URL + "app/brokenlist/getList";//失信名单全部
    public static final String URL_searchList= URL + "app/brokenlist/searchList";//失信名单关键字查询
    public static final String URL_getdetail= URL + "app/brokenlist/getDetail";//失信名单获取详情

    public static final String URL_turnLogo= URL + "app/user/";//修改个人信息
    public static final String URL_getUserInfo= URL + "app/user/getUserInfos";//获取个人信息

    public static final String URL_getDetailqcc= URL + "app/qichacha/getDetail";//企查查搜索详情
    public static final String URL_getCardInfo= URL + "app/card/getCardInfo";//企查查搜索详情（自己）

    public static final String URL_getGoods= URL + "app/goods/getGoods";//产品详情（自己）

    public static final String URL_turnIdentification= URL + "app/user/turnIdentification";//改变认证状态
    public static final String URL_identification= URL + "app/user/identification";//认证

    public static final String URL_addAsk= URL + "app/ask/addAsk";

    public static final String URL_roleSessionName= URL + "videoUpload/getCredentials?roleSessionName=";//获取key

    public static final String URL_newsDetails= URL + "app/newsDetails?id=";//获取key

    public static final String URL_addFeedback= URL + "app/feedback/addFeedback";//用户反馈

    public static final String URL_goPrivacy= URL + "app/privacy/goPrivacy";//隐私策略

    public static final String URL_judge= URL + "app/user/judge";//判断是否授权过

    public static final String URL_fastLogin= URL + "app/user/fastLogin";//快速登录

    public static final String URL_updateInfo= URL + "app/updateInfo";//版本更新

    public static final String URL_vote= URL + "vote/add";//huod分享

    public static final String URL_share= URL + "vote/share?uid=";//huod分享

//    public static final String URL_activeRule= URL + "app/activeRule";//活动规则

    public static final String URL_wxzfhq= URL + "mgshop/pay/wxPay";//微信支付获取

    public static final String URL_zfbzfhq= URL + "mgshop/pay/aliPay";//支付宝支付获取

    public static final String URL_ylindex= URL + "mgshop/index";//有料获取

    public static final String URL_getAddr= URL + "mgshop/getAddr";//地址

    public static final String URL_addAddr= URL + "mgshop/addAddr";//添加地址

    public static final String URL_updateDefault= URL + "mgshop/updateDefault";//修改默认地址地址

    public static final String URL_deleteAddr= URL + "mgshop/deleteAddr";//删除地址

    public static final String URL_updateAddr= URL + "mgshop/updateAddr";//修改地址

    public static final String URL_getAll= URL + "mgshop/order/getAll";//全部订单

    public static final String URL_goods= URL + "mgshop/goods/addGoods";//库存上传

    public static final String URL_getByUid= URL + "mgshop/goods/getByUid";//我发布的

    public static final String URL_updateGoods= URL + "mgshop/goods/updateGoods"; //修改商品

    public static final String URL_deleteGoods= URL + "mgshop/goods/deleteGoods"; //删除我发布的

    public static final String URL_deleteOrder= URL + "mgshop/order/deleteOrder"; //删除订单地址

    public static final String URL_getLimit= URL + "mgshop/goods/getLimit"; //历史限购数量

//    public static final String URL_activity= URL + "app/activity";//活动

    /**
     *
     *  @Description    : 这个包名的程序是否在运行
     *  @Method_Name    : isRunningApp
     *  @param context 上下文
     *  @param packageName 判断程序的包名
     *  @return 必须加载的权限
     *      <uses-permission android:name="android.permission.GET_TASKS" />
     *  @return         : boolean
     *  @Creation Date  : 2014-10-31 下午1:14:15
     *  @version        : v1.00
     *  @Author         : JiaBin

     *  @Update Date    :
     *  @Update Author  : JiaBin
     */
    public static boolean isRunningApp(Context context, String packageName) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }
}
