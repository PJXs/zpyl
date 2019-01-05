package com.hankexu.bean;

/**
 * Created by hanke on 2015-10-23.
 *
 * 信息数据实体
 */
public class Sms {

    private String fromAddress;
    private String body;
    private String datetime;

    public Sms(String fromAddress, String body, String datetime) {
        setFromAddress(fromAddress);
        setBody(body);
        setDatetime(datetime);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datatime) {
        this.datetime = datatime;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}
