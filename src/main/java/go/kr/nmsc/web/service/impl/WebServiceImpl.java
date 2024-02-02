package go.kr.nmsc.web.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import go.kr.nmsc.dao.ItemDao;
import go.kr.nmsc.sqliteDb.SQLiteManager;
import go.kr.nmsc.web.service.WebService;

@Service("webService")
public class WebServiceImpl extends SQLiteManager implements WebService {
	
	//     * 100건마다 적재 시도
	private static final int OPT_BATCH_SIZE = 100;
	
	@Autowired
	ItemDao dao;
	
	
	@Override
	public Map<String, Object> reqItemList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String queryType = (String) paramMap.get("queryType");
        String SQL = "   SELECT                                                                "+"\n";
		        		if("RGB".equals(queryType)) {
							 SQL+= "   		'RGB' AS DATA_TYPE,                                            "+"\n";
						 }else if("EIR".equals(queryType)) {
							 SQL+= "   		'EIR' AS DATA_TYPE,                                            "+"\n";
						 }else if("DEFAULT".equals(queryType)) {
							 SQL+= "   		'DEFAULT' AS DATA_TYPE,                                            "+"\n";
						 }
		        		SQL += "   	COUNT(*) TOT_CNT,                                                "+"\n"
						 + "   		SUM(CASE WHEN RCT_CD = '1' THEN 1 ELSE 0 END) AS S_CNT,        		"+"\n"
						 + "   		SUM(CASE WHEN RCT_CD IN ('2','3') THEN 1 ELSE 0 END) AS R_CNT,        "+"\n"
						 + "   		SUM(CASE WHEN RCT_CD = '4' THEN 1 ELSE 0 END) AS F_CNT,        		"+"\n"
						 + "   		SUM(CASE WHEN RCT_YN = 'Y' THEN 1 ELSE 0 END) AS SUC_CNT,        "+"\n"
						 + "   		SUM(CASE WHEN RCT_YN = 'N' THEN 1 ELSE 0 END) AS FAIL_CNT,        "+"\n"
						 + "   		MAX(CASE WHEN RCT_YN='N' AND RCT_CD='4' THEN MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN ELSE ' ' END) AS MNT_TIME        "+"\n"
						 + "   FROM                                                                  "+"\n"
						 + "   		DMS02.TB_MONITOR                                                       "+"\n"
						 + "   WHERE                                                                 "+"\n"
						 + "   		MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                        "+"\n"
						 + "   		AND DATA_LVL = 'LE1B'                                            "+"\n"
						 + "   		AND SATELLITE = 'GK2A'                                            "+"\n"
						 + "   		AND VIEW_YN = 'Y'                                            "+"\n"
						 + "   		AND MNT_CYCLE_YN = 'Y'                                            "+"\n"
		        		+ "   		AND RCT_CD IN ('1','2','3','4')                                            "+"\n";
						 if("RGB".equals(queryType)) {
							 SQL+= "   		AND DATA_TYPE LIKE 'RGB%'                                            "+"\n";
						 }else if("EIR".equals(queryType)) {
							 SQL+= "   		AND DATA_TYPE LIKE 'EIR%'                                            "+"\n";
						 }else if("DEFAULT".equals(queryType)) {
							 SQL+= "   		AND DATA_TYPE NOT LIKE 'RGB%'                                            "+"\n";
							 SQL+= "   		AND DATA_TYPE NOT LIKE 'EIR%'                                            "+"\n";
						 }
		// 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	conn = createConnection();
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":00");
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if (rs.next()) {
	            resultMap.put("DATA_TYPE",queryType);
	            resultMap.put("TOT_CNT",rs.getInt("TOT_CNT"));
	            resultMap.put("S_CNT",rs.getInt("S_CNT"));
	            resultMap.put("R_CNT",rs.getInt("R_CNT"));
	            resultMap.put("F_CNT",rs.getInt("F_CNT"));
	            resultMap.put("SUC_CNT",rs.getInt("SUC_CNT"));
	            resultMap.put("FAIL_CNT",rs.getInt("FAIL_CNT"));
	            resultMap.put("MNT_TIME",rs.getString("MNT_TIME"));
            }
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return resultMap;
	}
	
	@Override
	public List<Map<String, Object>> reqItemLvlList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String lvlType = (String) paramMap.get("lvlType");
		String SQL = "	 SELECT                                                                                                                              "+"\n"
					+ "		COUNT(*) TOT_CNT,                                                                                                                "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '1' THEN 1 ELSE 0 END) AS S_CNT,                                                                          "+"\n"
					+ "		SUM(CASE WHEN RCT_CD IN ('2','3') THEN 1 ELSE 0 END) AS R_CNT,                                                                   "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '4' THEN 1 ELSE 0 END) AS F_CNT,                                                                          "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'Y' THEN 1 ELSE 0 END) AS SUC_CNT,                                                                        "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'N' THEN 1 ELSE 0 END) AS FAIL_CNT,                                                                       "+"\n"
					+ "		MAX(CASE WHEN RCT_YN='N' AND RCT_CD='4' THEN MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN ELSE ' ' END) AS MNT_TIME,                     "+"\n"
					+ "		CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END AS GR_TYPE,               "+"\n"
					+ "   	'0' AS MNT_CYCLE                                                                  "+"\n"
					+ "	FROM DMS02.TB_MONITOR                                                                                                                      "+"\n"
					+ "	WHERE 1=1                                                                                                                            "+"\n"
					+ "	AND SATELLITE = 'GK2A'                                                                                                               "+"\n"
					+ "	AND VIEW_YN = 'Y'                                                                                                                    "+"\n"
					+ "	AND MNT_CYCLE_YN = 'Y'                                                                                                                    "+"\n"
					+ "	AND DATA_LVL = ?                                                                                                                 "+"\n"
					+ "	AND MNT_CYCLE != 1440                                                                                                                 "+"\n"
					+ "	AND RCT_CD IN ('1','2','3','4')                                                                                                                 "+"\n"
					+ "	AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                             "+"\n"
					+ "	GROUP BY (CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END)                   "+"\n"
					+ "	UNION ALL                                                                                                                            "+"\n"
					+ "	SELECT                                                                                                                               "+"\n"
					+ "		COUNT(*) TOT_CNT,                                                                                                                "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '1' THEN 1 ELSE 0 END) AS S_CNT,                                                                          "+"\n"
					+ "		SUM(CASE WHEN RCT_CD IN ('2','3') THEN 1 ELSE 0 END) AS R_CNT,                                                                   "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '4' THEN 1 ELSE 0 END) AS F_CNT,                                                                          "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'Y' THEN 1 ELSE 0 END) AS SUC_CNT,                                                                        "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'N' THEN 1 ELSE 0 END) AS FAIL_CNT,                                                                       "+"\n"
					+ "		MAX(CASE WHEN RCT_YN='N' AND RCT_CD='4' THEN MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN ELSE ' ' END) AS MNT_TIME,                  "+"\n"
					+ "		CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END AS GR_TYPE,               "+"\n"
					+ "   	'1440' AS MNT_CYCLE                                                                  "+"\n"
					+ "	FROM DMS02.TB_MONITOR                                                                                                                      "+"\n"
					+ "	WHERE 1=1                                                                                                                            "+"\n"
					+ "	AND SATELLITE = 'GK2A'                                                                                                               "+"\n"
					+ "	AND VIEW_YN = 'Y'                                                                                                                    "+"\n"
					+ "	AND MNT_CYCLE_YN = 'Y'                                                                                                               "+"\n"
					+ "	AND DATA_LVL =  ?                                                                                                                "+"\n"
					+ "	AND MNT_CYCLE = 1440                                                                                                                 "+"\n"
					+ "	AND RCT_CD IN ('1','2','3','4')                                                                                                      "+"\n"
					+ "	AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                      "+"\n"    
					+ "	GROUP BY (CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END)                   "+"\n"
					+ "	ORDER BY GR_TYPE                                                                                                                     "+"\n"
					;	
						
		//   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        
        //date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1일 전
	    cal.add(Calendar.DATE, -1);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, lvlType);
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":00");
            pstmt.setObject(3, lvlType);
            pstmt.setObject(4, yyyy+MM+dd+" "+HH+":00");
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> reqItemOhterList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL = "	 SELECT                                                                                                                   "+"\n"
					+ "		SATELLITE,                                                                                                            "+"\n"
					+ "		COUNT(*) TOT_CNT,                                                                                                     "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '1' THEN 1 ELSE 0 END) AS S_CNT,                                                               "+"\n"
					+ "		SUM(CASE WHEN RCT_CD IN ('2','3') THEN 1 ELSE 0 END) AS R_CNT,                                                        "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '4' THEN 1 ELSE 0 END) AS F_CNT,                                                               "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'Y' THEN 1 ELSE 0 END) AS SUC_CNT,                                                             "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'N' THEN 1 ELSE 0 END) AS FAIL_CNT,                                                            "+"\n"
					+ "		MAX(CASE WHEN RCT_YN='N' AND RCT_CD='4' THEN MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN ELSE ' ' END) AS MNT_TIME           "+"\n"
					+ "	FROM DMS02.TB_MONITOR                                                                                                           "+"\n"
					+ "	WHERE 1=1                                                                                                                 "+"\n"
					+ "	AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                   "+"\n"
					+ "	AND SATELLITE NOT IN ('GK2A','SWFC')                                                                                                   "+"\n"
					+ "	AND MNT_CYCLE_YN = 'Y'                                                                                                   "+"\n"
					+ "	AND VIEW_YN = 'Y'                                                                                                         "+"\n"
					+ "	AND RCT_CD IN ('1','2','3','4')                                                                                                         "+"\n"
					+ "	GROUP BY SATELLITE                                                                                                        "+"\n"
					;
		
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 24시간 전
	    cal.add(Calendar.HOUR, -24);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
	//   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> reqItemCollectList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL = "	 SELECT                                                                                                                   "+"\n"
					+ "		DATA_TYPE,                                                                                                            "+"\n"
					+ "		COUNT(*) TOT_CNT,                                                                                                     "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '1' THEN 1 ELSE 0 END) AS S_CNT,                                                               "+"\n"
					+ "		SUM(CASE WHEN RCT_CD IN ('2','3') THEN 1 ELSE 0 END) AS R_CNT,                                                        "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '4' THEN 1 ELSE 0 END) AS F_CNT,                                                               "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'Y' THEN 1 ELSE 0 END) AS SUC_CNT,                                                             "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'N' THEN 1 ELSE 0 END) AS FAIL_CNT,                                                            "+"\n"
					+ "		MAX(CASE WHEN RCT_YN='N' AND RCT_CD='4' THEN MNT_IYMD||' '||MNT_IHOUR ELSE ' ' END) AS MNT_TIME           "+"\n"
					+ "	FROM DMS02.TB_MONITOR                                                                                                           "+"\n"
					+ "	WHERE 1=1                                                                                                                 "+"\n"
					+ "	AND MNT_IYMD||MNT_IHOUR >= ?                                                                                                   "+"\n"
					+ "	AND VIEW_YN = 'Y'                                                                                                         "+"\n"
					+ "	AND MNT_CYCLE_YN = 'N'                                                                                                         "+"\n"
					+ "	AND RCT_CD IN ('1','2','3','4')                                                                                                         "+"\n"
					+ "	GROUP BY DATA_TYPE                                                                                                        "+"\n"
					;
	
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 24시간 전
	    cal.add(Calendar.HOUR, -12);
	    String now = sdf.format(cal.getTime());
		
		
	//   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, now);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> reqItemSwfcList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL = "	 SELECT                                                                                                                   "+"\n"
					+ "		SATELLITE,                                                                                                             "+"\n"
					+ "		CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END AS GR_TYPE,                                                                                                           "+"\n"
					+ "		COUNT(*) TOT_CNT,                                                                                                     "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '1' THEN 1 ELSE 0 END) AS S_CNT,                                                               "+"\n"
					+ "		SUM(CASE WHEN RCT_CD IN ('2','3') THEN 1 ELSE 0 END) AS R_CNT,                                                        "+"\n"
					+ "		SUM(CASE WHEN RCT_CD = '4' THEN 1 ELSE 0 END) AS F_CNT,                                                               "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'Y' THEN 1 ELSE 0 END) AS SUC_CNT,                                                             "+"\n"
					+ "		SUM(CASE WHEN RCT_YN = 'N' THEN 1 ELSE 0 END) AS FAIL_CNT,                                                            "+"\n"
					+ "		MAX(CASE WHEN RCT_YN='N' AND RCT_CD='4' THEN MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN ELSE ' ' END) AS MNT_TIME           "+"\n"
					+ "	FROM DMS02.TB_MONITOR                                                                                                           "+"\n"
					+ "	WHERE 1=1                                                                                                                 "+"\n"
					+ "	AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                   "+"\n"
					+ "	AND SATELLITE = 'SWFC'                                                                                                   "+"\n"
					+ "	AND MNT_CYCLE_YN = 'Y'                                                                                                   "+"\n"
					+ "	AND VIEW_YN = 'Y'                                                                                                         "+"\n"
					+ "	AND RCT_CD IN ('1','2','3','4')                                                                                                         "+"\n"
					+ "	GROUP BY SATELLITE, (CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END)                                                                                                        "+"\n"
					;
		
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 24시간 전
	    cal.add(Calendar.HOUR, -24);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
	//   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	
	@Override
	public String reqAreaList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String queryType = String.valueOf(paramMap.get("dataType"));
		String queryLvl = String.valueOf(paramMap.get("dataLevel"));
		String satellite = String.valueOf(paramMap.get("satellite"));
		int mntCycle = Integer.parseInt(String.valueOf(paramMap.get("mntCycle")));
		String querySql = "";
		String queryMadeSql = "";
		if("LE1B".equals(queryLvl)) {
			if("RGB".equals(queryType)) {
				querySql = "   		AND SATELLITE = 'GK2A' AND DATA_TYPE LIKE 'RGB%'  AND DATA_LVL = '"+queryLvl+"'                         "+"\n";
				queryMadeSql = "   		AND SATELLITE = ''GK2A'' AND DATA_TYPE LIKE ''RGB%''  AND DATA_LVL = ''"+queryLvl+"''                         "+"\n";
			}else if("EIR".equals(queryType)) {
				querySql = "   		AND SATELLITE = 'GK2A' AND DATA_TYPE LIKE 'EIR%'  AND DATA_LVL = '"+queryLvl+"'                         "+"\n";
				queryMadeSql = "   		AND SATELLITE = ''GK2A'' AND DATA_TYPE LIKE ''EIR%''  AND DATA_LVL = ''"+queryLvl+"''                         "+"\n";
			}else if("DEFAULT".equals(queryType)) {
				querySql = "   		AND SATELLITE = 'GK2A' AND DATA_TYPE NOT LIKE 'RGB%' AND DATA_TYPE NOT LIKE 'EIR%' AND DATA_LVL = '"+queryLvl+"'  "+"\n";
				queryMadeSql = "   		AND SATELLITE = ''GK2A'' AND DATA_TYPE NOT LIKE ''RGB%'' AND DATA_TYPE NOT LIKE ''EIR%'' AND DATA_LVL = ''"+queryLvl+"''  "+"\n";
			}
		}else if(("LE2,LE3,LE4").contains(queryLvl)) {
			querySql = "   		AND SATELLITE = 'GK2A' AND (CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END) = '"+queryType+"'   AND DATA_LVL = '"+queryLvl+"'                        "+"\n";
			queryMadeSql = "   		AND SATELLITE = ''GK2A'' AND (CASE WHEN INSTR(DATA_TYPE,''-'') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,''-'')-1)) ELSE DATA_TYPE END) = ''"+queryType+"''   AND DATA_LVL = ''"+queryLvl+"''                        "+"\n";
//			queryMadeSql = "   		AND SATELLITE = 'GK2A' AND DATA_LVL = '"+queryLvl+"'                        "+"\n";
		}else if("OTHER".equals(queryLvl)) {
			querySql = "   		AND SATELLITE = '"+satellite+"'                            "+"\n";
			queryMadeSql = "   		AND SATELLITE = ''"+satellite+"''                            "+"\n";
		}else if("SWFC".equals(queryLvl)) {
			querySql = "   		AND SATELLITE = '"+satellite+"'  AND (CASE WHEN INSTR(DATA_TYPE,'-') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,'-')-1)) ELSE DATA_TYPE END) = '"+queryType+"'                          "+"\n";
			queryMadeSql = "   		AND SATELLITE = ''"+satellite+"'' AND (CASE WHEN INSTR(DATA_TYPE,''-'') > 0 THEN SUBSTR(DATA_TYPE,1,(INSTR(DATA_TYPE,''-'')-1)) ELSE DATA_TYPE END) = ''"+queryType+"''                           "+"\n";
		}
		
		//date setting
  		Date nowDate = new Date(); 
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  		// Java 시간 더하기
  	    Calendar cal = Calendar.getInstance();
  	    cal.setTime(nowDate);
  	    // 24시간 전
  	    cal.add(Calendar.HOUR, -24);
  	    String now = sdf.format(cal.getTime());
  	    String iDate = "";
  	    if("OTHER".equals(queryLvl) || mntCycle == 1440) {
  	    	iDate = now;
  	    }else {
  	    	iDate = String.valueOf(paramMap.get("yyyyMMdd"));
  	    }
  	    
  	    String HH = "";
  	    if(mntCycle == 1440) {
  	    	HH = "00";
  	    }else {
  	    	HH = String.valueOf(paramMap.get("HH"));
  	    }
		
		String SQL = " WITH TB_TYPE AS (  	                                                                                                                                                                                                                                                                     "+"\n"
			+ "  	SELECT                                                                                                                                                                                                                                                                                                     "+"\n"
			+ "  		DATA_TYPE                                                                                                                                                                                                                                                                                              "+"\n"
			+ "  	FROM                                                                                                                                                                                                                                                                                                       "+"\n"
			+ "  		DMS02.TB_MONITOR                                                                                                                                                                                                                                                                                             "+"\n"
			+ "  	WHERE 1=1                                                                                                                                                                                                                                                                                                     "+"\n"
			+ "   		AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                        "+"\n"
			+ "			AND VIEW_YN = 'Y'                                                                                                                                                                                                                                                                                        "+"\n"
			+ "			AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                                                                                                                                        "+"\n"
			+ "			AND RCT_CD IN ('1','2','3','4')                                                                                                                                                                                                                                                                                        "+"\n"
			+ querySql
			+ "  	GROUP BY DATA_TYPE                                                                                                                                                                                                                                                                                         "+"\n"
			+ "  	ORDER BY DATA_TYPE                                                                                                                                                                                                                                                                                         "+"\n"
			+ "  ),                                                                                                                                                                                                                                                                                                            "+"\n"
			+ "  LINES AS (                                                                                                                                                                                                                                                                                                    "+"\n"
			+ "  	SELECT 'SELECT SATELLITE, MNT_IYMD||'' ''||MNT_IHOUR||'':''||MNT_IMIN||'' / ''||MNT_CYCLE||''min'' AS MNT_TIME ' AS PART FROM DUAL                                                                                                                                                                                  "+"\n"
			+ "  	UNION ALL                                                                                                                                                                                                                                                                                                  "+"\n"
			+ "  	SELECT ', SUM(CASE WHEN DATA_TYPE = ''' || DATA_TYPE || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_YN = ''Y'' AND DATA_TYPE = ''' || DATA_TYPE || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_YN = ''N'' AND DATA_TYPE = ''' || DATA_TYPE || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_CD IN (''2'',''3'') AND DATA_TYPE = ''' || DATA_TYPE || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_CD = ''4'' AND DATA_TYPE = ''' || DATA_TYPE || ''' THEN 1 ELSE 0 END)  AS \"' || DATA_TYPE || '\"'                "+"\n"
			+ "  	FROM TB_TYPE                                                                                                                                                                                                                                                                                               "+"\n"
			+ "  	UNION ALL                                                                                                                                                                                                                                                                                                  "+"\n"
			+ "  	SELECT ', SUM(CASE WHEN RCT_CD != ''1'' THEN 1 ELSE 0 END) AS RCT_NOT_CNT' FROM DUAL                  	                                                                                                                                                                                                       "+"\n"
			+ "  	UNION ALL                                                                                                                                                                                                                                                                                                  "+"\n"
			+ "  	SELECT ' FROM DMS02.TB_MONITOR WHERE 1=1 "+queryMadeSql+" AND MNT_IYMD||'' ''||MNT_IHOUR||'':''||MNT_IMIN >= ''"+iDate+"''||'' ''||''"+HH+"''||'':''||''00'' AND RCT_CD IN (''1'',''2'',''3'',''4'') GROUP BY SATELLITE, MNT_CYCLE, MNT_IYMD||'' ''||MNT_IHOUR||'':''||MNT_IMIN ORDER BY MNT_IYMD||'' ''||MNT_IHOUR||'':''||MNT_IMIN||MNT_CYCLE DESC'  FROM DUAL                                                          "+"\n"
			+ "  )                                                                                                                                                                                                                                                                                                             "+"\n"
			+ "  SELECT PART AS SQL_TEXT                                                                                                                                                                                                                                                                      "+"\n"
			+ "  FROM LINES                                                                                                                                                                                                                                                                                                    "+"\n"
		     ;  
		System.out.println(paramMap);
		System.out.println(SQL);
        // 변수설정
        //   - Database 변수
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String text = "";
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, iDate+" "+HH+":00");
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            while(rs.next()) {
            	text += (String) rs.getObject("SQL_TEXT");
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println("###reqAreaList : "+e.getMessage());
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
        
        // 결과 반환
        //   - 조회된 데이터 리스트
        System.out.println(text);
        return text;
	}
	
	@Override
	public Map<String, Object> textToQueryMap(String text) throws Exception {
        //   - 조회 결과 변수                                                                                                                                                                                                                                                                                                  
        final Set<String> columnNames = new LinkedHashSet<String>();        
        final List<Map<String, Object>> headList = new ArrayList<Map<String, Object>>();
        final List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(text);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            Map<String, Object> headMap = new HashMap<String, Object>();
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            headList.add(headMap);
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    dataList.add(resultMap);
                }
            }
         
            
        } catch (SQLException e) {
        	System.out.println("###textToQueryMap : "+e.getMessage());
        	
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("headList",columnNames);
        result.put("dataList",dataList);
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return result;
	}

	@Override
	public List<Map<String, Object>> reqFileList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
		//paramMap : {satellite=GOES16, mntTime=20240111 00:10 / 10min, dataTypeOne=CSR, dataType=GOES16, dataLevel=OTHER, yyyyMMdd=20240111, HH=04, mm=47}
		String dataLevel = String.valueOf(paramMap.get("dataLevel"));
        //   - SQL
        String SQL = "   SELECT                                                                "+"\n"
						 + "   		MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN AS MNT_TIME,                  "+"\n"
						 + "   		FILE_PATH,                                                           "+"\n"
						 + "   		FILE_PTN,                                                           "+"\n"
						 + "   		RCT_CD,                                                           "+"\n"
						 + "   		DATA_AREA                                                           "+"\n"
						 + "   FROM DMS02.TB_MONITOR                                                       "+"\n"
						 + "   WHERE DATA_TYPE = ?                                                 "+"\n"
						 + "    AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN = ?           				 "+"\n"
						 + "    AND MNT_CYCLE = ?           				 						"+"\n"
						 + "    AND SATELLITE = ?           				 						"+"\n"
						 + "    AND RCT_CD IN ('1','2','3','4')           				 						"+"\n";
						 if(!"OTHER".equals(dataLevel) && !"SWFC".equals(dataLevel)) {
							 SQL+= "   		AND DATA_LVL = '"+dataLevel+"'                                            "+"\n";
						 }
						 ;
						System.out.println(paramMap);
						System.out.println(SQL);
        //20231215 05:52 / 2min
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑 //dataTypeOne
            String mntTime = (String) paramMap.get("mntTime");
            String[] timeArray = mntTime.split("/");
            pstmt.setObject(1, paramMap.get("dataTypeOne"));
            pstmt.setObject(2, timeArray[0].trim());
            pstmt.setObject(3, timeArray[1].replace("min","").trim());
            pstmt.setObject(4, paramMap.get("satellite"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> reqFileCollectList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT                                                                "+"\n"
						 + "   		MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN AS MNT_TIME,                  "+"\n"
						 + "   		FILE_PATH,                                                           "+"\n"
						 + "   		FILE_PTN,                                                           "+"\n"
						 + "   		RCT_YN,                                                           "+"\n"
						 + "   		DATA_AREA                                                           "+"\n"
						 + "   FROM DMS02.TB_MONITOR                                                       "+"\n"
						 + "   WHERE DATA_TYPE = ?                                                 "+"\n"
						 + "    AND DATA_AREA = ?                                                 "+"\n"
						 + "    AND MNT_IYMD||' '||MNT_IHOUR = ?           				 				"+"\n"
						 + "    AND MNT_CYCLE_YN = 'N'           				 						"+"\n"
						 + "    AND RCT_CD IN ('1','2','3','4')           				 						"+"\n"
						 ;
        //20231215 05:52 / 2min
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        
        //date setting
  		Date nowDate = new Date(); 
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
  		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  		// Java 시간 더하기
  	    Calendar cal = Calendar.getInstance();
  	    cal.setTime(nowDate);
  	    // 24시간 전
  	    cal.add(Calendar.HOUR, -12);
        
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑 //dataTypeOne
            pstmt.setObject(1, paramMap.get("dataType"));
            pstmt.setObject(2, paramMap.get("dataTypeOne"));
            pstmt.setObject(3, paramMap.get("mntTime"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	
	@Override
	public List<Map<String, Object>> reqItemNonRctList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT                                              "+"\n"
						 + "   	FILE_PATH, FILE_PTN                                "+"\n"
						 + "   FROM                                                "+"\n"
						 + "   	DMS02.TB_MONITOR_NOT_RCT                                 "+"\n"
						 + "   ORDER BY                                            "+"\n"
						 + "   	MNT_IYMD DESC, MNT_IHOUR DESC, MNT_IMIN DESC          "+"\n"
						 + "   LIMIT 20                                            "+"\n"
						 ;
        
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
//            pstmt.setObject(1, yyyy+MM+dd);
//            pstmt.setObject(2, HH);
//            pstmt.setObject(3, mm);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	@Transactional
	public int insertMonitorViewYn(List<Map<String, Object>> dataMapList, String dataType, String dataLevel) throws Exception{
		
		String sql = "INSERT INTO DMS02.TB_MONITOR_VIEW_YN ( DATA_GUBUN, DATA_TYPE, MNT_TIME )		"+"\n"
				+ " VALUES (?, ?, ?)				"+"\n"
				;

	     // 변수설정
	     //   - Database 변수
		Connection conn = null;
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int updateed = 0;
	
	     try {
	    	 conn = createConnection();
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
	        	String mntTime = (String) dataMap.get("mntTime");
	            String[] timeArray = mntTime.split("/");
		        // 입력 데이터 매핑
	            pstmt.setObject(1, dataLevel);
	            pstmt.setObject(2, dataType);
		        pstmt.setObject(3, timeArray[0].trim());
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
					updateed += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         updateed += pstmt.executeBatch().length;
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         updateed = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	    	 closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return updateed;
	}
	
	
	
	@Override
	public int gk2aListCnt(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String lvlType = (String) paramMap.get("lvlType");
		String sqlText = "";
		if("a".equals(lvlType)) {
			sqlText = "	AND DATA_AREA IN ('FD','TP','EA','EXA') "+"\n";
		}else if("b".equals(lvlType)) {
			sqlText = "	AND DATA_AREA IN ('ELA','KO','SKO','NKO') "+"\n";
		}else if("c".equals(lvlType)) {
			sqlText = "	AND DATA_AREA = 'LA' "+"\n";
		}
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	COUNT(*) AS CNT                                                                                                                          "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = 'GK2A'                                                                                                                                                          "+"\n"
					+ sqlText
					+ "AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					;
        // 변수설정
        //   - Database 변수
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
            	result = rs.getInt("CNT");
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return result;
	}
	
	
	@Override
	public List<Map<String, Object>> gk2aList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String lvlType = (String) paramMap.get("lvlType");
		String sqlText = "";
		if("a".equals(lvlType)) {
			sqlText = "	AND DATA_AREA IN ('FD','TP','EA','EXA') "+"\n";
		}else if("b".equals(lvlType)) {
			sqlText = "	AND DATA_AREA IN ('ELA','KO','SKO','NKO') "+"\n";
		}else if("c".equals(lvlType)) {
			sqlText = "	AND DATA_AREA = 'LA' "+"\n";
		}
		String SQL =  "SELECT * FROM (                                                                                                                                                                          "+"\n"
				+ "	SELECT a.*, ROWNUM as rnum FROM ( SELECT																																							 "+"\n"
					+ "	MNT_SEQ,                                                                                                                          "+"\n"
					+ "	MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN AS INSERT_TIME,                                                                                                                          "+"\n"
					+ "	FILE_PATH,                                                                                                                                                                      "+"\n"
					+ "	FILE_PTN,                                                                                                                                                                      "+"\n"
					+ "	DATA_AREA,                                                                                                                                                                     "+"\n"
					+ "	TRUNC(SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')) || ' DAY ' ||	"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')),1)*24),'FM00') || ':' ||		"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS'))*24,1)*60),'FM00') AS DELAY_TIME              "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = 'GK2A'                                                                                                                                                          "+"\n"
					+ sqlText
					+ "AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					+ "ORDER BY MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN DESC, FILE_PTN DESC                                                                                                                            "+"\n"
					+ ") a)  WHERE rnum >= ? and rnum <=?                                                                                                                           "+"\n"
					;
		
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            pstmt.setObject(2, paramMap.get("startnum"));
            pstmt.setObject(3, paramMap.get("endnum"));
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> gk2aTimeGroup(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String lvlType = (String) paramMap.get("lvlType");
		String sqlText = "";
		if("a".equals(lvlType)) {
			sqlText = "	AND DATA_AREA IN ('FD','TP','EA','EXA') "+"\n";
		}else if("b".equals(lvlType)) {
			sqlText = "	AND DATA_AREA IN ('ELA','KO','SKO','NKO') "+"\n";
		}else if("c".equals(lvlType)) {
			sqlText = "	AND DATA_AREA = 'LA' "+"\n";
		}
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	MNT_IYMD, MNT_IHOUR||':'||MNT_IMIN AS MNT_TIME, COUNT(*) AS GROUP_CNT                                                                                                                          "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = 'GK2A'                                                                                                                                                          "+"\n"
					+ sqlText
					+ "AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					+ "GROUP BY MNT_IYMD, MNT_IHOUR||':'||MNT_IMIN                                                                                                                           "+"\n"
					+ "ORDER BY MNT_IYMD DESC, MNT_IHOUR||':'||MNT_IMIN DESC                                                                                                                            "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public int otherListCnt(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	COUNT(*) AS CNT                                                                                                                         "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = ?															"+"\n"
					+ "AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					;
        // 변수설정
        //   - Database 변수
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("satellite"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	result = rs.getInt("CNT");
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return result;
	}
	
	@Override
	public List<Map<String, Object>> otherGroup() throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT SATELLITE, SYNC_YN FROM DMS02.TB_MONITOR_NOT_RCT WHERE SATELLITE NOT IN ('GK2A','SWFC') AND MNT_CYCLE_YN = 'Y' GROUP BY SATELLITE, SYNC_YN   "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> otherList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT * FROM (    SELECT a.*, ROWNUM as rnum FROM (	 SELECT                                                                                                                                                                        "+"\n"
					+ "	MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN AS INSERT_TIME,                                                                                                                          "+"\n"
					+ "	FILE_PATH,                                                                                                                                                                      "+"\n"
					+ "	FILE_PTN,                                                                                                                                                                      "+"\n"
					+ "	DATA_AREA,                                                                                                                                                                     "+"\n"
					+ "	DATA_TYPE,                                                                                                                                                                     "+"\n"
					+ "	TRUNC(SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')) || ' DAY ' ||	"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')),1)*24),'FM00') || ':' ||		"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS'))*24,1)*60),'FM00') AS DELAY_TIME               "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = ?															"+"\n"
					+ "AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					+ "ORDER BY MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN DESC, FILE_PTN DESC                                                                                                                            "+"\n"
					+ ") a)  WHERE rnum >= ? and rnum <=?                                                                                                                          "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("satellite"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            pstmt.setObject(3, paramMap.get("startnum"));
            pstmt.setObject(4, paramMap.get("endnum"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> otherTimeGroup(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	MNT_IYMD||' '||MNT_IHOUR||'('||COUNT(*)||')' AS GROUP_CNT                                                                                                                          "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = ?															"+"\n"
					+ "AND MNT_CYCLE_YN = 'Y'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					+ "GROUP BY MNT_IYMD||' '||MNT_IHOUR                                                                                                                            "+"\n"
					+ "ORDER BY MNT_IYMD||' '||MNT_IHOUR DESC                                                                                                                          "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("satellite"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public int collectListCnt(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	COUNT(*) AS CNT                                                                                                                         "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND DATA_TYPE = ?															"+"\n"
					+ "AND MNT_CYCLE_YN = 'N'                                                                                                                                                                "+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					;
        // 변수설정
        //   - Database 변수
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("dataType"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	result = rs.getInt("CNT");
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return result;
	}
	
	@Override
	public List<Map<String, Object>> collectGroup() throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT DATA_TYPE FROM DMS02.TB_MONITOR_NOT_RCT WHERE MNT_CYCLE_YN = 'N' GROUP BY DATA_TYPE   "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> collectList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  " SELECT a.*, ROWNUM as rnum FROM (	 SELECT                                                                                                                                                                           				"+"\n"
					+ "	MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN AS INSERT_TIME,                                                                                                                          "+"\n"
					+ "	FILE_PATH,                                                                                                                                                                      		"+"\n"
					+ "	FILE_PTN,                                                                                                                                                                      				"+"\n"
					+ "	DATA_AREA,                                                                                                                                                                     			"+"\n"
					+ "	TRUNC(SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')) || ' DAY ' ||	"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')),1)*24),'FM00') || ':' ||		"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS'))*24,1)*60),'FM00') AS DELAY_TIME               "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       			"+"\n"
					+ "AND DATA_TYPE = ?																																										"+"\n"
					+ "AND MNT_CYCLE_YN = 'N'                                                                                                                                                                		"+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               			"+"\n"
					+ "ORDER BY MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN DESC, FILE_PATH DESC                                                                                                                            "+"\n"
					+ ") a)  WHERE rnum >= ? and rnum <=?                                                                                                                            "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("dataType"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            pstmt.setObject(3, paramMap.get("startnum"));
            pstmt.setObject(4, paramMap.get("endnum"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> swfcGroup() throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  "SELECT DATA_TYPE FROM DMS02.TB_MONITOR_NOT_RCT WHERE SATELLITE = 'SWFC' GROUP BY DATA_TYPE   "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> swfcList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
		String SQL =  " SELECT a.*, ROWNUM as rnum FROM (	 SELECT                                                                                                                                                                           				"+"\n"
					+ "	MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN AS INSERT_TIME,                                                                                                                          "+"\n"
					+ "	FILE_PATH,                                                                                                                                                                      		"+"\n"
					+ "	FILE_PTN,                                                                                                                                                                      				"+"\n"
					+ "	DATA_AREA,                                                                                                                                                                     			"+"\n"
					+ "	TRUNC(SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')) || ' DAY ' ||	"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS')),1)*24),'FM00') || ':' ||		"+"\n"
					+ " TO_CHAR(TRUNC(mod((SYSDATE - TO_DATE((SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00'),'YYYY-MM-DD HH24:MI:SS'))*24,1)*60),'FM00') AS DELAY_TIME               "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       			"+"\n"
					+ "AND SATELLITE = 'SWFC'																																										"+"\n"
					+ "AND DATA_TYPE = ?																																										"+"\n"
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               			"+"\n"
					+ "ORDER BY MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN DESC, FILE_PATH DESC                                                                                                                            "+"\n"
					+ ") a)  WHERE rnum >= ? and rnum <=?                                                                                                                            "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("dataType"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            pstmt.setObject(3, paramMap.get("startnum"));
            pstmt.setObject(4, paramMap.get("endnum"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> selMetadbList(Map<String, Object> map) throws Exception{
		return dao.selMetadbList(map);
	}
	
	@Override
	public int updateMetadb(Map<String, Object> map) throws Exception{
		return dao.updateMetadb(map);
	}
	
	@Override
	@Transactional
	public int deleteExceptTime() throws Exception {
		final String sql = "DELETE FROM DMS02.TB_MONITOR_EXCEPT_TIME		";

	     // 변수설정
	     //   - Database 변수
		Connection conn = null;
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int updateed = 0;
	
	     try {
	    	 conn = createConnection();
	        pstmt = conn.prepareStatement(sql);
	         
		    // 쿼리 실행    
			pstmt.executeUpdate();
			
			// 업데이트 건수 조회
			updateed = pstmt.getUpdateCount();
				
	        // 트랜잭션 COMMIT
	        conn.commit();
	
	     } catch (SQLException e) {
	        // 오류출력
	        System.out.println(e.getMessage());
	         
	        // 트랜잭션 ROLLBACK
	        if( conn != null ) {
	             conn.rollback();
	        }
	         
	        // 오류
	        updateed = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	    	 closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return updateed;
	}
	
	@Override
	@Transactional
	public int insertExceptTime(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "INSERT INTO DMS02.TB_MONITOR_EXCEPT_TIME ("+"\n"
				+ "  START_HOUR,     		"+"\n"
        		+ "  START_MIN,      	"+"\n"
        		+ "  END_HOUR,     	"+"\n"
        		+ "  END_MIN     	"+"\n"
                + ") VALUES (                           "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?                               "+"\n"
		        + ")";

	     // 변수설정
	     //   - Database 변수
		Connection conn = null;
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int inserted = 0;
	
	     try {
	    	 conn = createConnection();
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("startHour"));
		        pstmt.setObject(2, dataMap.get("startMin"));
		        pstmt.setObject(3, dataMap.get("endHour"));
		        pstmt.setObject(4, dataMap.get("endMin"));
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
				    inserted += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         inserted += pstmt.executeBatch().length;
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         inserted = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	    	 closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return inserted;
	}
	
	@Override
	public List<Map<String, Object>> exceptList(Map<String,Object> paramMap) throws Exception {
        //   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	START_HOUR,                                                                                                                          "+"\n"
					+ "	START_MIN,                                                                                                                                                                      "+"\n"
					+ "	END_HOUR,                                                                                                                                                                     "+"\n"
					+ "	END_MIN                                                                                                                                                                     "+"\n"
					+ "FROM DMS02.TB_MONITOR_EXCEPT_TIME                                                                                                                                                                 "+"\n"
					;
		
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public String reqAreaCollectList(Map<String,Object> paramMap) throws Exception {
		//date setting
  		Date nowDate = new Date(); 
  		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
  		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
  		// Java 시간 더하기
  	    Calendar cal = Calendar.getInstance();
  	    cal.setTime(nowDate);
  	    // 24시간 전
  	    cal.add(Calendar.HOUR, -12);
  	    String now = sdf.format(cal.getTime());
		
		// 상수설정
        //   - SQL
		String dataType = (String) paramMap.get("dataType");
		String querySql = "";
		String queryMadeSql = "";
		
		querySql = "   		AND DATA_TYPE = '"+dataType+"'   "+"\n";
		queryMadeSql = "   		AND DATA_TYPE = ''"+dataType+"''   "+"\n";
		
		String SQL = " WITH TB_TYPE AS (  	                                                                                                                                                                                                                                                                     "+"\n"
			+ "  	SELECT                                                                                                                                                                                                                                                                                                     "+"\n"
			+ "  		DATA_AREA                                                                                                                                                                                                                                                                                              "+"\n"
			+ "  	FROM                                                                                                                                                                                                                                                                                                       "+"\n"
			+ "  		DMS02.TB_MONITOR                                                                                                                                                                                                                                                                                             "+"\n"
			+ "  	WHERE 1=1                                                                                                                                                                                                                                                                                                     "+"\n"
			+ "   		AND MNT_IYMD||MNT_IHOUR >= ?                        "+"\n"
			+ "			AND VIEW_YN = 'Y'                                                                                                                                                                                                                                                                                        "+"\n"
			+ "			AND RCT_CD IN ('1','2','3','4')                                                                                                                                                                                                                                                                                        "+"\n"
			+ "			AND MNT_CYCLE_YN = 'N'                                                                                                                                                                                                                                                                                        "+"\n"
			+ querySql
			+ "  	GROUP BY DATA_AREA                                                                                                                                                                                                                                                                                         "+"\n"
			+ "  ),                                                                                                                                                                                                                                                                                                            "+"\n"
			+ "  LINES AS (                                                                                                                                                                                                                                                                                                    "+"\n"
			+ "  	SELECT 'SELECT MNT_IYMD||'' ''||MNT_IHOUR AS MNT_TIME ' AS PART FROM DUAL                                                                                                                                                                                                                                    "+"\n"
			+ "  	UNION ALL                                                                                                                                                                                                                                                                                                  "+"\n"
			+ "  	SELECT ', SUM(CASE WHEN DATA_AREA = ''' || DATA_AREA || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_YN = ''Y'' AND DATA_AREA = ''' || DATA_AREA || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_YN = ''N'' AND DATA_AREA = ''' || DATA_AREA || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_CD IN (''2'',''3'') AND DATA_AREA = ''' || DATA_AREA || ''' THEN 1 ELSE 0 END)||''/''||SUM(CASE WHEN RCT_CD = ''4'' AND DATA_AREA = ''' || DATA_AREA || ''' THEN 1 ELSE 0 END)  AS \"' || DATA_AREA || '\"'                "+"\n"
			+ "  	FROM TB_TYPE                                                                                                                                                                                                                                                                                               "+"\n"
			+ "  	UNION ALL                                                                                                                                                                                                                                                                                                  "+"\n"
			+ "  	SELECT ', SUM(CASE WHEN RCT_CD != ''1'' THEN 1 ELSE 0 END) AS RCT_NOT_CNT' FROM DUAL                   	                                                                                                                                                                                                       "+"\n"
			+ "  	UNION ALL                                                                                                                                                                                                                                                                                                  "+"\n"
			+ "  	SELECT ' FROM DMS02.TB_MONITOR WHERE 1=1 "+queryMadeSql+" AND MNT_IYMD||MNT_IHOUR >= ''"+now+"'' AND RCT_CD IN (''1'',''2'',''3'',''4'') AND MNT_CYCLE_YN = ''N'' GROUP BY MNT_IYMD||'' ''||MNT_IHOUR ORDER BY MNT_IYMD||'' ''||MNT_IHOUR DESC' FROM DUAL                                                           "+"\n"
			+ "  )                                                                                                                                                                                                                                                                                                             "+"\n"
			+ "  SELECT PART AS SQL_TEXT                                                                                                                                                                                                                                                                      "+"\n"
			+ "  FROM LINES                                                                                                                                                                                                                                                                                                    "+"\n"
		     ;  
        // 변수설정
        //   - Database 변수
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String text = "";
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, now);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            while(rs.next()) {
            	text += (String) rs.getObject("SQL_TEXT");
            }
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
        // 결과 반환
        //   - 조회된 데이터 리스트
        return text;
	}
	
	@Override
	public List<Map<String, Object>> reqFileNotSyncList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT                                                                "+"\n"
						 + "   		MNT_YMD AS MNT_TIME,                  "+"\n"
						 + "   		FILE_PATH,                                                           "+"\n"
						 + "   		FILE_PTN,                                                           "+"\n"
						 + "   		RCT_YN,                                                           "+"\n"
						 + "   		DATA_AREA                                                           "+"\n"
						 + "   FROM DMS02.TB_MONITOR                                                       "+"\n"
						 + "   WHERE SATELLITE = ?                                                 "+"\n"
						 + "   AND DATA_TYPE = ?                                                 "+"\n"
						 + "    AND MNT_IYMD = ?   						        				 "+"\n"
						 + "    AND SYNC_YN = 'N'   						        				 "+"\n"
						 + "    AND MNT_CYCLE_YN = 'Y'   						        				 "+"\n"
						 + "    AND MNT_CYCLE = 0   						        				 "+"\n"
						 + "    AND RCT_CD IN ('1','2','3','4')           				 						"+"\n"
						 ;
        
        //   - 조회 결과 변수
        final Set<String> columnNames = new HashSet<String>();        
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑 //dataTypeOne
            pstmt.setObject(1, paramMap.get("satellite"));
            pstmt.setObject(2, paramMap.get("dataTypeOne"));
            pstmt.setObject(3, paramMap.get("mntTime"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
                resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> notRctCheckList(Map<String,Object> paramMap) throws Exception {
		// 상수설정
		String dataArea = (String) paramMap.get("dataArea");
		String sqlText = "";
		if("a".equals(dataArea)) {
			sqlText = "	AND DATA_AREA IN ('FD','TP','EA','EXA') "+"\n";
		}else if("b".equals(dataArea)) {
			sqlText = "	AND DATA_AREA IN ('ELA','KO','SKO','NKO') "+"\n";
		}else if("c".equals(dataArea)) {
			sqlText = "	AND DATA_AREA = 'LA' "+"\n";
		}
        //   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	MNT_SEQ,                                                                                                                          "+"\n"
					+ "	FILE_PATH,                                                                                                                                                                      "+"\n"
					+ "	FILE_PTN                                                                                                                                                                      "+"\n"
					+ "FROM DMS02.TB_MONITOR                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = ?															"+"\n"
					+ "AND RCT_YN = 'N'                                                                                                                                                                "+"\n"
					+ "AND RCT_CD = '4'                                                                                                                                                                "+"\n"
					+sqlText
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("satellite"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> notRctCheckList2(Map<String,Object> paramMap) throws Exception {
		// 상수설정
		String dataArea = (String) paramMap.get("dataArea");
		String sqlText = "";
		if("a".equals(dataArea)) {
			sqlText = "	AND DATA_AREA IN ('FD','TP','EA','EXA') "+"\n";
		}else if("b".equals(dataArea)) {
			sqlText = "	AND DATA_AREA IN ('ELA','KO','SKO','NKO') "+"\n";
		}else if("c".equals(dataArea)) {
			sqlText = "	AND DATA_AREA = 'LA' "+"\n";
		}
        //   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	MNT_SEQ,                                                                                                                          "+"\n"
					+ "	FILE_PATH,                                                                                                                                                                      "+"\n"
					+ "	FILE_PTN                                                                                                                                                                      "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOT_RCT                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND SATELLITE = ?															"+"\n"
					+sqlText
					+ "AND MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN >= ?                                                                                                                               "+"\n"
					;
		//   - 조회 결과 변수                                                                                                                                                              
        final Set<String> columnNames = new HashSet<String>();                                                                                                                            
        final List<Map<String, Object>> selected = new ArrayList<Map<String, Object>>();
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSetMetaData meta = null;
        ResultSet rs = null;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, paramMap.get("satellite"));
            pstmt.setObject(2, paramMap.get("yyyyMMdd")+" "+paramMap.get("HH")+":"+paramMap.get("mm"));
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            // 조회된 데이터의 컬럼명 저장
            meta = pstmt.getMetaData();
            for(int i=1; i<=meta.getColumnCount(); i++) {
                columnNames.add(meta.getColumnName(i));
            }
            // ResultSet -> List<Map> 객체
            Map<String, Object> resultMap = null;
            
            while(rs.next()) {
            	resultMap = new HashMap<String, Object>();
                
                for(String column : columnNames) {
                    resultMap.put(column, rs.getObject(column));
                }
                
                if( resultMap != null ) {
                    selected.add(resultMap);
                }
            }
            
            
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return selected;
	}
	
	@Override
	public List<Map<String, Object>> checkMonitor(List<Map<String, Object>> list) throws Exception{
		//- 수신 : RCT_YN Y, RCT_CD 1 체크리스트 업데이트
		// - 미수신 : RCT_YN N, RCT_CD 2 체크리스트 업데이트
		// - 결과 히스토리 등록 (체크리스트와 동일)
		String RCT_YN = "";
		List<Map<String, Object>> mtList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map:list) {
			RCT_YN = cmdExecute(String.valueOf(map.get("FILE_PATH"))+String.valueOf(map.get("FILE_PTN")));
			map.put("RCT_YN",RCT_YN);
			if("Y".equals(RCT_YN)) {
				map.put("MNT_SEQ",map.get("MNT_SEQ"));
				mtList.add(map);
			}
		}
		return mtList;
	}
	
	
	
	public static String cmdExecute(String path) {
        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        StringBuffer successOutput = new StringBuffer(); // 성공 스트링 버퍼
        StringBuffer errorOutput = new StringBuffer(); // 오류 스트링 버퍼
        BufferedReader successBufferReader = null; // 성공 버퍼
        BufferedReader errorBufferReader = null; // 오류 버퍼
        String msg = null; // 메시지
 
        List<String> cmdList = new ArrayList<String>();
        cmdList.add("/bin/sh");
        cmdList.add("-c");
        // 명령어 셋팅
//        String cmd = "ls -l --time-style full-iso "+path;
//        String cmd = "locate "+path;
//        String cmd = "stat --printf=\"%n %z\" "+rootPath+path;
        String cmd = "stat --printf=\"%n %z\" "+path;
        cmdList.add(cmd);
        String[] array = cmdList.toArray(new String[cmdList.size()]);
        String result = "";
        String output = "";
        try {
            // 명령어 실행
            process = runtime.exec(array);
 
            // shell 실행이 정상 동작했을 경우
            successBufferReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
 
            while ((msg = successBufferReader.readLine()) != null) {
                successOutput.append(msg);
            }
 
            // shell 실행시 에러가 발생했을 경우
            errorBufferReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "EUC-KR"));
            while ((msg = errorBufferReader.readLine()) != null) {
                errorOutput.append(msg);
            }
 
            // 프로세스의 수행이 끝날때까지 대기
            process.waitFor();
 
            // shell 실행이 정상 종료되었을 경우
            if (process.exitValue() == 0) {
//                System.out.println("성공");
//                System.out.println(successOutput.toString());
                output = successOutput.toString();
            } else {
                // shell 실행이 비정상 종료되었을 경우
//                System.out.println("비정상 종료");
//                System.out.println(successOutput.toString());
                output = successOutput.toString();
            }
 
            // shell 실행시 에러가 발생
            if (!"".equals(errorOutput.toString())) {
                // shell 실행이 비정상 종료되었을 경우
//                System.out.println("오류");
//                System.out.println(errorOutput.toString());
                output = errorOutput.toString();
            }
            
            ///home/norfos/nmsc/nmsc.war 2023-11-09 21:31:40.205487524 +0900
            if(output.contains(path) && !output.contains("cannot")) {
            	result = "Y";
            }else {
            	result = "N";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
                if (successBufferReader != null) successBufferReader.close();
                if (errorBufferReader != null) errorBufferReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }
	
	
	
	@Override
	@Transactional
	public int insertNotRctTimeChk(int time) throws Exception{
		
		final String sql = "MERGE INTO DMS02.TB_MONITOR_NOTRCT_TIME MNT USING dual ON (MNT.NOT_RCT_GUBUN  = ?) "+"\n"
				+ " WHEN MATCHED THEN UPDATE SET MNT.NOT_RCT_TIME  = ?								"+"\n"
				+ " WHEN NOT MATCHED THEN INSERT (NOT_RCT_GUBUN, NOT_RCT_TIME) VALUES (?, ?);		"+"\n"
				;

	     // 변수설정
	     //   - Database 변수
		Connection conn = null;
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int updateed = 0;
	
	     try {
	    	 conn = createConnection();
	    	 pstmt = conn.prepareStatement(sql);
	        
	         
	    	 // 입력 데이터 매핑
	    	 pstmt.setObject(1, "CK");
	    	 pstmt.setObject(2, time);
	    	 pstmt.setObject(3, "CK");
	    	 pstmt.setObject(4, time);
	    	 
	    	 // 쿼리 실행    
	    	 pstmt.executeUpdate();
				
	    	 // 업데이트 건수 조회
	    	 updateed = pstmt.getUpdateCount();
	        
	    	 // 트랜잭션 COMMIT
	    	 conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         updateed = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	    	 closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return updateed;
	}
	
	@Override
	public int SelNotRctTimeChk() throws Exception {
        
		//   - SQL
		String SQL =  "SELECT                                                                                                                                                                          "+"\n"
					+ "	NOT_RCT_TIME                                                                                                                          "+"\n"
					+ "FROM DMS02.TB_MONITOR_NOTRCT_TIME                                                                                                                                                                 "+"\n"
					+ "WHERE 1=1                                                                                                                                                                       "+"\n"
					+ "AND NOT_RCT_GUBUN = 'CK'                                                                                                                                                          "+"\n"
					;
        // 변수설정
        //   - Database 변수
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int result = 0;
        try {
        	conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
            	result = rs.getInt("NOT_RCT_TIME");
            }
            
        } catch (SQLException e) {
            // 오류처리
            System.out.println(e.getMessage());
            
        } finally  {
        	closeResultSet(rs);
        	closeStatement(pstmt);
        	closeConnection(conn);
        }
 
        // 결과 반환
        //   - 조회된 데이터 리스트
        return result;
	}
	
	@Override
	@Transactional
	public int insertNotRctReScan(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "INSERT INTO DMS02.TB_MONITOR_NOTRCT_RESCAN ( MNT_SEQ ) VALUES ( ? )";

	     // 변수설정
	     //   - Database 변수
		Connection conn = null;
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int inserted = 0;
	     
	     try {
	    	 conn = createConnection();
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("MNT_SEQ"));
		         
		        // Batch에 추가
				pstmt.addBatch();
				
				// Batch 실행
				if( i % OPT_BATCH_SIZE == 0 ) {
				    inserted += pstmt.executeBatch().length;
				}
	         }
	
	         // 입력 건수 조회
	         inserted += pstmt.executeBatch().length;
 
	         // 트랜잭션 COMMIT
	         conn.commit();
	
	     } catch (SQLException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         
	         // 트랜잭션 ROLLBACK
	         if( conn != null ) {
	             conn.rollback();
	         }
	         
	         // 오류
	         inserted = -1;
	
	     } finally {
	    	 closeStatement(pstmt);
	    	 closeConnection(conn);
	     }
	
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return inserted;
	}

}
