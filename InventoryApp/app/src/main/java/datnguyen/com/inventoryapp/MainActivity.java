package datnguyen.com.inventoryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import datnguyen.com.inventoryapp.data.Product;
import datnguyen.com.inventoryapp.data.ProductDbHelper;
import datnguyen.com.inventoryapp.data.Supplier;

import static datnguyen.com.inventoryapp.Constants.EXTRA_PRODUCT_KEY;
import static datnguyen.com.inventoryapp.Constants.EXTRA_UPDATE_PRODUCT_RESULT_KEY;

public class MainActivity extends AppCompatActivity {

	private final String TAG_VIEW = getClass().getSimpleName();
	public static final int REQUEST_CODE_EDIT_PRODUCT = 100;
	public static final int REQUEST_CODE_ADD_PRODUCT = 200;

	public static final int RESULT_CODE_EDIT_PRODUCT_SUCCESS = 101;
	public static final int RESULT_CODE_EDIT_PRODUCT_FAILURE = 102;

	public static final int RESULT_CODE_DELETE_PRODUCT_SUCCESS = 301;
	public static final int RESULT_CODE_DELETE_PRODUCT_FAILURE = 302;


	private SearchView searchView = null;
	private RecyclerView recycleView = null;
	private TextView tvErrorMessage = null;

	private ProductAdapter productAdapter = null;
	private ArrayList<Product> productList = new ArrayList<>();

	private static MainActivity mSharedInstance;

	public static MainActivity getSharedInstance() {
		return mSharedInstance;
	}

	ProductDbHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		searchView = (SearchView) findViewById(R.id.searchView);
		recycleView = (RecyclerView) findViewById(R.id.recycleView);
		tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
		tvErrorMessage.setVisibility(View.GONE);

		// grab a static instance of MainActivity
		mSharedInstance = this;

		mDbHelper = ProductDbHelper.getDbHelper(getApplicationContext());

		// when in need to create dummy data, chagne to true.
		// switch to false to persist data so creating new or deleting products will have changes we can see and test
		boolean createDummyData = false;
		if (createDummyData) {
			mDbHelper.deleteAllSuppliers();
			mDbHelper.insertDummySuppliers();

			mDbHelper.deleteAllProducts();

			//test suppliers
			ArrayList<Supplier> listSup = mDbHelper.getAllSuppliers();
			Log.v(TAG_VIEW, "listSup count: " + listSup.size());

			// test
			{
				Product product = new Product();
				product.setName("Product 1");
				product.setPrice(350);
				product.setQuantity(30);
				product.setThumnailPath("product1.jpg");
				product.setSupplierId(listSup.get(0).getId());

				// insert to db
				mDbHelper.insertOrUpdateProduct(product);
			}

			{
				Product product = new Product();
				product.setName("Product 2");
				product.setPrice(1000);
				product.setQuantity(5);
				product.setThumnailPath("product2.jpg");
				product.setSupplierId(listSup.get(1).getId());

				// insert to db
				mDbHelper.insertOrUpdateProduct(product);
			}

			// test
			{
				Product product = new Product();
				product.setName("Product 3");
				product.setPrice(3500);
				product.setQuantity(600);
				product.setThumnailPath("product3.jpg");
				product.setSupplierId(listSup.get(2).getId());

				// insert to db
				mDbHelper.insertOrUpdateProduct(product);
			}
		}

		productAdapter = new ProductAdapter(productList);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recycleView.setLayoutManager(mLayoutManager);
		recycleView.setItemAnimator(new DefaultItemAnimator());
		recycleView.setAdapter(productAdapter);

		productAdapter.setOnClickProductListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// get position
				int position = (int) view.getTag();
				Product product = productList.get(position);
				// open detail view
				Intent intent = new Intent(getApplicationContext(), NewProductActivity.class);

				// pass data
				intent.putExtra(EXTRA_PRODUCT_KEY, product);

				startActivityForResult(intent, REQUEST_CODE_EDIT_PRODUCT);
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {

				searchView.clearFocus();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {

				// hide error message if showing
				tvErrorMessage.setText("");

				recycleView.setVisibility(View.VISIBLE);
				tvErrorMessage.setVisibility(View.GONE);

				startSearch(s);

				return true;
			}
		});

		// reload data
		reloadData();
	}

	/**
	 * Reload product lists from database and refresh recyclerview
	 */
	private void reloadData() {
		ArrayList<Product> allRecords = mDbHelper.getAllProducts();

		productList.clear();
		productList.addAll(allRecords);
		productAdapter.notifyDataSetChanged();
	}

	/**
	 * Start searching by sending search query to BookService, and update UI when get result
	 *
	 * @param keyword: keyword to search
	 */
	private void startSearch(String keyword) {
		keyword = keyword.trim();

		// test query from db first
		ArrayList<Product> searchResult = mDbHelper.searchProduct(keyword);

		productList.clear();
		productList.addAll(searchResult);

		Log.v(TAG_VIEW, "search result count: " + productList.size());

		productAdapter.notifyDataSetChanged();

	}

	/**
	 * open NewProductActivity to either create a new product or editing an existing one
	 */
	private void openAddNewActivity() {
		// open detail view
		Intent intent = new Intent(getApplicationContext(), NewProductActivity.class);

		startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT);
	}

	/**
	 * show toast
	 * TODO: later will move this method to a separated CommonUtilities class which contains all common and necessary methods like this.
	 * @param text to show in toast
	 */
	public void showToast(String text) {
		// show toast to let us know it works
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.home_menu_layout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuAdd) {
			openAddNewActivity();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.v(TAG_VIEW, "onActivityResult requestCode: " + requestCode + " - resultCode: " + resultCode);
		switch (requestCode) {
			case REQUEST_CODE_EDIT_PRODUCT: {
				if (resultCode == Activity.RESULT_OK) {
					// check real result from intents
					int result = data.getExtras().getInt(EXTRA_UPDATE_PRODUCT_RESULT_KEY);
					if (result == RESULT_CODE_EDIT_PRODUCT_SUCCESS) {
						// update db
						reloadData();
						// show toast to let us know it works
						showToast(getString(R.string.text_data_change_updated));
					} else if (result == RESULT_CODE_DELETE_PRODUCT_SUCCESS) {
						// update db
						reloadData();
						// show toast to let us know it works
						showToast(getString(R.string.text_delete_success));
					} else {
						// update error, do nothing
					}
				}

			}
			break;
			case REQUEST_CODE_ADD_PRODUCT: {
				if (resultCode == Activity.RESULT_OK) {
					// check real result from intents
					int result = data.getExtras().getInt(EXTRA_UPDATE_PRODUCT_RESULT_KEY);
					if (result == RESULT_CODE_EDIT_PRODUCT_SUCCESS) {
						// update db
						reloadData();

					} else {
						// update error, do nothing
					}
				}

			}
			break;

		}
	}
}
