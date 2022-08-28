package com.bluedot.service;

import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.UserRole;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class UserRoleService extends BaseService<UserRole>{

    public UserRoleService(Data data) {
        super(data);
    }

    protected UserRoleService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {

    }

    /**
     * 修改用户角色
     */
    private void updateUserRoles(){
        int[] roleIds = (int[]) paramList.get("roleIds");
        UserRole userRole = new UserRole();
        userRole.setUserEmail((String) paramList.get("userEmail"));

        entityInfo.addEntity(userRole);
        delete();
        ArrayList<UserRole> userRoleArrayList1 = new ArrayList<>();
        for (int roleId : roleIds) {
            UserRole userRole1 = new UserRole();
            userRole1.setRoleId(roleId);
            userRole1.setUserEmail((String) paramList.get("userEmail"));
            userRoleArrayList1.add(userRole1);
        }
//        entityInfo.setEntity(userRoleArrayList1);
        insert();
    }
}
