package top.cfl.cflwork.pojo;

public interface Constant {
    String RES_PRE="http://10.111.144.75:8081";
    String JWT_SECRET="7786df7fc3a34e26a61c034d5ec8245d";
    String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    String DEFAULT_PWD="djd666";//登录的默认密码
    //默认头像
    interface AVATAR{
        String BOY="/headProfile/boy.png";
        String GIRL="/headProfile/girl.png";
        String MAN="/headProfile/man.png";
        String WOMEN="/headProfile/women.png";
    }

    //第三方服务的常量
    interface THIRDPARTY_SERVICE{
        String WIRIS_SHOW_IMAGE = "http://218.6.69.201:10878/pluginwiris_engine/app/showimage";
    }
    interface NEXT{
        //上一篇
        String UP="1";
        //下一篇
        String DOWN="2";
    }
    String TOKEN="token";//登录的请求令牌名称
    int HAVEN_LOGIN=909;//未登录
    int NO_PERMISSION=904;//没有权限
    int LOGIN_INVALID=905;//登录失效

    interface Redis{
        String CFLWORK_ADMIN_CACHE="adminLogin";
        String CFLWORK_ADMIN_ID_HEADER="admin_id";
        String CFLWORK_ADMIN_PERMS="adminPerms";//admin的权限存放cachename
        String CFLWORK_ADMIN_KEY_SUFFIX="_PERMS";//admin的权限存放key年后缀
        int CFLWORK_ADMIN_TIMEOUT=3600;//admin的登录缓存超时时间


    }


    interface Upload{
        String UPLOAD_AVATAR="upload/avatar/";
        String UPLOAD_COVER="upload/cover/";
        String UPLOAD_EDITOR_IMAGE="upload/editor/image/";
        String UPLOAD_EDITOR_VEDIO="upload/editor/vedio/";
        String UPLOAD_EDITOR_AUDIO="upload/editor/audio/";
        String UPLOAD_MATERIAL="upload/material/";
        String UPLOAD_APP_ICON="app/icon/";
        String UPLOAD_PROBLEM="upload/problem/";
        String UPLOAD_FILE="upload/center/";
        String UPLOAD_JOBPLANIN="upload/jobPlanIn/";
        String UPLOAD_CENTER="upload/center/";
        String UPLOAD_JOBPLANCAR="upload/jobPlanCar/";
    }


    /**
     *  字典表
     */
    interface DD{
        String SEX_CODE = "sex_code";
        String JOB_CODE = "job_code";
    }
    /**
     *  异常状态
     */
    interface EXCEPTION{
        //未启动
        int noStart=0;
        //处理中
        int doing=1;
        //已消号
        int end=2;
    }
    /**
     *  问题状态
     *  问题状态:1:未提交，2:已登记（待反馈），3:已反馈（待审核），4:已消号，5:驳回,6:申诉，7:关闭
     */
    interface PROBLEM{
        //未提交
        int NOT_SUBMIT=1;
        //已登记（待反馈）
        int REGISTERED=2;
        //已反馈（待审核）
        int FEEDBACK_ALREADY=3;
        //已消号
        int END=4;
        //驳回
        int REBUT=5;
        //申诉
        int APPEAL=6;
        //关闭
        int CLOSE=3;
    }
    /**
     *  显示状态
     */
    interface SHOW_STATUS{
        String SHOW = "0";//显示
        String HIDDEN = "1";//隐藏
    }

    /**
     *  显示状态
     */
    interface FILE_CHANGE_STUTUS{
        String ADD = "新增";//新增，或创建 文件夹，上传文件
        String DELETE = "删除";//删除   放入回收站
        String Shift_Delete="彻底删除";//彻底删除
        String Recover="还原";//从回收站还原
        String Download="下载";
        String Scann="扫描";
        String UPDATE="修改";
    }
}
