package com.bluedot.pojo.vo;

import com.bluedot.pojo.entity.Role;

/**
 * @Author Jason
 * @CreationDate 2022/10/03 - 19:48
 * @Description ：
 */
public class RoleWithCountNum {
    private Role role;
    private Long count;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
