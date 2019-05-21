package com.utils.net;

import android.content.Context;
import android.text.TextUtils;

import com.utils.LogcatFileHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

/**
 * Created by jiongfang on 2018/9/25.
 */
public class WebSocketDemo {

    public WebSocketDemo(Context context) {
        mContext = context;
    }

    private Context mContext;


    //潘多拉相关参数
    private final static String OP_LOGIN = "login";
    private final static String OP_REPORTSTATUS = "reportstatus";
    private final static String OP_GETIDENTIFY = "getidentifyingcode";
    private final static String OP_CANCEBIND = "cancelbind";
    private final static String OP_QUERYBIND = "querybindinfo";
    private final static String APPID = "2846";    //酷小狗"2846"    潘多拉 "2806"       //fail酷小狗2  "2869"  酷小狗2: kFwO3LmT7KQqThOUr3T1EuRR31HljPid
    private final static String APPKEY = "jnF1JGXQ8lAvGBPNp64YJpgGxAfLK8YH";  //酷小狗jnF1JGXQ8lAvGBPNp64YJpgGxAfLK8YH   潘多拉ZxFSV7B1x2maIQjzTQ7FCjg6cCsdeR1y
    private final static String CLIENTVER = "500";
    private final static String MID = "888";

    private int callid = 0;
    private int clienttime = 0;
    private String key = "";
    private String wechatid = "";

    //相关网络接口
    //public final static String URL = "ws://pandora.service.kugou.com/v1/pandora?mid="+MID;  //潘多拉
    public final static String URL = "ws://hwrelay.kugou.com/v1/pandora?mid=" + MID;       //酷小狗


