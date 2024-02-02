package go.kr.nmsc.metadb.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import go.kr.nmsc.metadb.dao.MetadbDao;
import go.kr.nmsc.metadb.service.MetadbService;

@Service("metadbService")
public class MetadbServiceImpl implements MetadbService {
	
	@Autowired
	MetadbDao dao;
	
	@Override
	public List<Map<String, Object>> selSatellite() throws Exception{
		return dao.selSatellite();
	}
	@Override
	public List<Map<String, Object>> selSensor() throws Exception{
		return dao.selSensor();
	}
	@Override
	public List<Map<String, Object>> selDataLvl() throws Exception{
		return dao.selDataLvl();
	}
	@Override
	public List<Map<String, Object>> selDataType() throws Exception{
		return dao.selDataType();
	}
	@Override
	public List<Map<String, Object>> selDataFormat() throws Exception{
		return dao.selDataFormat();
	}
	@Override
	public List<Map<String, Object>> selDataArea() throws Exception{
		return dao.selDataArea();
	}
	@Override
	public List<Map<String, Object>> selDataRes() throws Exception{
		return dao.selDataRes();
	}
	@Override
	public List<Map<String, Object>> selDataProj() throws Exception{
		return dao.selDataProj();
	}
	@Override
	public List<Map<String, Object>> selFileList(Map<String, Object> map) throws Exception{
		return dao.selFileList(map);
	}
	@Override
	public List<Map<String, Object>> selMetadbList(Map<String, Object> map) throws Exception{
		return dao.selMetadbList(map);
	}
}
