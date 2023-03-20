package Drug_Management.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Drug_Management.POJO.Category;


public interface CategoryDao extends JpaRepository <Category,Integer> {

	List<Category> getAllCategory();
}
