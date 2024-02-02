package go.kr.nmsc.metadb.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MetadbDao {
	
	public List<Map<String, Object>> selSatellite();
	
	public List<Map<String, Object>> selSensor();
	
	public List<Map<String, Object>> selDataLvl();
	
	public List<Map<String, Object>> selDataType();
	
	public List<Map<String, Object>> selDataFormat();
	
	public List<Map<String, Object>> selDataArea();
	
	public List<Map<String, Object>> selDataRes();
	
	public List<Map<String, Object>> selDataProj();
	
	public List<Map<String, Object>> selFileList(Map<String, Object> map);
	
	public List<Map<String, Object>> selMetadbList(Map<String, Object> map);
}



