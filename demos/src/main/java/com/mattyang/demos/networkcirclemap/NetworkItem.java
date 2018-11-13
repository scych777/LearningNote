package com.mattyang.demos.networkcirclemap;

public class NetworkItem {
    String name;
    String mac;
    String role;
    String uplinkMac;
    String uplinkType;
    String varInfo;
    String phoneNickName;
    int index;

    public NetworkItem() {
    }

    public NetworkItem(String name, String mac, String uplinkMac) {
        this.name = name;
        this.mac = mac;
        this.uplinkMac = uplinkMac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPhoneNickName() {
        return phoneNickName;
    }

    public void setPhoneNickName(String phoneNickName) {
        this.phoneNickName = phoneNickName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUplinkMac() {
        return uplinkMac;
    }

    public void setUplinkMac(String uplinkMac) {
        this.uplinkMac = uplinkMac;
    }

    public String getUplinkType() {
        return uplinkType;
    }

    public void setUplinkType(String uplinkType) {
        this.uplinkType = uplinkType;
    }

    public String getVarInfo() {
        return varInfo;
    }

    public void setVarInfo(String varInfo) {
        this.varInfo = varInfo;
    }
}
