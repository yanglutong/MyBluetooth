package com.example.mybluetooth.bean;

/**APP_ARM_PARAM_CFG_REQ
 * @author: 小杨同志
 * @date: 2021/12/16
 */
public class APP_ARM_PARAM_CFG_REQ {
    private String MSG_TYPE;//1 消息类型，此处为0xa0
    private String Mode;//单兵工作模式。0:TDD  1:FDD
    private String ArFcn;//3-5  单兵工作频点，其中字节3作为频点的最高8bit，字节5作为频点的最低8bit
    private String PCI_PN;// 6-7 该值仅TDD、 FDD、CDMA模式使用，若为GSM模式，该字段预留。其中字节6为PCI的高8bit，字节7为PCI的低8bit     取值范围为0-503
    private String Yu_Liu;//预留 8

    public APP_ARM_PARAM_CFG_REQ() {
    }

    public APP_ARM_PARAM_CFG_REQ(String MSG_TYPE, String mode, String arFcn, String PCI_PN, String yu_Liu) {
        this.MSG_TYPE = MSG_TYPE;
        Mode = mode;
        ArFcn = arFcn;
        this.PCI_PN = PCI_PN;
        Yu_Liu = yu_Liu;
    }

    public String getMSG_TYPE() {
        return MSG_TYPE;
    }

    public void setMSG_TYPE(String MSG_TYPE) {
        this.MSG_TYPE = MSG_TYPE;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getArFcn() {
        return ArFcn;
    }

    public void setArFcn(String arFcn) {
        ArFcn = arFcn;
    }

    public String getPCI_PN() {
        return PCI_PN;
    }

    public void setPCI_PN(String PCI_PN) {
        this.PCI_PN = PCI_PN;
    }

    public String getYu_Liu() {
        return Yu_Liu;
    }

    public void setYu_Liu(String yu_Liu) {
        Yu_Liu = yu_Liu;
    }
    public String  getAllSendMsg(){//获取全部发送的数据
        StringBuffer bf = new StringBuffer();
        if(MSG_TYPE!=null&&!MSG_TYPE.equals("")){
            bf.append(MSG_TYPE);
            bf.append(Mode);
            bf.append(ArFcn);
            bf.append(PCI_PN);
            bf.append(Yu_Liu);
        }
        return bf.toString();
    }
    @Override
    public String toString() {
        return "APP_ARM_PARAM_CFG_REQ{" +
                "MSG_TYPE='" + MSG_TYPE + '\'' +
                ", Mode='" + Mode + '\'' +
                ", ArFcn='" + ArFcn + '\'' +
                ", PCI_PN='" + PCI_PN + '\'' +
                ", Yu_Liu='" + Yu_Liu + '\'' +
                '}';
    }
}
