package jSqlDeveloper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {


	
	public static   Connection getConnection(String user,String password,String ipAddr,int port,String sid) throws ClassNotFoundException, SQLException {
		Connection conn =null;
		
		String jdbcUrl="jdbc:oracle:thin:@"+ipAddr+":"+Integer.toString(port)+":"+sid;
		
		
			Class.forName("oracle.jdbc.driver.OracleDriver");
			DriverManager.setLoginTimeout(3); // 로그인 3초제한.
				conn = DriverManager.getConnection(jdbcUrl, user, password);
			System.out.println("DB커넥션 성공");

		
		return conn;
	}
	
}
