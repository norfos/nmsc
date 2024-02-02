package go.kr.nmsc.sqliteDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;


public class SQLiteManager {
	 // 상수 설정
    //   - Database 변수
//	private static String  dbFile = "/vol01/home/dms02test01/MONITOR/nmscMonitor.db";	//위성센터 개발서버
//	private static String  dbFile = "C:/20231213/nmscMonitor.db";						//위성센터 로컬서버
//	private static String  dbFile = "/home/norfos/nmsc/nmscMonitor.db";					//wizai 개발서버
//	private static String  dbFile = "C:/작업폴더_20231212/nmscMonitor.db";						//wizai 로컬
    private static final String FILE_DB_URL = "jdbc:oracle:thin:@172.19.23.29:1522/gk2atest";
    private static final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";
    private static final String ID = "dms02_read";
    private static final String PW = "wkfytjqltmDB4#";
    private static final String VALIDATION_QUERY = "SELECT 1 FROM DUAL";
    private static BasicDataSource dataSource;
    
    

    //  - Database 옵션 변수
    private static final boolean OPT_AUTO_COMMIT = false;
    private static final int OPT_VALID_TIMEOUT = 30000;
 
    // 변수 설정
    //   - Database 접속 정보 변수
    private Connection conn = null;
    private String driver = null;
    private String url = null;
 
    // 생성자
 /**   
    public SQLiteManager(){
        this(FILE_DB_URL);
    }
    public SQLiteManager(String url) {
        // JDBC Driver 설정
        this.driver = JDBC_DRIVER;
        this.url = url;
    }
 **/
    
   
    static{
        initializeDataSource();
    }

    private static void initializeDataSource() {
    	dataSource = new BasicDataSource();
    	dataSource.setDriverClassName(JDBC_DRIVER);
    	dataSource.setUrl(FILE_DB_URL);
    	dataSource.setUsername(ID);
    	dataSource.setPassword(PW);
        
    	dataSource.setValidationQuery(VALIDATION_QUERY);
    	dataSource.setTestOnBorrow(true);
    	//4개의 설정은 동일하게 설정하는 것이 예외 케이스를 줄일 수 있음
        // 풀 설정
    	dataSource.setInitialSize(100);	//최초 시점에 getConnection() 를 통해 커넥션 풀에 채워 넣을 커넥션 개수 (default = 0)
    	dataSource.setMaxTotal(150);	//동시에 사용할 수 있는 최대 커넥션 개수 (default = 8)
    	dataSource.setMaxIdle(50);	//Connection Pool에 반납할 때 최대로 유지될 수 있는 커넥션 개수 (default = 8)
    	dataSource.setMinIdle(50);	//최소한으로 유지할 커넥션 개수 (default = 0)
    	dataSource.setDefaultAutoCommit(OPT_AUTO_COMMIT);	//default = 드라이버기본값
    	
    	dataSource.setMaxWaitMillis(10000);	//pool이 고갈되었을 경우 최대 대기 타임 ms
        
    }
/**
    public Connection createConnection() throws SQLException {
    	// DB 연결 객체 생성
        try {
			this.conn = dataSource.getConnection();
			// 로그 출력
            System.out.println("CONNECTED");
            
        } catch (SQLException e) {
			e.printStackTrace();
		}
        return this.conn;
    }
    
    public void closeConnection() {
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.conn = null;
 
            // 로그 출력
            System.out.println("CLOSED");
        }
    }
**/    
    public Connection createConnection() throws SQLException {
    	return dataSource.getConnection();
    }
    
    public void closeConnection(Connection conn) {
    	if( conn != null ) {
	    	try {
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } 
    	}
    }
    
    public static void closeResultSet(ResultSet rs) {
    	if(rs != null) {
    		try {
    			rs.close();
    		}catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    public static void closeStatement(PreparedStatement ps) {
    	if(ps != null) {
    		try {
    			ps.close();
    		}catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
    public Connection ensureConnection() {
        try {
            if( this.conn == null || this.conn.isValid(OPT_VALID_TIMEOUT) ) {
                closeConnection();      // 연결 종료
                createConnection();     // 연결
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        return this.conn;
    }


    
    // DB 연결 함수
    public Connection createConnection() {
        try {
            // JDBC Driver Class 로드
            Class.forName(this.driver);
 
            // DB 연결 객체 생성
            this.conn = DriverManager.getConnection(this.url, ID, PW);
 
            // 로그 출력
            System.out.println("CONNECTED");
 
            // 옵션 설정
            //   - 자동 커밋
            this.conn.setAutoCommit(OPT_AUTO_COMMIT);
 
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
 
        return this.conn;
    }
 
    // DB 연결 종료 함수
    public void closeConnection() {
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.conn = null;
 
            // 로그 출력
            System.out.println("CLOSED");
        }
    }
 
    // DB 재연결 함수
    public Connection ensureConnection() {
        try {
            if( this.conn == null || this.conn.isValid(OPT_VALID_TIMEOUT) ) {
                closeConnection();      // 연결 종료
                createConnection();     // 연결
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        return this.conn;
    }
 
    // DB 연결 객체 가져오기
    public Connection getConnection() {
        return this.conn;
    }
    **/
}
