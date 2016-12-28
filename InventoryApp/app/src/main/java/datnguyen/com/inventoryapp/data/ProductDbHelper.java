package datnguyen.com.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import datnguyen.com.inventoryapp.data.SupplierContract.*;
import datnguyen.com.inventoryapp.data.ProductContract.*;

import static datnguyen.com.inventoryapp.Constants.INVALID_ID;

/**
 * Created by datnguyen on 12/27/16.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1; // first version
	private static final String DATABASE_NAME = "product.db";
	public static final int INSERTION_FAIL_CODE = -1;
	public static final int DELETION_FAIL_CODE = -1;

	private static ProductDbHelper sharedInstance = null;

	private ProductDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static ProductDbHelper getDbHelper(Context context) {

		synchronized (ProductDbHelper.class) {
			if (sharedInstance == null) {
				sharedInstance = new ProductDbHelper(context);
			}
		}
		return sharedInstance;
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
	public long insertOrUpdateProduct(Product product) {
		long insertResult = INSERTION_FAIL_CODE;

		SQLiteDatabase database = getWritableDatabase();
		// wrap insertion in transaction
		database.beginTransaction();

		try {
			// insert query
			ContentValues values = new ContentValues();

			if (product.getId() != INVALID_ID) {
				// put Id to values, so database can replace/update if exists
				values.put(ProductEntry._ID, product.getId());
			}

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
				values.put(ProductEntry.COLUMN_CREATED_DATE, createdDate.getTime());
			}

			// run the insert statement
			insertResult = database.insertWithOnConflict(ProductEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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

			// TODO: delete images in internal storage linked to any products
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// end transaction
			database.endTransaction();
		}

		return countRowsAffected;
	}

	/**
	 * Delete one Product from database
	 * @return number of row affected
	 */
	public long deleteProduct(Product product) {
		SQLiteDatabase database = getWritableDatabase();

		long countRowsAffected = 0;
		// wrap deletion in transaction
		database.beginTransaction();
		try {
			// run the delete statement
			String where = ProductEntry._ID + " = ?";
			String[] whereArgs = { String.valueOf(product.getId())};

			countRowsAffected = database.delete(ProductEntry.TABLE_NAME, where, whereArgs);
			database.setTransactionSuccessful();

			// TODO: delete image in internal storage linked to this product
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

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		queryBuilder.setTables(ProductEntry.TABLE_NAME + " INNER JOIN " + SupplierEntry.TABLE_NAME +
				" ON " + ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_SUPPLIER_ID + " = " + SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID);

		// a projection specifies which columns from database will be querying
		String[] projection = {
				ProductEntry.TABLE_NAME + "." + ProductEntry._ID,
				ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_NAME,
				ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_PRICE,
				ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_QUANTITY,
				ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_IMAGE_PATH,
				ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_CREATED_DATE,
				ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_SUPPLIER_ID,
				SupplierEntry.TABLE_NAME + "." + SupplierEntry.COLUMN_NAME + " AS SupplierName"
		};

		Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);

		while(cursor.moveToNext()) {

			// get info out of cursor
			long id = cursor.getLong(cursor.getColumnIndex(ProductEntry._ID));
			String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_NAME));
			int price = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRICE));
			int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));
			String imagePath = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_IMAGE_PATH));
			long createdDate = cursor.getLong(cursor.getColumnIndex(ProductEntry.COLUMN_CREATED_DATE));
			long supplierId = cursor.getLong(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_ID));
			String supplierName = cursor.getString(cursor.getColumnIndex("SupplierName"));

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

		return getProducts(null, null, ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_NAME + " ASC");
	}

	/**
	 * Fetch Product entries from database matching name starting with provided string
	 * @return list of sorted Products from database matching conditions
	 */
	public ArrayList<Product> getProductsNameStartWith(String prefix) {
		// filter result WHERE name starts with T
		String selection = ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_NAME + " LIKE ?";
		String[] selectionArgs = { prefix + "%" };

		String sortOrder = ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_NAME + " ASC";

		return getProducts(selection, selectionArgs, sortOrder);
	}

	/**
	 * Fetch Product entries from database matching name starting with provided string
	 * @return list of sorted Products from database matching conditions
	 */
	public ArrayList<Product> searchProduct(String keyword) {
		// filter result WHERE name starts with T
		String selection = ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_NAME + " LIKE ? ";
		String[] selectionArgs = { "%" + keyword + "%" };

		String sortOrder = ProductEntry.TABLE_NAME + "." + ProductEntry.COLUMN_NAME + " ASC ";

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

			if (supplier.getId() != INVALID_ID) {
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


	/**
	 * Fetch Supplier entries from database
	 * @return list of all Supplier entries from database
	 */
	public ArrayList<Supplier> getAllSuppliers() {

		return getSuppliers(null, null, SupplierEntry.COLUMN_NAME + " ASC");
	}

	/**
	 * Fetch Supplier entry by Id
	 * @return Supplier Entry matching Id, otherwise return null
	 */
	public Supplier getSupplier(long id) {

		// filter result WHERE name starts with T
		String selection = SupplierEntry._ID + " = ? ";
		String[] selectionArgs = {"" + id};

		ArrayList<Supplier> list = getSuppliers(selection, selectionArgs, SupplierEntry.COLUMN_NAME + " ASC");

		if (list != null && list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Fetch Supplier entries from database
	 * @param selection WHERE condition, e.g. "NAME LIKE ?"
	 * @param selectionArgs values to pass into selection
	 * @param sortOrder sortOrder to sort result records
	 * @return list of sorted Suppliers from database matching conditions
	 */
	public ArrayList<Supplier> getSuppliers(String selection, String[] selectionArgs, String sortOrder) {

		ArrayList<Supplier> results = new ArrayList();

		SQLiteDatabase database = getReadableDatabase();
		// a projection specifies which columns from database will be querying
		String[] projection = {
				SupplierEntry._ID,
				SupplierEntry.COLUMN_NAME,
				SupplierEntry.COLUMN_ADDRESS,
				SupplierEntry.COLUMN_TEL,
				SupplierEntry.COLUMN_EMAIL,
				SupplierEntry.COLUMN_CREATED_DATE
		};

		Cursor cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		while(cursor.moveToNext()) {

			// get info out of cursor
			long id = cursor.getLong(cursor.getColumnIndex(SupplierEntry._ID));
			String name = cursor.getString(cursor.getColumnIndex(SupplierEntry.COLUMN_NAME));
			String address = cursor.getString(cursor.getColumnIndex(SupplierEntry.COLUMN_ADDRESS));
			String tel = cursor.getString(cursor.getColumnIndex(SupplierEntry.COLUMN_TEL));
			String email = cursor.getString(cursor.getColumnIndex(SupplierEntry.COLUMN_EMAIL));
			long createdDate = cursor.getLong(cursor.getColumnIndex(SupplierEntry.COLUMN_CREATED_DATE));

			Supplier supplier = new Supplier();
			supplier.setId(id);
			supplier.setName(name);
			supplier.setAddress(address);
			supplier.setTel(tel);
			supplier.setEmail(email);
			supplier.setCreatedDate(new Date(createdDate));

			results.add(supplier);
		}

		cursor.close();
		return results;
	}

}
