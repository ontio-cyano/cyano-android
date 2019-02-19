package com.github.ont.cyanowallet.beans;

import java.util.List;

public class Oep4ListBean {

    /**
     * ContractList : [{"Description":"contractsDescriptionTest","Symbol":"TNT","CreateTime":1530316800,"ABI":"{\"contractHash\": \"2a9cc8a5d0644283e7d7705abe5bbcb979c9bb03\"}","Creator":"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ","TotalSupply":1000000000,"Decimals":8,"Code":"6a55c36a54c3936a00527ac462c8ff6161616a53c36c7566","ContractHash":"49f0908f08b3ebce1e71dc5083cb9a8a54cc4a24","Name":"TNT coin","Logo":"https://luckynumber.one/index/img/logo.png","OngCount":"0.000000000","UpdateTime":1545209968,"Addresscount":8,"ContactInfo":"{\"Website\":\"https://github.com/ontio\"}","OntCount":"0.000000000","TxCount":851},{"Description":"contractsDescription","Symbol":"OEP","CreateTime":1530316800,"ABI":"","Creator":"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ","TotalSupply":10000000000,"Decimals":8,"Code":"","ContractHash":"9f612aff420d11dc781be892545346607d13fd8f","Name":"OEP4TEST","Logo":"","OngCount":"0.000000000","Addresscount":0,"ContactInfo":"","OntCount":"0.000000000","TxCount":0},{"Description":"contractsDescription","Symbol":"HP","CreateTime":1530316800,"ABI":"","Creator":"AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ","TotalSupply":10000000000,"Decimals":8,"Code":"","ContractHash":"e814f496c5eb94b769210be13d319b6177c0c9ac","Name":"HP","Logo":"","OngCount":"0.280000000","Addresscount":11,"ContactInfo":"","OntCount":"7018.000000000","TxCount":24},{"Description":"tt","Symbol":"MYT","CreateTime":1544706198,"ABI":"","Creator":"AY2txUFHgu6ovzNia1LaBUaxCFEcXy4Y97","TotalSupply":1000000000,"Decimals":8,"Code":"","ContractHash":"8cae506e0c37359626e341e44a2ab166055bec78","Name":"","Logo":"","OngCount":"0.000000000","UpdateTime":1544706198,"Addresscount":6,"ContactInfo":"","OntCount":"0.000000000","TxCount":10},{"Description":"LUCKY token is used for Lucky series games","Symbol":"LCY","CreateTime":1545639758,"ABI":"{\"contractHash\":\"25277b421a58cfc2ef5836767e54eb7abdd31afd\",\"abi\":{\"CompilerVersion\":\"1.0.2\",\"hash\":\"25277b421a58cfc2ef5836767e54eb7abdd31afd\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"init\",\"parameters\":[]},{\"name\":\"name\",\"parameters\":[]},{\"name\":\"symbol\",\"parameters\":[]},{\"name\":\"decimals\",\"parameters\":[]},{\"name\":\"totalSupply\",\"parameters\":[]},{\"name\":\"balanceOf\",\"parameters\":[{\"name\":\"account\",\"type\":\"\"}]},{\"name\":\"transfer\",\"parameters\":[{\"name\":\"from_acct\",\"type\":\"\"},{\"name\":\"to_acct\",\"type\":\"\"},{\"name\":\"amount\",\"type\":\"\"}]},{\"name\":\"transferMulti\",\"parameters\":[{\"name\":\"args\",\"type\":\"\"}]},{\"name\":\"approve\",\"parameters\":[{\"name\":\"owner\",\"type\":\"\"},{\"name\":\"spender\",\"type\":\"\"},{\"name\":\"amount\",\"type\":\"\"}]},{\"name\":\"transferFrom\",\"parameters\":[{\"name\":\"spender\",\"type\":\"\"},{\"name\":\"from_acct\",\"type\":\"\"},{\"name\":\"to_acct\",\"type\":\"\"},{\"name\":\"amount\",\"type\":\"\"}]},{\"name\":\"allowance\",\"parameters\":[{\"name\":\"owner\",\"type\":\"\"},{\"name\":\"spender\",\"type\":\"\"}]},{\"name\":\"burn\",\"parameters\":[{\"name\":\"account\",\"type\":\"\"},{\"name\":\"amount\",\"type\":\"\"}]}]}}","Creator":"ATdX8jx2Zc9yqqgeu24xVRzqT7GJ5MAqTE","TotalSupply":10000000000,"Decimals":9,"Code":"\"0138c56b6a00527ac46a51527ac46a00c304696e69749c64090065980a6c7566616a00c3046e616d659c64090065710a6c7566616a00c30673796d626f6c9c640900654a0a6c7566616a00c308646563696d616c739c64090065240a6c7566616a00c30b746f74616c537570706c799c64090065b6096c7566616a00c30962616c616e63654f669c6424006a51c3c0519e640700006c7566616a51c300c36a52527ac46a52c3650f096c7566616a00c3087472616e736665729c6440006a51c3c0539e640700006c7566616a51c300c36a53527ac46a51c351c36a54527ac46a51c352c36a55527ac46a53c36a54c36a55c352726516076c7566616a00c30d7472616e736665724d756c74699c640c006a51c3655e066c7566616a00c30c7472616e7366657246726f6d9c645f006a51c3c0549e640700006c7566616a51c300c36a56527ac46a51c351c36a53527ac46a51c352c36a54527ac46a51c353c36a55527ac46a56c36a53c36a54c36a55c353795179557275517275527952795472755272756596026c7566616a00c307617070726f76659c6440006a51c3c0539e640700006c7566616a51c300c36a57527ac46a51c351c36a56527ac46a51c352c36a55527ac46a57c36a56c36a55c352726592046c7566616a00c309616c6c6f77616e63659c6432006a51c3c0529e640700006c7566616a51c300c36a57527ac46a51c351c36a56527ac46a57c36a56c37c65a4016c7566616a00c3046275726e9c6432006a51c3c0529e640700006c7566616a51c300c36a52527ac46a51c351c36a55527ac46a52c36a55c37c650b006c756661006c75660113c56b6a00527ac46a51527ac4681953797374656d2e53746f726167652e476574436f6e74657874616a52527ac42241546458386a78325a633979717167657532347856527a715437474a354d417154457514820d0eeb470250d1427f7cdf42fb776d876ebab66a53527ac401016a54527ac40b546f74616c537570706c796a55527ac46a00c3681b53797374656d2e52756e74696d652e436865636b5769746e65737361009c630b006a51c300a164080061006c7566616a00c36a53c39e640700006c7566616a00c36591066a56527ac46a56c36a51c39f640700006c7566616a52c36a54c36a00c37e6a56c36a51c3945272681253797374656d2e53746f726167652e507574616a52c36a55c365c0066a51c3945272681253797374656d2e53746f726167652e507574616a00c3006a51c35272087472616e7366657254c1681553797374656d2e52756e74696d652e4e6f74696679516c756658c56b6a00527ac46a51527ac4681953797374656d2e53746f726167652e476574436f6e74657874616a52527ac401026a53527ac46a53c36a00c37e6a51c37e6a54527ac46a52c36a54c37c681253797374656d2e53746f726167652e476574616c7566011fc56b6a00527ac46a51527ac46a52527ac46a53527ac4681953797374656d2e53746f726167652e476574436f6e74657874616a54527ac401016a55527ac401026a56527ac46a00c3c001149e6317006a51c3c001149e630d006a52c3c001149e641a00611461646472657373206c656e677468206572726f72f0616a00c3681b53797374656d2e52756e74696d652e436865636b5769746e65737361009c640700006c7566616a55c36a51c37e6a57527ac46a54c36a57c37c681253797374656d2e53746f726167652e476574616a58527ac46a53c36a58c3a0630b006a53c3009f64080061006c7566616a56c36a51c37e6a00c37e6a59527ac46a54c36a59c37c681253797374656d2e53746f726167652e476574616a5a527ac46a55c36a52c37e6a5b527ac46a53c36a5ac3a0640700006c7566616a53c36a5ac39c6449006a54c36a59c37c681553797374656d2e53746f726167652e44656c657465616a54c36a57c36a58c36a53c3945272681253797374656d2e53746f726167652e50757461624c00616a54c36a59c36a5ac36a53c3945272681253797374656d2e53746f726167652e507574616a54c36a57c36a58c36a53c3945272681253797374656d2e53746f726167652e50757461616a54c36a5bc37c681253797374656d2e53746f726167652e476574616a5c527ac46a54c36a5bc36a5cc36a53c3935272681253797374656d2e53746f726167652e507574616a51c36a52c36a53c35272087472616e7366657254c1681553797374656d2e52756e74696d652e4e6f74696679516c75660111c56b6a00527ac46a51527ac46a52527ac4681953797374656d2e53746f726167652e476574436f6e74657874616a53527ac401026a54527ac46a51c3c001149e630d006a00c3c001149e641a00611461646472657373206c656e677468206572726f72f0616a00c3681b53797374656d2e52756e74696d652e436865636b5769746e65737361009c640700006c7566616a52c36a00c365ba02a0630b006a52c3009f64080061006c7566616a54c36a00c37e6a51c37e6a55527ac46a53c36a55c36a52c35272681253797374656d2e53746f726167652e507574616a00c36a51c36a52c3527208617070726f76616c54c1681553797374656d2e52756e74696d652e4e6f74696679516c756659c56b6a00527ac4006a52527ac46a00c3c06a53527ac4616a52c36a53c39f6473006a00c36a52c3c36a51527ac46a52c351936a52527ac46a51c3c0539e6420001b7472616e736665724d756c746920706172616d73206572726f722ef0616a51c300c36a51c351c36a51c352c35272652900009c64a2ff157472616e736665724d756c7469206661696c65642ef06288ff616161516c75660117c56b6a00527ac46a51527ac46a52527ac4681953797374656d2e53746f726167652e476574436f6e74657874616a53527ac401016a54527ac46a51c3c001149e630d006a00c3c001149e641a00611461646472657373206c656e677468206572726f72f0616a00c3681b53797374656d2e52756e74696d652e436865636b5769746e65737361009c630b006a52c3009f64080061006c7566616a54c36a00c37e6a55527ac46a53c36a55c37c681253797374656d2e53746f726167652e476574616a56527ac46a52c36a56c3a0640700006c7566616a52c36a56c39c6425006a53c36a55c37c681553797374656d2e53746f726167652e44656c65746561622800616a53c36a55c36a56c36a52c3945272681253797374656d2e53746f726167652e50757461616a54c36a51c37e6a57527ac46a53c36a57c37c681253797374656d2e53746f726167652e476574616a58527ac46a53c36a57c36a58c36a52c3935272681253797374656d2e53746f726167652e507574616a00c36a51c36a52c35272087472616e7366657254c1681553797374656d2e52756e74696d652e4e6f74696679516c756658c56b6a00527ac4681953797374656d2e53746f726167652e476574436f6e74657874616a51527ac401016a52527ac46a00c3c001149e6419001461646472657373206c656e677468206572726f72f0616a51c36a52c36a00c37e7c681253797374656d2e53746f726167652e476574616c756655c56b681953797374656d2e53746f726167652e476574436f6e74657874616a00527ac40b546f74616c537570706c796a51527ac46a00c36a51c37c681253797374656d2e53746f726167652e476574616c756654c56b596a00527ac46a00c36c756654c56b034c43596a00527ac46a00c36c756654c56b054c55434b596a00527ac46a00c36c75660113c56b681953797374656d2e53746f726167652e476574436f6e74657874616a00527ac40400ca9a3b6a51527ac42241546458386a78325a633979717167657532347856527a715437474a354d417154457514820d0eeb470250d1427f7cdf42fb776d876ebab66a52527ac40500e40b54026a53527ac401016a54527ac40b546f74616c537570706c796a55527ac46a52c3c001149e6432000e4f776e657220696c6c6567616c2151c176c9681553797374656d2e52756e74696d652e4e6f7469667961006c7566616a00c36a55c37c681253797374656d2e53746f726167652e4765746164340014416c726561647920696e697469616c697a656421681553797374656d2e52756e74696d652e4e6f7469667961006c7566616a53c36a51c3956a56527ac46a00c36a55c36a56c35272681253797374656d2e53746f726167652e507574616a00c36a54c36a52c37e6a56c35272681253797374656d2e53746f726167652e50757461006a52c36a56c35272087472616e7366657254c1681553797374656d2e52756e74696d652e4e6f74696679516c7566006c75665ec56b6a00527ac46a51527ac46a51c36a00c3946a52527ac46a52c3c56a53527ac4006a54527ac46a00c36a55527ac461616a00c36a51c39f6433006a54c36a55c3936a56527ac46a56c36a53c36a54c37bc46a54c351936a54527ac46a55c36a54c3936a00527ac462c8ff6161616a53c36c7566\"","ContractHash":"25277b421a58cfc2ef5836767e54eb7abdd31afd","Name":"Lucky","Logo":"","OngCount":"605.571237000","UpdateTime":1545639758,"Addresscount":418,"ContactInfo":"","OntCount":"0.000000000","TxCount":1659}]
     * Total : 5
     */

