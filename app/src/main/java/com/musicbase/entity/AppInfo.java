package com.musicbase.entity;

public class AppInfo {
    private String AppName;
    private String AppVersion;
    private String Platform;
    private DeviceInfo DeviceInfo;

    public AppInfo(String appName, String appVersion, AppInfo.DeviceInfo deviceInfo) {
        AppName = appName;
        AppVersion = appVersion;
        Platform = "Android OS";
        DeviceInfo = deviceInfo;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public static class DeviceInfo {
        private String SystemName;//系统
        private String MachineModel;//设备
        private String NodeName;//别名
        private String SystemVersion;//系统版本号

        public DeviceInfo(String machineModel,  String systemVersion) {
            SystemName = "Android";
            MachineModel = machineModel;
            NodeName = "";
            SystemVersion = systemVersion;
        }

        public String getSystemName() {
            return SystemName;
        }

        public void setSystemName(String systemName) {
            SystemName = systemName;
        }

        public String getMachineModel() {
            return MachineModel;
        }

        public void setMachineModel(String machineModel) {
            MachineModel = machineModel;
        }

        public String getNodeName() {
            return NodeName;
        }

        public void setNodeName(String nodeName) {
            NodeName = nodeName;
        }

        public String getSystemVersion() {
            return SystemVersion;
        }

        public void setSystemVersion(String systemVersion) {
            SystemVersion = systemVersion;
        }
    }
}
