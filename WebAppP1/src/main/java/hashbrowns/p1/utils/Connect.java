package hashbrowns.p1.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Connect {
	
	static Logger logger = Logger.getLogger();

	private static String jbdcURL = System.getenv("DB_URL");
	private static String username = System.getenv("DB_USER");
	private static String password = System.getenv("DB_PASS");
	private static Connect con;
	public Connect() {}
	
	public static synchronized Connect getConnect() {
		if(con == null) {
			con = new Connect();
			logger.log("Connection Object Created", LoggingLevel.INFO);
		}
		return con;
	}
	public Connection getConnection() throws SQLException{
		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(jbdcURL, username, password);
		} catch (SQLException | ClassNotFoundException e) {
			logger.log("Database Connection Failed", LoggingLevel.FATAL);
			e.printStackTrace();
		}

		return con;
	}
}
