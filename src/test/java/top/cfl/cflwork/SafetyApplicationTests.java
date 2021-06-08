package top.cfl.cflwork;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SafetyApplicationTests {
	@Autowired
	StringEncryptor encryptor;
	@Test
	public void getPass() {
		String url = encryptor.encrypt("jdbc:mysql://47.110.157.144:3306/safety?characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC");
		String name = encryptor.encrypt("root");
		String password = encryptor.encrypt("lbz20192019");
		System.out.println(url+"----------------");
		System.out.println(name+"----------------");
		System.out.println(password+"----------------");
	}

}
