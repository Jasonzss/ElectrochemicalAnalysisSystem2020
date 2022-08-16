package com.bluedot.pojo.entity;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:55
 * @Description ：
 */
public class Permission {
    private Integer permissionId;
    private String permissionName;
    private String serviceName;
    private String url;

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
