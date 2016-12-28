package datnguyen.com.inventoryapp.data;

import java.io.Serializable;
import java.util.Date;

import static datnguyen.com.inventoryapp.Constants.INVALID_ID;

/**
 * Created by datnguyen on 12/27/16.
 */

public class Product implements Serializable {

	private long id = INVALID_ID;
	private String name;
	private int price;
	private int quantity;
	private Date createdDate;
	private String thumnailPath = null;

	//foreign key to Supplier table
	private long supplierId;

	// supplier name will be queried from database, not a property of Product
	private String supplierName;

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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getThumnailPath() {
		return thumnailPath;
	}

	public void setThumnailPath(String thumnailPath) {
		this.thumnailPath = thumnailPath;
	}

	public long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(long supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
}
