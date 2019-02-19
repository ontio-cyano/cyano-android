package com.github.ont.cyanowallet.beans;

import java.util.List;

public class DDOBean {

    /**
     * OntId : did:ont:AZzyt1QtaYJ71bpvBX8pyG7m5X3e84Ejbz
     * Owners : [{"Curve":"P256","Type":"ECDSA","PubKeyId":"did:ont:AZzyt1QtaYJ71bpvBX8pyG7m5X3e84Ejbz#keys-1","Value":"022b10ffdbce28330aaca3fba3d5151147163c2688c4d2671cb8f55ffc1f61ba15"},{"Curve":"P256","Type":"ECDSA","PubKeyId":"did:ont:AZzyt1QtaYJ71bpvBX8pyG7m5X3e84Ejbz#keys-2","Value":"035384561673e76c7e3003e705e4aa7aee67714c8b68d62dd1fb3221f48c5d3da0"}]
     * Recovery : AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve
     * Attributes : [{"Type":"String","Value":"test","Key":"test"}]
     */

    private String OntId;
    private String Recovery;
    private List<OwnersBean> Owners;
    private List<AttributesBean> Attributes;

    public String getOntId() {
        return OntId;
    }

    public void setOntId(String OntId) {
        this.OntId = OntId;
    }

    public String getRecovery() {
        return Recovery;
    }

    public void setRecovery(String Recovery) {
        this.Recovery = Recovery;
    }

    public List<OwnersBean> getOwners() {
        return Owners;
    }

    public void setOwners(List<OwnersBean> Owners) {
        this.Owners = Owners;
    }

    public List<AttributesBean> getAttributes() {
        return Attributes;
    }

    public void setAttributes(List<AttributesBean> Attributes) {
        this.Attributes = Attributes;
    }

    public static class OwnersBean {
        /**
         * Curve : P256
         * Type : ECDSA
         * PubKeyId : did:ont:AZzyt1QtaYJ71bpvBX8pyG7m5X3e84Ejbz#keys-1
         * Value : 022b10ffdbce28330aaca3fba3d5151147163c2688c4d2671cb8f55ffc1f61ba15
         */

        private String Curve;
        private String Type;
        private String PubKeyId;
        private String Value;

        public String getCurve() {
            return Curve;
        }

        public void setCurve(String Curve) {
            this.Curve = Curve;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getPubKeyId() {
            return PubKeyId;
        }

        public void setPubKeyId(String PubKeyId) {
            this.PubKeyId = PubKeyId;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }
    }

    public static class AttributesBean {
        /**
         * Type : String
         * Value : test
         * Key : test
         */

        private String Type;
        private String Value;
        private String Key;

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }

        public String getKey() {
            return Key;
        }

        public void setKey(String Key) {
            this.Key = Key;
        }
    }
}
