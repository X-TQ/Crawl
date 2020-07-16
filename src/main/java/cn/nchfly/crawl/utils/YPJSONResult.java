package cn.nchfly.crawl.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Title: STJSONResult.java
 * @Package com.yuepeng.common.utils
 * @Description: 自定义响应数据结构
 * 				本类可提供给 H5/ios/安卓/公众号/小程序 使用
 * 				前端接受此类数据（json object)后，可自行根据业务去实现相关功能
 * 
 * 				200：表示成功
 * 				500：表示错误，错误信息在msg字段中
 * 				501：bean验证错误，不管多少个错误都以map形式返回
 * 				502：拦截器拦截到用户token出错
 * 				555：异常抛出信息
 * @Copyright: Copyright (c) 2020
 * @version V1.0
 */
public class YPJSONResult {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    public static YPJSONResult build(Integer status, String msg, Object data) {
        return new YPJSONResult(status, msg, data);
    }
    
    public static YPJSONResult ok(Object data) {
        return new YPJSONResult(data);
    }

    public static YPJSONResult ok() {
        return new YPJSONResult(null);
    }
    
    public static YPJSONResult errorMsg(String msg) {
        return new YPJSONResult(500, msg, null);
    }
    
    public static YPJSONResult errorMap(Object data) {
        return new YPJSONResult(501, "error", data);
    }
    
    public static YPJSONResult errorTokenMsg(String msg) {
        return new YPJSONResult(502, msg, null);
    }
    
    public static YPJSONResult errorException(String msg) {
        return new YPJSONResult(555, msg, null);
    }


    public YPJSONResult() {

    }

    public YPJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public YPJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
