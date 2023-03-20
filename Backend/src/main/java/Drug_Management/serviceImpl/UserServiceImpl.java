package Drug_Management.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import Drug_Management.JWT.CustomerUserDetailsService;
import Drug_Management.JWT.JwtFilter;
import Drug_Management.JWT.JwtUtil;
import Drug_Management.POJO.User;
import Drug_Management.constants.DrugConstants;
import Drug_Management.dao.UserDao;
import Drug_Management.service.UserService;
import Drug_Management.utils.DrugUtils;
import Drug_Management.utils.EmailUtils;
import Drug_Management.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired 
	JwtUtil jwtUtil;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	EmailUtils emailUtils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Inside signup {}",requestMap);
		try {
			if(validateSignUpMap(requestMap)) {
				User user = userDao.findByEmailId(requestMap.get("email"));
				if(Objects.isNull(user)) {
					userDao.save(getUserFromMap(requestMap));
					return DrugUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
				}
				else
				{
					return DrugUtils.getResponseEntity(DrugConstants.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
				}
			}
			else {
				return DrugUtils.getResponseEntity(DrugConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if(requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("password") 
				&& requestMap.containsKey("address")) {
			return true;
		}
		return false;
	}
	
	//(this.bCryptPasswordEncoder.encode
	private  User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setAddress(requestMap.get("address"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		log.info("Inside login");
		try {
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
					);
			if(auth.isAuthenticated()) {
				if(customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>("{\"token\":\""+jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
							customerUserDetailsService.getUserDetail().getRole())+"\"}",
							HttpStatus.OK);
				}
				else {
					return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval."+"\"}",HttpStatus.BAD_REQUEST);
				}
			}
		}
		catch (Exception ex) {
			log.error("{}",ex);
		}
		return new ResponseEntity<String>("{\"message\":\""+"Bad Credentials"+"\"}",HttpStatus.BAD_REQUEST);

	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if(jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				Optional<User> optional= userDao.findById(Integer.parseInt(requestMap.get("id")));
				if(!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
					return DrugUtils.getResponseEntity("User Status Updated Successfully", HttpStatus.OK);
				} else {
					DrugUtils.getResponseEntity("User id doesn't exist", HttpStatus.OK);
				}
			} else {
				return DrugUtils.getResponseEntity(DrugConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {

		allAdmin.remove(jwtFilter.getCurrentUser());
		if (status != null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved", "USER:- " + user + " \n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		} else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled", "USER:- " + user + " \n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}
	}

	@Override
	public ResponseEntity<String> checkToken() {
		return DrugUtils.getResponseEntity("true", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
			if(!userObj.equals(null)) {
				if(this.bCryptPasswordEncoder.matches(requestMap.get("oldPassword"),userObj.getPassword())) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return DrugUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
				}
				return DrugUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
			}
			return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			User user = userDao.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
				emailUtils.forgotMail(user.getEmail(), "Credentials by Drug Management System", user.getPassword());
			}
			return DrugUtils.getResponseEntity("Check your mail for credentials", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return DrugUtils.getResponseEntity(DrugConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
