package com.example.mybluetooth.bean;

/**ARM_APP_PARAM_CFG_RESP
 * @author: 小杨同志
 * @date: 2021/12/16
 */
public class ARM_APP_PARAM_CFG_RESP {
    private String MSG_TYPE;//消息类型，此处为0xa1 1
    private String Result;// 参数配置结果 0:success 1:fail
    private String cause;//失败原因： 1：参数设置错误 2：射频配置失败 3：预留
    private String Reserved; //4-8字节 0xff

    public ARM_APP_PARAM_CFG_RESP(String MSG_TYPE, String result, String cause, String reserved) {
        this.MSG_TYPE = MSG_TYPE;
        Result = result;
        this.cause = cause;
        Reserved = reserved;
    }

    public String getMSG_TYPE() {
        return MSG_TYPE;
    }

    public void setMSG_TYPE(String MSG_TYPE) {
        this.MSG_TYPE = MSG_TYPE;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getReserved() {
        return Reserved;
    }

    public void setReserved(String reserved) {
        Reserved = reserved;
    }

    @Override
    public String toString() {
        return "ARM_APP_PARAM_CFG_RESP{" +
                "MSG_TYPE='" + MSG_TYPE + '\'' +
                ", Result='" + Result + '\'' +
                ", cause='" + cause + '\'' +
                ", Reserved='" + Reserved + '\'' +
                '}';
    }
}
