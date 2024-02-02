package go.kr.nmsc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import go.kr.nmsc.service.MonitorService;


@Component("monitorController")
public class MonitorController {

	@Resource(name = "monitorService")
	private MonitorService monitorService;
	
	
	public void createCheckList() throws Exception {

		//아이템리스트 가져오기
		List<Map<String, Object>> itemList = monitorService.reqItemList();
		
		//주기(분)별 체크리스트 만들기
		List<Map<String, Object>> checkList = monitorService.createCheckList(itemList, "HOUR");
		
		//메모리 sqllist 등록(1시간후 TODO리스트)
		int inserted = monitorService.insertCheckList(checkList);
		if( inserted >= 0 ) {
            System.out.println(String.format("createCheckList.insertCheckList 데이터 입력 성공: %d건", inserted));
        } else {
            System.out.println("createCheckList.insertCheckList 데이터 입력 실패");
        }
		
	}
	
	public void createOneCheckList() throws Exception {

		//아이템리스트 가져오기
		List<Map<String, Object>> itemList = monitorService.reqItemOneList();
		
		//주기(분)별 체크리스트 만들기
		List<Map<String, Object>> checkList = monitorService.createCheckOneList(itemList);
		
		//메모리 sqllist 등록(1시간후 TODO리스트)
		int inserted = monitorService.insertCheckList(checkList);
		if( inserted >= 0 ) {
            System.out.println(String.format("createCheckOneList.insertCheckList 데이터 입력 성공: %d건", inserted));
        } else {
            System.out.println("createCheckOneList.insertCheckList 데이터 입력 실패");
        }
		
	}
	
