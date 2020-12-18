package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;

public enum LearningCode implements ResultCode {
    LEARNING_GETMEDIA_ERROR(false,26001,"获取视频播放地址出错！"),
    CHOOSECOURSE_USERISNULL(false,26002,"选课用户不未登录！"),
    CHOOSECOUSER_NOHAVE(false,26003,"用戶沒有选课！"),
    CHOOSECO_ISEXPIRE(false,26004,"选课已结束或不存在！"),
    CHOOSECO_NOOPEN(false,26004,"该课程为收费课程！"),
    CHOOSECOURSE_TASKISNULL(false,26010,"选课任务不存在！"),

    COURSE_COMMENT_ISNULL(false,26020,"课程评论不存在！");
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
