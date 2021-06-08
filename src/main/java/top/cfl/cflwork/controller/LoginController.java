package top.cfl.cflwork.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cfl.cflwork.pojo.ResponseJson;
import top.cfl.cflwork.pojo.admin.Admin;
import top.cfl.cflwork.pojo.admin.SysPerm;
import top.cfl.cflwork.pojo.validateClass.GroupOne;
import top.cfl.cflwork.service.system.admin.AdminService;
import top.cfl.cflwork.util.jwt.JwtUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private AdminService adminService;
    @PostMapping("/login")
    @ApiOperation("系统用户登录")
    public ResponseJson login(@RequestBody @Validated(value = GroupOne.class) Admin admin){
        try{
            Admin exist=adminService.adminLogin(admin);
            if(exist!=null){
                List<SysPerm> permList = new ArrayList<>();
                if(exist.getUsername().equals("admin")){
                    permList = adminService.findSysFuncPermsByAdminId(null);
                }else{
                    permList=adminService.findSysFuncPermsByAdminId(exist.getId());
                }
                String token = JwtUtil.createJWT(exist.getId(), null, null, -1);
                return new ResponseJson(token,permList,exist);
            }
            return new ResponseJson(false,"错误的用户名或密码");
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseJson(false,"服务器异常");
        }
    }


}
