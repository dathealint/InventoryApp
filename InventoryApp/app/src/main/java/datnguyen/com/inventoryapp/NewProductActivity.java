package datnguyen.com.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import datnguyen.com.inventoryapp.data.Product;
import datnguyen.com.inventoryapp.data.ProductDbHelper;
import datnguyen.com.inventoryapp.data.Supplier;

import static datnguyen.com.inventoryapp.Constants.EXTRA_PRODUCT_KEY;
import static datnguyen.com.inventoryapp.Constants.EXTRA_UPDATE_PRODUCT_RESULT_KEY;
import static datnguyen.com.inventoryapp.MainActivity.RESULT_CODE_DELETE_PRODUCT_SUCCESS;
import static datnguyen.com.inventoryapp.MainActivity.RESULT_CODE_EDIT_PRODUCT_SUCCESS;
import static datnguyen.com.inventoryapp.data.Product.getOutputImageFile;
import static datnguyen.com.inventoryapp.data.ProductDbHelper.DELETION_FAIL_CODE;
import static datnguyen.com.inventoryapp.data.ProductDbHelper.INSERTION_FAIL_CODE;

/**
 * Created by datnguyen on 12/28/16.
 */

public class NewProductActivity extends AppCompatActivity {

	private final String TAG_VIEW = getClass().getSimpleName();
	private static final int REQUEST_CODE_PICK_IMAGE = 1;
	private static final int REQUEST_CODE_CALL_PERMISSION = 10;

	private Product product;

	private EditText txtName;
	private EditText txtPrice;
	private EditText txtQuantity;
	private Spinner spinnerSupplier;
	private ImageView imvProduct;

	private ArrayList<Supplier> listSuppliers = new ArrayList();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_product_layout);

		// show UP button to navi back
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// grab UI controls
		txtName = (EditText) findViewById(R.id.txtName);
		txtPrice = (EditText) findViewById(R.id.txtPrice);
		txtQuantity = (EditText) findViewById(R.id.txtQuantity);
		spinnerSupplier = (Spinner) findViewById(R.id.spinnerSupplier);
		imvProduct = (ImageView) findViewById(R.id.imvProduct);

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
		ViewGroup viewEditActions = (ViewGroup) findViewById(R.id.viewEditActions);
		if (getIntent().getExtras() != null) {
			this.product = (Product) getIntent().getExtras().getSerializable(EXTRA_PRODUCT_KEY);

			// show edit actions view
			viewEditActions.setVisibility(View.VISIBLE);
		} else {
			this.product = new Product();

			// show edit actions view
			viewEditActions.setVisibility(View.GONE);
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
				if (checkPermissionSuccess(Manifest.permission.CALL_PHONE)) {
					contactSupplier();
				}
			}
		});

		btnSale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// decrease quantity by 1, minimum 0
				int quantity;
				if (TextUtils.isEmpty(txtQuantity.getText().toString())) {
					quantity = 0;
				} else {
					quantity = Integer.valueOf(txtQuantity.getText().toString());
				}
				quantity = Math.max(quantity - 1, 0);
				product.setQuantity(quantity);

				refreshUI();
			}
		});

		btnSelectImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// open Intent Image Picker
				Intent intent = new Intent();
				//filter images only, not video
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(Intent.createChooser(intent, getString(R.string.title_image_picker)), REQUEST_CODE_PICK_IMAGE);
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
			MainActivity.getSharedInstance().showToast(getString(R.string.text_delete_fail));
		} else {
			// delete success, set result to OK and go back to main activity
			resultIntent.putExtra(EXTRA_UPDATE_PRODUCT_RESULT_KEY, Integer.valueOf(RESULT_CODE_DELETE_PRODUCT_SUCCESS));
			setResult(Activity.RESULT_OK, resultIntent);

			finish();
		}

	}

	/**
	 * update product using information from screen: name, price, quantity
	 */
	private void updateProduct() {

		// get all changes
		product.setName(txtName.getText().toString());
		int price = 0;
		if (TextUtils.isEmpty(txtPrice.getText().toString())) {
			price = 0;
		} else {
			price = Integer.valueOf(txtPrice.getText().toString());
		}
		product.setPrice(price);

		int quantity = 0;
		if (TextUtils.isEmpty(txtQuantity.getText().toString())) {
			quantity = 0;
		} else {
			quantity = Integer.valueOf(txtQuantity.getText().toString());
		}
		product.setQuantity(quantity);

		if (listSuppliers != null && listSuppliers.size() > 0) {
			// get selecting supplier
			Supplier supplier = listSuppliers.get(spinnerSupplier.getSelectedItemPosition());
			product.setSupplierId(supplier.getId());
		}

		// imagepath is already updated

		// validate first
		CustomError error = product.validateEntry();

		if (error != null) {
			// show error
			MainActivity.getSharedInstance().showToast(error.getErrorMessage());
		} else {

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

	}

	private void contactSupplier() {
		ProductDbHelper mDbHelper = ProductDbHelper.getDbHelper(getApplicationContext());
		// fins Supplier Info
		Supplier supplier = mDbHelper.getSupplier(product.getSupplierId());

		Intent intent = null;

		// check phone number first
		if (!TextUtils.isEmpty(supplier.getTel())) {
			intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplier.getTel()));
		} else if (!TextUtils.isEmpty(supplier.getEmail())) {
			intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + supplier.getEmail()));

			intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_supplier_email_subject));
			String mailContent = String.format(getString(R.string.contact_supplier_email_text), product.getName());
			intent.putExtra(Intent.EXTRA_TEXT, mailContent);
		}

		if (intent != null) {
			startActivity(intent);
		}
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
		ArrayAdapter<Supplier> supplierAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, spinnerDataSource);
		spinnerSupplier.setAdapter(supplierAdapter);

		refreshUI();
	}

	private void refreshUI() {
		// is editing existing record
		// pre-fill all information
		txtName.setText(product.getName());
		txtPrice.setText("" + product.getPrice());
		txtQuantity.setText("" + product.getQuantity());

		// load image
		File pictureFile = product.getImageFile();
		if (pictureFile != null) {
			// load image from file and assign to imageview
			Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
			imvProduct.setImageBitmap(bitmap);
		}

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

	private void storeBitmapToInternal(Bitmap bitmap, String fileName) {
		// save image file to local
		File pictureFile = getOutputImageFile(fileName);
		if (pictureFile == null) {
			return;
		}

		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(pictureFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			outputStream.close();

			product.setThumnailPath(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private boolean checkPermissionSuccess(String permisson) {
		if (ContextCompat.checkSelfPermission(this, permisson)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this, permisson)) {
				MainActivity.getSharedInstance().showToast(getString(R.string.text_ask_call_permission));
			}

			// No explanation needed, we can request the permission.
			ActivityCompat.requestPermissions(this, new String[]{permisson}, REQUEST_CODE_CALL_PERMISSION);

			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_CALL_PERMISSION: {
				// check if call permisson is granted
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					contactSupplier();
				} else {
					// permission denied :(
				}
			}
			break;
		}
	}

	// handle activityresult after picking image
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.v(TAG_VIEW, "onActivityResult requestCode: " + requestCode + " - resultCode: " + resultCode);
		switch (requestCode) {
			case REQUEST_CODE_PICK_IMAGE: {
				if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

					// get selected image out of image picker
					Uri uri = data.getData();
					try {
						String fileName = String.valueOf((new Date()).getTime());

						Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
						imvProduct.setImageBitmap(bitmap);

						storeBitmapToInternal(bitmap, fileName);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			break;

		}

	}
}
