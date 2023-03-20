package Drug_Management.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import Drug_Management.POJO.Product;
import Drug_Management.wrapper.ProductWrapper;
import jakarta.transaction.Transactional;

public interface ProductDao extends JpaRepository<Product, Integer> {

	List<ProductWrapper> getAllProduct();

	@Modifying
	@Transactional
	void updateProductStatus(@Param("status") String status,@Param("id") Integer id);

	List<ProductWrapper> getProductByCategory(@Param("id") Integer id);

	ProductWrapper getProductById(Integer id); 

}
