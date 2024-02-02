package go.kr.nmsc.web.service;

import java.util.List;
import java.util.Map;

public interface WebService  {

	
	public Map<String, Object> reqItemList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> reqItemLvlList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> reqItemOhterList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> reqItemCollectList(Map<String,Object> paramMap) throws Exception ;
	
	public List<Map<String, Object>> reqItemSwfcList(Map<String,Object> paramMap) throws Exception;
	
	public String reqAreaList(Map<String,Object> paramMap) throws Exception;
	
	public Map<String, Object> textToQueryMap(String text) throws Exception;
	
	public List<Map<String, Object>> reqFileList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> reqFileCollectList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> reqItemNonRctList(Map<String,Object> paramMap) throws Exception;
	
	public int insertMonitorViewYn(List<Map<String, Object>> dataMapList, String dataType, String dataLevel) throws Exception;
	
	public int gk2aListCnt(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> gk2aList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> gk2aTimeGroup(Map<String,Object> paramMap) throws Exception;
	
	public int otherListCnt(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> otherGroup() throws Exception;
	
	public List<Map<String, Object>> otherList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> otherTimeGroup(Map<String,Object> paramMap) throws Exception;
	
	public int collectListCnt(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> collectGroup() throws Exception;
	
	public List<Map<String, Object>> collectList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> swfcGroup() throws Exception;
	
	public List<Map<String, Object>> swfcList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> selMetadbList(Map<String, Object> map) throws Exception;
	
	public int updateMetadb(Map<String, Object> dataMapList) throws Exception;
	
	public int deleteExceptTime() throws Exception;
	
	public int insertExceptTime(List<Map<String, Object>> dataMapList) throws Exception;
	
	public List<Map<String, Object>> exceptList(Map<String,Object> paramMap) throws Exception;
	
	public String reqAreaCollectList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> reqFileNotSyncList(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> notRctCheckList(Map<String,Object> paramMap) throws Exception;

	public List<Map<String, Object>> notRctCheckList2(Map<String,Object> paramMap) throws Exception;
	
	public List<Map<String, Object>> checkMonitor(List<Map<String, Object>> list) throws Exception;
	
	public int insertNotRctTimeChk(int time) throws Exception;
	
	public int SelNotRctTimeChk() throws Exception;
	
	public int insertNotRctReScan(List<Map<String, Object>> notRctCheckList) throws Exception;
}
