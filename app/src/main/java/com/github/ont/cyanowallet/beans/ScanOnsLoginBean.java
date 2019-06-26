package com.github.ont.cyanowallet.beans;

public class ScanOnsLoginBean {

    /**
     * action : onsLogin
     * version : v1.0.0
     * id : 66124900-5381-4442-b93e-8166ac1478be
     * params : {"domain":"localhost","dappName":"dapp Name","dappIcon":"dapp Icon","message":"hello 1561520724000","callback":"http://192.168.3.121:7878/api/v1/message/callback"}
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
         * domain : localhost
         * dappName : dapp Name
         * dappIcon : dapp Icon
         * message : hello 1561520724000
         * callback : http://192.168.3.121:7878/api/v1/message/callback
         */

        private String domain;
        private String dappName;
        private String dappIcon;
        private String message;
        private String callback;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getDappName() {
            return dappName;
        }

        public void setDappName(String dappName) {
            this.dappName = dappName;
        }

        public String getDappIcon() {
            return dappIcon;
        }

        public void setDappIcon(String dappIcon) {
            this.dappIcon = dappIcon;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCallback() {
            return callback;
        }

        public void setCallback(String callback) {
            this.callback = callback;
        }
    }
}
