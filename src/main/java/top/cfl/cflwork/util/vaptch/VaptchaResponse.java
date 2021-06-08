package top.cfl.cflwork.util.vaptch;

import lombok.Data;

@Data
public class VaptchaResponse {
    private Integer success;
    private Integer score;
    private String msg;
}
