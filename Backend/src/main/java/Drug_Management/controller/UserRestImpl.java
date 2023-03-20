package Drug_Management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import Drug_Management.constants.DrugConstants;
import Drug_Management.service.UserService;
import Drug_Management.utils.DrugUtils;
import Drug_Management.wrapper.UserWrapper;

@RestController
public class UserRestImpl implements UserRest {

	@Autowired
	UserService userService;

	@Override
	public ResponseEntity<String> signup(Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		try {
			return userService.signUp(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		try {
			return userService.login(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			return userService.getAllUser();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			return userService.update(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> checkToken() {
		try {
			return userService.checkToken();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			return userService.changePassword(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			return userService.forgotPassword(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
