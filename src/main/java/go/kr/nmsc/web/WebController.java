package go.kr.nmsc.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import go.kr.nmsc.web.service.WebService;

@Controller
public class WebController {
	
	@Resource(name="webService")
	private WebService webService;
	
//	@RequestMapping(value="/")
//    public String main() {
//        return "nmsc/itemList";
//    }
	
    @RequestMapping(value = {"/","/nmsc/itemListView"})
    public String itemListView(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -1);
//	    cal.add(Calendar.DATE, -10);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();
		paramMap.put("queryType","DEFAULT");
		Map<String, Object> defaultList = webService.reqItemList(paramMap);
		chkList.add(defaultList);
		paramMap.put("queryType","EIR");
		Map<String, Object> eirList = webService.reqItemList(paramMap);
		chkList.add(eirList);
		paramMap.put("queryType","RGB");
		Map<String, Object> rgbList = webService.reqItemList(paramMap);
		chkList.add(rgbList);
		
		paramMap.put("lvlType","LE2");
		List<Map<String, Object>> reqItemLvl2List = webService.reqItemLvlList(paramMap);
		paramMap.put("lvlType","LE3");
		List<Map<String, Object>> reqItemLvl3List = webService.reqItemLvlList(paramMap);
		paramMap.put("lvlType","LE4");
		List<Map<String, Object>> reqItemLvl4List = webService.reqItemLvlList(paramMap);
		
		List<Map<String, Object>> reqItemOhterList = webService.reqItemOhterList(paramMap);
		
		List<Map<String, Object>> reqItemCollectList = webService.reqItemCollectList(paramMap);
		
