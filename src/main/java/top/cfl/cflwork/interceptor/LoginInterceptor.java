package top.cfl.cflwork.interceptor;

import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import top.cfl.cflwork.pojo.Constant;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.service.system.admin.AdminService;
import top.cfl.cflwork.util.jwt.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

public class LoginInterceptor implements HandlerInterceptor {
    Pattern pattern = Pattern.compile("^/login|^/$|\\..*$|.*/swagger.*|.*/center.*|.*/iconfont/.*|.*/webjars.*|.*/v2/api-docs.*$");
    public static ThreadLocal<Admin> tl=new ThreadLocal<>();
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Autowired
    private AdminService adminService;
    public static Admin currentAdmin(){
        return tl.get();
    }
    public static String myId(){
        return currentAdmin().getId();
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String p = request.getRequestURI();
        int lastQ = p.lastIndexOf("?");
        final String path = p.substring(0,lastQ==-1?p.length():lastQ);//获取路径
        if(pattern.matcher(path).find()){
            return true;
        }else{
            String token = request.getHeader(Constant.TOKEN);
            if(token==null){
                return writeResponse(new ResponseJson(false, Constant.HAVEN_LOGIN,"登录信息失效"),response);
            }else{
                Claims claims = null;
                try {
                    claims = JwtUtil.parseJWT(token);
                    if(claims!=null) {
                        if(claims.getSubject()==null){
                            String adminId = claims.getId();
                            if(adminId==null){
                                return writeResponse(new ResponseJson(false, Constant.HAVEN_LOGIN,"登录信息失效"),response);
                            }else{
                                final Admin admin = adminService.findAdminById(adminId);
                                tl.set(admin);
                                return true;
//                                final List<SysPerm> perms = adminService.findSysFuncPermsByAdminId(adminId);
//                                boolean match = perms.stream().anyMatch(perm -> antPathMatcher.match(perm.getUrlPath(), path));
//                                if(admin.getUsername().equals("admin")){
//                                    tl.set(admin);
//                                    return true;
//                                }else{
//                                    if(match){
//                                        tl.set(admin);
//                                        return true;
//                                    }else{
//                                        return writeResponse(new ResponseJson(false, Constant.HAVEN_LOGIN,"无权访问"),response);
//                                    }
//                                }

                            }
                        }else{
                            return writeResponse(new ResponseJson(false, Constant.HAVEN_LOGIN,"异常登录"),response);
                        }
                    }else{
                        return writeResponse(new ResponseJson(false, Constant.HAVEN_LOGIN,"登录信息失效"),response);
                    }

                } catch (Exception e) {
                    System.out.println("解析token失败:");
                    System.out.println(e.getMessage());
                    return writeResponse(new ResponseJson(false, Constant.HAVEN_LOGIN,"登录信息失效"),response);
                }

            }
        }
    }

    private boolean writeResponse(ResponseJson responseJson,HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=1");
        PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonStr(responseJson));
        return false;
    }
}
