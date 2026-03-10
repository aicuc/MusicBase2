package com.musicbase.entity;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class FirstBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"courseRoundMapList":[{"courseUrl":"http://www.baidu.com","courseImgPathVertical":"2018/9/20/images/00ec1143-ad5c-4406-b5c4-39e52062a3d3.png","classification":2,"courseImgPathHorizontal":"2018/9/20/images/d58cab6d-4fc1-48d8-a1e3-b9a755c75812.png","courseId":5,"courseName":"音乐资讯1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/69a3f001-66ba-4710-ad5f-c6839724c038.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3b404370-aaa1-40ae-9ca6-d97cf50df432.jpg","courseId":2,"courseName":"音乐课堂1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/dbe8de99-1fc3-4c14-a213-5e40d011c7a2.jpg","classification":1,"courseImgPathHorizontal":"2018/9/20/images/a0d39346-9be8-4214-acab-dd729964046a.png","courseId":1,"courseName":"音基教材1"}],"columnList":[{"systemCodeId":1,"systemCodeName":"音乐课堂","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/69a3f001-66ba-4710-ad5f-c6839724c038.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3b404370-aaa1-40ae-9ca6-d97cf50df432.jpg","courseId":2,"courseName":"音乐课堂1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/de2a8599-747d-4527-a3e3-142077fddffd.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/ba5593ff-bd63-4864-80dd-6b05d7f3325c.png","courseId":3,"courseName":"音乐课堂2"}]},{"systemCodeId":2,"systemCodeName":"精品教材","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/dbe8de99-1fc3-4c14-a213-5e40d011c7a2.jpg","classification":1,"courseImgPathHorizontal":"2018/9/20/images/a0d39346-9be8-4214-acab-dd729964046a.png","courseId":1,"courseName":"音基教材1"}]},{"systemCodeId":3,"systemCodeName":"精品习题","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/dbe8de99-1fc3-4c14-a213-5e40d011c7a2.jpg","classification":1,"courseImgPathHorizontal":"2018/9/20/images/a0d39346-9be8-4214-acab-dd729964046a.png","courseId":1,"courseName":"音基教材1"}]},{"systemCodeId":4,"systemCodeName":"名家名师","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/8d7fd018-06dd-4f86-90d3-dee1a7fc988f.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3981a3cb-21a7-492b-925f-fa521e869df9.png","courseId":4,"courseName":"名家名师1"}]},{"systemCodeId":5,"systemCodeName":"音乐资讯","courseList":[{"courseUrl":"http://www.baidu.com","courseImgPathVertical":"2018/9/20/images/00ec1143-ad5c-4406-b5c4-39e52062a3d3.png","classification":2,"courseImgPathHorizontal":"2018/9/20/images/d58cab6d-4fc1-48d8-a1e3-b9a755c75812.png","courseId":5,"courseName":"音乐资讯1"}]},{"systemCodeId":6,"systemCodeName":"教育联盟","courseList":[{"courseUrl":"http://www.baidu.com","courseImgPathVertical":"2018/9/20/images/4b734723-84f0-475d-9155-08b365aae19e.png","classification":2,"courseImgPathHorizontal":"2018/9/20/images/8e90f17a-091e-4ab7-bb48-aea225500bf1.png","courseId":6,"courseName":"教育联盟1"}]}]}
     */

    private String code;
    private String codeInfo;
    private Data data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        /**
         * courseRoundMapList : [{"courseUrl":"http://www.baidu.com","courseImgPathVertical":"2018/9/20/images/00ec1143-ad5c-4406-b5c4-39e52062a3d3.png","classification":2,"courseImgPathHorizontal":"2018/9/20/images/d58cab6d-4fc1-48d8-a1e3-b9a755c75812.png","courseId":5,"courseName":"音乐资讯1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/69a3f001-66ba-4710-ad5f-c6839724c038.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3b404370-aaa1-40ae-9ca6-d97cf50df432.jpg","courseId":2,"courseName":"音乐课堂1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/dbe8de99-1fc3-4c14-a213-5e40d011c7a2.jpg","classification":1,"courseImgPathHorizontal":"2018/9/20/images/a0d39346-9be8-4214-acab-dd729964046a.png","courseId":1,"courseName":"音基教材1"}]
         * columnList : [{"systemCodeId":1,"systemCodeName":"音乐课堂","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/69a3f001-66ba-4710-ad5f-c6839724c038.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3b404370-aaa1-40ae-9ca6-d97cf50df432.jpg","courseId":2,"courseName":"音乐课堂1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/de2a8599-747d-4527-a3e3-142077fddffd.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/ba5593ff-bd63-4864-80dd-6b05d7f3325c.png","courseId":3,"courseName":"音乐课堂2"}]},{"systemCodeId":2,"systemCodeName":"精品教材","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/dbe8de99-1fc3-4c14-a213-5e40d011c7a2.jpg","classification":1,"courseImgPathHorizontal":"2018/9/20/images/a0d39346-9be8-4214-acab-dd729964046a.png","courseId":1,"courseName":"音基教材1"}]},{"systemCodeId":3,"systemCodeName":"精品习题","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/dbe8de99-1fc3-4c14-a213-5e40d011c7a2.jpg","classification":1,"courseImgPathHorizontal":"2018/9/20/images/a0d39346-9be8-4214-acab-dd729964046a.png","courseId":1,"courseName":"音基教材1"}]},{"systemCodeId":4,"systemCodeName":"名家名师","courseList":[{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/8d7fd018-06dd-4f86-90d3-dee1a7fc988f.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3981a3cb-21a7-492b-925f-fa521e869df9.png","courseId":4,"courseName":"名家名师1"}]},{"systemCodeId":5,"systemCodeName":"音乐资讯","courseList":[{"courseUrl":"http://www.baidu.com","courseImgPathVertical":"2018/9/20/images/00ec1143-ad5c-4406-b5c4-39e52062a3d3.png","classification":2,"courseImgPathHorizontal":"2018/9/20/images/d58cab6d-4fc1-48d8-a1e3-b9a755c75812.png","courseId":5,"courseName":"音乐资讯1"}]},{"systemCodeId":6,"systemCodeName":"教育联盟","courseList":[{"courseUrl":"http://www.baidu.com","courseImgPathVertical":"2018/9/20/images/4b734723-84f0-475d-9155-08b365aae19e.png","classification":2,"courseImgPathHorizontal":"2018/9/20/images/8e90f17a-091e-4ab7-bb48-aea225500bf1.png","courseId":6,"courseName":"教育联盟1"}]}]
         */
        private List<NavigationBars> navigationBars;
        private List<ColumnList.CourseList> courseRoundMapList;
        private List<ColumnList> columnList;

        public List<NavigationBars> getNavigationBars() {
            return navigationBars;
        }

        public void setNavigationBars(List<NavigationBars> navigationBars) {
            this.navigationBars = navigationBars;
        }

        public void setCourseRoundMapList(List<ColumnList.CourseList> courseRoundMapList) {
            this.courseRoundMapList = courseRoundMapList;
        }

        public void setColumnList(List<ColumnList> columnList) {
            this.columnList = columnList;
        }

        public List<ColumnList.CourseList> getCourseRoundMapList() {
            return courseRoundMapList;
        }

        public List<ColumnList> getColumnList() {
            return columnList;
        }


        public static class ColumnList {
            /**
             * systemCodeId : 1
             * systemCodeName : 音乐课堂
             * courseList : [{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/69a3f001-66ba-4710-ad5f-c6839724c038.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/3b404370-aaa1-40ae-9ca6-d97cf50df432.jpg","courseId":2,"courseName":"音乐课堂1"},{"courseUrl":null,"courseImgPathVertical":"2018/9/20/images/de2a8599-747d-4527-a3e3-142077fddffd.png","classification":1,"courseImgPathHorizontal":"2018/9/20/images/ba5593ff-bd63-4864-80dd-6b05d7f3325c.png","courseId":3,"courseName":"音乐课堂2"}]
             */

            private int systemCodeId;
            private String systemCodeName;
            private String iconDark;
            private String iconLight;
            private List<CourseList> courseList;


            public String getIconDark() {
                return iconDark;
            }

            public void setIconDark(String iconDark) {
                this.iconDark = iconDark;
            }

            public String getIconLight() {
                return iconLight;
            }

            public void setIconLight(String iconLight) {
                this.iconLight = iconLight;
            }

            public void setSystemCodeId(int systemCodeId) {
                this.systemCodeId = systemCodeId;
            }

            public void setSystemCodeName(String systemCodeName) {
                this.systemCodeName = systemCodeName;
            }

            public void setCourseList(List<CourseList> courseList) {
                this.courseList = courseList;
            }

            public int getSystemCodeId() {
                return systemCodeId;
            }

            public String getSystemCodeName() {
                return systemCodeName;
            }

            public List<CourseList> getCourseList() {
                return courseList;
            }

            public static class CourseList {
                /**
                 * courseUrl : null
                 * courseImgPathVertical : 2018/9/20/images/69a3f001-66ba-4710-ad5f-c6839724c038.png
                 * classification : 1
                 * courseImgPathHorizontal : 2018/9/20/images/3b404370-aaa1-40ae-9ca6-d97cf50df432.jpg
                 * courseId : 2
                 * courseName : 音乐课堂1
                 */

                private String courseUrl;
                private String courseImgPathVertical;
                private int classification;//1为课程，2为资讯，3实体图书，4为APP跳转，5为定位即跳转到哪个栏目
                private String courseImgPathHorizontal;
                private int courseId;
                private int courseBookType;
                private int isPack;
                private String courseName;
                private int resourceCount;
//                private String courseState;
                private int chargeType;
                private int allowOpenInBrowser;
                private String currentPrice;
                private String originalPrice;
                private String courseState;
                private int systemCodeId;
                private AppJumpInfoBean appJumpInfo;

                public int getSystemCodeId() {
                    return systemCodeId;
                }

                public void setSystemCodeId(int systemCodeId) {
                    this.systemCodeId = systemCodeId;
                }

                public AppJumpInfoBean getAppJumpInfo() {
                    return appJumpInfo;
                }

                public void setAppJumpInfo(AppJumpInfoBean appJumpInfo) {
                    this.appJumpInfo = appJumpInfo;
                }

                public int getAllowOpenInBrowser() {
                    return allowOpenInBrowser;
                }

                public void setAllowOpenInBrowser(int allowOpenInBrowser) {
                    this.allowOpenInBrowser = allowOpenInBrowser;
                }

                public String getCourseState() {
                    return courseState;
                }

                public void setCourseState(String courseState) {
                    this.courseState = courseState;
                }

                public int getCourseBookType() {
                    return courseBookType;
                }

                public void setCourseBookType(int courseBookType) {
                    this.courseBookType = courseBookType;
                }

                public int getIsPack() {
                    return isPack;
                }

                public void setIsPack(int isPack) {
                    this.isPack = isPack;
                }

                public int getResourceCount() {
                    return resourceCount;
                }

                public void setResourceCount(int resourceCount) {
                    this.resourceCount = resourceCount;
                }

//                public String getCourseState() {
//                    return courseState;
//                }
//
//                public void setCourseState(int courseState) {
//                    this.courseState = courseState;
//                }

                public int getChargeType() {
                    return chargeType;
                }

                public void setChargeType(int chargeType) {
                    this.chargeType = chargeType;
                }

                public String getCurrentPrice() {
                    return currentPrice;
                }

                public void setCurrentPrice(String currentPrice) {
                    this.currentPrice = currentPrice;
                }

                public String getOriginalPrice() {
                    return originalPrice;
                }

                public void setOriginalPrice(String originalPrice) {
                    this.originalPrice = originalPrice;
                }

                public void setCourseUrl(String courseUrl) {
                    this.courseUrl = courseUrl;
                }

                public void setCourseImgPathVertical(String courseImgPathVertical) {
                    this.courseImgPathVertical = courseImgPathVertical;
                }

                public void setClassification(int classification) {
                    this.classification = classification;
                }

                public void setCourseImgPathHorizontal(String courseImgPathHorizontal) {
                    this.courseImgPathHorizontal = courseImgPathHorizontal;
                }

                public void setCourseId(int courseId) {
                    this.courseId = courseId;
                }

                public void setCourseName(String courseName) {
                    this.courseName = courseName;
                }

                public String getCourseUrl() {
                    return courseUrl;
                }

                public String getCourseImgPathVertical() {
                    return courseImgPathVertical;
                }

                public int getClassification() {
                    return classification;
                }

                public String getCourseImgPathHorizontal() {
                    return courseImgPathHorizontal;
                }

                public int getCourseId() {
                    return courseId;
                }

                public String getCourseName() {
                    return courseName;
                }

                public static class AppJumpInfoBean {
                    /**
                     * androidSucc : android
                     * androidFail : https://www.yinyuesuyang.com/
                     * iosSucc : ios
                     * iosFail : https://www.yinyuesuyang.com/
                     */

                    private String androidSucc;
                    private String androidFail;
                    private String iosSucc;
                    private String iosFail;

                    public String getAndroidSucc() {
                        return androidSucc;
                    }

                    public void setAndroidSucc(String androidSucc) {
                        this.androidSucc = androidSucc;
                    }

                    public String getAndroidFail() {
                        return androidFail;
                    }

                    public void setAndroidFail(String androidFail) {
                        this.androidFail = androidFail;
                    }

                    public String getIosSucc() {
                        return iosSucc;
                    }

                    public void setIosSucc(String iosSucc) {
                        this.iosSucc = iosSucc;
                    }

                    public String getIosFail() {
                        return iosFail;
                    }

                    public void setIosFail(String iosFail) {
                        this.iosFail = iosFail;
                    }
                }
            }
        }

        public static class NavigationBars {
            private String iconName;
            private String iconPath;
            private String iconType;
            private String iconLink;
            private int allowOpenInBrowser;

            public String getIconName() {
                return iconName;
            }

            public void setIconName(String iconName) {
                this.iconName = iconName;
            }

            public String getIconPath() {
                return iconPath;
            }

            public void setIconPath(String iconPath) {
                this.iconPath = iconPath;
            }

            public String getIconType() {
                return iconType;
            }

            public void setIconType(String iconType) {
                this.iconType = iconType;
            }

            public String getIconLink() {
                return iconLink;
            }

            public void setIconLink(String iconLink) {
                this.iconLink = iconLink;
            }

            public int getAllowOpenInBrowser() {
                return allowOpenInBrowser;
            }

            public void setAllowOpenInBrowser(int allowOpenInBrowser) {
                this.allowOpenInBrowser = allowOpenInBrowser;
            }
        }
    }
}
