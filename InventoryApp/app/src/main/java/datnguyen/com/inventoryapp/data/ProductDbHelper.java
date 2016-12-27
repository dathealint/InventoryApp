package datnguyen.com.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import datnguyen.com.inventoryapp.data.SupplierContract.*;
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

		final String SQL_CREATE_SUPPLIER_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " (" +
				SupplierEntry._ID + " INTEGER PRIMARY KEY," +
				SupplierEntry.COLUMN_NAME + " TEXT NOT NULL," +
				SupplierEntry.COLUMN_ADDRESS + " TEXT," +
				SupplierEntry.COLUMN_TEL + " TEXT," +
				SupplierEntry.COLUMN_EMAIL + " TEXT," +
				SupplierEntry.COLUMN_CREATED_DATE + " INTEGER" +

				" );";

		Log.v("", "SQL_CREATE_SUPPLIER_TABLE: " + SQL_CREATE_SUPPLIER_TABLE);

		// execute query to create Supplier table
		sqLiteDatabase.execSQL(SQL_CREATE_SUPPLIER_TABLE);

		// create table Product here
		final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
				ProductEntry._ID + " INTEGER PRIMARY KEY," +
				ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
				ProductEntry.COLUMN_PRICE + " INTEGER," +
				ProductEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0," +
				ProductEntry.COLUMN_IMAGE_PATH + " TEXT," +
				ProductEntry.COLUMN_CREATED_DATE + " INTEGER," +
				ProductEntry.COLUMN_SUPPLIER_ID + " INTEGER," +

				// Set up the location column as a foreign key to location table.
				" FOREIGN KEY (" + ProductEntry.COLUMN_SUPPLIER_ID + ") REFERENCES " +
				SupplierEntry.TABLE_NAME + " (" + SupplierEntry._ID + ") " +

				" );";

		Log.v("", "SQL_CREATE_PRODUCT_TABLE: " + SQL_CREATE_PRODUCT_TABLE);

		// execute query to create Product table
		sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		// drop table to create a new one
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SupplierEntry.TABLE_NAME);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
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
			values.put(ProductEntry.COLUMN_PRICE, product.getPrice());
			values.put(ProductEntry.COLUMN_QUANTITY, product.getQuantity());
			values.put(ProductEntry.COLUMN_SUPPLIER_ID, product.getSupplierId());

			if (product.getThumnailPath() != null) {
				values.put(ProductEntry.COLUMN_IMAGE_PATH, product.getThumnailPath());
			}

			Date createdDate = product.getCreatedDate();
			if (createdDate == null) {
				createdDate = new Date();
			}
			values.put(ProductEntry.COLUMN_CREATED_DATE, createdDate.getTime());

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
	public ArrayList<Product> getProducts(String selection, String[] selectionArgs, String sortOrder) {

		ArrayList<Product> results = new ArrayList();

		SQLiteDatabase database = getReadableDatabase();
		// a projection specifies which columns from database will be querying

		selection = (selection == null) ? " " : selection;
		sortOrder = (sortOrder == null) ? " " : sortOrder;
		String query = "SELECT A.*, B.? FROM  ? A INNER JOIN ? B ON A.?=B.? WHERE " + selection + " " + sortOrder;

		// A: ProductEntry, B: SupplierEntry
		String[] args = {
				SupplierEntry.COLUMN_NAME,
				ProductEntry.TABLE_NAME,
				SupplierEntry.TABLE_NAME,
				ProductEntry.COLUMN_SUPPLIER_ID,
				SupplierEntry._ID
		};

		ArrayList listArgs = new ArrayList(Arrays.asList(args));
		listArgs.addAll(Arrays.asList(selectionArgs));

		Cursor cursor = database.rawQuery(query, (String[])listArgs.toArray());

//		Cursor cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		while(cursor.moveToNext()) {

			// get info out of cursor
			long id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
			String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
			int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRICE));
			int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));
			String imagePath = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE_PATH));
			long createdDate = cursor.getLong(cursor.getColumnIndex(ProductEntry.COLUMN_CREATED_DATE));
			long supplierId = cursor.getLong(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_ID));
			String supplierName = cursor.getString(cursor.getColumnIndex(SupplierEntry.COLUMN_NAME));

			Product product = new Product();
			product.setId(id);
			product.setName(name);
			product.setPrice(price);
			product.setQuantity(quantity);
			product.setThumnailPath(imagePath);
			product.setCreatedDate(new Date(createdDate));
			product.setSupplierId(supplierId);
			product.setSupplierName(supplierName);

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
		return getProducts(null, null, null);
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

		return getProducts(selection, selectionArgs, sortOrder);
	}


	/* SUPPLIER METHODS */
	/**
	 * Insert or update a Supplier to database
	 * @param supplier object to insert
	 * @return rowId of newly inserted Supplier, or -1 if insertion fails.
	 */
	public long insertOrUpdateSupplier(Supplier supplier) {
		long insertResult = INSERTION_FAIL_CODE;

		SQLiteDatabase database = getWritableDatabase();
		// wrap insertion in transaction
		database.beginTransaction();

		try {
			// insert query
			ContentValues values = new ContentValues();

			if (supplier.getId() != Supplier.INVALID_ID) {
				// put Id to values, so database can replace/update if exists
				values.put(SupplierEntry._ID, supplier.getId());
			}
			
			// put info to content values
			values.put(SupplierEntry.COLUMN_NAME, supplier.getName());

			if (supplier.getAddress() != null) {
				values.put(SupplierEntry.COLUMN_ADDRESS, supplier.getAddress());
			}

			if (supplier.getTel() != null) {
				values.put(SupplierEntry.COLUMN_TEL, supplier.getTel());
			}

			if (supplier.getEmail() != null) {
				values.put(SupplierEntry.COLUMN_EMAIL, supplier.getEmail());
			}

			if (supplier.getCreatedDate() != null) {
				values.put(SupplierEntry.COLUMN_CREATED_DATE, supplier.getCreatedDate().getTime());
			}

			// run the insert statement
			insertResult = database.insertWithOnConflict(SupplierEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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
	 * Delete all Suppliers in database
	 * @return rowId of newly deleted Suppliers, or -1 if deletion fails.
	 */
	public long deleteAllSuppliers() {
		SQLiteDatabase database = getWritableDatabase();

		long countRowsAffected = 0;
		// wrap deletion in transaction
		database.beginTransaction();
		try {
			// run the delete statement
			countRowsAffected = database.delete(SupplierEntry.TABLE_NAME, null, null);
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
	 * Insert dummy suppliers to database for testing
	 */
	public void insertDummySuppliers() {
		{
			Supplier supplier = new Supplier();

			supplier.setName("Supplier A");
			supplier.setTel("+6512345678");
			supplier.setAddress("+test address A");
			supplier.setEmail("email_supplierA@gmail.com");
			insertOrUpdateSupplier(supplier);
		}

		{
			Supplier supplier = new Supplier();

			supplier.setName("Supplier B");
			supplier.setTel("+6511111");
			supplier.setAddress("+test address B");
			supplier.setEmail("emailB@gmail.com");
			insertOrUpdateSupplier(supplier);
		}

		{
			Supplier supplier = new Supplier();

			supplier.setName("Supplier C");
			supplier.setTel("999999");
			supplier.setAddress("+test address C");
			supplier.setEmail("email_supplier_C@gmail.com");
			insertOrUpdateSupplier(supplier);
		}

	}

}
