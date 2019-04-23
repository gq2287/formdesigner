package com.wskj.project.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 全局返回对象
 */
public  class  ResponseResult {
    public static final int OK=0;
    @ApiModelProperty("返回码 非0即失败")
    private int code;
    @ApiModelProperty("消息提示")
    private String msg;
    @ApiModelProperty("返回的数据")
    private Object data;
    @ApiModelProperty("是否成功")
    private boolean success;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResponseResult() {
    }

    public ResponseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public ResponseResult(int code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    public ResponseResult(int code, String msg, Object data, boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }
}
