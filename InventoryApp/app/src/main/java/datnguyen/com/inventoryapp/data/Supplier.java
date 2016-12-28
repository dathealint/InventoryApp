package datnguyen.com.inventoryapp.data;

import java.io.Serializable;
import java.util.Date;

import static datnguyen.com.inventoryapp.Constants.INVALID_ID;

/**
 * Created by datnguyen on 12/27/16.
 */

public class Supplier implements Serializable {


	private long id = INVALID_ID;
	private String name;
	private String address;
	private String tel;
	private String email;
	private Date createdDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}

