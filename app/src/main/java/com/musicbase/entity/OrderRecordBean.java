package com.musicbase.entity;

import java.util.List;

public class OrderRecordBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":70,"attachCode":"20193291553848468751","bodyName":"音乐素养\u2014精品习题中级","realPrice":"328.00","payTime":{"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":28,"time":1553848468000,"timezoneOffset":-480,"year":119},"payType":0,"phoneType":1,"attachList":[{"courseBookType":"1","attachName":"音乐素养\u2014精品习题中级","price":"328.00","amount":"1","isPack":"0"}]},{"id":71,"attachCode":"20193291553848471295","bodyName":"音乐素养\u2014精品习题初级","realPrice":"328.00","payTime":{"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":31,"time":1553848471000,"timezoneOffset":-480,"year":119},"payType":0,"phoneType":1,"attachList":[{"courseBookType":"1","attachName":"音乐素养\u2014精品习题初级","price":"328.00","amount":"1","isPack":"0"}]},{"id":72,"attachCode":"20193291553848475232","bodyName":"音乐素养课-初级-学生版","realPrice":"288.00","payTime":{"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":35,"time":1553848475000,"timezoneOffset":-480,"year":119},"payType":0,"phoneType":1,"attachList":[{"courseBookType":"1","attachName":"音乐素养课-初级-学生版","price":"288.00","amount":"1","isPack":"0"}]},{"id":73,"attachCode":"20193291553848477771","bodyName":"音乐素养课-中级-学生版","realPrice":"288.00","payTime":{"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":37,"time":1553848477000,"timezoneOffset":-480,"year":119},"payType":0,"phoneType":1,"attachList":[{"courseBookType":"1","attachName":"音乐素养课-中级-学生版","price":"288.00","amount":"1","isPack":"0"}]},{"id":74,"attachCode":"20193291553848481497","bodyName":"音乐素养课-初级-课堂版","realPrice":"998.00","payTime":{"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":41,"time":1553848481000,"timezoneOffset":-480,"year":119},"payType":0,"phoneType":1,"attachList":[{"courseBookType":"1","attachName":"音乐素养课-初级-课堂版","price":"998.00","amount":"1","isPack":"0"}]},{"id":75,"attachCode":"20193291553848483785","bodyName":"音乐素养课-中级-课堂版","realPrice":"998.00","payTime":{"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":43,"time":1553848483000,"timezoneOffset":-480,"year":119},"payType":0,"phoneType":1,"attachList":[{"courseBookType":"1","attachName":"音乐素养课-中级-课堂版","price":"998.00","amount":"1","isPack":"0"}]}]
     */

    private String code;
    private String codeInfo;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 70
         * attachCode : 20193291553848468751
         * bodyName : 音乐素养—精品习题中级
         * realPrice : 328.00
         * payTime : {"date":29,"day":5,"hours":16,"minutes":34,"month":2,"seconds":28,"time":1553848468000,"timezoneOffset":-480,"year":119}
         * payType : 0
         * phoneType : 1
         * attachList : [{"courseBookType":"1","attachName":"音乐素养\u2014精品习题中级","price":"328.00","amount":"1","isPack":"0"}]
         */

        private int id;
        private String attachCode;
        private String bodyName;
        private String realPrice;
        private PayTimeBean payTime;
        private int payType;
        private int phoneType;
        private List<AttachListBean> attachList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAttachCode() {
            return attachCode;
        }

        public void setAttachCode(String attachCode) {
            this.attachCode = attachCode;
        }

        public String getBodyName() {
            return bodyName;
        }

        public void setBodyName(String bodyName) {
            this.bodyName = bodyName;
        }

        public String getRealPrice() {
            return realPrice;
        }

        public void setRealPrice(String realPrice) {
            this.realPrice = realPrice;
        }

        public PayTimeBean getPayTime() {
            return payTime;
        }

        public void setPayTime(PayTimeBean payTime) {
            this.payTime = payTime;
        }

        public int getPayType() {
            return payType;
        }

        public void setPayType(int payType) {
            this.payType = payType;
        }

        public int getPhoneType() {
            return phoneType;
        }

        public void setPhoneType(int phoneType) {
            this.phoneType = phoneType;
        }

        public List<AttachListBean> getAttachList() {
            return attachList;
        }

        public void setAttachList(List<AttachListBean> attachList) {
            this.attachList = attachList;
        }

        public static class PayTimeBean {
            /**
             * date : 29
             * day : 5
             * hours : 16
             * minutes : 34
             * month : 2
             * seconds : 28
             * time : 1553848468000
             * timezoneOffset : -480
             * year : 119
             */

            private int date;
            private int day;
            private int hours;
            private int minutes;
            private int month;
            private int seconds;
            private long time;
            private int timezoneOffset;
            private int year;

            public int getDate() {
                return date;
            }

            public void setDate(int date) {
                this.date = date;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }

            public int getHours() {
                return hours;
            }

            public void setHours(int hours) {
                this.hours = hours;
            }

            public int getMinutes() {
                return minutes;
            }

            public void setMinutes(int minutes) {
                this.minutes = minutes;
            }

            public int getMonth() {
                return month;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public int getSeconds() {
                return seconds;
            }

            public void setSeconds(int seconds) {
                this.seconds = seconds;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public int getTimezoneOffset() {
                return timezoneOffset;
            }

            public void setTimezoneOffset(int timezoneOffset) {
                this.timezoneOffset = timezoneOffset;
            }

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }
        }

        public static class AttachListBean {
            /**
             * courseBookType : 1
             * attachName : 音乐素养—精品习题中级
             * price : 328.00
             * amount : 1
             * isPack : 0
             */

            private String courseBookType;
            private String attachName;
            private String price;
            private String amount;
            private String isPack;

            public String getCourseBookType() {
                return courseBookType;
            }

            public void setCourseBookType(String courseBookType) {
                this.courseBookType = courseBookType;
            }

            public String getAttachName() {
                return attachName;
            }

            public void setAttachName(String attachName) {
                this.attachName = attachName;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getIsPack() {
                return isPack;
            }

            public void setIsPack(String isPack) {
                this.isPack = isPack;
            }
        }
    }
}