		List<Map<String, Object>> reqItemSwfcList = webService.reqItemSwfcList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		model.addAttribute("chkList",chkList);
		model.addAttribute("reqItemLvl2List",reqItemLvl2List);
		model.addAttribute("reqItemLvl3List",reqItemLvl3List);
		model.addAttribute("reqItemLvl4List",reqItemLvl4List);
		model.addAttribute("reqItemOhterList",reqItemOhterList);
		model.addAttribute("reqItemCollectList",reqItemCollectList);
		model.addAttribute("reqItemSwfcList",reqItemSwfcList);
		model.addAttribute("yyyyMMdd",yyyy+MM+dd);
		model.addAttribute("HH",HH);
		model.addAttribute("mm",mm);
		
		
		//date setting
		Date date = new Date(); 
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
		String udate = sdf2.format(date);
		model.addAttribute("udate",udate+" UTC");
		// Java 시간 더하기
	    Calendar mal = Calendar.getInstance();
	    mal.setTime(date);
	    // UTC에 9시간더하기
	    mal.add(Calendar.HOUR, 9);
	    String kdate = sdf2.format(mal.getTime());
	    model.addAttribute("kdate",kdate+" KST");
		
		
		return "nmsc/itemList";
    }
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/itemDefaultList")
	public Map<String,Object> itemDEFAULTList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		paramMap.put("queryType","DEFAULT");
		Map<String, Object> defaultList = webService.reqItemList(paramMap);
		
		List<Map<String, Object>> reqItemLvl1List = new ArrayList<Map<String, Object>>();
		Map<String,Object> result = new HashMap<String, Object>();
		reqItemLvl1List.add(defaultList);
		result.put("reqItemLvl1List",reqItemLvl1List);

		//date setting
		Date date = new Date(); 
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sdf2.setTimeZone(TimeZone.getTimeZone("UTC"));
		String udate = sdf2.format(date);
		result.put("udate",udate+" UTC");
		// Java 시간 더하기
	    Calendar mal = Calendar.getInstance();
	    mal.setTime(date);
	    // UTC에 9시간더하기
	    mal.add(Calendar.HOUR, 9);
	    String kdate = sdf2.format(mal.getTime());
		result.put("kdate",kdate+" KST");
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/itemEirList")
	public Map<String,Object> itemEirListList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		paramMap.put("queryType","EIR");
		Map<String, Object> eirList = webService.reqItemList(paramMap);
		
		List<Map<String, Object>> reqItemLvl1List = new ArrayList<Map<String, Object>>();
		Map<String,Object> result = new HashMap<String, Object>();
		reqItemLvl1List.add(eirList);
		result.put("reqItemLvl1List",reqItemLvl1List);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/itemGgbList")
	public Map<String,Object> itemGgbList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		paramMap.put("queryType","RGB");
		Map<String, Object> rgbList = webService.reqItemList(paramMap);
		
		List<Map<String, Object>> reqItemLvl1List = new ArrayList<Map<String, Object>>();
		Map<String,Object> result = new HashMap<String, Object>();
		reqItemLvl1List.add(rgbList);
		result.put("reqItemLvl1List",reqItemLvl1List);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/reqItemLvl2List")
	public Map<String,Object> reqItemLvl2List(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		paramMap.put("lvlType","LE2");
		List<Map<String, Object>> reqItemLvl2List = webService.reqItemLvlList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("reqItemLvl2List",reqItemLvl2List);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/reqItemLvl3List")
	public Map<String,Object> reqItemLvl3List(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		paramMap.put("lvlType","LE3");
		List<Map<String, Object>> reqItemLvl3List = webService.reqItemLvlList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("reqItemLvl3List",reqItemLvl3List);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/reqItemLvl4List")
	public Map<String,Object> reqItemLvl4List(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		paramMap.put("lvlType","LE4");
		List<Map<String, Object>> reqItemLvl4List = webService.reqItemLvlList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("reqItemLvl4List",reqItemLvl4List);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/reqItemOhterList")
	public Map<String,Object> reqItemOhterList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		List<Map<String, Object>> reqItemOhterList = webService.reqItemOhterList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("reqItemOhterList",reqItemOhterList);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/reqItemCollectList")
	public Map<String,Object> reqItemCollectList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		List<Map<String, Object>> reqItemCollectList = webService.reqItemCollectList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("reqItemCollectList",reqItemCollectList);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/reqItemSwfcList")
	public Map<String,Object> reqItemSwfcList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		List<Map<String, Object>> reqItemSwfcList = webService.reqItemSwfcList(paramMap);
		
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("reqItemSwfcList",reqItemSwfcList);
		
		return result;
	}
	
	@RequestMapping(value = "/nmsc/dataTypePopup")
	public String fileDataPopupView(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		Map<String, Object> areaList = new HashMap<String, Object>();
		String text = "";
		if("COLLECT".equals(paramMap.get("dataLevel"))) {
			text = webService.reqAreaCollectList(paramMap);
		}else {
			text = webService.reqAreaList(paramMap);
		}
		areaList = webService.textToQueryMap(text);
		model.addAttribute("headList", areaList.get("headList"));
		model.addAttribute("dataList", areaList.get("dataList"));
		model.addAttribute("dataType", paramMap.get("dataType"));
		model.addAttribute("dataLevel", paramMap.get("dataLevel"));
		model.addAttribute("satellite", paramMap.get("satellite"));
		model.addAttribute("yyyyMMdd", paramMap.get("yyyyMMdd"));
		model.addAttribute("HH", paramMap.get("HH"));
		model.addAttribute("mm", paramMap.get("mm"));
		return "nmsc/popup/dataTypePopup";
	}
	
	@RequestMapping(value = "/nmsc/fileDataPopup")
	public String fileDataPopup(@RequestParam Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
		if("COLLECT".equals(paramMap.get("dataLevel"))) {
			fileList = webService.reqFileCollectList(paramMap);
		}else {
			fileList = webService.reqFileList(paramMap);
		}
		model.addAttribute("mntTime", paramMap.get("mntTime"));
		model.addAttribute("dataTypeOne", paramMap.get("dataTypeOne"));
		model.addAttribute("fileList", fileList);
		model.addAttribute("dataLevel", paramMap.get("dataLevel"));
		return "nmsc/popup/fileDataPopup";
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/checkViewYn")
	public Map<String, Object> checkViewYn(@RequestParam(value="checkArray[]") List<String> arrayParams, @RequestParam(value="dataType") String dataType, @RequestParam(value="dataLevel") String dataLevel) throws Exception {
		List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		for(String str : arrayParams) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("mntTime",str);
			dataMapList.add(map);
		}
		int updateed = webService.insertMonitorViewYn(dataMapList, dataType, dataLevel);
		if( updateed >= 0 ) {
			System.out.println(String.format("TB_MONITOR 데이터 입력 성공: %d건", updateed));
		} else {
			System.out.println("데이터 입력 실패");
		}
		result.put("updateed",updateed);
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctListView")
    public String notRctListView(Model model) throws Exception {
		int notRctTime = webService.SelNotRctTimeChk();
		model.addAttribute("notRctTime", notRctTime);
		return "nmsc/notRctList";
    }
	
	@RequestMapping(value = "/nmsc/notRctAList")
	@ResponseBody
	public Map<String,Object> notRctAList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int notRctTime = webService.SelNotRctTimeChk();
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int limit = 10;
		paramMap.put("limit",limit);

		int page = (int) paramMap.get("page");
		paramMap.put("lvlType","a");
		int listcount = webService.gk2aListCnt(paramMap);
		// 총 페이지수
		int maxpage = (listcount + limit - 1) / limit; // (14 + 10 - 1) / 10 //2
		// 시작 페이지수
		int startpage = ((page - 1) / 10) * 10 + 1;
		// 마지막 페이지수
		int endpage = startpage + 10 - 1;
		if (endpage > maxpage)	endpage = maxpage;
		int endnum = page * limit;
		int startnum = endnum - limit + 1;
		paramMap.put("startnum",startnum);
		paramMap.put("endnum",endnum);
		paramMap.put("page",page);
		
		result.put("page",  page);//1
		result.put("startpage",  startpage);
		result.put("endpage",  endpage);
		paramMap.put("startpage",  startpage);
		paramMap.put("endpage",  endpage);
		List<Map<String, Object>> gk2aAList = webService.gk2aList(paramMap);
		result.put("gk2aAList",gk2aAList);
		
		List<Map<String, Object>> gk2aATimeGroup = webService.gk2aTimeGroup(paramMap);
		result.put("gk2aATimeGroup",gk2aATimeGroup);
			
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctBList")
	@ResponseBody
	public Map<String,Object> notRctBList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		int notRctTime = webService.SelNotRctTimeChk();
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int limit = 10;
		paramMap.put("limit",limit);

		int page = (int) paramMap.get("page");
		paramMap.put("lvlType","b");
		int listcount = webService.gk2aListCnt(paramMap);
		// 총 페이지수
		int maxpage = (listcount + limit - 1) / limit; // (13 + 9) / 10
		// 시작 페이지수
		int startpage = ((page - 1) / 10) * 10 + 1;
		// 마지막 페이지수
		int endpage = startpage + 10 - 1;
		if (endpage > maxpage)	endpage = maxpage;
		int endnum = page * limit;
		int startnum = endnum - limit + 1;
		paramMap.put("startnum",startnum);
		paramMap.put("endnum",endnum);
		paramMap.put("page",page);
		
		result.put("page",  page);
		result.put("startpage",  startpage);
		result.put("endpage",  endpage);
		
		List<Map<String, Object>> gk2aBList = webService.gk2aList(paramMap);
		result.put("gk2aBList",gk2aBList);
		
		List<Map<String, Object>> gk2aBTimeGroup = webService.gk2aTimeGroup(paramMap);
		result.put("gk2aBTimeGroup",gk2aBTimeGroup);
		
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctCList")
	@ResponseBody
	public Map<String,Object> notRctCList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		int notRctTime = webService.SelNotRctTimeChk();
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int limit = 10;
		paramMap.put("limit",limit);

		int page = (int) paramMap.get("page");
		paramMap.put("lvlType","c");
		int listcount = webService.gk2aListCnt(paramMap);
		// 총 페이지수
		int maxpage = (listcount + limit - 1) / limit; // (13 + 9) / 10
		// 시작 페이지수
		int startpage = ((page - 1) / 10) * 10 + 1;
		// 마지막 페이지수
		int endpage = startpage + 10 - 1;
		if (endpage > maxpage)	endpage = maxpage;
		int endnum = page * limit;
		int startnum = endnum - limit + 1;
		paramMap.put("startnum",startnum);
		paramMap.put("endnum",endnum);
		paramMap.put("page",page);
		
		result.put("page",  page);
		result.put("startpage",  startpage);
		result.put("endpage",  endpage);
		
		List<Map<String, Object>> gk2aCList = webService.gk2aList(paramMap);
		result.put("gk2aCList",gk2aCList);
		
		List<Map<String, Object>> gk2aCTimeGroup = webService.gk2aTimeGroup(paramMap);
		result.put("gk2aCTimeGroup",gk2aCTimeGroup);

		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctOtherGroup")
	@ResponseBody
	public Map<String,Object> notRctOtherGroup(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> otherGroup = webService.otherGroup();
		result.put("otherGroup",otherGroup);

		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctOtherList")
	@ResponseBody
	public Map<String,Object> notRctOtherList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		int notRctTime = webService.SelNotRctTimeChk();
		
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int limit = 10;
		paramMap.put("limit",limit);

//		int page = (Integer) paramMap.get("page");
		int page = Integer.parseInt(String.valueOf(paramMap.get("page")));
		int listcount = webService.otherListCnt(paramMap);
		// 총 페이지수
		int maxpage = (listcount + limit - 1) / limit; // (13 + 9) / 10
		// 시작 페이지수
		int startpage = ((page - 1) / 10) * 10 + 1;
		// 마지막 페이지수
		int endpage = startpage + 10 - 1;
		if (endpage > maxpage)	endpage = maxpage;
		int endnum = page * limit;
		int startnum = endnum - limit + 1;
		paramMap.put("startnum",startnum);
		paramMap.put("endnum",endnum);
		paramMap.put("page",page);
		
		result.put("page",  page);
		result.put("startpage",  startpage);
		result.put("endpage",  endpage);
		paramMap.put("startpage",  startpage);
		paramMap.put("endpage",  endpage);
		
		List<Map<String, Object>> otherList = webService.otherList(paramMap);
		result.put("otherList",otherList);
		List<Map<String, Object>> otherTimeGroup = webService.otherTimeGroup(paramMap);
		result.put("otherTimeGroup",otherTimeGroup);
		result.put("satellite",paramMap.get("satellite"));
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctCollectGroup")
	@ResponseBody
	public Map<String,Object> notRctCollectGroup(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> collectGroup = webService.collectGroup();
		result.put("collectGroup",collectGroup);

		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctCollectList")
	@ResponseBody
	public Map<String,Object> notRctCollectList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		int notRctTime = webService.SelNotRctTimeChk();
		
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int limit = 10;
		paramMap.put("limit",limit);

//		int page = (Integer) paramMap.get("page");
		int page = Integer.parseInt(String.valueOf(paramMap.get("page")));
		int listcount = webService.collectListCnt(paramMap);
		// 총 페이지수
		int maxpage = (listcount + limit - 1) / limit; // (13 + 9) / 10
		// 시작 페이지수
		int startpage = ((page - 1) / 10) * 10 + 1;
		// 마지막 페이지수
		int endpage = startpage + 10 - 1;
		if (endpage > maxpage)	endpage = maxpage;
		int endnum = page * limit;
		int startnum = endnum - limit + 1;
		paramMap.put("startnum",startnum);
		paramMap.put("endnum",endnum);
		paramMap.put("page",page);
		
		result.put("page",  page);
		result.put("startpage",  startpage);
		result.put("endpage",  endpage);
		
		List<Map<String, Object>> collectList = webService.collectList(paramMap);
		result.put("collectList",collectList);
		result.put("dataType",paramMap.get("dataType"));
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctSwfcGroup")
	@ResponseBody
	public Map<String,Object> notRctSwfcGroup(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		List<Map<String, Object>> swfcGroup = webService.swfcGroup();
		result.put("swfcGroup",swfcGroup);

		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctSwfcList")
	@ResponseBody
	public Map<String,Object> notRctSwfcList(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		int notRctTime = webService.SelNotRctTimeChk();
		
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 1시간 전
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int limit = 10;
		paramMap.put("limit",limit);

//		int page = (Integer) paramMap.get("page");
		int page = Integer.parseInt(String.valueOf(paramMap.get("page")));
		int listcount = webService.collectListCnt(paramMap);
		// 총 페이지수
		int maxpage = (listcount + limit - 1) / limit; // (13 + 9) / 10
		// 시작 페이지수
		int startpage = ((page - 1) / 10) * 10 + 1;
		// 마지막 페이지수
		int endpage = startpage + 10 - 1;
		if (endpage > maxpage)	endpage = maxpage;
		int endnum = page * limit;
		int startnum = endnum - limit + 1;
		paramMap.put("startnum",startnum);
		paramMap.put("endnum",endnum);
		paramMap.put("page",page);
		
		result.put("page",  page);
		result.put("startpage",  startpage);
		result.put("endpage",  endpage);
		
		List<Map<String, Object>> swfcList = webService.swfcList(paramMap);
		result.put("swfcList",swfcList);
		result.put("dataType",paramMap.get("dataType"));
		return result;
	}
	
	
	@RequestMapping(value = "/nmsc/adminListView")
    public String adminListView() throws Exception {
		return "nmsc/adminList";
    }
	@ResponseBody
	@RequestMapping(value = "/nmsc/adminList")
	public Map<String,Object> adminList(@RequestBody Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> selMetadbList = webService.selMetadbList(paramMap);
		
		result.put("selMetadbList",selMetadbList);
		result.put("paramMap",paramMap);
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/nmsc/metadbUpdate")
	public Map<String, Object> metadbUpdate(@RequestParam(value="checkArray[]") List<String> arrayParams, @RequestParam(value="checkSize") int checkSize) throws Exception {
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		if(checkSize > 1) {
			for(String str : arrayParams) {
				String[] strList = str.split(",");
				Map<String,Object> updateInfo = new HashMap<String,Object>();
				updateInfo.put("detailSeq",strList[0]);
				updateInfo.put("cycle",strList[1]);
				updateInfo.put("stdTime",strList[2]);
				updateInfo.put("monitorYn",strList[3]);
				updateInfo.put("productCycleYn",strList[4]);
				updateList.add(updateInfo);
			}
		}else {
			Map<String,Object> updateInfo = new HashMap<String,Object>();
			updateInfo.put("detailSeq",arrayParams.get(0));
			updateInfo.put("cycle",arrayParams.get(1));
			updateInfo.put("stdTime",arrayParams.get(2));
			updateInfo.put("monitorYn",arrayParams.get(3));
			updateInfo.put("productCycleYn",arrayParams.get(4));
			updateList.add(updateInfo);
		}
		
		int updateCnt = 0;
		for(Map<String,Object> map : updateList) {
			updateCnt += webService.updateMetadb(map);
		}
		result.put("updateCnt",updateCnt);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/exceptUpdate")
	public Map<String, Object> exceptUpdate(@RequestParam(value="shArray[]") List<String> shArray,@RequestParam(value="smArray[]") List<String> smArray,@RequestParam(value="ehArray[]") List<String> ehArray,@RequestParam(value="emArray[]") List<String> emArray) throws Exception {
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		for(int i=0;i<shArray.size();i++) {
			Map<String,Object> updateInfo = new HashMap<String,Object>();
			updateInfo.put("startHour",shArray.get(i));
			updateInfo.put("startMin",smArray.get(i));
			updateInfo.put("endHour",ehArray.get(i));
			updateInfo.put("endMin",emArray.get(i));
			updateList.add(updateInfo);
		}
		webService.deleteExceptTime();
		int updateCnt = webService.insertExceptTime(updateList);
		result.put("updateCnt",updateCnt);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/nmsc/exceptList")
	public Map<String,Object> exceptList(@RequestBody Map<String, Object> paramMap, Model model, HttpServletRequest req) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> exceptList = webService.exceptList(paramMap);
		
		result.put("exceptList",exceptList);
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctUpdate")
	@ResponseBody
	public Map<String,Object> notRctUpdate(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		int notRctTime = webService.SelNotRctTimeChk();
		//date setting
		Date nowDate = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Java 시간 더하기
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(nowDate);
	    // 24/48
	    cal.add(Calendar.HOUR, -notRctTime);
	    String now = sdf.format(cal.getTime());
		String yyyy = now.substring(0,4);
		String MM = now.substring(4,6);
		String dd = now.substring(6,8);
		String HH = now.substring(8,10);
		String mm = now.substring(10,12);
		paramMap.put("yyyyMMdd",yyyy+MM+dd);
		paramMap.put("HH",HH);
		paramMap.put("mm",mm);
		
		int updateCnt = 0;
		List<Map<String, Object>> notRctCheckList = webService.notRctCheckList(paramMap);
		//List<Map<String, Object>> notRctCheckList2 = webService.notRctCheckList2(paramMap);
		if(notRctCheckList.size() > 0) {
			//2. stat ctime 검색
			List<Map<String, Object>> monitorChkList = webService.checkMonitor(notRctCheckList);
			
			updateCnt = webService.insertNotRctReScan(monitorChkList);
		}
		result.put("updateCnt",updateCnt);
		return result;
	}
	
	@RequestMapping(value = "/nmsc/notRctSelTimeUpdate")
	@ResponseBody
	public Map<String,Object> notRctSelTimeUpdate(@RequestBody Map<String,Object> paramMap, HttpServletRequest req) throws Exception {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		int time = Integer.parseInt(String.valueOf(paramMap.get("notRctSelTime")));
		
		webService.insertNotRctTimeChk(time);
			
		result.put("time",time);
		return result;
	}
}
