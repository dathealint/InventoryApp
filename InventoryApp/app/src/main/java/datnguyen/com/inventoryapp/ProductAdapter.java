package datnguyen.com.inventoryapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import datnguyen.com.inventoryapp.data.Product;

/**
 * Created by datnguyen on 12/27/16.
 */

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private ArrayList<Product> productList;
	private View.OnClickListener onClickListener;
	private View.OnClickListener onBtnSaleClickListener;

	public void setOnClickProductListener(View.OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public void setOnBtnSaleClickListener(View.OnClickListener onBtnSaleClickListener) {
		this.onBtnSaleClickListener = onBtnSaleClickListener;
	}

	public ProductAdapter(ArrayList<Product> list) {
		this.productList = list;
	}

	// nested class for ViewHolder
	public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private ImageView imvThumb = null;
		private TextView tvName = null;
		private TextView tvPrice = null;
		private TextView tvQuantity = null;
		private TextView tvSupplier = null;
		private Button btnSale = null;

		private View.OnClickListener onBtnSaleClickListenerHolder;

		public void setOnBtnSaleClickListener(View.OnClickListener listener) {
			this.onBtnSaleClickListenerHolder = listener;
		}

		public ProductHolder(View itemView) {
			super(itemView);
			this.imvThumb = (ImageView) itemView.findViewById(R.id.imvThumb);
			this.tvName = (TextView) itemView.findViewById(R.id.tvName);
			this.tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
			this.tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
			this.tvSupplier = (TextView) itemView.findViewById(R.id.tvSupplier);
			this.btnSale = (Button) itemView.findViewById(R.id.btnSale);
			this.btnSale.setOnClickListener(this);
		}

		public void bindProduct(Product product, int position) {
			this.tvName.setText(product.getName());
			this.tvPrice.setText(MainActivity.getSharedInstance().getString(R.string.text_row_product_price) + " " + product.getPrice());
			this.tvQuantity.setText(MainActivity.getSharedInstance().getString(R.string.text_row_product_quantity) + " " + product.getQuantity());
			this.tvSupplier.setText(MainActivity.getSharedInstance().getString(R.string.text_row_product_supplier) + " " + product.getSupplierName());

			// get imageUrl and use connection to download image
			File pictureFile = product.getImageFile();
			Bitmap bitmap = null;
			if (pictureFile != null) {
				// load image from file and assign to imageview
				bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
			}

			if (bitmap != null) {
				imvThumb.setImageBitmap(bitmap);
			} else {
				// use default image if specific bitmap not available
				imvThumb.setImageDrawable(MainActivity.getSharedInstance().getResources().getDrawable(R.drawable.ic_default_product_thumb));
			}

			this.btnSale.setTag(position);
			this.btnSale.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if (view.getId() == R.id.btnSale) {
				if (onBtnSaleClickListenerHolder != null) {
					onBtnSaleClickListenerHolder.onClick(view);
				}
			}
		}

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		RecyclerView.ViewHolder viewHolder;
		View inflatedView = inflater.inflate(R.layout.product_holder_layout, parent, false);
		viewHolder = new ProductHolder(inflatedView);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		Product product = productList.get(position);

		ProductHolder productHolder = (ProductHolder) holder;
		productHolder.bindProduct(product, position);
		productHolder.itemView.setTag(position);
		productHolder.itemView.setOnClickListener(onClickListener);
		productHolder.setOnBtnSaleClickListener(this.onBtnSaleClickListener);
	}

	@Override
	public int getItemCount() {
		Log.v("Adapter", "getItemCount: " + productList.size());
		return productList.size();
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}
}
