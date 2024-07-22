package com.example.myapplication2222;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private Context context;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        context = this;

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this, this); // Context로서 this(CartActivity)를 전달
        recyclerView.setAdapter(productAdapter);

        // 총 결제액 TextView 초기화
        totalPriceTextView = findViewById(R.id.total_amount);

        // FirebaseFirestore 객체 초기화
        db = FirebaseFirestore.getInstance();

        // Firestore에서 상품 데이터 가져오기
        fetchProducts();

        // Firestore 실시간 업데이트 설정
        setupFirestoreListener();

        // '결제' 버튼 설정
        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, PayProductActivity.class);
            startActivity(intent);
        });
    }

    private void fetchProducts() {
        db.collection("kartrider")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear(); // 기존 목록 초기화
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // 문서 ID 가져오기
                            String name = document.getString("name");

                            // 가격을 double로 가져와서 int로 변환
                            Double priceDouble = document.getDouble("price");
                            int price = (priceDouble != null) ? priceDouble.intValue() : 0;

                            Long quantityLong = document.getLong("quantity");
                            int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                            Product product = new Product(id, name, price, quantity);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged(); // Adapter에 데이터 변경 알림
                        updateTotalPrice(); // 총 결제액 업데이트
                    } else {
                        // 데이터 가져오기 실패 처리
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("CartActivity", "Error fetching data: " + e.getMessage());
                            Toast.makeText(context, "데이터를 가져오는 데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupFirestoreListener() {
        db.collection("kartrider")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("CartActivity", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("CartActivity", "New product: " + dc.getDocument().getData());
                                    updateProductList(dc.getDocument());
                                    break;
                                case MODIFIED:
                                    Log.d("CartActivity", "Modified product: " + dc.getDocument().getData());
                                    updateProductList(dc.getDocument());
                                    break;
                                case REMOVED:
                                    Log.d("CartActivity", "Removed product: " + dc.getDocument().getData());
                                    deleteProduct(dc.getDocument().getId());
                                    break;
                            }
                        }
                    }
                });
    }

    private void updateProductList(QueryDocumentSnapshot document) {
        String id = document.getId(); // 문서 ID 가져오기
        String name = document.getString("name");

        // 가격을 double로 가져와서 int로 변환
        Double priceDouble = document.getDouble("price");
        int price = (priceDouble != null) ? priceDouble.intValue() : 0;

        Long quantityLong = document.getLong("quantity");
        int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

        Product updatedProduct = new Product(id, name, price, quantity);

        // 기존 목록에 추가되지 않았으면 새로 추가
        boolean isNewProduct = true;
        for (Product product : productList) {
            if (product.getId().equals(id)) {
                productList.set(productList.indexOf(product), updatedProduct);
                productAdapter.notifyItemChanged(productList.indexOf(product)); // 변경된 위치의 아이템 업데이트
                isNewProduct = false;
                break;
            }
        }

        if (isNewProduct) {
            productList.add(updatedProduct);
            productAdapter.notifyItemInserted(productList.size() - 1); // 마지막 위치에 아이템 추가
        }

        // 총 결제액 업데이트
        updateTotalPrice();
    }

    private void deleteProduct(String productId) {
        // 상품 목록에서 제거
        productList.removeIf(product -> product.getId().equals(productId));
        productAdapter.notifyDataSetChanged();

        // 총 결제액 업데이트
        updateTotalPrice();
    }

    // 총 결제액을 계산하고 업데이트하는 함수
    private void updateTotalPrice() {
        int totalPrice = 0;
        for (Product product : productList) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        totalPriceTextView.setText("총 금액: " + totalPrice + "원");
    }

    // ProductAdapter에서 클릭 이벤트를 처리하기 위한 인터페이스 구현
    @Override
    public void onProductDeleteClick(int position) {
        Product product = productList.get(position);
        deleteProduct(product.getId()); // 상품 ID를 기반으로 상품 삭제 메서드 호출
        // Firestore에서도 삭제할 수 있도록 추가 작업 필요
        db.collection("kartrider").document(product.getId()).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("CartActivity", "DocumentSnapshot successfully deleted!");
                    } else {
                        Log.w("CartActivity", "Error deleting document", task.getException());
                    }
                });
    }

    // 수량 변경 이벤트를 처리하여 총 결제액 업데이트
    @Override
    public void onProductQuantityChanged() {
        updateTotalPrice();
    }
}