    private int Total;
    private List<ContractListBean> ContractList;

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public List<ContractListBean> getContractList() {
        return ContractList;
    }

    public void setContractList(List<ContractListBean> ContractList) {
        this.ContractList = ContractList;
    }

    public static class ContractListBean {
        /**
         * Description : contractsDescriptionTest
         * Symbol : TNT
         * CreateTime : 1530316800
         * ABI : {"contractHash": "2a9cc8a5d0644283e7d7705abe5bbcb979c9bb03"}
         * Creator : AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ
         * TotalSupply : 1000000000
         * Decimals : 8
         * Code : 6a55c36a54c3936a00527ac462c8ff6161616a53c36c7566
         * ContractHash : 49f0908f08b3ebce1e71dc5083cb9a8a54cc4a24
         * Name : TNT coin
         * Logo : https://luckynumber.one/index/img/logo.png
         * OngCount : 0.000000000
         * UpdateTime : 1545209968
         * Addresscount : 8
         * ContactInfo : {"Website":"https://github.com/ontio"}
         * OntCount : 0.000000000
         * TxCount : 851
         */

        private String Description;
        private String Symbol;
        private long CreateTime;
        private String ABI;
        private String Creator;
        private long TotalSupply;
        private int Decimals;
        private String Code;
        private String ContractHash;
        private String Name;
        private String Logo;
        private String OngCount;
        private long UpdateTime;
        private int Addresscount;
        private String ContactInfo;
        private String OntCount;
        private int TxCount;

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getSymbol() {
            return Symbol;
        }

