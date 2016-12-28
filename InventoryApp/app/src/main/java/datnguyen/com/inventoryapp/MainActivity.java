package datnguyen.com.inventoryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import datnguyen.com.inventoryapp.data.Product;
import datnguyen.com.inventoryapp.data.ProductDbHelper;
import datnguyen.com.inventoryapp.data.Supplier;

public class MainActivity extends AppCompatActivity {

	private final String TAG_VIEW = getClass().getSimpleName();

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

		mSharedInstance = this;

		mDbHelper = new ProductDbHelper(this);
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
			mDbHelper.insertProduct(product);
		}

		{
			Product product = new Product();
			product.setName("Product 2");
			product.setPrice(1000);
			product.setQuantity(5);
			product.setThumnailPath("product2.jpg");
			product.setSupplierId(listSup.get(1).getId());

			// insert to db
			mDbHelper.insertProduct(product);
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
			mDbHelper.insertProduct(product);
		}

		productList = mDbHelper.getAllProducts();

		productAdapter = new ProductAdapter(productList);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recycleView.setLayoutManager(mLayoutManager);
		recycleView.setItemAnimator(new DefaultItemAnimator());
		recycleView.setAdapter(productAdapter);

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

	}

	/**
	 * Start searching by sending search query to BookService, and update UI when get result
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

}
