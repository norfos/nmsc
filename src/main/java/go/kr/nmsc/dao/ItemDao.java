package go.kr.nmsc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ItemDao {
	public List<Map<String, Object>> itemList();
	
	public List<Map<String, Object>> itemOneList();
	
	public List<Map<String, Object>> selMetadbList(Map<String, Object> map);
	
	public int updateMetadb(Map<String, Object> map);
	
	public List<Map<String, Object>> itemNotSyncList(Map<String, Object> map);

	public List<Map<String, Object>> itemCollectList();
	
	
	
}



