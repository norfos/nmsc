package go.kr.nmsc.metadb.service;

import java.util.List;
import java.util.Map;

public interface MetadbService  {

	public List<Map<String, Object>> selSatellite() throws Exception;
	
	public List<Map<String, Object>> selSensor() throws Exception;
	
	public List<Map<String, Object>> selDataLvl() throws Exception;
	
	public List<Map<String, Object>> selDataType() throws Exception;
	
	public List<Map<String, Object>> selDataFormat() throws Exception;
	
	public List<Map<String, Object>> selDataArea() throws Exception;
	
	public List<Map<String, Object>> selDataRes() throws Exception;
	
	public List<Map<String, Object>> selDataProj() throws Exception;
	
	public List<Map<String, Object>> selFileList(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> selMetadbList(Map<String, Object> map) throws Exception;
}
