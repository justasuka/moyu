package top.cfl.cflwork.service.system.sysRole;

import top.cfl.cflwork.dao.admin.adminSysRole.AdminSysRoleDao;
import top.cfl.cflwork.dao.admin.roleSysPerm.RoleSysPermDao;
import top.cfl.cflwork.dao.admin.sysPerm.SysPermDao;
import top.cfl.cflwork.dao.admin.sysRole.SysRoleDao;
import top.cfl.cflwork.pojo.Pager;
import top.cfl.cflwork.pojo.admin.RoleSysPerm;
import top.cfl.cflwork.pojo.admin.SysPerm;
import top.cfl.cflwork.pojo.admin.SysRole;
import top.cfl.cflwork.util.ObjectKit;
import top.cfl.cflwork.util.SequenceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static top.cfl.cflwork.interceptor.LoginInterceptor.myId;

@Service
public class SysRoleService {
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private SequenceId sequenceId;
    @Autowired
    private AdminSysRoleDao adminSysRoleDao;
    @Autowired
    private RoleSysPermDao roleSysPermDao;
    @Autowired
    private SysPermDao sysPermDao;
    @Transactional(readOnly = true)
    public SysRole findSysRoleById(String id) {
        return sysRoleDao.findSysRoleById(id);
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveSysRole(SysRole sysRole) {
        sysRole.setId(sequenceId.nextId());
        sysRoleDao.saveSysRole(sysRole);
    }
    @Transactional(readOnly = true)
    public List<SysRole> findSysRoleListByCondition(SysRole sysRole) {
        return sysRoleDao.findSysRoleListByCondition(sysRole);
    }
    @Transactional(readOnly = true)
    public long findSysRoleCountByCondition(SysRole sysRole) {
        return sysRoleDao.findSysRoleCountByCondition(sysRole);
    }
    @Transactional
    public void updateSysRole(SysRole sysRole) {
        sysRoleDao.updateSysRole(sysRole);
    }
    @Transactional
    public void deleteSysRole(String id) {
        sysRoleDao.deleteSysRole(id);
        //同时删除adminRole和SysRolePerm的数据
        adminSysRoleDao.deleteAdminSysRoleByRoleId(id);
        roleSysPermDao.deleteRoleSysPermByRoleId(id);
    }
    @Transactional
    public void delsertRolePerms(Map<String, String> map) {
        String roleId = map.get("roleId");
        String permIds = map.get("permIds");
        roleSysPermDao.deleteRoleSysPermByRoleId(roleId);
        if(permIds!=null){
            String[] permArr = permIds.split(",");
            if(permArr.length>0){
                List<RoleSysPerm> roleSysPerms = new ArrayList<>();
                for (String permId : permArr) {
                    RoleSysPerm roleSysPerm = new RoleSysPerm();
                    roleSysPerm.setId(sequenceId.nextId());
                    roleSysPerm.setRoleId(roleId);
                    roleSysPerm.setPermId(permId);
                    roleSysPerms.add(roleSysPerm);
                }
                roleSysPermDao.batchSaveRoleSysPerm(roleSysPerms);
            }
        }
    }
    @Transactional(readOnly = true)
    public List<SysPerm> findAllTreeMenu() {
        SysPerm sysPerm = new SysPerm();
        Pager pager = new Pager();
        pager.setPaging(false);
        pager.setSortField("sort_num");
        pager.setSortOrder(Pager.ASC);
        sysPerm.setPager(pager);
        return ObjectKit.buildTree(sysPermDao.findSysPermListByCondition(sysPerm),"-1");
    }

    @Transactional(readOnly = true)
    public List<SysPerm> findAllSysPermTree() {
        return this.findAllTreeMenu();
    }
    public List<String> findSysPermChecked(String roleId) {
        return sysPermDao.findSysPermChecked(roleId);
    }

    @Transactional(readOnly = true)
    public List<SysRole> findRoleByAdminId() {
        return sysRoleDao.findRoleByAdminId(myId());
    }
}
