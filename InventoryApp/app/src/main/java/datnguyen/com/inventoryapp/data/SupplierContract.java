package datnguyen.com.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by datnguyen on 12/27/16.
 */

public class SupplierContract {

	public static final class SupplierEntry implements BaseColumns {
		// tablename
		public static final String TABLE_NAME = "supplier";

		// Name of product, stored as TEXT
		public static final String COLUMN_NAME = "name";

		// Address of supplier, stored as STRING
		public static final String COLUMN_ADDRESS = "address";

		// Tel number of supplier, Stored as String
		public static final String COLUMN_TEL = "tel";

		// Email address of supplier, Stored as String
		public static final String COLUMN_EMAIL = "email";

		// Date of creating habit record, stored as long in milliseconds since the epoch
		public static final String COLUMN_CREATED_DATE = "created_date";
	}

}
