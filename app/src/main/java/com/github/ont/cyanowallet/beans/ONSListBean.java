package com.github.ont.cyanowallet.beans;

import java.util.List;

public class ONSListBean {

    /**
     * action : getOnsList
     * code : 0
     * msg : SUCCESS
     * result : ["eri.on.ont"]
     * version : v1
     */

    private String action;
    private int code;
    private String msg;
    private String version;
    private List<String> result;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }
}
