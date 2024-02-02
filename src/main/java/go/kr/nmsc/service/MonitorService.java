package go.kr.nmsc.service;

import java.util.List;
import java.util.Map;

public interface MonitorService  {

	public List<Map<String, Object>> reqItemList() throws Exception;
	
	public List<Map<String, Object>> reqItemOneList() throws Exception;
	
	public List<Map<String, Object>> createCheckList(List<Map<String, Object>> list, String timeAdd) throws Exception;
	
	public List<Map<String, Object>> createCheckOneList(List<Map<String, Object>> itemList) throws Exception;
	
	public int insertCheckList(List<Map<String, Object>> list) throws Exception;
	
	public boolean selectCheckCnt() throws Exception;
	
	public List<Map<String, Object>> selectCheckList() throws Exception;
	
	public List<Map<String, Object>> checkMonitor(List<Map<String, Object>> list) throws Exception;
	
	public int insertMonitorLog(List<Map<String, Object>> list) throws Exception;
	
	public int updateMonitor(List<Map<String, Object>> list) throws Exception;
	
	public List<Map<String, Object>> selectCheckListNotRct() throws Exception;
	
	public List<Map<String, Object>> checkMonitorStat(List<Map<String, Object>> list) throws Exception;
	
	public int updateMonitorLog(List<Map<String, Object>> list) throws Exception;
	
	public int insertMonitorNotRct(List<Map<String, Object>> dataMapList) throws Exception;
	
	public int deleteMonitor() throws Exception;
	
	public void createLogTable() throws Exception;
	
	public void dropLogTable() throws Exception;
	
	public void cmdSqlite(String cmd) throws Exception;
	
	public boolean selectNotSyncCheckCnt(int stdTime) throws Exception;
	
	public List<Map<String, Object>> selItemNotSyncList(Map<String, Object> map) throws Exception;
	
	public int insertCheckNotSyncList(List<Map<String, Object>> dataMapList) throws Exception;
	
	public List<Map<String, Object>> selectCheckNotSyncList(int stdTime) throws Exception;
	
	public List<Map<String, Object>> checkNotSyncMonitor(List<Map<String, Object>> list) throws Exception;
	
	public int updateNotSyncMonitor(List<Map<String, Object>> dataMapList) throws Exception;
	
	public boolean selectCollectCheckCnt() throws Exception;
	
	public List<Map<String, Object>> itemCollectList() throws Exception;
	
	public List<Map<String, Object>> checkCollectMonitor(List<Map<String, Object>> list) throws Exception;
	
	public int insertCollectList(List<Map<String, Object>> dataMapList) throws Exception;
	
	public List<Map<String, Object>> selectNotRctReScanList() throws Exception;
	
	public int updateMonitorReScan(List<Map<String, Object>> dataMapList) throws Exception;
	
	public int updateMonitorNotRctReScan(List<Map<String, Object>> dataMapList) throws Exception;
	
	public int updateMonitorReScanDel() throws Exception;
	
	public List<Map<String, Object>> selectViewYnList() throws Exception;
	
	public int updateMonitorViewYn(Map<String, Object> dataMap) throws Exception;
	
	public int deleteMonitorViewYn() throws Exception;
}
