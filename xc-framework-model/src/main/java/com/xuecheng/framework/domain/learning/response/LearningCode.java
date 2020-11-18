package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;

public enum LearningCode implements ResultCode {
    LEARNING_GETMEDIA_ERROR(false,26001,"获取视频播放地址出错！"),
    CHOOSECOURSE_USERISNULL(false,26002,"选课用户不存在！"),
    CHOOSECOURSE_TASKISNULL(false,26003,"选课任务不错在！");
    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
