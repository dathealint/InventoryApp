package datnguyen.com.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by datnguyen on 12/27/16.
 */

public class ProductContract {

	public static final class ProductEntry implements BaseColumns {

		// tablename
		public static final String TABLE_NAME = "product";

		// Name of product, stored as TEXT
		public static final String COLUMN_NAME = "name";

		// Price of product, stored as INTEGER
		public static final String COLUMN_PRICE = "price";

		// Quanity of product in inventory, Stored as INTEGER
		public static final String COLUMN_QUANTITY = "quantity";

		// Image path, Stored as TEXT
		public static final String COLUMN_IMAGE_PATH = "image_path";

		// SupplierId of product, foreign key to Supplier Table, stored as long
		public static final String COLUMN_SUPPLIER_ID = "supplierId";

		// Date of creating habit record, stored as long in milliseconds since the epoch
		public static final String COLUMN_CREATED_DATE = "created_date";
	}

}
