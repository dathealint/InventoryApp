package datnguyen.com.inventoryapp.data;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import datnguyen.com.inventoryapp.CustomError;
import datnguyen.com.inventoryapp.MainActivity;
import datnguyen.com.inventoryapp.R;

import static datnguyen.com.inventoryapp.Constants.INVALID_ID;

/**
 * Created by datnguyen on 12/27/16.
 */

public class Product implements Serializable {

	private long id = INVALID_ID;
	private String name;
	private int price = 0;
	private int quantity = 0;
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

	public File getImageFile() {
		if (getThumnailPath() == null) {
			return null;
		}

		File pictureFile = Product.getOutputImageFile(getThumnailPath());
		return pictureFile;
	}

	public static File getOutputImageFile(String fileName) {
		File file = new File(MainActivity.getSharedInstance().getBaseContext().getCacheDir(), fileName);
		return file;
	}

	public CustomError validateEntry() {
		// check name empty
		if (TextUtils.isEmpty(getName())) {
			return new CustomError(CustomError.CODE_TEXT_EMPTY, MainActivity.getSharedInstance().getString(R.string.error_product_name_empty));
		}

		// check price negative
		if (getPrice() < 0) {
			return new CustomError(CustomError.CODE_NUMBER_NEGATIVE, MainActivity.getSharedInstance().getString(R.string.error_price_negative));
		}

		// check quantity negative
		if (getQuantity() < 0) {
			return new CustomError(CustomError.CODE_NUMBER_NEGATIVE, MainActivity.getSharedInstance().getString(R.string.error_quantity_negative));
		}

		return null;
	}

}