        public void setSymbol(String Symbol) {
            this.Symbol = Symbol;
        }

        public long getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(long CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getABI() {
            return ABI;
        }

        public void setABI(String ABI) {
            this.ABI = ABI;
        }

        public String getCreator() {
            return Creator;
        }

        public void setCreator(String Creator) {
            this.Creator = Creator;
        }

        public long getTotalSupply() {
            return TotalSupply;
        }

        public void setTotalSupply(long TotalSupply) {
            this.TotalSupply = TotalSupply;
        }

        public int getDecimals() {
            return Decimals;
        }

        public void setDecimals(int Decimals) {
            this.Decimals = Decimals;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public String getContractHash() {
            return ContractHash;
        }

        public void setContractHash(String ContractHash) {
            this.ContractHash = ContractHash;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getLogo() {
            return Logo;
        }

        public void setLogo(String Logo) {
            this.Logo = Logo;
        }

        public String getOngCount() {
            return OngCount;
        }

        public void setOngCount(String OngCount) {
            this.OngCount = OngCount;
        }

        public long getUpdateTime() {
            return UpdateTime;
        }

        public void setUpdateTime(long UpdateTime) {
            this.UpdateTime = UpdateTime;
        }

        public int getAddresscount() {
            return Addresscount;
        }

        public void setAddresscount(int Addresscount) {
            this.Addresscount = Addresscount;
        }

        public String getContactInfo() {
            return ContactInfo;
        }

        public void setContactInfo(String ContactInfo) {
            this.ContactInfo = ContactInfo;
        }

        public String getOntCount() {
            return OntCount;
        }

        public void setOntCount(String OntCount) {
            this.OntCount = OntCount;
        }

        public int getTxCount() {
            return TxCount;
        }

        public void setTxCount(int TxCount) {
            this.TxCount = TxCount;
        }
    }
}
