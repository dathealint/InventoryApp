package datnguyen.com.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import datnguyen.com.inventoryapp.data.Product;
import datnguyen.com.inventoryapp.data.ProductContract.*;

/**
 * Created by datnguyen on 12/27/16.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1; // first version
	private static final String DATABASE_NAME = "product.db";
	public static final int INSERTION_FAIL_CODE = -1;

	public ProductDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		// create table Product here

		final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
				ProductEntry._ID + " INTEGER PRIMARY KEY," +
				ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
				ProductEntry.COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0," +
				ProductEntry.COLUMN_REMIND_TIME + " INTEGER," +
				ProductEntry.COLUMN_REPEAT_MODE + " INTEGER," +
				ProductEntry.COLUMN_REPEAT_MODE_VALUE + " INTEGER," +
				ProductEntry.COLUMN_CREATED_DATE + " INTEGER," +
				ProductEntry.COLUMN_COMPLETED_DATE + " INTEGER" +
				" );";

		Log.v("", "SQL_CREATE_PRODUCT_TABLE: " + SQL_CREATE_PRODUCT_TABLE);
		// execute query to create Product table
		sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		// drop table to create a new one
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ");
		onCreate(sqLiteDatabase);
	}

	/**
	 * Insert a Product to database
	 * @param product object to insert
	 * @return rowId of newly inserted Product, or -1 if insertion fails.
	 */
	public long insertProduct(Product product) {
		long insertResult = INSERTION_FAIL_CODE;

		SQLiteDatabase database = getWritableDatabase();
		// wrap insertion in transaction
		database.beginTransaction();

		try {
			// insert query
			ContentValues values = new ContentValues();

			// put info to content values
			values.put(ProductEntry.COLUMN_NAME, product.getName());
			values.put(ProductEntry.COLUMN_IS_COMPLETED, product.isCompleted());
			values.put(ProductEntry.COLUMN_REMIND_TIME, product.getRemindTime());
			values.put(ProductEntry.COLUMN_REPEAT_MODE, product.getRepeatMode());

			if (product.getCreatedDate() != null) {
				values.put(ProductEntry.COLUMN_CREATED_DATE, product.getCreatedDate().getTime());
			}

			if (product.getCompletedDate() != null) {
				values.put(ProductEntry.COLUMN_COMPLETED_DATE, product.getCompletedDate().getTime());
			}

			// run the insert statement
			insertResult = database.insert(ProductEntry.TABLE_NAME, null, values);
			database.setTransactionSuccessful();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// end transaction
			database.endTransaction();
		}

		return insertResult;
	}

	/**
	 * Delete all Products in database
	 * @return rowId of newly deleted Product, or -1 if deletion fails.
	 */
	public long deleteAllProducts() {
		SQLiteDatabase database = getWritableDatabase();

		long countRowsAffected = 0;
		// wrap deletion in transaction
		database.beginTransaction();
		try {
			// run the delete statement
			countRowsAffected = database.delete(ProductEntry.TABLE_NAME, null, null);
			database.setTransactionSuccessful();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// end transaction
			database.endTransaction();
		}

		return countRowsAffected;
	}

	/**
	 * Fetch Product entries from database
	 * @param selection WHERE condition, e.g. "NAME LIKE ?"
	 * @param selectionArgs values to pass into selection
	 * @param sortOrder sortOrder to sort result records
	 * @return list of sorted Products from database matching conditions
	 */
	public ArrayList<Product> getAllProducts(String selection, String[] selectionArgs, String sortOrder) {

		ArrayList<Product> results = new ArrayList();

		SQLiteDatabase database = getReadableDatabase();
		// a projection specifies which columns from database will be querying
		String[] projection = {
				ProductEntry._ID,
				ProductEntry.COLUMN_NAME,
				ProductEntry.COLUMN_IS_COMPLETED,
				ProductEntry.COLUMN_REMIND_TIME,
				ProductEntry.COLUMN_REPEAT_MODE,
				ProductEntry.COLUMN_REPEAT_MODE_VALUE,
				ProductEntry.COLUMN_CREATED_DATE,
				ProductEntry.COLUMN_COMPLETED_DATE
		};

		Cursor cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		while(cursor.moveToNext()) {

			// get info out of cursor
			long id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
			String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
			boolean isCompleted = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_IS_COMPLETED)) == 0 ? false : true;
			int remindTime = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_REMIND_TIME));
			int repeatMode = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_REPEAT_MODE));
			int repeatModeValue = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_REPEAT_MODE_VALUE));
			long createdDate = cursor.getLong(cursor.getColumnIndex(ProductEntry.COLUMN_CREATED_DATE));
			long completedDate = cursor.getLong(cursor.getColumnIndex(ProductEntry.COLUMN_COMPLETED_DATE));

			Product product = new Product();
			product.setId(id);
			product.setName(name);
			product.setCompleted(isCompleted);
			product.setRemindTime(remindTime);
			product.setRepeatMode(repeatMode);
			product.setRepeatModeValue(repeatModeValue);
			product.setCreatedDate(new Date(createdDate));
			product.setCompletedDate(new Date(completedDate));

			results.add(product);
		}

		cursor.close();
		return results;
	}

	/**
	 * Fetch Product entries from database
	 * @return list of all Product entries from database
	 */
	public ArrayList<Product> getAllProducts() {
		return getAllProducts(null, null, null);
	}

	/**
	 * Fetch Product entries from database matching name starting with provided string
	 * @return list of sorted Products from database matching conditions
	 */
	public ArrayList<Product> getProductsNameStartWith(String prefix) {
		// filter result WHERE name starts with T
		String selection = ProductEntry.COLUMN_NAME + " LIKE ?";
		String[] selectionArgs = { prefix + "%" };

		String sortOrder = ProductEntry.COLUMN_NAME + " ASC";

		return getAllProducts(selection, selectionArgs, sortOrder);
	}

}
