package Drug_Management.wrapper;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWrapper {

	private Integer id;

	private String name;

	private String email;
	
	private String contactNumber;

	private String address;

	private String status;

	private String role;

	public UserWrapper(Integer id, String name, String email, String contactNumber, String address, String status,
			String role) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.contactNumber = contactNumber;
		this.address = address;
		this.status = status;
		this.role = role;
	}

	
}
