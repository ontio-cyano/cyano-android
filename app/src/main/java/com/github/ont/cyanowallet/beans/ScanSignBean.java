package com.github.ont.cyanowallet.beans;

public class ScanSignBean {

    /**
     * action : signMessage
     * version : v1.0.0
     * id : 10ba038e-48da-487b-96e8-8d3b99b6d18a
     * params : {"type":"ontid or account","message":"helloworld","expire":1546415363,"callback":"http://127.0.0.1:80/login/callback"}
     */

    private String action;
    private String version;
    private String id;
    private ParamsBean params;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * type : ontid or account
         * message : helloworld
         * expire : 1546415363
         * callback : http://127.0.0.1:80/login/callback
         */

        private String type;
        private String message;
        private int expire;
        private String callback;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public String getCallback() {
            return callback;
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }
    }
}
