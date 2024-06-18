package com.example.myapplication2222;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication2222.R; // R.java 파일을 임포트합니다.
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        // FirebaseFirestore 객체 초기화
        db = FirebaseFirestore.getInstance();

        // Firestore에서 상품 데이터 가져오기
        db.collection("kartrider")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                double price = document.getDouble("price");
                                int quantity = document.getLong("quantity").intValue();

                                Product product = new Product(name, price, quantity);
                                productList.add(product);
                            }
                            productAdapter.notifyDataSetChanged(); // Adapter에 데이터 변경 알림
                        } else {
                            // 데이터 가져오기 실패 처리
                            Exception e = task.getException(); // 예외 객체 가져오기
                            if (e != null) {
                                // 예외 로깅 또는 사용자에게 메시지 표시 등의 처리
                                Log.e("CartActivity", "Error fetching data: " + e.getMessage());
                                // 사용자에게 메시지 표시 예시
                                Toast.makeText(CartActivity.this, "데이터를 가져오는 데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


        // '상품 추가' 버튼 설정
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        // '상품 삭제' 버튼 설정
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, DeleteProductActivity.class);
                startActivity(intent);
            }
        });

        // '결제' 버튼 설정
        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, PayProductActivity.class);
                startActivity(intent);
            }
        });
    }
}

