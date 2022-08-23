package com.bluedot.pojo.entity;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:56
 * @Description ：
 */
public class MaterialType {
    private Integer materialTypeId;
    private String materialTypeName;
    private Integer materialNumber;
    private String materialDesc;

    public Integer getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(Integer materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public Integer getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(Integer materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    @Override
    public String toString() {
        return "MaterialType{" +
                "materialTypeId=" + materialTypeId +
                ", materialTypeName='" + materialTypeName + '\'' +
                ", materialNumber=" + materialNumber +
                ", materialDesc='" + materialDesc + '\'' +
                '}';
    }
}
