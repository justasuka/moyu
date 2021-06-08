package top.cfl.cflwork.service.system.sysPerm;

import cn.hutool.core.util.StrUtil;
import top.cfl.cflwork.dao.admin.roleSysPerm.RoleSysPermDao;
import top.cfl.cflwork.dao.admin.sysPerm.SysPermDao;
import top.cfl.cflwork.pojo.Pager;
import top.cfl.cflwork.pojo.admin.SysPerm;
import top.cfl.cflwork.util.ObjectKit;
import top.cfl.cflwork.util.SequenceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SysPermService {
    @Autowired
    private SysPermDao sysPermDao;
    @Autowired
    private SequenceId sequenceId;

    @Autowired
    private RoleSysPermDao roleSysPermDao;


    @Transactional(readOnly = true)
    public SysPerm findSysPermById(String id) {
        return sysPermDao.findSysPermById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSysPerm(SysPerm sysPerm) {
        List<SysPerm> perms = new ArrayList<>();
        sysPerm.setId(sequenceId.nextId());
        perms.add(sysPerm);
        String type = sysPerm.getType();
        Boolean autoBtn = sysPerm.getAutoBtn();
        if("0".equals(type)&&autoBtn!=null&&autoBtn){
            String identify = sysPerm.getIdentify();
            String upperIdentify = StrUtil.upperFirst(identify);
            SysPerm find = new SysPerm();
            find.setId(sequenceId.nextId());
            find.setTitle("查询");
            find.setParentId(sysPerm.getId());
            find.setIdentify(identify +"-find");
            find.setUrlPath("/"+ identify +"/find"+ upperIdentify +"sByCondition");
            find.setType("1");
            perms.add(find);

            SysPerm save = new SysPerm();
            save.setId(sequenceId.nextId());
            save.setTitle("添加");
            save.setParentId(sysPerm.getId());
            save.setIdentify(identify +"-save");
            save.setUrlPath("/"+ identify +"/save"+ upperIdentify);
            save.setType("1");
            perms.add(save);


            SysPerm update = new SysPerm();
            update.setId(sequenceId.nextId());
            update.setTitle("修改");
            update.setParentId(sysPerm.getId());
            update.setIdentify(identify +"-update");
            update.setUrlPath("/"+identify+"/update/**");
            update.setType("1");
            perms.add(update);

            SysPerm look = new SysPerm();
            look.setId(sequenceId.nextId());
            look.setTitle("查看");
            look.setParentId(sysPerm.getId());
            look.setIdentify(identify +"-look");
            look.setUrlPath("/"+identify+"/look/look"+upperIdentify+"ById/*");
            look.setType("1");
            perms.add(look);

            SysPerm delete = new SysPerm();
            delete.setId(sequenceId.nextId());
            delete.setTitle("删除");
            delete.setParentId(sysPerm.getId());
            delete.setIdentify(identify +"-delete");
            delete.setUrlPath("/"+identify+"/delete"+upperIdentify+"/*");
            delete.setType("1");
            perms.add(delete);


            SysPerm leadingIn = new SysPerm();
            leadingIn.setId(sequenceId.nextId());
            leadingIn.setTitle("导入");
            leadingIn.setParentId(sysPerm.getId());
            leadingIn.setIdentify(identify +"-leadingIn");
            leadingIn.setUrlPath("/"+identify+"/importByExcel");
            leadingIn.setType("1");
            perms.add(leadingIn);

            SysPerm export = new SysPerm();
            export.setId(sequenceId.nextId());
            export.setTitle("导出");
            export.setParentId(sysPerm.getId());
            export.setIdentify(identify +"-export");
            export.setUrlPath("/"+identify+"/export"+upperIdentify);
            export.setType("1");
            perms.add(export);

            SysPerm exportModule = new SysPerm();
            exportModule.setId(sequenceId.nextId());
            exportModule.setTitle("下载模板");
            exportModule.setParentId(sysPerm.getId());
            exportModule.setIdentify(identify +"-exportModule");
            exportModule.setUrlPath("/"+identify+"/export"+upperIdentify+"Module");
            exportModule.setType("1");
            perms.add(exportModule);

            SysPerm batchUpdate = new SysPerm();
            batchUpdate.setId(sequenceId.nextId());
            batchUpdate.setTitle("批量更新");
            batchUpdate.setParentId(sysPerm.getId());
            batchUpdate.setIdentify(identify +"-batchUpdate");
            batchUpdate.setUrlPath("/"+identify+"/batchUpdate"+upperIdentify);
            batchUpdate.setType("1");
            perms.add(batchUpdate);

            SysPerm batchDelete = new SysPerm();
            batchDelete.setId(sequenceId.nextId());
            batchDelete.setTitle("批量删除");
            batchDelete.setParentId(sysPerm.getId());
            batchDelete.setIdentify(identify +"-batchDelete");
            batchDelete.setUrlPath("/"+identify+"/batchDelete"+upperIdentify);
            batchDelete.setType("1");
            perms.add(batchDelete);
        }
        sysPermDao.batchSaveSysPerm(perms);
    }

    @Transactional(readOnly = true)
    public List<SysPerm> findSysPermListByCondition(SysPerm sysPerm) {
        return sysPermDao.findSysPermListByCondition(sysPerm);
    }

    @Transactional(readOnly = true)
    public long findSysPermCountByCondition(SysPerm sysPerm) {
        return sysPermDao.findSysPermCountByCondition(sysPerm);
    }

    @Transactional
    public void updateSysPerm(SysPerm sysPerm) {
        sysPermDao.updateSysPerm(sysPerm);
    }

    @Transactional
    public void deleteSysPerm(String id) {
        sysPermDao.deleteSysPerm(id);
    }

    @Transactional(readOnly = true)
    public List<SysPerm> findAllTreeMenu() {
        SysPerm sysPerm = new SysPerm();
        Pager pager = new Pager();
        pager.setPaging(false);
        pager.setSortField("sort_num");
        pager.setSortOrder(Pager.ASC);
        sysPerm.setPager(pager);
        List<SysPerm> sysPermList = sysPermDao.findSysPermListByCondition(sysPerm);
        sysPermList.stream().
                filter((SysPerm s) -> Objects.nonNull(s.getChildren())).peek(x->{
            x.setChildren(x.getChildren().stream().sorted(Comparator.comparing(SysPerm::getSortNum)).collect(Collectors.toList()));;
        }).collect(Collectors.toList());
        return ObjectKit.buildTree(sysPermList,"-1");
    }


    @Transactional
    public void deleteSysPermRecursive(String id) {
        sysPermDao.deleteSysPerm(id);
        roleSysPermDao.deleteRoleSysPermByPermId(id);
        deleteSysPermByPId(id);
    }
    @Transactional
    public void deleteSysPermByPId(String pId) {
        List<SysPerm> perms = sysPermDao.findSysPermByPId(pId);
        for (SysPerm perm : perms) {
            sysPermDao.deleteSysPerm(perm.getId());
            roleSysPermDao.deleteRoleSysPermByPermId(perm.getId());
            deleteSysPermByPId(perm.getId());
        }
    }


    @Transactional(readOnly = true)
    public List<SysPerm> findAllSysPermTree() {
        return this.findAllTreeMenu();
    }

    @Transactional(readOnly = true)
    public List<String> findSysPermChecked(String roleId) {
        return sysPermDao.findSysPermChecked(roleId);
    }

    public List<SysPerm> findAdminTreeMenuV2(String adminId) {
        List<SysPerm> perms=sysPermDao.findAminTreeMenuV2(adminId);
        perms.stream().
                filter((SysPerm s) -> Objects.nonNull(s.getChildren())).peek(x->{
            x.setChildren(x.getChildren().stream().sorted(Comparator.comparing(SysPerm::getSortNum)).collect(Collectors.toList()));;
        }).collect(Collectors.toList());
        return ObjectKit.buildTree(perms,"-1");
    }
    public List<SysPerm> findAdminTreeMenu(String adminId) {
        List<SysPerm> perms = sysPermDao.findAminTreeMenuV2(adminId);
        perms.stream().
                filter((SysPerm s) -> Objects.nonNull(s.getChildren())).peek(x->{
            x.setChildren(x.getChildren().stream().sorted(Comparator.comparing(SysPerm::getSortNum)).collect(Collectors.toList()));;
        }).collect(Collectors.toList());
        return ObjectKit.buildTree(perms,"-1");
    }

}
