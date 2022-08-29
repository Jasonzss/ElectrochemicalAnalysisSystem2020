package com.bluedot.service;

import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Role;
import com.bluedot.pojo.entity.RolePermission;

import javax.servlet.http.HttpSession;

public class RoleService extends BaseService<Role>{
    public RoleService(Data data) {
        super(data);
    }

    protected RoleService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {
        insertRole();
    }

    private void insertRole(){
        Role role= (Role) paramList.get("role");
        entityInfo.addEntity(role);
        insert();
    }
}
