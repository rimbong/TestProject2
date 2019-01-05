package common.jdbc;

import java.io.IOException;
import java.io.StringReader;
import java.sql.DriverManager;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


public class DBCPInitListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("-------------------[1] DBListener 초기화 시작-------------------");
		String poolConfig = sce.getServletContext().getInitParameter("poolConfig");
		Properties prop = new Properties();		
		try {
			prop.load(new StringReader(poolConfig));
		}catch(IOException e) {
			throw new RuntimeException("config load fail",e);
		}
		loadJDBCDriver(prop);
		initConnectionPool(prop);
	}
	
	private void loadJDBCDriver(Properties prop) {
		String driverClass = prop.getProperty("jdbcdriver");
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("fail to load JDBCDriver",e);
		}
	}
	private void initConnectionPool(Properties prop) {
		try {
			String jdbcUrl = prop.getProperty("jdbcUrl");
			String username = prop.getProperty("dbUser");
			String pw = prop.getProperty("dbPass");			
			
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl,username,pw);
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);			
			String validationQuery = prop.getProperty("validationQuery");
			if(validationQuery != null && !validationQuery.isEmpty()) {
				poolableConnectionFactory.setValidationQuery(validationQuery);
			}
			GenericObjectPoolConfig<PoolableConnection> poolConfig = new GenericObjectPoolConfig<>();
			poolConfig.setTimeBetweenEvictionRunsMillis(1000L*60L*5L);
			poolConfig.setTestWhileIdle(true);
			int minIdle = getIntProperty(prop, "minIdle", 5);
			poolConfig.setMinIdle(minIdle);
			int maxTotal = getIntProperty(prop,"maxTotal", 50);
			poolConfig.setMaxTotal(maxTotal);
			
			GenericObjectPool<PoolableConnection>  connectionPool = new GenericObjectPool<>(poolableConnectionFactory,poolConfig);
			poolableConnectionFactory.setPool(connectionPool);
			
			Class.forName("org.apache.commons.dbcp2.PoolingDriver");
			PoolingDriver  driver = (PoolingDriver)DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			String poolName = prop.getProperty("poolName");
			driver.registerPool(poolName, connectionPool);
			System.out.println("-------------------[2] DBListener 초기화 종료-------------------");
			
		} catch (Exception e) {

		}
	}
	private int getIntProperty(Properties prop, String propName,int defaultValue) {
		String value = prop.getProperty("propName");
		if (value == null) return defaultValue;
		return Integer.parseInt(value);
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {}	

}
