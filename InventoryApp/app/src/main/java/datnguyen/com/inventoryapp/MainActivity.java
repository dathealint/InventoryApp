package datnguyen.com.inventoryapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import datnguyen.com.inventoryapp.data.Product;
import datnguyen.com.inventoryapp.data.ProductDbHelper;

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
		mDbHelper.insertDummySuppliers();

		// test
		{
			Product product = new Product();
			product.setName("Product 1");
			productList.add(product);
		}

		{
			Product product = new Product();
			product.setName("Product 2");
			productList.add(product);
		}

		productAdapter = new ProductAdapter(productList);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recycleView.setLayoutManager(mLayoutManager);
		recycleView.setItemAnimator(new DefaultItemAnimator());
		recycleView.setAdapter(productAdapter);

	}

}