	public void chkMonitor() throws Exception {
		System.out.println("chkMonitor");
		long start = System.currentTimeMillis();
		boolean existYn = monitorService.selectCheckCnt();
		if(!existYn) {
			//아이템리스트 가져오기
			List<Map<String, Object>> itemList = monitorService.reqItemList();
			//주기(분)별 체크리스트 만들기
			List<Map<String, Object>> checkList = monitorService.createCheckList(itemList, "");
			//메모리 sqllist 등록(1시간후 TODO리스트)
			monitorService.insertCheckList(checkList);
		}
		
		//1. 체크리스트 조회(UTC기준 현재 년월일시분으로 검색)
		List<Map<String, Object>> itemList = monitorService.selectCheckList();
		
		if(itemList.size() > 0) {
			//2. stat ctime 검색
			List<Map<String, Object>> monitorChkList = monitorService.checkMonitor(itemList);
			
			//3. 모니터결과 체크리스트 업데이트
			int updateed = monitorService.updateMonitor(monitorChkList);
			if( updateed >= 0 ) {
				System.out.println(String.format("chkMonitor.updateMonitor 데이터 업데이트 성공: %d건", updateed));
			} else {
				System.out.println("chkMonitor.updateMonitor 데이터 업데이트 실패");
			}
			
			//4. 모니터결과 히스토리 등록
			int inserted = monitorService.insertMonitorLog(monitorChkList);
			if( inserted >= 0 ) {
				System.out.println(String.format("chkMonitor.insertMonitorLog 데이터 입력 성공: %d건", inserted));
			} else {
				System.out.println("chkMonitor.insertMonitorLog 데이터 입력 실패");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("###execute time : "+(end-start)/1000.0+"sec");
		
	}
	
	public void chkMonitorNotRct() throws Exception {
		System.out.println("chkMonitorNotRct");
		long start = System.currentTimeMillis();
		//* 미수신 체크리스트 다시 체크 
		// - 체크리스트 조회(UTC기준 5분전 미수신(RCT_CD 2))
		// - 체크 결과값 체크리스트 업데이트, 히스토리 업데이트
		List<Map<String, Object>> itemList = monitorService.selectCheckListNotRct();
		
		if(itemList.size() > 0) {
			//2. stat ctime 검색
			List<Map<String, Object>> monitorChkList = monitorService.checkMonitorStat(itemList);
			
			//3. 모니터결과 체크리스트 업데이트
			int updateedMonitor = monitorService.updateMonitor(monitorChkList);
			if( updateedMonitor >= 0 ) {
	            System.out.println(String.format("chkMonitorNotRct.updateMonitor 성공: %d건", updateedMonitor));
	        } else {
	            System.out.println("chkMonitorNotRct.updateMonitor 데이터 업데이트 실패");
	        }
			
			//4. 모니터결과 히스토리 업데이트
			int updateedMonitorLog = monitorService.insertMonitorLog(monitorChkList);
			if( updateedMonitorLog >= 0 ) {
	            System.out.println(String.format("chkMonitorNotRct.insertMonitorLog 업데이트 성공: %d건", updateedMonitorLog));
	        } else {
	            System.out.println("chkMonitorNotRct.insertMonitorLog 업데이트 실패");
	        }
			
			//5. 모니터결과 미수신 결과가 초과 미수신(RCT_CD:4)건 별도 저장
			int inserted = monitorService.insertMonitorNotRct(monitorChkList);
			if( inserted >= 0 ) {
				System.out.println(String.format("chkMonitorNotRct.insertMonitorNotRct 데이터 입력 성공: %d건", inserted));
			} else {
				System.out.println("chkMonitorNotRct.insertMonitorNotRct 데이터 입력 실패");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("###execute time : "+(end-start)/1000.0+"sec");
	}
	
	public void delMonitor() throws Exception {
		//* 체크리스트 삭제(2시간경과건)
		monitorService.deleteMonitor();
	}
	
	public void createNDropMonitorLog() throws Exception {
		//하루단위로 로그 테이블 생성 및 삭제
		monitorService.createLogTable();
		monitorService.dropLogTable();
	}
	
	public void dbSizeDel() throws Exception {
		//sqlite3 vacuum 실행 
		String cmd = "sqlite3 nmscMonitor.db VACUUM;";
		monitorService.cmdSqlite(cmd);
	}
	
	public void chkMonitorNotSync(int stdTime) throws Exception {
		boolean existYn = monitorService.selectNotSyncCheckCnt(stdTime);
		if(!existYn) {
			//아이템리스트 가져오기
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("stdTime",stdTime);
			List<Map<String, Object>> itemList = monitorService.selItemNotSyncList(map);
			//메모리 sqllist 등록
			monitorService.insertCheckNotSyncList(itemList);
		}
		//1. 체크리스트 조회(UTC기준 현재 년월일시분으로 검색)
		List<Map<String, Object>> itemList = monitorService.selectCheckNotSyncList(stdTime);
		
		if(itemList.size() > 0) {
			//2. stat ctime 검색
			List<Map<String, Object>> monitorChkList = monitorService.checkNotSyncMonitor(itemList);
			
			//3. 모니터결과 체크리스트 업데이트
			int updateed = monitorService.updateNotSyncMonitor(monitorChkList);
			if( updateed >= 0 ) {
				System.out.println(String.format("chkMonitorNotSync.updateNotSyncMonitor 데이터 업데이트 성공: %d건", updateed));
			} else {
				System.out.println("chkMonitorNotSync.updateNotSyncMonitor 데이터 업데이트 실패");
			}
			
			//4. 모니터결과 히스토리 등록
			int inserted = monitorService.insertMonitorLog(monitorChkList);
			if( inserted >= 0 ) {
				System.out.println(String.format("chkMonitorNotSync.insertMonitorLog 데이터 입력 성공: %d건", inserted));
			} else {
				System.out.println("chkMonitorNotSync.insertMonitorLog 데이터 입력 실패");
			}
			
			//5. 모니터결과 미수신 결과가 초과 미수신(RCT_CD:4)건 별도 저장
			int inserted2 = monitorService.insertMonitorNotRct(monitorChkList);
			if( inserted2 >= 0 ) {
				System.out.println(String.format("chkMonitorNotSync.insertMonitorNotRct 데이터 입력 성공: %d건", inserted2));
			} else {
				System.out.println("chkMonitorNotSync.insertMonitorNotRct 데이터 입력 실패");
			}
		}
	}
	
	public void chkMonitorCollect() throws Exception {
		//해당 시간대 monitor 데이터 있는지 여부 체크
		boolean existYn = monitorService.selectCollectCheckCnt();
		if(!existYn) {
		//없으면
			//metadb 조회
			List<Map<String, Object>> itemList = monitorService.itemCollectList();
			//cmd check
			List<Map<String, Object>> checkList = monitorService.checkCollectMonitor(itemList);
			//monitor 등록
			int updateed = monitorService.insertCollectList(checkList);
			if( updateed >= 0 ) {
				System.out.println(String.format("chkMonitorCollect.insertCollectList 데이터 등록 성공: %d건", updateed));
			} else {
				System.out.println("chkMonitorCollect.insertCollectList 데이터 업데이트 실패");
			}
			//4. 모니터결과 히스토리 등록
			int inserted = monitorService.insertMonitorLog(checkList);
			if( inserted >= 0 ) {
				System.out.println(String.format("chkMonitorCollect.insertMonitorLog 데이터 입력 성공: %d건", inserted));
			} else {
				System.out.println("chkMonitorCollect.insertMonitorLog 데이터 입력 실패");
			}
			
			//5. 모니터결과 미수신 결과가 초과 미수신(RCT_CD:4)건 별도 저장
			int inserted2 = monitorService.insertMonitorNotRct(checkList);
			if( inserted2 >= 0 ) {
				System.out.println(String.format("chkMonitorCollect.insertMonitorNotRct 데이터 입력 성공: %d건", inserted2));
			} else {
				System.out.println("chkMonitorCollect.insertMonitorNotRct 데이터 입력 실패");
			}
		}
		
	}
	
	public void notRctUpdate() throws Exception {
		System.out.println("notRctUpdate");
		long start = System.currentTimeMillis();
		List<Map<String, Object>> itemList = monitorService.selectNotRctReScanList();
		if(itemList.size() > 0) {
			//1. 모니터결과 체크리스트 업데이트
			int updateed = monitorService.updateMonitorReScan(itemList);
			if( updateed >= 0 ) {
				System.out.println(String.format("TB_MONITOR 재스캔 데이터 업데이트 성공: %d건", updateed));
			} else {
				System.out.println("TB_MONITOR 재스캔 데이터 업데이트 실패");
			}
			//2. 모니터결과 미표출 체크리스트 업데이트
			int updateed2 = monitorService.updateMonitorNotRctReScan(itemList);
			if( updateed2 >= 0 ) {
				System.out.println(String.format("TB_MONITOR_NOT_RCT 재스캔 데이터 업데이트 성공: %d건", updateed));
			} else {
				System.out.println("TB_MONITOR_NOT_RCT 재스캔 데이터 업데이트 실패");
			}
			//3. 모니터재스캔결과 업데이트 시퀀스 삭제
			int updateed3 = monitorService.updateMonitorReScanDel();
			if( updateed3 >= 0 ) {
				System.out.println(String.format("TB_MONITOR_NOTRCT_RESCAN 데이터 삭제 성공: %d건", updateed));
			} else {
				System.out.println("TB_MONITOR_NOTRCT_RESCAN 데이터 삭제 실패");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("###execute time : "+(end-start)/1000.0+"sec");
	}
	
	public void viewYnUpdate() throws Exception {
		System.out.println("viewYnUpdate");
		long start = System.currentTimeMillis();
		List<Map<String, Object>> itemList = monitorService.selectViewYnList();
		if(itemList.size() > 0) {
			//1. 모니터결과 체크리스트 업데이트
			int updateed = 0;
			for(Map<String, Object> item:itemList) {
				updateed += monitorService.updateMonitorViewYn(item);
			}
			if( updateed >= 0 ) {
				System.out.println(String.format("TB_MONITOR VIEW_YN 업데이트 성공: %d건", updateed));
			} else {
				System.out.println("TB_MONITOR VIEW_YN 데이터 업데이트 실패");
			}
			
			//2. 모니터viewyn 업데이트 시퀀스 삭제
			int updateed3 = monitorService.deleteMonitorViewYn();
			if( updateed3 >= 0 ) {
				System.out.println(String.format("TB_MONITOR_VIEW_YN 데이터 삭제 성공: %d건", updateed));
			} else {
				System.out.println("TB_MONITOR_VIEW_YN 데이터 삭제 실패");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("###execute time : "+(end-start)/1000.0+"sec");
	}
	
	public void nohupLogDel() throws Exception {
		monitorService.cmdSqlite("cat /dev/null > nohup.out");
	}
	

}
