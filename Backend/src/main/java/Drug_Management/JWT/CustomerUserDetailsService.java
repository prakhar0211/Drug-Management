package Drug_Management.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Drug_Management.dao.UserDao;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerUserDetailsService implements UserDetailsService  {

	@Autowired
	UserDao userDao;
	
	private Drug_Management.POJO.User userDetail;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Inside loadUserByUsername {}", username);
		userDetail = userDao.findByEmailId(username);
		if(!Objects.isNull(userDetail))
			return new User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
		else {
			throw new UsernameNotFoundException("User not found");
			
		}
	}
	public Drug_Management.POJO.User getUserDetail(){
		return userDetail;
	}

}
