package top.cfl.cflwork.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.cfl.cflwork.dao.SysLogDao;

@Service
public class AnnotationQuartzService {
    @Autowired
    private SysLogDao sysLogDao;

    @Transactional(rollbackFor = Exception.class)
    public void annotationQuartzStatus(){
        sysLogDao.deleteManySysLog();
    }
}
