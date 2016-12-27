package datnguyen.com.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by datnguyen on 12/27/16.
 */

public class ProductContract {

	public static final class ProductEntry implements BaseColumns {

		// tablename
		public static final String TABLE_NAME = "habit";

		// Name of habitm, stored as TEXT
		public static final String COLUMN_NAME = "name";

		// Completion status of Habit record: completed or not, stored as INTEGER
		public static final String COLUMN_IS_COMPLETED = "is_completed";

		// Remind time to remind user of the habit. This is Time value only, not containing Date information
		// Stored as INTEGER, convert to 24H time format. E.g. 2330 means 23:30 or 11:30PM
		public static final String COLUMN_REMIND_TIME = "remind_time";

		// Repeat period to remind user.
		// Stored as INTEGER, Values are: 0 (for daily), 1 (for weekly), 2 (for monthly)
		public static final String COLUMN_REPEAT_MODE = "repeat_mode";

		// Repeat period value specifies exact day in week/month to remind. Multiple values can be joined by using comma
		// For mode Daily, this value doesnt matter, should be left blank
		// For mode Weekly, this value ranges from 0 to 6, starting from Sunday (value 0) to Saturday (value 6)
		// For mode Monthly, this value ranges from 1 to 31, as date of month.
		// For example, user can set to remind him at 9:00AM on mode Weekly but for Monday and Thursday only
		// In that case, remind_time is 9:00AM, repeat_mode is 1, repeat_mode_value is "1,4"
		// Stored as INTEGER
		public static final String COLUMN_REPEAT_MODE_VALUE = "repeat_mode_value";

		// Date of creating habit record, stored as long in milliseconds since the epoch
		public static final String COLUMN_CREATED_DATE = "created_date";

		// Date of completing habit record, stored as long in milliseconds since the epoch
		public static final String COLUMN_COMPLETED_DATE = "completed_date";

	}

}
