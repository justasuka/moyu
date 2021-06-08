package top.cfl.cflwork.controller;

import cn.hutool.http.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cfl.cflwork.pojo.ResponseJson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/iconfont")
@RestController
public class IconfontController {
    @Value("${iconfont.link}")
    private String link;
    @GetMapping("/getIconfontLink")
    public ResponseJson getIconfontLink(){
        return new ResponseJson(link);
    }

    /**
     * 获取css连接里的css样式表
     * @return
     */
    @GetMapping("/getIconfontCss")
    public ResponseJson getIconfontCss(){
        Pattern pattern = Pattern.compile("\\.icon-[a-zA-Z0-9\\-_]+:");
        String res = HttpUtil.get("http:" + link);
        Matcher matcher = pattern.matcher(res);
        List<String> icons=new ArrayList<String>();
        while(matcher.find()){
            icons.add(matcher.group().replaceAll("\\.|:",""));
        }
        return new ResponseJson(icons);
    }
}
