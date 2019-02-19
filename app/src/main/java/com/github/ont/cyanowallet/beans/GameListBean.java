package com.github.ont.cyanowallet.beans;

import java.util.List;

public class GameListBean {

    /**
     * action : apps
     * error : 0
     * desc : SUCCESS
     * result : {"banner":[{"image":"https://info.ont.io/listtriones/en","name":"banner 2","link":"https://onto.app/"}],"apps":[{"name":"H5 Demo","icon":"https://raw.githubusercontent.com/ontology-cyano/mobile-dapp-demo/master/src/assets/logo.png","link":"http://101.132.193.149:5000/#/"}]}
     * version : v1.0.0
     */

    private String action;
    private int error;
    private String desc;
    private ResultBean result;
    private String version;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class ResultBean {
        private List<BannerBean> banner;
        private List<AppsBean> apps;

        public List<BannerBean> getBanner() {
            return banner;
        }

        public void setBanner(List<BannerBean> banner) {
            this.banner = banner;
        }

        public List<AppsBean> getApps() {
            return apps;
        }

        public void setApps(List<AppsBean> apps) {
            this.apps = apps;
        }

        public static class BannerBean {
            /**
             * image : https://info.ont.io/listtriones/en
             * name : banner 2
             * link : https://onto.app/
             */

            private String image;
            private String name;
            private String link;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }
        }

        public static class AppsBean {
            /**
             * name : H5 Demo
             * icon : https://raw.githubusercontent.com/ontology-cyano/mobile-dapp-demo/master/src/assets/logo.png
             * link : http://101.132.193.149:5000/#/
             */

            private String name;
            private String icon;
            private String link;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }
        }
    }
}
