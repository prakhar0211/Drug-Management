package Drug_Management.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import Drug_Management.dao.BillDao;
import Drug_Management.dao.CategoryDao;
import Drug_Management.dao.ProductDao;
import Drug_Management.service.DashBoardService;

@Service
public class DashBoardServiceImpl implements DashBoardService {

	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	BillDao billDao;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		Map<String, Object> map = new HashMap<>();
		map.put("category", categoryDao.count()); 
		map.put("product", productDao.count());
		map.put("bill", billDao.count());
		return new ResponseEntity<>(map,HttpStatus.OK);
	}

}
