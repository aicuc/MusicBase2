package com.musicbase.entity;

import java.util.List;

/**
 * Created by BAO on 2018-11-07.
 */

public class BuyedBean {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"systemCodeId":1,"systemCodeName":"音乐课堂","courseList":[{"resourceCount":18,"courseImgPathVertical":null,"currentTotalPrice":"141.30","classification":1,"courseImgPathHorizontal":"2018\\8\\30\\images/e2386505-4d5a-4f0b-9eae-48a111e1dab7.jpg","courseId":1,"originalTotalPrice":"280.00","courseName":"音基课程名称"},{"resourceCount":3,"courseUrl":null,"courseImgPathVertical":"2018\\9\\19\\images/e62a1879-14fc-4e58-9463-bbf5be170a43.jpg","currentTotalPrice":"24.00","classification":1,"courseImgPathHorizontal":"2018\\9\\19\\images/c913242d-4381-4c0c-beed-659c6483c9ab.jpg","courseId":29,"originalTotalPrice":"30.00","courseName":"音基课程名称1"}]},{"systemCodeId":2,"systemCodeName":"精品教材","courseList":[{"resourceCount":3,"courseUrl":null,"courseImgPathVertical":"2018\\9\\19\\images/e62a1879-14fc-4e58-9463-bbf5be170a43.jpg","currentTotalPrice":"24.00","classification":1,"courseImgPathHorizontal":"2018\\9\\19\\images/c913242d-4381-4c0c-beed-659c6483c9ab.jpg","courseId":29,"originalTotalPrice":"30.00","courseName":"音基课程名称1"}]},{"systemCodeId":3,"systemCodeName":"精品习题","courseList":[{"resourceCount":3,"courseUrl":null,"courseImgPathVertical":"2018\\9\\19\\images/e62a1879-14fc-4e58-9463-bbf5be170a43.jpg","currentTotalPrice":"24.00","classification":1,"courseImgPathHorizontal":"2018\\9\\19\\images/c913242d-4381-4c0c-beed-659c6483c9ab.jpg","courseId":29,"originalTotalPrice":"30.00","courseName":"音基课程名称1"}]},{"systemCodeId":6,"systemCodeName":"教育联盟","courseList":[{"resourceCount":1,"courseUrl":null,"courseImgPathVertical":"2018\\9\\21\\images/62ce2c58-6168-4204-b964-1795e2de1d5f.jpg","currentTotalPrice":"8.00","classification":1,"courseImgPathHorizontal":"2018\\9\\21\\images/89f02679-7193-4c90-9c22-66e2818be278.jpg","courseId":34,"originalTotalPrice":"10.00","courseName":"dsad"}]}]
     */

    private String code;
    private String codeInfo;
    private List<Data> data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {
        /**
         * systemCodeId : 1
         * systemCodeName : 音乐课堂
         * courseList : [{"resourceCount":18,"courseImgPathVertical":null,"currentTotalPrice":"141.30","classification":1,"courseImgPathHorizontal":"2018\\8\\30\\images/e2386505-4d5a-4f0b-9eae-48a111e1dab7.jpg","courseId":1,"originalTotalPrice":"280.00","courseName":"音基课程名称"},{"resourceCount":3,"courseUrl":null,"courseImgPathVertical":"2018\\9\\19\\images/e62a1879-14fc-4e58-9463-bbf5be170a43.jpg","currentTotalPrice":"24.00","classification":1,"courseImgPathHorizontal":"2018\\9\\19\\images/c913242d-4381-4c0c-beed-659c6483c9ab.jpg","courseId":29,"originalTotalPrice":"30.00","courseName":"音基课程名称1"}]
         */

        private int systemCodeId;
        private String systemCodeName;
        private List<CourseList> courseList;

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
             * resourceCount : 18
             * courseImgPathVertical : null
             * currentTotalPrice : 141.30
             * classification : 1
             * courseImgPathHorizontal : 2018\8\30\images/e2386505-4d5a-4f0b-9eae-48a111e1dab7.jpg
             * courseId : 1
             * originalTotalPrice : 280.00
             * courseName : 音基课程名称
             */

            private int resourceCount;
            private Object courseImgPathVertical;
            private String currentPrice;
            private int classification;
            private String courseImgPathHorizontal;
            private int courseId;
            private int isOverdue;
//            private String courseState;
            private int isPublish;
            private LoginBean.Data.AddTime addTime;
            private String originalPrice;
            private String courseName;
            private String courseUrl;
            private String courseState;

            public String getCourseState() {
                return courseState;
            }

            public void setCourseState(String courseState) {
                this.courseState = courseState;
            }

            public void setResourceCount(int resourceCount) {
                this.resourceCount = resourceCount;
            }

            public void setCourseImgPathVertical(Object courseImgPathVertical) {
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

            public int getResourceCount() {
                return resourceCount;
            }

            public Object getCourseImgPathVertical() {
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

            public String getCurrentPrice() {
                return currentPrice;
            }

            public void setCurrentPrice(String currentPrice) {
                this.currentPrice = currentPrice;
            }

            public int getIsOverdue() {
                return isOverdue;
            }

            public void setIsOverdue(int isOverdue) {
                this.isOverdue = isOverdue;
            }

//            public String getCourseState() {
//                return courseState;
//            }
//
//            public void setCourseState(String courseState) {
//                this.courseState = courseState;
//            }

            public int getIsPublish() {
                return isPublish;
            }

            public void setIsPublish(int isPublish) {
                this.isPublish = isPublish;
            }

            public LoginBean.Data.AddTime getAddTime() {
                return addTime;
            }

            public void setAddTime(LoginBean.Data.AddTime addTime) {
                this.addTime = addTime;
            }

            public String getOriginalPrice() {
                return originalPrice;
            }

            public void setOriginalPrice(String originalPrice) {
                this.originalPrice = originalPrice;
            }

            public String getCourseUrl() {
                return courseUrl;
            }

            public void setCourseUrl(String courseUrl) {
                this.courseUrl = courseUrl;
            }
        }
    }
}