    private JSONObject InitStatusifoJson(String mode, String playstatus,
                                         int volumecur, int volumemax, int soundeffect, int tokenvalid) {
        JSONObject statusinfo = new JSONObject();
        try {
            statusinfo.put("mode", mode);
            statusinfo.put("playstatus", playstatus);
            statusinfo.put("volumecur", volumecur);
            statusinfo.put("volumemax", volumemax);
            statusinfo.put("soundeffect", soundeffect);
            statusinfo.put("tokenvalid", tokenvalid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusinfo;
    }

    private JSONObject InitSonginfoJson(String radioname, int radiotype, int radioid, int songid,
                                        int scid, String songname, String songhash, int favorite,
                                        int timelength, String remark, int timeoffset, String singername, int radioindex) {
        JSONObject songinfo = new JSONObject();
        try {
            songinfo.put("radioname", radioname);
            songinfo.put("radiotype", radiotype);
            songinfo.put("radioid", radioid);
            songinfo.put("songid", songid);
            songinfo.put("scid", scid);
            songinfo.put("songname", songname);
            songinfo.put("songhash", songhash);
            songinfo.put("favorite", favorite);
            songinfo.put("timelength", timelength);
            songinfo.put("remark", remark);
            songinfo.put("timeoffset", timeoffset);
            songinfo.put("singername", singername);
            songinfo.put("radioindex", radioindex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return songinfo;
    }

    private static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    private static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getMD5String(int clienttime) {
        String strClienttime = String.valueOf(clienttime);
        String str = APPID + APPKEY + CLIENTVER + strClienttime;
        String md5 = MD5(str);
        return md5;
    }

    //-------------------------------------------
    //登录请求包
    public JSONObject RequestLoginMsg() {
        JSONObject requestLoginJson = new JSONObject();
        try {
            requestLoginJson.put("op", OP_LOGIN);
            requestLoginJson.put("callid", ++callid);
            requestLoginJson.put("appid", APPID);
            requestLoginJson.put("clientver", CLIENTVER);
            requestLoginJson.put("mid", MID);
            clienttime = (int) (System.currentTimeMillis() / 1000);
            requestLoginJson.put("clienttime", clienttime);
            key = getMD5String(clienttime);
            requestLoginJson.put("key", key);
            requestLoginJson.put("wechatid", wechatid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestLoginJson;
    }

    // 心跳上报包
    public JSONObject RequestReportstatusMsg() {
        // LogcatFileHelper.w("Jiong>>","心跳  wechatid="+wechatid);
        JSONObject requestReportstatusJson = new JSONObject();
        try {
            requestReportstatusJson.put("op", OP_REPORTSTATUS);
            requestReportstatusJson.put("callid", ++callid);
            requestReportstatusJson.put("appid", APPID);
            requestReportstatusJson.put("clientver", CLIENTVER);
            requestReportstatusJson.put("mid", MID);
            clienttime = (int) (System.currentTimeMillis() / 1000);
            requestReportstatusJson.put("clienttime", clienttime);
            key = getMD5String(clienttime);
            requestReportstatusJson.put("key", key);
            requestReportstatusJson.put("wechatid", wechatid);
            JSONObject statusinfo = InitStatusifoJson("radio", "pause", 10, 15, 1, 1);
            JSONObject songinfo = InitSonginfoJson("流行", 1, 1, 1, 1, "春天里", "12345",
                    0, 300, "xxx", 0, "汪峰", 1);
            requestReportstatusJson.put("statusinfo", statusinfo);
            requestReportstatusJson.put("songinfo", songinfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestReportstatusJson;
    }

    //申请验证码
    public JSONObject RequestGetindentifyMsg() {
        JSONObject requestGetindentifyJson = new JSONObject();
        try {
            requestGetindentifyJson.put("op", OP_GETIDENTIFY);
            requestGetindentifyJson.put("callid", ++callid);
            requestGetindentifyJson.put("appid", APPID);
            requestGetindentifyJson.put("clientver", CLIENTVER);
            requestGetindentifyJson.put("mid", MID);
            clienttime = (int) (System.currentTimeMillis() / 1000);
            requestGetindentifyJson.put("clienttime", clienttime);
            key = getMD5String(clienttime);
            requestGetindentifyJson.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestGetindentifyJson;
    }

    //取消绑定
    public JSONObject RequestCancebindMsg() {
        JSONObject requestCancebindJson = new JSONObject();
        try {
            requestCancebindJson.put("op", OP_CANCEBIND);
            requestCancebindJson.put("callid", ++callid);
            requestCancebindJson.put("appid", APPID);
            requestCancebindJson.put("clientver", CLIENTVER);
            requestCancebindJson.put("mid", MID);
            clienttime = (int) (System.currentTimeMillis() / 1000);
            requestCancebindJson.put("clienttime", clienttime);
            key = getMD5String(clienttime);
            requestCancebindJson.put("key", key);
            requestCancebindJson.put("wechatid", wechatid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestCancebindJson;
    }

    //查询绑定
    public JSONObject RequestQuerybingMsg() {
        JSONObject requestQuerybingJson = new JSONObject();
        try {
            requestQuerybingJson.put("op", OP_QUERYBIND);
            requestQuerybingJson.put("callid", ++callid);
            requestQuerybingJson.put("appid", APPID);
            requestQuerybingJson.put("clientver", CLIENTVER);
            requestQuerybingJson.put("mid", MID);
            clienttime = (int) (System.currentTimeMillis() / 1000);
            requestQuerybingJson.put("clienttime", clienttime);
            key = getMD5String(clienttime);
            requestQuerybingJson.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestQuerybingJson;
    }

    //解析 回包
    public JsonResoponPara ParseResponseJson(String jsonStr) {
        JsonResoponPara jsonResoponPara = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            jsonResoponPara = new JsonResoponPara();
            if (jsonObject.has("op")) {
                jsonResoponPara.op = (String) jsonObject.get("op");
            }
            if (jsonObject.has("status")) {
                jsonResoponPara.status = (int) jsonObject.get("status");
            }
            if (jsonObject.has("error_code")) {
                jsonResoponPara.error_code = (int) jsonObject.get("error_code");
            }
            if (jsonObject.has("errmsg")) {
                jsonResoponPara.errmsg = (String) jsonObject.get("errmsg");
            }
            JSONObject jsonData = null;
            if (jsonObject.has("data") && !TextUtils.isEmpty(jsonObject.get("data").toString())) {
                jsonData = (JSONObject) jsonObject.get("data");
                if (jsonData != null) {
                    if (jsonData.has("validity")) jsonResoponPara.validity = (int) jsonData.get("validity");
                    if (jsonData.has("identifyingcode"))
                        jsonResoponPara.identifyingcode = (String) jsonData.get("identifyingcode");
                    if (jsonData.has("online")) jsonResoponPara.online = (int) jsonData.get("online");
                    if (jsonData.has("wechatid")) jsonResoponPara.wechatid = (String) jsonData.get("wechatid");
                    if (jsonData.has("pandoramid")) jsonResoponPara.pandoramid = (String) jsonData.get("pandoramid");
                    // LogcatFileHelper.w("Jiong>>","flag1>>解析出  wechatid="+jsonResoponPara.wechatid+"    pandoramid="+jsonResoponPara.pandoramid);
                }
            }
            if (jsonObject.has("pandoramid")) {
                jsonResoponPara.pandoramid = (String) jsonObject.get("pandoramid");
            }
            if (jsonObject.has("wechatid")) {
                jsonResoponPara.wechatid = (String) jsonObject.get("wechatid");
            }
            //解析Control Json
            if (jsonObject.has("action")) {
                jsonResoponPara.action = (String) jsonObject.get("action");
            }
            if (jsonObject.has("insertsonghash")) {
                jsonResoponPara.insertsonghash = (String) jsonObject.get("insertsonghash");
            }
            if (jsonObject.has("insertsongname")) {
                jsonResoponPara.insertsongname = (String) jsonObject.get("insertsongname");
            }
            if (jsonObject.has("channel_id")) {
                jsonResoponPara.channel_id = (String) jsonObject.get("channel_id");
            }
            if (jsonObject.has("channel_name")) {
                jsonResoponPara.channel_name = (String) jsonObject.get("channel_name");
            }

            //判断逻辑赋予wechatid
            if (MID.equals(jsonResoponPara.pandoramid) && !TextUtils.isEmpty(jsonResoponPara.wechatid)) {
                wechatid = jsonResoponPara.wechatid;
                LogcatFileHelper.w("Jiong>>", "解析出  wechatid=" + wechatid);
            }
            //取消绑定成功后 考虑清除wechatid
            //1.接收到取消绑定通知
            if ("cancelbindnotice".equals(jsonResoponPara.op) && MID.equals(jsonResoponPara.pandoramid)) {
                wechatid = "";
            }
            //2.主动取消绑定回包
            if ("cancelbind-rsp".equals(jsonResoponPara.op) && jsonResoponPara.status == 1) {
                wechatid = "";
            }
            //3.查询绑定信息返回
            if ("querybindinfo-rsp".equals(jsonResoponPara.op) && jsonResoponPara.status == 0) {
                wechatid = "";
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
//        LogcatFileHelper.w("Jiong>>","接收  wechatid="+wechatid);
        return jsonResoponPara;  //后面的使用 control判断pandoramid  其他判断status
    }

    //回包 参数类
    public class JsonResoponPara {
        public String op = "";
        public int status = -1;
        public int error_code = -1;
        public String errmsg = "";
        public String pandoramid = "";
        public String wechatid = "";
        public int online = -1;
        public int validity = -1;
        public String identifyingcode = "";

        public String action = "";
        public String insertsonghash = "";
        public String insertsongname = "";
        public String channel_id = "";
        public String channel_name = "";
    }

}
