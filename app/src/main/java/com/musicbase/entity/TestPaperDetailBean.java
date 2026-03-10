package com.musicbase.entity;

import java.io.Serializable;
import java.util.List;

public class TestPaperDetailBean  implements Serializable{

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"paperId":3,"paperName":"初级试卷3","paperRemark":"初级试卷3初级试卷3","packSavePath":"testpaper/paper_zip/26421c49-af53-46f8-8193-e677882b40be.zip","questions":[{"bankId":6,"questionType":"video","topicName":"初级视频题3-2","topicStem":"从<icon>testpaper/topic_icon/earphone.png<\/icon>处听音乐，然后根据下面图谱唱出并上传（初级视频题3-2）<icon>testpaper/topic_icon/trumpet.png<\/icon>","topicSound":["testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"],"topicImg":"testpaper/topic_img/34f0804b-280c-4d29-9b3f-9f79d2cef9dd.png","topicVideo":"testpaper/topic_video/d7d5ddb5-527d-4b1d-bfe1-441df4797b1a.mp4","topicGrade":5,"playSequence":[{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"},{"topic_type":"topicVideo","topic_path":"testpaper/topic_video/d7d5ddb5-527d-4b1d-bfe1-441df4797b1a.mp4"}],"beginReord":0,"allowTime":10,"allowSubmit":1,"uploadRecords":[{"recordId":28,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":0,"judgeState":0,"submitNum":1}],"uploadState":"allow_upload"},{"bankId":5,"questionType":"video","topicName":"初级视频题3-1","topicStem":"从<icon>testpaper/topic_icon/earphone.png<\/icon>处听音乐，然后根据下面图谱唱出并上传（初级视频题3-1）<icon>testpaper/topic_icon/trumpet.png<\/icon>","topicSound":["testpaper/topic_sound/3d8fdd69-72c8-4022-b9ec-636679e73f9d.mp3","testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"],"topicImg":"testpaper/topic_img/f61a6f78-128c-4166-a89e-70ec0882b1fa.png","topicAudio":"testpaper/topic_audio/c6097015-fdf3-402f-9308-23c044ad3af7.mp3","topicGrade":6,"playSequence":[{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/3d8fdd69-72c8-4022-b9ec-636679e73f9d.mp3"},{"topic_type":"topicAudio","topic_path":"testpaper/topic_audio/c6097015-fdf3-402f-9308-23c044ad3af7.mp3"},{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"},{"topic_type":"topicAudio","topic_path":"testpaper/topic_audio/c6097015-fdf3-402f-9308-23c044ad3af7.mp3"}],"beginReord":0,"allowTime":20,"allowSubmit":1,"uploadRecords":[{"recordId":23,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":1,"judgeState":1,"judgeTime":"2021-11-22 16:19:21","scoreAppraisal":"真好","submitNum":1,"averageScore":"5.75","scoreInfo":[{"criterionName":"音准","userScore":"6"},{"criterionName":"节奏","userScore":"6"},{"criterionName":"划拍","userScore":"5"},{"criterionName":"音乐完整性","userScore":"6"}]},{"recordId":26,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":2,"judgeState":0,"judgeTime":"2021-11-22 16:19:45","scoreAppraisal":"相片与视频不符合","submitNum":1}],"uploadState":"allow_upload"}]}
     */

    private String code;
    private String codeInfo;
    /**
     * paperId : 3
     * paperName : 初级试卷3
     * paperRemark : 初级试卷3初级试卷3
     * packSavePath : testpaper/paper_zip/26421c49-af53-46f8-8193-e677882b40be.zip
     * questions : [{"bankId":6,"questionType":"video","topicName":"初级视频题3-2","topicStem":"从<icon>testpaper/topic_icon/earphone.png<\/icon>处听音乐，然后根据下面图谱唱出并上传（初级视频题3-2）<icon>testpaper/topic_icon/trumpet.png<\/icon>","topicSound":["testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"],"topicImg":"testpaper/topic_img/34f0804b-280c-4d29-9b3f-9f79d2cef9dd.png","topicVideo":"testpaper/topic_video/d7d5ddb5-527d-4b1d-bfe1-441df4797b1a.mp4","topicGrade":5,"playSequence":[{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"},{"topic_type":"topicVideo","topic_path":"testpaper/topic_video/d7d5ddb5-527d-4b1d-bfe1-441df4797b1a.mp4"}],"beginReord":0,"allowTime":10,"allowSubmit":1,"uploadRecords":[{"recordId":28,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":0,"judgeState":0,"submitNum":1}],"uploadState":"allow_upload"},{"bankId":5,"questionType":"video","topicName":"初级视频题3-1","topicStem":"从<icon>testpaper/topic_icon/earphone.png<\/icon>处听音乐，然后根据下面图谱唱出并上传（初级视频题3-1）<icon>testpaper/topic_icon/trumpet.png<\/icon>","topicSound":["testpaper/topic_sound/3d8fdd69-72c8-4022-b9ec-636679e73f9d.mp3","testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"],"topicImg":"testpaper/topic_img/f61a6f78-128c-4166-a89e-70ec0882b1fa.png","topicAudio":"testpaper/topic_audio/c6097015-fdf3-402f-9308-23c044ad3af7.mp3","topicGrade":6,"playSequence":[{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/3d8fdd69-72c8-4022-b9ec-636679e73f9d.mp3"},{"topic_type":"topicAudio","topic_path":"testpaper/topic_audio/c6097015-fdf3-402f-9308-23c044ad3af7.mp3"},{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"},{"topic_type":"topicAudio","topic_path":"testpaper/topic_audio/c6097015-fdf3-402f-9308-23c044ad3af7.mp3"}],"beginReord":0,"allowTime":20,"allowSubmit":1,"uploadRecords":[{"recordId":23,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":1,"judgeState":1,"judgeTime":"2021-11-22 16:19:21","scoreAppraisal":"真好","submitNum":1,"averageScore":"5.75","scoreInfo":[{"criterionName":"音准","userScore":"6"},{"criterionName":"节奏","userScore":"6"},{"criterionName":"划拍","userScore":"5"},{"criterionName":"音乐完整性","userScore":"6"}]},{"recordId":26,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":2,"judgeState":0,"judgeTime":"2021-11-22 16:19:45","scoreAppraisal":"相片与视频不符合","submitNum":1}],"uploadState":"allow_upload"}]
     */

    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean  implements Serializable{
        private int paperId;
        private String paperName;
        private String paperRemark;
        private String packSavePath;
        /**
         * bankId : 6
         * questionType : video
         * topicName : 初级视频题3-2
         * topicStem : 从<icon>testpaper/topic_icon/earphone.png</icon>处听音乐，然后根据下面图谱唱出并上传（初级视频题3-2）<icon>testpaper/topic_icon/trumpet.png</icon>
         * topicSound : ["testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"]
         * topicImg : testpaper/topic_img/34f0804b-280c-4d29-9b3f-9f79d2cef9dd.png
         * topicVideo : testpaper/topic_video/d7d5ddb5-527d-4b1d-bfe1-441df4797b1a.mp4
         * topicGrade : 5
         * playSequence : [{"topic_type":"topicSound","topic_path":"testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3"},{"topic_type":"topicVideo","topic_path":"testpaper/topic_video/d7d5ddb5-527d-4b1d-bfe1-441df4797b1a.mp4"}]
         * beginReord : 0
         * allowTime : 10
         * allowSubmit : 1
         * uploadRecords : [{"recordId":28,"avatarId":19,"userAvatar":"testpaper/user_avater/t013f5efcfb02eba705.jpg","savePath":"testpaper/user_record/example.mp4","addTime":"2021-11-22 16:08:34","checkState":0,"judgeState":0,"submitNum":1}]
         * uploadState : allow_upload
         */

        private List<QuestionsBean> questions;

        public int getPaperId() {
            return paperId;
        }

        public void setPaperId(int paperId) {
            this.paperId = paperId;
        }

        public String getPaperName() {
            return paperName;
        }

        public void setPaperName(String paperName) {
            this.paperName = paperName;
        }

        public String getPaperRemark() {
            return paperRemark;
        }

        public void setPaperRemark(String paperRemark) {
            this.paperRemark = paperRemark;
        }

        public String getPackSavePath() {
            return packSavePath;
        }

        public void setPackSavePath(String packSavePath) {
            this.packSavePath = packSavePath;
        }

        public List<QuestionsBean> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionsBean> questions) {
            this.questions = questions;
        }

        public static class QuestionsBean implements Serializable {
            private int bankId;
            private String questionType;
            private String topicName;
            private String topicStem;
            private String topicImg;
            private String topicVideo;
            private int topicGrade;
            private int beginReord;
            private int allowTime;
            private int allowSubmit;
            private String uploadState;
            private List<String> topicSound;
            /**
             * topic_type : topicSound
             * topic_path : testpaper/topic_sound/4df0e9d7-ac01-4234-b258-917b5a5bece2.mp3
             */

            private List<PlaySequenceBean> playSequence;
            /**
             * recordId : 28
             * avatarId : 19
             * userAvatar : testpaper/user_avater/t013f5efcfb02eba705.jpg
             * savePath : testpaper/user_record/example.mp4
             * addTime : 2021-11-22 16:08:34
             * checkState : 0
             * judgeState : 0
             * submitNum : 1
             */

            private List<UploadRecordsBean> uploadRecords;

            public int getBankId() {
                return bankId;
            }

            public void setBankId(int bankId) {
                this.bankId = bankId;
            }

            public String getQuestionType() {
                return questionType;
            }

            public void setQuestionType(String questionType) {
                this.questionType = questionType;
            }

            public String getTopicName() {
                return topicName;
            }

            public void setTopicName(String topicName) {
                this.topicName = topicName;
            }

            public String getTopicStem() {
                return topicStem;
            }

            public void setTopicStem(String topicStem) {
                this.topicStem = topicStem;
            }

            public String getTopicImg() {
                return topicImg;
            }

            public void setTopicImg(String topicImg) {
                this.topicImg = topicImg;
            }

            public String getTopicVideo() {
                return topicVideo;
            }

            public void setTopicVideo(String topicVideo) {
                this.topicVideo = topicVideo;
            }

            public int getTopicGrade() {
                return topicGrade;
            }

            public void setTopicGrade(int topicGrade) {
                this.topicGrade = topicGrade;
            }

            public int getBeginReord() {
                return beginReord;
            }

            public void setBeginReord(int beginReord) {
                this.beginReord = beginReord;
            }

            public int getAllowTime() {
                return allowTime;
            }

            public void setAllowTime(int allowTime) {
                this.allowTime = allowTime;
            }

            public int getAllowSubmit() {
                return allowSubmit;
            }

            public void setAllowSubmit(int allowSubmit) {
                this.allowSubmit = allowSubmit;
            }

            public String getUploadState() {
                return uploadState;
            }

            public void setUploadState(String uploadState) {
                this.uploadState = uploadState;
            }

            public List<String> getTopicSound() {
                return topicSound;
            }

            public void setTopicSound(List<String> topicSound) {
                this.topicSound = topicSound;
            }

            public List<PlaySequenceBean> getPlaySequence() {
                return playSequence;
            }

            public void setPlaySequence(List<PlaySequenceBean> playSequence) {
                this.playSequence = playSequence;
            }

            public List<UploadRecordsBean> getUploadRecords() {
                return uploadRecords;
            }

            public void setUploadRecords(List<UploadRecordsBean> uploadRecords) {
                this.uploadRecords = uploadRecords;
            }

            public static class PlaySequenceBean implements Serializable {
                private String topic_type;
                private String topic_path;

                public String getTopic_type() {
                    return topic_type;
                }

                public void setTopic_type(String topic_type) {
                    this.topic_type = topic_type;
                }

                public String getTopic_path() {
                    return topic_path;
                }

                public void setTopic_path(String topic_path) {
                    this.topic_path = topic_path;
                }

                @Override
                public String toString() {
                    return "PlaySequenceBean{" + "topic_type='" + topic_type + '\'' + ", topic_path='" + topic_path + '\'' + '}';
                }
            }

            public static class UploadRecordsBean  implements Serializable{
                private int recordId;
                private int avatarId;
                private String userAvatar;
                private String savePath;
                private String addTime;
                private int checkState;
                private int judgeState;
                private int submitNum;
                private String scoreAppraisal;
                private List<ScoreInfoBean> scoreInfo;
                private String averageScore;

                public String getAverageScore() {
                    return averageScore;
                }

                public void setAverageScore(String averageScore) {
                    this.averageScore = averageScore;
                }

                public List<ScoreInfoBean> getScoreInfo() {
                    return scoreInfo;
                }

                public void setScoreInfo(List<ScoreInfoBean> scoreInfo) {
                    this.scoreInfo = scoreInfo;
                }

                public String getScoreAppraisal() {
                    return scoreAppraisal;
                }

                public void setScoreAppraisal(String scoreAppraisal) {
                    this.scoreAppraisal = scoreAppraisal;
                }

                public int getRecordId() {
                    return recordId;
                }

                public void setRecordId(int recordId) {
                    this.recordId = recordId;
                }

                public int getAvatarId() {
                    return avatarId;
                }

                public void setAvatarId(int avatarId) {
                    this.avatarId = avatarId;
                }

                public String getUserAvatar() {
                    return userAvatar;
                }

                public void setUserAvatar(String userAvatar) {
                    this.userAvatar = userAvatar;
                }

                public String getSavePath() {
                    return savePath;
                }

                public void setSavePath(String savePath) {
                    this.savePath = savePath;
                }

                public String getAddTime() {
                    return addTime;
                }

                public void setAddTime(String addTime) {
                    this.addTime = addTime;
                }

                public int getCheckState() {
                    return checkState;
                }

                public void setCheckState(int checkState) {
                    this.checkState = checkState;
                }

                public int getJudgeState() {
                    return judgeState;
                }

                public void setJudgeState(int judgeState) {
                    this.judgeState = judgeState;
                }

                public int getSubmitNum() {
                    return submitNum;
                }

                public void setSubmitNum(int submitNum) {
                    this.submitNum = submitNum;
                }

                public static class ScoreInfoBean implements Serializable{
                    private String criterionName;
                    private String userScore;

                    public String getCriterionName() {
                        return criterionName;
                    }

                    public void setCriterionName(String criterionName) {
                        this.criterionName = criterionName;
                    }

                    public String getUserScore() {
                        return userScore;
                    }

                    public void setUserScore(String userScore) {
                        this.userScore = userScore;
                    }

                    @Override
                    public String toString() {
                        return "ScoreInfoBean{" + "criterionName='" + criterionName + '\'' + ", userScore='" + userScore + '\'' + '}';
                    }
                }

                @Override
                public String toString() {
                    return "UploadRecordsBean{" + "recordId=" + recordId + ", avatarId=" + avatarId + ", userAvatar='" + userAvatar + '\'' + ", savePath='" + savePath + '\'' + ", addTime='" + addTime + '\'' + ", checkState=" + checkState + ", judgeState=" + judgeState + ", submitNum=" + submitNum + ", scoreAppraisal='" + scoreAppraisal + '\'' + ", scoreInfo=" + scoreInfo + ", averageScore='" + averageScore + '\'' + '}';
                }
            }

            @Override
            public String toString() {
                return "QuestionsBean{" + "bankId=" + bankId + ", questionType='" + questionType + '\'' + ", topicName='" + topicName + '\'' + ", topicStem='" + topicStem + '\'' + ", topicImg='" + topicImg + '\'' + ", topicVideo='" + topicVideo + '\'' + ", topicGrade=" + topicGrade + ", beginReord=" + beginReord + ", allowTime=" + allowTime + ", allowSubmit=" + allowSubmit + ", uploadState='" + uploadState + '\'' + ", topicSound=" + topicSound + ", playSequence=" + playSequence + ", uploadRecords=" + uploadRecords + '}';
            }
        }
    }
}
