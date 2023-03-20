package Drug_Management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import Drug_Management.POJO.Bill;
import Drug_Management.constants.DrugConstants;
import Drug_Management.service.BillService;
import Drug_Management.utils.DrugUtils;

@RestController
public class BillRestImpl implements BillRest {

	
	@Autowired
	BillService billService;
	
	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		try {
			return billService.getBills();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
		try {
			return billService.getPdf(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			return billService.deleteBill(id);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);


	}

}
