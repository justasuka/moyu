package top.cfl.cflwork.service.system.adminSysRole;

import top.cfl.cflwork.dao.admin.adminSysRole.AdminSysRoleDao;
import top.cfl.cflwork.pojo.admin.AdminSysRole;
import top.cfl.cflwork.util.SequenceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AdminSysRoleService {
    @Autowired
    private AdminSysRoleDao adminSysRoleDao;
    @Autowired
    private SequenceId sequenceId;

    @Transactional(readOnly = true)
    public AdminSysRole findAdminSysRoleById(String id) {
        return adminSysRoleDao.findAdminSysRoleById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveAdminSysRole(AdminSysRole adminSysRole) {
        adminSysRole.setId(sequenceId.nextId());
        adminSysRoleDao.saveAdminSysRole(adminSysRole);
    }
    @Transactional(readOnly = true)
    public List<AdminSysRole> findAdminSysRoleListByCondition(AdminSysRole adminSysRole) {
        return adminSysRoleDao.findAdminSysRoleListByCondition(adminSysRole);
    }
    @Transactional(readOnly = true)
    public long findAdminSysRoleCountByCondition(AdminSysRole adminSysRole) {
        return adminSysRoleDao.findAdminSysRoleCountByCondition(adminSysRole);
    }
    @Transactional
    public void updateAdminSysRole(AdminSysRole adminSysRole) {
        adminSysRoleDao.updateAdminSysRole(adminSysRole);
    }
    @Transactional
    public void deleteAdminSysRole(String id) {
        adminSysRoleDao.deleteAdminSysRole(id);
    }
    @Transactional
    public void batchSaveAdminSysRole(List<AdminSysRole> adminSysRoles) {
        adminSysRoles.forEach(adminSysRole -> adminSysRole.setId(sequenceId.nextId()));
    }
    @Transactional
    public void delsertAdminSysRoles(Map<String, String> map) {
        String adminId = map.get("adminId");

        adminSysRoleDao.deleteAdminSysRoleByAdminId(adminId);
        List<AdminSysRole> list = new ArrayList<>();
        String roleStr = map.get("roleIds");
        if(roleStr==null)return;
        String[] roleIds= roleStr.split(",");
        if(roleIds.length>0){
            for (String roleId : roleIds) {
                AdminSysRole adminSysRole = new AdminSysRole();
                adminSysRole.setId(sequenceId.nextId());
                adminSysRole.setAdminId(adminId);
                adminSysRole.setRoleId(roleId);
                list.add(adminSysRole);
            }
            adminSysRoleDao.batchSaveAdminSysRole(list);
        }
    }
}
