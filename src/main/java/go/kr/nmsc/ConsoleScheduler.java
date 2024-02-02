package go.kr.nmsc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import go.kr.nmsc.controller.MonitorController;

@Component
public class ConsoleScheduler {

	@Resource(name = "monitorController")
	private MonitorController monitorController;
	

	//체크리스트 생성(매시 50분 실행)
	@Scheduled(cron = "0 50 * * * *")	//매시 50분마다 호출
    public void sche01 () {
		try {
			monitorController.createCheckList();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	//하루1번 동기 체크리스트 생성(매일1시 실행)
	@Scheduled(cron = "0 0 1 * * *", zone="UTC")	//매일 1시0분마다 실행
    public void sche011 () {
		try {
			System.out.println("하루1번 1시0분 체크리스트 생성");
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.out.println(sdf.format(nowDate.getTime()));
			monitorController.createOneCheckList();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    //모니터 체크(1분마다 실행)
	@Scheduled(cron = "0 * * * * *")
	public void sche02 () {
		try {
			System.out.println("모니터 체크(1분마다 실행)");
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.out.println(sdf.format(nowDate.getTime()));
			monitorController.chkMonitor();
			monitorController.chkMonitorNotRct();
			monitorController.notRctUpdate();
			monitorController.delMonitor();
			monitorController.viewYnUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//하루단위로 로그 테이블 생성 및 삭제
	@Scheduled(cron = "0 1 0 * * *", zone="UTC")	//매일 0시1분마다 실행
	public void sche05 () {
		try {
			System.out.println("매일 0시1분마다 실행");
			monitorController.createNDropMonitorLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//sqlite3 vacuum 실행 
	//nohup 로그 삭제
	//(sqlite 데이터베이스 빈공간 삭제 : 내용을 임시 데이터베이스에 한번 옮겼다가 다시 되돌리며, 데이터를 순차적으로 저장하는 작업) 
	@Scheduled(cron = "0 30 4 * * *", zone="UTC")
	public void sche06 () {
		try {
			System.out.println("매일 4시30분에 실행");
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.out.println(sdf.format(nowDate.getTime()));
			monitorController.nohupLogDel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//비동기01 체크
	@Scheduled(cron = "0 1 0,12 * * *", zone="UTC")	//매일 9시1분, 21시1분마다 실행(UTC기준0시,12시)
	public void sche07 () {
		try {
			System.out.println("비동기01 매일 0시1분, 12시1분 실행");
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.out.println(sdf.format(nowDate.getTime()));
			monitorController.chkMonitorNotSync(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//비동기02 체크
	@Scheduled(cron = "0 0 0/4 * * *")	
	public void sche08 () {
		try {
			System.out.println("비동기02 4시간마다 실행");
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.out.println(sdf.format(nowDate.getTime()));
			monitorController.chkMonitorNotSync(4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//수집데이터 체크
	@Scheduled(cron = "0 5 0,12 * * *", zone="UTC")	//하루2번 UTC기준 0시,12시
	public void sche09 () {
		try {
			System.out.println("수집데이터 하루2번 UTC기준 0시,12시 5분 실행");
			Date nowDate = new Date(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			System.out.println(sdf.format(nowDate.getTime()));
			monitorController.chkMonitorCollect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
