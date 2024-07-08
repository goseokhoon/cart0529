package com.example.myapplication2222;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication2222.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductClickListener onProductClickListener;
    private Context context;

    public ProductAdapter(List<Product> productList, OnProductClickListener listener, Context context) {
        this.productList = productList;
        this.onProductClickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private TextView priceTextView;
        private TextView quantityTextView;
        private Button deleteButton;
        private Button decreaseButton;
        private Button increaseButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);

            // 클릭 리스너 설정
            deleteButton.setOnClickListener(this);
            decreaseButton.setOnClickListener(this);
            increaseButton.setOnClickListener(this);
        }

        public void bind(Product product) {
            nameTextView.setText(product.getName());
            priceTextView.setText(String.valueOf(product.getPrice()));
            quantityTextView.setText(String.valueOf(product.getQuantity()));
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Product product = productList.get(position);

            switch (v.getId()) {
                case R.id.deleteButton:
                    // 삭제 버튼 클릭 시
                    onProductClickListener.onProductDeleteClick(position);
                    break;
                case R.id.decreaseButton:
                    // 감소 버튼 클릭 시
                    int currentQuantity = product.getQuantity();
                    if (currentQuantity > 0) {
                        product.setQuantity(currentQuantity - 1);
                        quantityTextView.setText(String.valueOf(currentQuantity - 1));
                        updatePrice(product);
                    }
                    break;
                case R.id.increaseButton:
                    // 증가 버튼 클릭 시
                    product.setQuantity(product.getQuantity() + 1);
                    quantityTextView.setText(String.valueOf(product.getQuantity()));
                    updatePrice(product);
                    break;
            }
        }

        // 가격 업데이트 메서드
        private void updatePrice(Product product) {
            double totalPrice = product.getPrice() * product.getQuantity();
            priceTextView.setText(String.valueOf(totalPrice));
        }
    }

    // 클릭 리스너를 위한 인터페이스 정의
    public interface OnProductClickListener {
        void onProductDeleteClick(int position);
    }
}








