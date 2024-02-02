package go.kr.nmsc.metadb.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import go.kr.nmsc.metadb.service.MetadbService;

@Controller
public class MetadbController {
	
	@Resource(name="metadbService")
	private MetadbService metadbService;
	
	
	@RequestMapping(value = "/nmsc/metadb")
	public String metadbList(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		System.out.println("####paramMap : "+paramMap.toString());
		List<Map<String, Object>> satelliteList = metadbService.selSatellite();
		List<Map<String, Object>> sensorList = metadbService.selSensor();
		List<Map<String, Object>> dataLvlList = metadbService.selDataLvl();
		List<Map<String, Object>> dataTypeList = metadbService.selDataType();
		List<Map<String, Object>> dataFormatList = metadbService.selDataFormat();
		List<Map<String, Object>> dataAreaList = metadbService.selDataArea();
		List<Map<String, Object>> dataResList = metadbService.selDataRes();
		List<Map<String, Object>> dataProjList = metadbService.selDataProj();
		
		List<Map<String, Object>> selFileList = metadbService.selFileList(paramMap);
		
		model.addAttribute("satelliteList",satelliteList);
		model.addAttribute("sensorList",sensorList);
		model.addAttribute("dataLvlList",dataLvlList);
		model.addAttribute("dataTypeList",dataTypeList);
		model.addAttribute("dataFormatList",dataFormatList);
		model.addAttribute("dataAreaList",dataAreaList);
		model.addAttribute("dataResList",dataResList);
		model.addAttribute("dataProjList",dataProjList);
		
		model.addAttribute("selFileList",selFileList);
		model.addAttribute("paramMap",paramMap);
		return "nmsc/metadbList";
	}
	
	@RequestMapping(value = "/nmsc/metadb2")
	public String metadbList2(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		System.out.println("####paramMap : "+paramMap.toString());
		
		List<Map<String, Object>> selMetadbList = metadbService.selMetadbList(paramMap);
		
		model.addAttribute("selMetadbList",selMetadbList);
		model.addAttribute("paramMap",paramMap);
		return "nmsc/metadbList2";
	}
	
	
}
