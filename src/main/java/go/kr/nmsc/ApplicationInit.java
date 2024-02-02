package go.kr.nmsc;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import go.kr.nmsc.service.MonitorService;
import go.kr.nmsc.web.service.WebService;

@Component
public class ApplicationInit implements ApplicationRunner{
	@Resource(name = "monitorService")
	private MonitorService monitorService;
//	@Resource(name = "webService")
//	private WebService webService;
	public void run(ApplicationArguments args) {
		//최초 한번 실행
		System.out.println("최초한번실행!!!!");
		try {
//			monitorService.createCheckTable();
//			monitorService.createCheckNonRctTable();
			monitorService.createLogTable();
//			monitorService.createMonitorExceptTimeTable();
//			monitorService.createNotRctTimeChkTable();
//			webService.insertNotRctTimeChk(24);
//			monitorService.createNotRctReScanTable();
//			monitorService.createViewYnTable();
//			monitorService.cmdSqlite("sqlite3 nmscMonitor.db pragma journal_mode = WAL;");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
