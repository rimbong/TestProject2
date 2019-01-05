package common.mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
/**
 * <pre>
 * sqlsession을 반환한다.
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2019.01.03
 * 
 */
public class MybatisApp {
	private static SqlSessionFactory sessionFactory;
	
	static {
		String resource = "mybatis/mybatis-config.xml";
		InputStream inputStream = null;
		try {
			 inputStream = Resources.getResourceAsStream(resource);
			 sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static SqlSessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
