package top.cfl.cflwork.util.vaptch;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VaptchaTest {
    @Value("${vaptcha.VID:5b43229da485e5041018dad8}")
    private String id;
    @Value("${vaptcha.KEY:fa3aabfb70654c048b797745746258ca}")
    private String secretKey;
    private static final String url = "http://api.vaptcha.com/v2/validate";

    public VaptchaResponse verify(String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",id);
        map.put("secretkey",secretKey);
        map.put("token",token);
        String res = HttpUtil.post(url,map);
        return JSONUtil.toBean(res, VaptchaResponse.class);
    }

}
