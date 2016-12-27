package datnguyen.com.inventoryapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import datnguyen.com.inventoryapp.data.Product;

/**
 * Created by datnguyen on 12/27/16.
 */

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_ITEM = 1;
	private final int VIEW_TYPE_PROGRESSBAR = 0;
	private boolean isFooterEnabled = false;

	private boolean isLoadingmore = false;

	private ArrayList<Product> productList;
	public ProductAdapter(ArrayList<Product> list) {
		this.productList = list;
	}

	public void setFooterEnabled(boolean footerEnabled) {
		isFooterEnabled = footerEnabled;
	}

	public void loadmoreCompleted() {
		isLoadingmore = false;
	}

	// nested class for ViewHolder
	public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private ImageView imvThumb = null;
		private TextView tvTitle = null;
		private Product product = null;

		public ProductHolder(View itemView) {
			super(itemView);
			this.imvThumb = (ImageView) itemView.findViewById(R.id.imvThumb);
			this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

			itemView.setOnClickListener(this);
		}

		public void bindProduct(Product product) {
			this.product = product;
			this.tvTitle.setText(product.getName());

			// get imageUrl and use connection to download image
			String imageUrl = product.getThumnailPath();
			if (imageUrl != null) {
				Glide.with(MainActivity.getSharedInstance().getApplicationContext()).load(imageUrl).into(this.imvThumb);
			} else {
				// show default image
			}
		}

		@Override
		public void onClick(View view) {
			Log.v("ProductHolder", "DID CLICK HOLDER");

		}
	}

	public static class LoadmoreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private ProgressBar progressBar = null;
		public LoadmoreHolder(View itemView) {
			super(itemView);
			this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
		}

		public void bindLoadmore(boolean isLoading) {
			this.progressBar.setIndeterminate(isLoading);
		}

		@Override
		public void onClick(View view) {
			Log.v("ProductHolder", "DID CLICK PROGRESS HOLDER");
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		RecyclerView.ViewHolder viewHolder;
		if (viewType == VIEW_TYPE_ITEM) {
			View inflatedView = inflater.inflate(R.layout.product_holder_layout, parent, false);
			viewHolder = new ProductHolder(inflatedView);
		} else {
			View inflatedView = inflater.inflate(R.layout.loadmore_holder_layout, parent, false);
			viewHolder = new LoadmoreHolder(inflatedView);
		}

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof ProductHolder) {
			Product product = productList.get(position);
			((ProductHolder) holder).bindProduct(product);
		} else {
			((LoadmoreHolder) holder).bindLoadmore(true);
//			if (!isLoadingmore && loadmoreInterface != null && position == newsList.size()) {
//				loadmoreInterface.onLoadmoreBegin();
//				isLoadingmore = true;
//			}
		}
	}

	@Override
	public int getItemCount() {
		Log.v("Adapter", "getItemCount: "+ productList.size());

		return (isFooterEnabled) ? productList.size() + 1 : productList.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (isFooterEnabled && position >= productList.size()) {
			return VIEW_TYPE_PROGRESSBAR;
		}
		return VIEW_TYPE_ITEM;
	}
}
