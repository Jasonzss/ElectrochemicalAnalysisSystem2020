package com.bluedot.pojo.entity;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:56
 * @Description ：
 */
public class BufferSolution {
    private Integer bufferSolutionId;
    private String bufferSolutionName;
    private String bufferSolutionDesc;

    public Integer getBufferSolutionId() {
        return bufferSolutionId;
    }

    public void setBufferSolutionId(Integer bufferSolutionId) {
        this.bufferSolutionId = bufferSolutionId;
    }

    public String getBufferSolutionName() {
        return bufferSolutionName;
    }

    public void setBufferSolutionName(String bufferSolutionName) {
        this.bufferSolutionName = bufferSolutionName;
    }

    public String getBufferSolutionDesc() {
        return bufferSolutionDesc;
    }

    public void setBufferSolutionDesc(String bufferSolutionDesc) {
        this.bufferSolutionDesc = bufferSolutionDesc;
    }

    @Override
    public String toString() {
        return "BufferSolution{" +
                "bufferSolutionId=" + bufferSolutionId +
                ", bufferSolutionName='" + bufferSolutionName + '\'' +
                ", bufferSolutionDesc='" + bufferSolutionDesc + '\'' +
                '}';
    }
}
