package Drug_Management.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import Drug_Management.service.DashBoardService;

@RestController
public class DashboardRestImpl implements DashboardRest {

	@Autowired
	DashBoardService dashBoardService;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		// TODO Auto-generated method stub
		return dashBoardService.getCount();
	}

}
