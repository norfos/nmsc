package go.kr.nmsc.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import go.kr.nmsc.dao.ItemDao;
import go.kr.nmsc.service.MonitorService;
import go.kr.nmsc.sqliteDb.SQLiteManager;
import go.kr.nmsc.web.service.WebService;

@Service("monitorService")
public class MonitorServiceImpl extends SQLiteManager implements MonitorService {
	
	//     * 100건마다 적재 시도
	private static final int OPT_BATCH_SIZE = 100;
	
	@Autowired
	ItemDao dao;
	
	@Resource(name="webService")
	private WebService webService;
	
	@Override
	public List<Map<String, Object>> reqItemList() throws Exception {
		return dao.itemList();
	}
	
	@Override
	public List<Map<String, Object>> reqItemOneList() throws Exception {
		return dao.itemOneList();
	}

	@Override
	public List<Map<String, Object>> createCheckList(List<Map<String, Object>> itemList, String timeAdd) throws Exception{
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		try {
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Java 시간 더하기
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(nowDate);
		    // 1시간 후
		    if("HOUR".equals(timeAdd)) {
		    	cal.add(Calendar.HOUR, 1);
		    }
		    String now = sdf.format(cal.getTime());
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			String HH = now.substring(8,10);
			
            for(Map<String, Object> item:itemList){
                //메모리 등록
            	String PRODUCT_STD_TIME = String.valueOf(item.get("PRODUCT_STD_TIME"));
            	String PRODUCT_CYCLE = String.valueOf(item.get("PRODUCT_CYCLE"));
            	if(PRODUCT_STD_TIME == "null" || "null".equals(PRODUCT_STD_TIME)) {
            		PRODUCT_STD_TIME = "0";
            	}
            	if(PRODUCT_CYCLE == "null" || "null".equals(PRODUCT_CYCLE)) {
            		PRODUCT_CYCLE = "0";
            	}
            	int d = Integer.parseInt(PRODUCT_STD_TIME); //3
            	int c = Integer.parseInt(PRODUCT_CYCLE); //10
            	int nowHHmin = Integer.parseInt(HH) * 60;
            	int xmin = 0;
            	for(int i=0;i<60;i+=c) {	
            		if(c > 60 && nowHHmin%c > 0) {
        				continue;
        			}
            		Map<String, Object> map = new HashMap<String, Object>();
            		xmin = i+d+c;
            		String FILE_PATH = String.valueOf(item.get("FILE_PATH")).trim().replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H",HH).replaceAll("%type",(String) item.get("DATA_TYPE"));
            		String FILE_PTN = "";
            		if("GOES16".equals(String.valueOf(item.get("SATELLITE")))) {
            			FILE_PTN = String.valueOf(item.get("FILE_PTN")).trim().replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H",HH).replaceAll("%M",String.format("%01d", i/c));
            		}else {
            			FILE_PTN = String.valueOf(item.get("FILE_PTN")).trim().replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H",HH).replaceAll("%M",String.format("%02d", i));
            		}
            		String endStr = FILE_PATH.substring(FILE_PATH.length()-1);
            		if(!"/".equals(endStr)) {
            			FILE_PATH+= "/";
            		}
            		if(xmin >= 60) {
            			map.put("MNT_HOUR", String.format("%02d",(Integer.parseInt(HH)+(xmin/60))));
            			map.put("MNT_MIN", String.format("%02d", (xmin%60)));
            		}else {
            			map.put("MNT_HOUR", HH);
            			map.put("MNT_MIN", String.format("%02d", xmin));
            		}
            		int mntHour = Integer.parseInt(String.valueOf(map.get("MNT_HOUR")));
            		if(mntHour >= 24) {
            			Date mntDate = new Date(); 
            			SimpleDateFormat mdf = new SimpleDateFormat("yyyyMMdd");
            			mdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            			// Java 시간 더하기
            		    Calendar mal = Calendar.getInstance();
            		    mal.setTime(mntDate);
            		    // 1시간 후
            		    mal.add(Calendar.DATE, 1);
            		    String mow = mdf.format(mal.getTime());
            		    map.put("MNT_YMD", mow);
            		    map.put("MNT_HOUR", String.format("%02d",mntHour-24));
            		}else {
            			map.put("MNT_YMD", yyyy+MM+dd);
            		}
            		map.put("MNT_CYCLE", c);
            		map.put("MNT_STD_TIME", item.get("PRODUCT_STD_TIME"));
            		
            		map.put("MNT_IYMD", yyyy+MM+dd);
            		map.put("MNT_IHOUR", HH);
            		map.put("MNT_IMIN", String.format("%02d", i));
            		map.put("SATELLITE", item.get("SATELLITE"));
            		map.put("SENSOR", item.get("SENSOR"));
            		map.put("DATA_LVL", item.get("DATA_LVL"));
            		map.put("DATA_TYPE", item.get("DATA_TYPE"));
            		map.put("DATA_FORMAT", item.get("DATA_FORMAT"));
            		map.put("DATA_AREA", item.get("DATA_AREA"));
            		map.put("DATA_RES", item.get("DATA_RES"));
            		map.put("DATA_PROJ", item.get("DATA_PROJ"));
            		map.put("SYNC_YN", item.get("SYNC_YN"));
            		map.put("FILE_PATH", FILE_PATH);
            		map.put("FILE_PTN", FILE_PTN);
            		map.put("MNT_CYCLE_YN", item.get("CYCLE_YN"));
            		resultList.add(map);
            		
            	}
            }
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> createCheckOneList(List<Map<String, Object>> itemList) throws Exception{
		
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		
		try {
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Java 시간 더하기
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(nowDate);
		    String now = sdf.format(cal.getTime());
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			
            for(Map<String, Object> item:itemList){
                //메모리 등록
            	String PRODUCT_STD_TIME = String.valueOf(item.get("PRODUCT_STD_TIME"));
            	String PRODUCT_CYCLE = String.valueOf(item.get("PRODUCT_CYCLE"));
            	if(PRODUCT_STD_TIME == "null" || "null".equals(PRODUCT_STD_TIME)) {
            		PRODUCT_STD_TIME = "0";
            	}
            	if(PRODUCT_CYCLE == "null" || "null".equals(PRODUCT_CYCLE)) {
            		PRODUCT_CYCLE = "0";
            	}
            	int d = Integer.parseInt(PRODUCT_STD_TIME); //3
            	int c = Integer.parseInt(PRODUCT_CYCLE); //10
            	int xmin = 0;
        		Map<String, Object> map = new HashMap<String, Object>();
        		xmin = d+c;	//600
        		
    			map.put("MNT_HOUR", String.format("%02d",(xmin/60)));//10
    			map.put("MNT_MIN", String.format("%02d", (xmin%60)));//00
        		
        		int mntHour = Integer.parseInt(String.valueOf(map.get("MNT_HOUR")));
        		if(mntHour >= 24) {
        			Date mntDate = new Date(); 
        			SimpleDateFormat mdf = new SimpleDateFormat("yyyyMMdd");
        			mdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        			// Java 시간 더하기
        		    Calendar mal = Calendar.getInstance();
        		    mal.setTime(mntDate);
        		    // 1일 후
        		    mal.add(Calendar.DATE, 1);
        		    String mow = mdf.format(mal.getTime());
        		    map.put("MNT_YMD", mow);
        		    map.put("MNT_HOUR", String.format("%02d",mntHour-24));
        		}else {
        			map.put("MNT_YMD", yyyy+MM+dd);
        		}
        		
        		String FILE_PATH = String.valueOf(item.get("FILE_PATH")).trim().replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H","00").replaceAll("%type",(String) item.get("DATA_TYPE"));
        		String FILE_PTN = String.valueOf(item.get("FILE_PTN")).trim().replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd).replaceAll("%H","00").replaceAll("%M","00");
        		String endStr = FILE_PATH.substring(FILE_PATH.length()-1);
        		if(!"/".equals(endStr)) {
        			FILE_PATH+= "/";
        		}
        		map.put("MNT_CYCLE", c);
        		map.put("MNT_STD_TIME", d);
        		
        		map.put("MNT_IYMD", String.valueOf(map.get("MNT_YMD")));
        		map.put("MNT_IHOUR", String.valueOf(map.get("MNT_HOUR")));
        		map.put("MNT_IMIN", String.valueOf(map.get("MNT_MIN")));
        		map.put("SATELLITE", item.get("SATELLITE"));
        		map.put("SENSOR", item.get("SENSOR"));
        		map.put("DATA_LVL", item.get("DATA_LVL"));
        		map.put("DATA_TYPE", item.get("DATA_TYPE"));
        		map.put("DATA_FORMAT", item.get("DATA_FORMAT"));
        		map.put("DATA_AREA", item.get("DATA_AREA"));
        		map.put("DATA_RES", item.get("DATA_RES"));
        		map.put("DATA_PROJ", item.get("DATA_PROJ"));
        		map.put("SYNC_YN", item.get("SYNC_YN"));
        		map.put("FILE_PATH", FILE_PATH);
        		map.put("FILE_PTN", FILE_PTN);
        		map.put("MNT_CYCLE_YN", item.get("CYCLE_YN"));
        		resultList.add(map);
            		
            }
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	

	@Override
	@Transactional
	public int insertCheckList(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "INSERT INTO DMS02.TB_MONITOR ("+"\n"
				+ "  MNT_SEQ,      	"+"\n"
        		+ "  MNT_CYCLE,      	"+"\n"
        		+ "  MNT_STD_TIME,      	"+"\n"
        		+ "  MNT_YMD,     	"+"\n"
        		+ "  MNT_HOUR,     	"+"\n"
        		+ "  MNT_MIN,     	"+"\n"
        		+ "  MNT_IYMD,     	"+"\n"
        		+ "  MNT_IHOUR,     	"+"\n"
        		+ "  MNT_IMIN,     	"+"\n"
        		+ "  RCT_YN,     	"+"\n"
        		+ "  RCT_CD,     	"+"\n"
        		+ "  SATELLITE,     	"+"\n"
        		+ "  SENSOR,     	"+"\n"
        		+ "  DATA_LVL,     	"+"\n"
  				+ "  DATA_TYPE,     	"+"\n"
  				+ "  DATA_FORMAT,    "+"\n"
  				+ "  DATA_AREA,     	"+"\n"
  				+ "  DATA_RES,     	"+"\n"
  				+ "  DATA_PROJ,     	"+"\n"
                + "  FILE_PATH,     	"+"\n"
                + "  FILE_PTN,		"+"\n"
                + "  VIEW_YN,		"+"\n"
                + "  SYNC_YN,		"+"\n"
                + "  MNT_CYCLE_YN		"+"\n"
                + ") VALUES (                           "+"\n"
                + "    DMS02.TB_MONITOR_MNT_SEQ.NEXTVAL,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?                                "+"\n"
		        + ")";

	     // 변수설정
	     //   - Database 변수
		Connection conn = null;
	     PreparedStatement pstmt = null;
	
	     //   - 입력 결과 변수
	     int inserted = 0;
	     
	     List<Map<String, Object>> exceptList = webService.exceptList(new HashMap<String,Object>());
	     
	     try {
	    	 conn = createConnection();
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	 boolean f = true;
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
	        	
	        	// 시간 예외 처리(GK2A)
	        	if(exceptList.size() > 0 && "GK2A".equals(dataMap.get("SATELLITE"))) {
	        		for(int j = 0; j < exceptList.size(); j++ ) {
	        			Map<String, Object> cMap = exceptList.get(j);
	        			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
	        			String cStr1 = (String) cMap.get("START_HOUR")+(String) cMap.get("START_MIN");
	        			String cStr2 = (String) cMap.get("END_HOUR")+(String) cMap.get("END_MIN");
	        			String dStr = (String) dataMap.get("MNT_IHOUR")+(String) dataMap.get("MNT_IMIN");
	        			
        				Date cDate1 = sdf.parse(cStr1);
        				Date cDate2 = sdf.parse(cStr2);
        				Date dDate = sdf.parse(dStr);
        				
        				if((cDate1.before(dDate) && cDate2.after(dDate)) || cDate1.equals(dDate) || cDate2.equals(dDate)) {
        					f = false;
        					continue;
        				}
	        		}
	        	}
	        	if(!f) {
	        		continue;
	        	}
	
		        // 입력 데이터 매핑
	        	int pi = 1;
		        pstmt.setObject(pi++, dataMap.get("MNT_CYCLE"));
		        pstmt.setObject(pi++, dataMap.get("MNT_STD_TIME"));
		        pstmt.setObject(pi++, dataMap.get("MNT_YMD"));
		        pstmt.setObject(pi++, dataMap.get("MNT_HOUR"));
		        pstmt.setObject(pi++, dataMap.get("MNT_MIN"));
		        pstmt.setObject(pi++, dataMap.get("MNT_IYMD"));
		        pstmt.setObject(pi++, dataMap.get("MNT_IHOUR"));
		        pstmt.setObject(pi++, dataMap.get("MNT_IMIN"));
		        pstmt.setObject(pi++, "N");
		        pstmt.setObject(pi++, "0");
		        pstmt.setObject(pi++, dataMap.get("SATELLITE"));
		        pstmt.setObject(pi++, dataMap.get("SENSOR"));
		        pstmt.setObject(pi++, dataMap.get("DATA_LVL"));
		        pstmt.setObject(pi++, dataMap.get("DATA_TYPE"));
		        pstmt.setObject(pi++, dataMap.get("DATA_FORMAT"));
		        pstmt.setObject(pi++, dataMap.get("DATA_AREA"));
		        pstmt.setObject(pi++, dataMap.get("DATA_RES"));
		        pstmt.setObject(pi++, dataMap.get("DATA_PROJ"));
		        pstmt.setObject(pi++, dataMap.get("FILE_PATH"));
		        pstmt.setObject(pi++, dataMap.get("FILE_PTN"));
		        pstmt.setObject(pi++, "Y");
		        pstmt.setObject(pi++, dataMap.get("SYNC_YN"));
		        pstmt.setObject(pi++, dataMap.get("MNT_CYCLE_YN"));
		         
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
	public boolean selectCheckCnt() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT COUNT(*) AS CNT FROM DMS02.TB_MONITOR WHERE MNT_IYMD = ? AND MNT_IHOUR = ?   ";
						 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isYn = false;
        try {
        	conn = createConnection();
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String now = sdf.format(nowDate);
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			String HH = now.substring(8,10);
			
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, yyyy+MM+dd);
            pstmt.setObject(2, HH);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	int result = rs.getInt(1);
	            if(result>0) {
	            	isYn = true;
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
        return isYn;
	}
	
	@Override
	public List<Map<String, Object>> selectCheckList() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT                       "+"\n"
						 + "   	MNT_SEQ,                    "+"\n"
						 + "   	MNT_CYCLE,                  "+"\n"
						 + "   	MNT_STD_TIME,                  "+"\n"
						 + "   	MNT_YMD,                    "+"\n"
						 + "   	MNT_HOUR,                   "+"\n"
						 + "   	MNT_MIN,                    "+"\n"
						 + "   	RCT_YN,                     "+"\n"
						 + "   	RCT_CD,                     "+"\n"
						 + "   	SATELLITE,                  "+"\n"
						 + "   	SENSOR,                     "+"\n"
						 + "   	DATA_LVL,                   "+"\n"
						 + "   	DATA_TYPE,                  "+"\n"
						 + "   	DATA_FORMAT,                "+"\n"
						 + "   	DATA_AREA,                  "+"\n"
						 + "   	DATA_RES,                   "+"\n"
						 + "   	DATA_PROJ,                  "+"\n"
						 + "   	FILE_PATH,                  "+"\n"
						 + "   	FILE_PTN                    "+"\n"
						 + "   FROM DMS02.TB_MONITOR              "+"\n"
						 + "   WHERE 1=1                    "+"\n"
						 + "   AND MNT_YMD = ?			    "+"\n"
						 + "   AND MNT_HOUR = ?          	"+"\n"
						 + "   AND MNT_MIN = ?           	"+"\n"
						 + "   AND RCT_YN = 'N'           	"+"\n"
						 + "   AND RCT_CD = '0'           	"+"\n"
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
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String now = sdf.format(nowDate);
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			String HH = now.substring(8,10);
			String mm = now.substring(10,12);
			
			conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, yyyy+MM+dd);
            pstmt.setObject(2, HH);
            pstmt.setObject(3, mm);
            
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
			RCT_YN = cmdExecute(String.valueOf(map.get("FILE_PATH")),String.valueOf(map.get("FILE_PTN")));
			map.put("RCT_YN",RCT_YN);
			if("Y".equals(RCT_YN)) {
				map.put("RCT_CD","1");
			}else {
				map.put("RCT_CD","2");
			}
			mtList.add(map);
		}
		return mtList;
	}
	
	
	
	public static String cmdExecute(String path, String ptn) {
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
        String cmd = "stat --printf=\"%n %z\" "+path+ptn;
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
	public int insertMonitorLog(List<Map<String, Object>> dataMapList) throws Exception{
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String nowStr = sdf.format(nowDate);
		
		String filePathString = "TB_MONITOR_LOG_"+nowStr+".csv";
		Path filePath = FileSystems.getDefault().getPath(filePathString);
		
		List<String> stringList = new ArrayList<String>();
		int inserted = 0; 
		for(int i = 0; i < dataMapList.size(); i++ ) {
			// 입력 데이터 객체
			Map<String, Object> dataMap = dataMapList.get(i);
			String lines = dataMap.get("MNT_CYCLE")+","+dataMap.get("MNT_YMD")+","+dataMap.get("MNT_HOUR")+","+dataMap.get("MNT_MIN")+","+dataMap.get("RCT_YN")+","+dataMap.get("RCT_CD")+","+dataMap.get("SATELLITE")+","+dataMap.get("SENSOR")+","+
						dataMap.get("DATA_LVL")+","+dataMap.get("DATA_TYPE")+","+dataMap.get("DATA_FORMAT")+","+dataMap.get("DATA_AREA")+","+dataMap.get("DATA_RES")+","+dataMap.get("DATA_PROJ")+","+dataMap.get("FILE_PATH")+","+dataMap.get("FILE_PTN"); 
			stringList.add(lines);
			inserted += 1;
		}
	         
	     try {
	         Files.write(filePath, stringList, StandardOpenOption.APPEND);
	
	     } catch (IOException e) {
	         // 오류출력
	         System.out.println(e.getMessage());
	         inserted = -1;
	     }
	     // 결과 반환
	     //   - 입력된 데이터 건수
	     return inserted;
	}
	
	
	@Override
	@Transactional
	public int updateMonitor(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "UPDATE DMS02.TB_MONITOR		"+"\n"
				+ " SET RCT_YN = ?, RCT_CD = ?		"+"\n"
				+ " WHERE MNT_SEQ = ?				"+"\n"
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
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("RCT_YN"));
		        pstmt.setObject(2, dataMap.get("RCT_CD"));
		        pstmt.setObject(3, dataMap.get("MNT_SEQ"));
		         
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
	public List<Map<String, Object>> selectCheckListNotRct() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT                       "+"\n"
						 + "   	MNT_SEQ,                    "+"\n"
						 + "   	MNT_CYCLE,                  "+"\n"
						 + "   	MNT_STD_TIME,                  "+"\n"
						 + "   	MNT_YMD,                    "+"\n"
						 + "   	MNT_HOUR,                   "+"\n"
						 + "   	MNT_MIN,                    "+"\n"
						 + "   	MNT_IYMD,                    "+"\n"
						 + "   	MNT_IHOUR,                    "+"\n"
						 + "   	MNT_IMIN,                    "+"\n"
						 + "   	RCT_YN,                     "+"\n"
						 + "   	RCT_CD,                     "+"\n"
						 + "   	SATELLITE,                  "+"\n"
						 + "   	SENSOR,                     "+"\n"
						 + "   	DATA_LVL,                   "+"\n"
						 + "   	DATA_TYPE,                  "+"\n"
						 + "   	DATA_FORMAT,                "+"\n"
						 + "   	DATA_AREA,                  "+"\n"
						 + "   	DATA_RES,                   "+"\n"
						 + "   	DATA_PROJ,                  "+"\n"
						 + "   	FILE_PATH,                  "+"\n"
						 + "   	FILE_PTN,                    "+"\n"
						 + "   	SYNC_YN,                    "+"\n"
						 + "   	MNT_CYCLE_YN                    "+"\n"
						 + "   FROM DMS02.TB_MONITOR              "+"\n"
						 + "   WHERE 1=1                    "+"\n"
						 + "   AND RCT_YN = 'N'             "+"\n"
						 + "   AND RCT_CD = '2'      "+"\n"
						 + "   AND MNT_YMD||MNT_HOUR||MNT_MIN BETWEEN ? AND ? "+"\n"
						 + "   ORDER BY MNT_SEQ DESC        "+"\n"
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
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// 10분전
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(nowDate);
		    cal.add(Calendar.MINUTE, -10);
		    String now = sdf.format(cal.getTime());
			
			// 5분전
		    Calendar cal2 = Calendar.getInstance();
		    cal2.setTime(nowDate);
		    cal2.add(Calendar.MINUTE, -5);
		    String now2 = sdf.format(cal2.getTime());
			
			
            // PreparedStatement 객체 생성
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, now);
            pstmt.setObject(2, now2);
            
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
	public List<Map<String, Object>> checkMonitorStat(List<Map<String, Object>> list) throws Exception{
		//- 수신 : RCT_YN Y, RCT_CD 1 체크리스트 업데이트
		// - 미수신 : RCT_YN N, RCT_CD 2 체크리스트 업데이트
		// - 결과 히스토리 등록 (체크리스트와 동일)
		String RCT_YN = "";
		List<Map<String, Object>> mtList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map:list) {
			RCT_YN = cmdExecute(String.valueOf(map.get("FILE_PATH")),String.valueOf(map.get("FILE_PTN")));
			map.put("RCT_YN",RCT_YN);
			if("Y".equals(RCT_YN)) {
				map.put("RCT_CD","3");
			}else {
				map.put("RCT_CD","4");
			}
			mtList.add(map);
		}
		return mtList;
	}
	
	@Override
	@Transactional
	public int updateMonitorLog(List<Map<String, Object>> dataMapList) throws Exception{
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String nowStr = sdf.format(nowDate);
		String logTableName = "TB_MONITOR_LOG_"+nowStr;
		
		final String sql = "UPDATE "+logTableName+"		"+"\n"
				+ " SET RCT_YN = ?, RCT_CD = ?		"+"\n"
				+ " WHERE MNT_SEQ = ?				"+"\n"
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
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("RCT_YN"));
		        pstmt.setObject(2, dataMap.get("RCT_CD"));
		        pstmt.setObject(3, dataMap.get("MNT_SEQ"));
		         
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
	public int insertMonitorNotRct(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "INSERT INTO DMS02.TB_MONITOR_NOT_RCT ("+"\n"
				+ "  MNT_SEQ,      	"+"\n"
        		+ "  MNT_CYCLE,      	"+"\n"
        		+ "  MNT_STD_TIME,      	"+"\n"
        		+ "  MNT_YMD,     	"+"\n"
        		+ "  MNT_HOUR,     	"+"\n"
        		+ "  MNT_MIN,     	"+"\n"
        		+ "  MNT_IYMD,     	"+"\n"
        		+ "  MNT_IHOUR,     	"+"\n"
        		+ "  MNT_IMIN,     	"+"\n"
        		+ "  RCT_YN,     	"+"\n"
        		+ "  RCT_CD,     	"+"\n"
        		+ "  SATELLITE,     	"+"\n"
        		+ "  SENSOR,     	"+"\n"
        		+ "  DATA_LVL,     	"+"\n"
  				+ "  DATA_TYPE,     	"+"\n"
  				+ "  DATA_FORMAT,    "+"\n"
  				+ "  DATA_AREA,     	"+"\n"
  				+ "  DATA_RES,     	"+"\n"
  				+ "  DATA_PROJ,     	"+"\n"
                + "  FILE_PATH,     	"+"\n"
                + "  FILE_PTN,		"+"\n"
                + "  SYNC_YN,		"+"\n"
                + "  MNT_CYCLE_YN,		"+"\n"
                + "  MNT_PR_SEQ		"+"\n"
                + ") VALUES (                           "+"\n"
                + "    DMS02.TB_MONITOR_NOT_RCT_MNT_SEQ.NEXTVAL,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?                                "+"\n"
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
	        	if("4".equals(dataMap.get("RCT_CD"))) {
			        // 입력 데이터 매핑
	        		int pi = 1;
			        pstmt.setObject(pi++, dataMap.get("MNT_CYCLE"));
			        pstmt.setObject(pi++, dataMap.get("MNT_STD_TIME"));
			        pstmt.setObject(pi++, dataMap.get("MNT_YMD"));
			        pstmt.setObject(pi++, dataMap.get("MNT_HOUR"));
			        pstmt.setObject(pi++, dataMap.get("MNT_MIN"));
			        pstmt.setObject(pi++, dataMap.get("MNT_IYMD"));
			        pstmt.setObject(pi++, dataMap.get("MNT_IHOUR"));
			        pstmt.setObject(pi++, dataMap.get("MNT_IMIN"));
			        pstmt.setObject(pi++, dataMap.get("RCT_YN"));
			        pstmt.setObject(pi++, dataMap.get("RCT_CD"));
			        pstmt.setObject(pi++, dataMap.get("SATELLITE"));
			        pstmt.setObject(pi++, dataMap.get("SENSOR"));
			        pstmt.setObject(pi++, dataMap.get("DATA_LVL"));
			        pstmt.setObject(pi++, dataMap.get("DATA_TYPE"));
			        pstmt.setObject(pi++, dataMap.get("DATA_FORMAT"));
			        pstmt.setObject(pi++, dataMap.get("DATA_AREA"));
			        pstmt.setObject(pi++, dataMap.get("DATA_RES"));
			        pstmt.setObject(pi++, dataMap.get("DATA_PROJ"));
			        pstmt.setObject(pi++, dataMap.get("FILE_PATH"));
			        pstmt.setObject(pi++, dataMap.get("FILE_PTN"));
			        pstmt.setObject(pi++, dataMap.get("SYNC_YN"));
			        pstmt.setObject(pi++, dataMap.get("MNT_CYCLE_YN"));
			        pstmt.setObject(pi++, dataMap.get("MNT_SEQ"));
			         
			        // Batch에 추가
					pstmt.addBatch();
					
					// Batch 실행
					if( i % OPT_BATCH_SIZE == 0 ) {
					    inserted += pstmt.executeBatch().length;
					}
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
	@Transactional
	public int deleteMonitor() throws Exception {
		final String sql = "DELETE FROM DMS02.TB_MONITOR		"+"\n"
				+ " WHERE (SUBSTR(MNT_IYMD,1,4)||'-'||SUBSTR(MNT_IYMD,5,2)||'-'||SUBSTR(MNT_IYMD,7,2)||' '||MNT_IHOUR||':'||MNT_IMIN||':00') <= TO_CHAR(SYSDATE - 4,'YYYY-MM-DD HH24:MI:SS')			    "+"\n"
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
	public void createLogTable() throws Exception{
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String nowStr = sdf.format(nowDate);
		
		String filePathString = "TB_MONITOR_LOG_"+nowStr+".csv";
		Path filePath = FileSystems.getDefault().getPath(filePathString);
		
		String header = "MNT_CYCLE,MNT_YMD,MNT_HOUR,MNT_MIN,RCT_YN,RCT_CD,SATELLITE,SENSOR,DATA_LVL,DATA_TYPE,DATA_FORMAT,DATA_AREA,DATA_RES,DATA_PROJ,FILE_PATH,FILE_PTN";
		List<String> stringList = new ArrayList<String>();
		stringList.add(header);
		if (!Files.exists(filePath)) {
	        try {
	        	Files.write(filePath, stringList, StandardOpenOption.CREATE);
	            System.out.println("File has been written successfully.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
	}
	
	@Override
	public void dropLogTable() throws Exception{
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1달전
	    cal.add(Calendar.DATE, -30);
	    String now = sdf.format(cal.getTime());
		String filePathString = "TB_MONITOR_LOG_"+now+".csv";
		
		// 파일 경로 지정
        Path filePath = Paths.get(filePathString);

        // 파일 삭제
        try {
            Files.delete(filePath);
            System.out.println("File has been deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public void cmdSqlite(String cmd) throws Exception{
        
		Process process = null;
        // cmd 변수에 명령어 입력 
        String[] command = {"/bin/sh","-c",cmd};
        
        try {
        	process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            StringBuffer sb = new StringBuffer();
           
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line);
            }
            System.out.println("처리결과 : 	"+sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                process.destroy();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
	
	@Override
	public boolean selectNotSyncCheckCnt(int stdTime) throws Exception{
		// 상수설정
		String querySql = "";
		if(stdTime == 0) {
			querySql = " AND MNT_STD_TIME = 0 ";
		}else if(stdTime > 0) {
			querySql = " AND MNT_STD_TIME > 0 ";
		}
        //   - SQL
        final String SQL = "   SELECT COUNT(*) AS CNT FROM DMS02.TB_MONITOR WHERE MNT_YMD = ? AND MNT_CYCLE = 0 "+querySql;
						 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isYn = false;
        try {
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String now = sdf.format(nowDate);
			
			conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, now);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
	            int result = rs.getInt(1);
	            if(result>0) {
	            	isYn = true;
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
        return isYn;
	}
	
	@Override
	public List<Map<String, Object>> selItemNotSyncList(Map<String, Object> map) throws Exception{
		return dao.itemNotSyncList(map);
	}
	
	@Override
	@Transactional
	public int insertCheckNotSyncList(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "INSERT INTO DMS02.TB_MONITOR ("+"\n"
				+ "  MNT_SEQ,      	"+"\n"
        		+ "  MNT_CYCLE,      	"+"\n"
        		+ "  MNT_STD_TIME,      	"+"\n"
        		+ "  MNT_YMD,     	"+"\n"
        		+ "  MNT_HOUR,     	"+"\n"
        		+ "  MNT_MIN,     	"+"\n"
        		+ "  MNT_IYMD,     	"+"\n"
        		+ "  MNT_IHOUR,     	"+"\n"
        		+ "  MNT_IMIN,     	"+"\n"
        		+ "  RCT_YN,     	"+"\n"
        		+ "  RCT_CD,     	"+"\n"
        		+ "  SATELLITE,     	"+"\n"
        		+ "  SENSOR,     	"+"\n"
        		+ "  DATA_LVL,     	"+"\n"
  				+ "  DATA_TYPE,     	"+"\n"
  				+ "  DATA_FORMAT,    "+"\n"
  				+ "  DATA_AREA,     	"+"\n"
  				+ "  DATA_RES,     	"+"\n"
  				+ "  DATA_PROJ,     	"+"\n"
                + "  FILE_PATH,     	"+"\n"
                + "  FILE_PTN,		"+"\n"
                + "  VIEW_YN,		"+"\n"
                + "  SYNC_YN,		"+"\n"
                + "  MNT_CYCLE_YN		"+"\n"
                + ") VALUES (                           "+"\n"
                + "    DMS02.TB_MONITOR_MNT_SEQ.NEXTVAL,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?                                "+"\n"
		        + ")";

	    // 변수설정
	    //   - Database 변수
		Connection conn = null;
	    PreparedStatement pstmt = null;
	
	    //   - 입력 결과 변수
	    int inserted = 0;
	     
	    //date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);
		String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		
		Date mntDate = new Date(); 
		SimpleDateFormat mdf = new SimpleDateFormat("yyyyMMdd");
		mdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar mal = Calendar.getInstance();
	    mal.setTime(mntDate);
	    // 하루 전
	    mal.add(Calendar.DATE, -1);
	    String mow = mdf.format(mal.getTime());
	    String pyyyy = mow.substring(0,4);
		String pMM = mow.substring(4,6);
		String pdd = mow.substring(6,8);
	    
	     
	    try {
	    	conn = createConnection();
	         pstmt = conn.prepareStatement(sql);
	         
	         for(int i = 0; i < dataMapList.size(); i++ ) {
	        	// 입력 데이터 객체
	        	Map<String, Object> dataMap = dataMapList.get(i);
	
		        // 입력 데이터 매핑
	        	int pi = 1;
		        pstmt.setObject(pi++, dataMap.get("PRODUCT_CYCLE"));
		        pstmt.setObject(pi++, dataMap.get("PRODUCT_STD_TIME"));
		        pstmt.setObject(pi++, yyyy+MM+dd);
		        pstmt.setObject(pi++, "00");
		        pstmt.setObject(pi++, "00");
		        pstmt.setObject(pi++, yyyy+MM+dd);
		        pstmt.setObject(pi++, "00");
		        pstmt.setObject(pi++, "00");
		        pstmt.setObject(pi++, "N");
		        pstmt.setObject(pi++, "0");
		        pstmt.setObject(pi++, dataMap.get("SATELLITE"));
		        pstmt.setObject(pi++, dataMap.get("SENSOR"));
		        pstmt.setObject(pi++, dataMap.get("DATA_LVL"));
		        pstmt.setObject(pi++, dataMap.get("DATA_TYPE"));
		        pstmt.setObject(pi++, dataMap.get("DATA_FORMAT"));
		        pstmt.setObject(pi++, dataMap.get("DATA_AREA"));
		        pstmt.setObject(pi++, dataMap.get("DATA_RES"));
		        pstmt.setObject(pi++, dataMap.get("DATA_PROJ"));
		        if("00".equals(HH)) {
		        	pstmt.setObject(pi++, String.valueOf(dataMap.get("FILE_PATH")).replaceAll("%Y",pyyyy).replaceAll("%m",pMM).replaceAll("%d",pdd));
		        }else {
		        	pstmt.setObject(pi++, String.valueOf(dataMap.get("FILE_PATH")).replaceAll("%Y",yyyy).replaceAll("%m",MM).replaceAll("%d",dd));
		        }
		        pstmt.setObject(pi++, "0");
		        pstmt.setObject(pi++, "Y");
		        pstmt.setObject(pi++, dataMap.get("SYNC_YN"));
		        pstmt.setObject(pi++, dataMap.get("CYCLE_YN"));
		         
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
	public List<Map<String, Object>> selectCheckNotSyncList(int stdTime) throws Exception{
		// 상수설정
		String querySql = "";
		if(stdTime == 0) {
			querySql = " AND MNT_STD_TIME = 0 ";
		}else if(stdTime > 0) {
			querySql = " AND MNT_STD_TIME > 0 ";
		}
        //   - SQL
        final String SQL = "   SELECT                       "+"\n"
						 + "   	MNT_SEQ,                    "+"\n"
						 + "   	MNT_CYCLE,                  "+"\n"
						 + "   	MNT_STD_TIME,                  "+"\n"
						 + "   	MNT_YMD,                    "+"\n"
						 + "   	MNT_HOUR,                   "+"\n"
						 + "   	MNT_MIN,                    "+"\n"
						 + "   	RCT_YN,                     "+"\n"
						 + "   	RCT_CD,                     "+"\n"
						 + "   	SATELLITE,                  "+"\n"
						 + "   	SENSOR,                     "+"\n"
						 + "   	DATA_LVL,                   "+"\n"
						 + "   	DATA_TYPE,                  "+"\n"
						 + "   	DATA_FORMAT,                "+"\n"
						 + "   	DATA_AREA,                  "+"\n"
						 + "   	DATA_RES,                   "+"\n"
						 + "   	DATA_PROJ,                  "+"\n"
						 + "   	FILE_PATH,                  "+"\n"
						 + "   	FILE_PTN                    "+"\n"
						 + "   FROM DMS02.TB_MONITOR              "+"\n"
						 + "   WHERE 1=1                    "+"\n"
						 + "   AND MNT_YMD = ?			    "+"\n"
						 + "   AND MNT_CYCLE = 0			    "+"\n"
						 + "   AND SYNC_YN = 'N'			    "+"\n"
						 +querySql
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
			//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			String now = sdf.format(nowDate);
			
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
	public List<Map<String, Object>> checkNotSyncMonitor(List<Map<String, Object>> list) throws Exception{
		//- 수신 : RCT_YN Y, RCT_CD 1 체크리스트 업데이트
		// - 미수신 : RCT_YN N, RCT_CD 2 체크리스트 업데이트
		// - 결과 히스토리 등록 (체크리스트와 동일)
		List<Map<String, Object>> mtList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map:list) {
			Map<String, Object> result = cmdNotSyncExecute(String.valueOf(map.get("FILE_PATH")));
			map.put("RCT_YN",result.get("RCT_YN"));
			map.put("FILE_PTN",result.get("FILE_PTN"));
			if("Y".equals(result.get("RCT_YN"))) {
				int stdTime = Integer.parseInt(String.valueOf(map.get("MNT_STD_TIME")));
				int fileCnt = Integer.parseInt(String.valueOf(map.get("FILE_PTN")));
				if(stdTime == 0) {
					if(fileCnt > 0) {
						map.put("RCT_CD","1");
					}else {
						map.put("RCT_CD","3");
					}
				}else if(stdTime > 0) {
					if(stdTime > fileCnt) {
						map.put("RCT_CD","3");
					}else {
						map.put("RCT_CD","1");
					}
				}
			}else {
				map.put("RCT_CD","4");
			}
			mtList.add(map);
		}
		return mtList;
	}
	
	
	
	public static Map<String, Object> cmdNotSyncExecute(String path) {
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
//        path = rootPath+path;	//위즈아이 로컬용. 위성센터 전용시 주석처리!
        String cmd = "find "+path+" -type f|wc -l";
        cmdList.add(cmd);
        String[] array = cmdList.toArray(new String[cmdList.size()]);
        Map<String, Object> result = new HashMap<String, Object>();
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
            if(output.length() > 0 && !output.contains("find")) {
            	result.put("RCT_YN","Y");
            	result.put("FILE_PTN",output);
            	result.put("FILE_PATH",output);
            }else {
            	result.put("RCT_YN","N");
            	result.put("FILE_PTN","");
            	result.put("FILE_PATH","0");
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
	public int updateNotSyncMonitor(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "UPDATE DMS02.TB_MONITOR		"+"\n"
				+ " SET RCT_YN = ?, RCT_CD = ?, FILE_PTN = ?		"+"\n"
				+ " WHERE MNT_SEQ = ?				"+"\n"
				+ "   AND MNT_YMD = ?			    "+"\n"
				+ "   AND FILE_PATH = ?          	"+"\n"
				+ "   AND SYNC_YN = 'N'          	"+"\n"
				+ "   AND MNT_CYCLE = 0          	"+"\n"
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
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("RCT_YN"));
		        pstmt.setObject(2, dataMap.get("RCT_CD"));
		        pstmt.setObject(3, dataMap.get("FILE_PTN"));
		        pstmt.setObject(4, dataMap.get("MNT_SEQ"));
		        pstmt.setObject(5, dataMap.get("MNT_YMD"));
		        pstmt.setObject(6, dataMap.get("FILE_PATH"));
		         
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
	public boolean selectCollectCheckCnt() throws Exception{
		
        //   - SQL
        final String SQL = "   SELECT COUNT(*) AS CNT FROM DMS02.TB_MONITOR WHERE MNT_CYCLE_YN = 'N' AND MNT_YMD = ? AND MNT_HOUR = ?";
						 
        // 변수설정
        //   - Database 변수
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isYn = false;
        try {
        	//03시에 데이터 있음
        	//체크시간 04시에 03시자료 체크해야됨
        	//date setting
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Java 시간 더하기
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(nowDate);
		    // 1시간 전
//		    cal.add(Calendar.HOUR, -1);
		    String now = sdf.format(cal.getTime());
			String yyyy = now.substring(0,4);
			String MM = now.substring(4,6);
			String dd = now.substring(6,8);
			String HH = now.substring(8,10);
			
			conn = createConnection();
            pstmt = conn.prepareStatement(SQL);
            
            // 조회 데이터 조건 매핑
            pstmt.setObject(1, yyyy+MM+dd);
            pstmt.setObject(2, HH);
            
            // 데이터 조회
            rs = pstmt.executeQuery();
            if(rs.next()) {
            	int result = rs.getInt(1);
            	if(result>0) {
                	isYn = true;
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
        return isYn;
	}
	
	@Override
	public List<Map<String, Object>> itemCollectList() throws Exception{
		return dao.itemCollectList();
	}
	
	@Override
	@Transactional
	public int insertCollectList(List<Map<String, Object>> dataMapList) throws Exception{
		final String sql = "INSERT INTO DMS02.TB_MONITOR ("+"\n"
				+ "  MNT_SEQ,      	"+"\n"
        		+ "  MNT_CYCLE,      	"+"\n"
        		+ "  MNT_STD_TIME,      	"+"\n"
        		+ "  MNT_YMD,     	"+"\n"
        		+ "  MNT_HOUR,     	"+"\n"
        		+ "  MNT_MIN,     	"+"\n"
        		+ "  MNT_IYMD,     	"+"\n"
        		+ "  MNT_IHOUR,     	"+"\n"
        		+ "  MNT_IMIN,     	"+"\n"
        		+ "  RCT_YN,     	"+"\n"
        		+ "  RCT_CD,     	"+"\n"
        		+ "  SATELLITE,     	"+"\n"
        		+ "  SENSOR,     	"+"\n"
        		+ "  DATA_LVL,     	"+"\n"
  				+ "  DATA_TYPE,     	"+"\n"
  				+ "  DATA_FORMAT,    "+"\n"
  				+ "  DATA_AREA,     	"+"\n"
  				+ "  DATA_RES,     	"+"\n"
  				+ "  DATA_PROJ,     	"+"\n"
                + "  FILE_PATH,     	"+"\n"
                + "  FILE_PTN,		"+"\n"
                + "  VIEW_YN,		"+"\n"
                + "  SYNC_YN,		"+"\n"
                + "  MNT_CYCLE_YN		"+"\n"
                + ") VALUES (                           "+"\n"
                + "    DMS02.TB_MONITOR_MNT_SEQ.NEXTVAL,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                               "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?,                                "+"\n"
		        + "    ?                                "+"\n"
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
	        	int pi = 1;
		        pstmt.setObject(pi++, dataMap.get("PRODUCT_CYCLE"));
		        pstmt.setObject(pi++, dataMap.get("PRODUCT_STD_TIME"));
		        pstmt.setObject(pi++, dataMap.get("MNT_YMD"));
		        pstmt.setObject(pi++, dataMap.get("MNT_HOUR"));
		        pstmt.setObject(pi++, dataMap.get("MNT_MIN"));
		        pstmt.setObject(pi++, dataMap.get("MNT_IYMD"));
		        pstmt.setObject(pi++, dataMap.get("MNT_IHOUR"));
		        pstmt.setObject(pi++, dataMap.get("MNT_IMIN"));
		        pstmt.setObject(pi++, dataMap.get("RCT_YN"));
		        pstmt.setObject(pi++, dataMap.get("RCT_CD"));
		        pstmt.setObject(pi++, dataMap.get("SATELLITE"));
		        pstmt.setObject(pi++, dataMap.get("SENSOR"));
		        pstmt.setObject(pi++, dataMap.get("DATA_LVL"));
		        pstmt.setObject(pi++, dataMap.get("DATA_TYPE"));
		        pstmt.setObject(pi++, dataMap.get("DATA_FORMAT"));
		        pstmt.setObject(pi++, dataMap.get("DATA_AREA"));
		        pstmt.setObject(pi++, dataMap.get("DATA_RES"));
		        pstmt.setObject(pi++, dataMap.get("DATA_PROJ"));
		        pstmt.setObject(pi++, dataMap.get("FILE_PATH"));
		        pstmt.setObject(pi++, dataMap.get("FILE_PTN"));
		        pstmt.setObject(pi++, "Y");
		        pstmt.setObject(pi++, "Y");
		        pstmt.setObject(pi++, "N");
		         
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
	public List<Map<String, Object>> checkCollectMonitor(List<Map<String, Object>> colList) throws Exception{
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
	    
		
		List<Map<String, Object>> mtList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> list:colList) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHH");
			sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
			Calendar cal2 = Calendar.getInstance();
		    cal2.setTime(nowDate);
			cal2.add(Calendar.HOUR, -12);
		    String inow = "";
			String iyyyy = "";
			String iMM = "";
			String idd = "";
			String iHH = "";
			
			for(int i=0;i<12;i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				inow = sdf2.format(cal2.getTime());
				iyyyy = inow.substring(0,4);
				iMM = inow.substring(4,6);
				idd = inow.substring(6,8);
				iHH = inow.substring(8,10);
				map.put("FILE_PATH",String.valueOf(list.get("FILE_PATH")).replaceAll("%Y",iyyyy).replaceAll("%m",iMM).replaceAll("%d",idd).replaceAll("%H",iHH));
				Map<String, Object> result = cmdNotSyncExecute(String.valueOf(map.get("FILE_PATH")));
				map.put("PRODUCT_CYCLE",list.get("PRODUCT_CYCLE"));
				map.put("PRODUCT_STD_TIME",list.get("PRODUCT_STD_TIME"));
				map.put("MNT_YMD",yyyy+MM+dd);
				map.put("MNT_HOUR",HH);
				map.put("MNT_MIN","00");
				map.put("MNT_IYMD",iyyyy+iMM+idd);
				map.put("MNT_IHOUR",iHH);
				map.put("MNT_IMIN","00");
				map.put("RCT_YN",result.get("RCT_YN"));
				map.put("SATELLITE",list.get("SATELLITE"));
				map.put("SENSOR",list.get("SENSOR"));
				map.put("DATA_LVL",list.get("DATA_LVL"));
				map.put("DATA_TYPE",list.get("DATA_TYPE"));
				map.put("DATA_FORMAT",list.get("DATA_FORMAT"));
				map.put("DATA_AREA",list.get("DATA_AREA"));
				map.put("DATA_RES",list.get("DATA_RES"));
				map.put("DATA_PROJ",list.get("DATA_PROJ"));
				map.put("FILE_PTN",result.get("FILE_PTN"));
				if("Y".equals(result.get("RCT_YN"))) {
					int stdTime = Integer.parseInt(String.valueOf(map.get("PRODUCT_STD_TIME")));
					int fileCnt = Integer.parseInt(String.valueOf(map.get("FILE_PTN")));
					if(stdTime > fileCnt) {
						map.put("RCT_CD","3");
					}else {
						map.put("RCT_CD","1");
					}
					
				}else {
					map.put("RCT_CD","4");
				}
				mtList.add(map);
				cal2.add(Calendar.MINUTE, Integer.parseInt(String.valueOf(map.get("PRODUCT_STD_TIME"))));
			}
		}
		return mtList;
	}
	
	
	@Override
	public List<Map<String, Object>> selectNotRctReScanList() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT MNT_SEQ FROM DMS02.TB_MONITOR_NOTRCT_RESCAN";
        
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
	@Transactional
	public int updateMonitorReScan(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "UPDATE DMS02.TB_MONITOR		"+"\n"
				+ " SET RCT_YN = '3', RCT_CD = 'Y'		"+"\n"
				+ " WHERE MNT_SEQ = ?				"+"\n"
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
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("MNT_SEQ"));
		         
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
	@Transactional
	public int updateMonitorNotRctReScan(List<Map<String, Object>> dataMapList) throws Exception{
		
		final String sql = "DELETE FROM DMS02.TB_MONITOR_NOT_RCT	WHERE MNT_PR_SEQ = ?	";

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
	
		        // 입력 데이터 매핑
		        pstmt.setObject(1, dataMap.get("MNT_SEQ"));
		         
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
	@Transactional
	public int updateMonitorReScanDel() throws Exception{
		
		final String sql = "DELETE FROM DMS02.TB_MONITOR_NOTRCT_RESCAN";

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
	public List<Map<String, Object>> selectViewYnList() throws Exception{
		// 상수설정
        //   - SQL
        final String SQL = "   SELECT DATA_GUBUN, DATA_TYPE, MNT_TIME FROM DMS02.TB_MONITOR_VIEW_YN";
        
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
	@Transactional
	public int updateMonitorViewYn(Map<String, Object> dataMap) throws Exception{
		String dataType = String.valueOf(dataMap.get("DATA_TYPE"));
		String dataGubun = String.valueOf(dataMap.get("DATA_GUBUN"));
		String queryTypeSql = "";
		if("OTHER".equals(dataGubun)) {
			queryTypeSql = "   		AND SATELLITE = '"+dataType+"'                           "+"\n";
		}else if("COLLECT".equals(dataGubun)) {
			queryTypeSql = "   		AND DATA_TYPE = '"+dataType+"'  AND MNT_CYCLE_YN = 'N'                         "+"\n";
		}else {
			if("LE1B".equals(dataGubun)) {
				if("RGB".equals(dataType)) {
					queryTypeSql = "   		AND DATA_TYPE LIKE 'RGB%'  AND SATELLITE = 'GK2A'                         "+"\n";
				}else if("EIR".equals(dataType)) {
					queryTypeSql = "   		AND DATA_TYPE LIKE 'EIR%'  AND SATELLITE = 'GK2A'                         "+"\n";
				}else if("DEFAULT".equals(dataType)) {
					queryTypeSql = "   		AND DATA_TYPE NOT LIKE 'RGB%' AND DATA_TYPE NOT LIKE 'EIR%' AND SATELLITE = 'GK2A'  "+"\n";
				}
			}else {
				queryTypeSql = "   		AND DATA_TYPE LIKE '"+dataType+"'||'%'  AND SATELLITE = 'GK2A'                         "+"\n";
			}
		}
		
		String sql = "UPDATE DMS02.TB_MONITOR		"+"\n"
				+ " SET VIEW_YN = 'N'				"+"\n"
				+ " WHERE MNT_SEQ IN (				"+"\n"
				+ " SELECT MNT_SEQ FROM DMS02.TB_MONITOR	"+"\n"	
				+ " WHERE MNT_IYMD||' '||MNT_IHOUR||':'||MNT_IMIN = ?				"+"\n"
				+ queryTypeSql;			
				sql += ")"
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
	         pstmt.setObject(1, dataMap.get("MNT_TIME"));
		         
	         // 입력 건수 조회
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
	public int deleteMonitorViewYn() throws Exception{
		
		final String sql = "DELETE FROM DMS02.TB_MONITOR_VIEW_YN";

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

}
