package Drug_Management.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import Drug_Management.POJO.User;
import Drug_Management.wrapper.UserWrapper;
import jakarta.transaction.Transactional;

public interface UserDao extends JpaRepository <User , Integer> {

	User findByEmailId(@Param("email") String email);
	
	List<UserWrapper> getAllUser();

	@Transactional
	@Modifying
	Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

	List<String> getAllAdmin();
	
	User findByEmail(String email);
}
