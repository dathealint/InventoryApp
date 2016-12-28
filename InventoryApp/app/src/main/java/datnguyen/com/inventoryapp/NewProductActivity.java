package datnguyen.com.inventoryapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import datnguyen.com.inventoryapp.data.Product;
import datnguyen.com.inventoryapp.data.ProductDbHelper;
import datnguyen.com.inventoryapp.data.Supplier;

import static datnguyen.com.inventoryapp.Constants.EXTRA_PRODUCT_KEY;
import static datnguyen.com.inventoryapp.Constants.EXTRA_UPDATE_PRODUCT_RESULT_KEY;
import static datnguyen.com.inventoryapp.MainActivity.RESULT_CODE_DELETE_PRODUCT_SUCCESS;
import static datnguyen.com.inventoryapp.MainActivity.RESULT_CODE_EDIT_PRODUCT_SUCCESS;
import static datnguyen.com.inventoryapp.data.ProductDbHelper.DELETION_FAIL_CODE;
import static datnguyen.com.inventoryapp.data.ProductDbHelper.INSERTION_FAIL_CODE;

/**
 * Created by datnguyen on 12/28/16.
 */

public class NewProductActivity extends AppCompatActivity {

	private Product product;

	private EditText txtName;
	private EditText txtPrice;
	private EditText txtQuantity;
	private Spinner spinnerSupplier;

	private ArrayList<Supplier> listSuppliers = new ArrayList();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_product_layout);

		// grab UI controls
		txtName = (EditText) findViewById(R.id.txtName);
		txtPrice = (EditText) findViewById(R.id.txtPrice);
		txtQuantity = (EditText) findViewById(R.id.txtQuantity);
		spinnerSupplier = (Spinner) findViewById(R.id.spinnerSupplier);

		registerButtonEventHandlers();

		spinnerSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				// get selected supplier
				Supplier selectedSupplier = listSuppliers.get(i);
				// update changes to product
				product.setSupplierId(selectedSupplier.getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		// get product out of bundle
		if (getIntent().getExtras() != null) {
			this.product = (Product) getIntent().getExtras().getSerializable(EXTRA_PRODUCT_KEY);
		} else {
			this.product = new Product();
		}

		reloadData();
	}

	private void registerButtonEventHandlers() {

		Button btnSave = (Button) findViewById(R.id.btnSave);
		Button btnDelete = (Button) findViewById(R.id.btnDelete);
		Button btnOrder = (Button) findViewById(R.id.btnOrder);
		Button btnSale = (Button) findViewById(R.id.btnSale);
		Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);

		// Button Save
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				updateProduct();
			}
		});

		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// show confirm dialog first
				showDeleteConfirmDialog();
			}
		});

		btnOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		btnSale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		btnSelectImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

	}

	private void showDeleteConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.alert_confirm_delelte_product_title));
		builder.setMessage(getString(R.string.alert_confirm_delelte_product_message));
		builder.setPositiveButton(getString(R.string.button_text_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				deleteProduct();
			}
		});

		builder.setNegativeButton(getString(R.string.button_text_no), null);
		builder.show();
	}

	private void deleteProduct() {
		ProductDbHelper mDbHelper = ProductDbHelper.getDbHelper(getApplicationContext());

		long deleteResult = mDbHelper.deleteProduct(product);
		Intent resultIntent = new Intent();

		if (deleteResult == DELETION_FAIL_CODE) {
			Toast.makeText(this, getString(R.string.text_delete_fail), Toast.LENGTH_SHORT).show();
		} else {
			// delete success, set result to OK and go back to main activity
			resultIntent.putExtra(EXTRA_UPDATE_PRODUCT_RESULT_KEY, Integer.valueOf(RESULT_CODE_DELETE_PRODUCT_SUCCESS));
			setResult(Activity.RESULT_OK, resultIntent);

			finish();
		}

	}

	private void updateProduct() {

		// get all changes
		product.setName(txtName.getText().toString());
		product.setPrice(Integer.valueOf(txtPrice.getText().toString()));
		product.setQuantity(Integer.valueOf(txtQuantity.getText().toString()));

		if (listSuppliers != null && listSuppliers.size() > 0) {
			// get selecting supplier
			Supplier supplier = listSuppliers.get(spinnerSupplier.getSelectedItemPosition());
			product.setSupplierId(supplier.getId());
		}

		// TODO: update image

		// save to database
		ProductDbHelper mDbHelper = ProductDbHelper.getDbHelper(getApplicationContext());
		long insertResult = mDbHelper.insertOrUpdateProduct(product);
		Intent resultIntent = new Intent();

		if (insertResult == INSERTION_FAIL_CODE) {
			resultIntent.putExtra(EXTRA_UPDATE_PRODUCT_RESULT_KEY, Integer.valueOf(RESULT_CODE_EDIT_PRODUCT_SUCCESS));
		} else {
			resultIntent.putExtra(EXTRA_UPDATE_PRODUCT_RESULT_KEY, Integer.valueOf(RESULT_CODE_EDIT_PRODUCT_SUCCESS));
		}

		setResult(Activity.RESULT_OK, resultIntent);

		finish();
	}

	private void reloadData() {

		// fetch list suppliers from database
		ProductDbHelper mDbHelper = ProductDbHelper.getDbHelper(getApplicationContext());
		listSuppliers = mDbHelper.getAllSuppliers();

		// generate new list of supplier's names used as datasource
		ArrayList<String> spinnerDataSource = new ArrayList<>();
		for (Supplier supplier : listSuppliers) {
			spinnerDataSource.add(supplier.getName());
		}
		// fill data to spinner
		ArrayAdapter<Supplier> supplierAdapter = new ArrayAdapter(this, R.layout.supplier_row_layout, spinnerDataSource);
		spinnerSupplier.setAdapter(supplierAdapter);

		// is editing existing record
		// pre-fill all information
		txtName.setText(product.getName());
		txtPrice.setText("" + product.getPrice());
		txtQuantity.setText("" + product.getQuantity());

		// find index if this supplier Id
		int index = -1;
		for (int i = 0; i < listSuppliers.size(); i++) {
			Supplier supplier = listSuppliers.get(i);
			if (supplier.getId() == this.product.getSupplierId()) {
				index = i;
				break;
			}
		}

		// we found a winner
		if (index != -1) {
			spinnerSupplier.setSelection(index);
		}
	}

}
