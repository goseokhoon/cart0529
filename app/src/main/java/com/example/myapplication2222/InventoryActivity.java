package com.example.myapplication2222;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter inventoryAdapter;
    private List<InventoryItem> inventoryList;
    private FirebaseFirestore db;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        context = this;

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // LinearLayoutManager를 통해 스크롤 설정
        inventoryList = new ArrayList<>();
        inventoryAdapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(inventoryAdapter);

        // FirebaseFirestore 객체 초기화
        db = FirebaseFirestore.getInstance();

        // Firestore에서 재고 데이터 가져오기
        fetchInventoryItems();

        // Firestore 실시간 업데이트 설정
        setupFirestoreListener();
    }

    private void fetchInventoryItems() {
        db.collection("inventory")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        inventoryList.clear(); // 기존 목록 초기화
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");

                            // 가격을 double로 가져와서 int로 변환
                            Double priceDouble = document.getDouble("price");
                            int price = (priceDouble != null) ? priceDouble.intValue() : 0;

                            Long stockLong = document.getLong("stock");
                            int stock = (stockLong != null) ? stockLong.intValue() : 0;

                            InventoryItem item = new InventoryItem(name, price, stock);
                            inventoryList.add(item);
                        }
                        inventoryAdapter.notifyDataSetChanged(); // Adapter에 데이터 변경 알림
                    } else {
                        // 데이터 가져오기 실패 처리
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("InventoryActivity", "Error fetching data: " + e.getMessage());
                            Toast.makeText(context, "데이터를 가져오는 데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupFirestoreListener() {
        db.collection("inventory")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("InventoryActivity", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d("InventoryActivity", "New item: " + dc.getDocument().getData());
                                    updateInventoryList(dc.getDocument());
                                    break;
                                case MODIFIED:
                                    Log.d("InventoryActivity", "Modified item: " + dc.getDocument().getData());
                                    updateInventoryList(dc.getDocument());
                                    break;
                                case REMOVED:
                                    Log.d("InventoryActivity", "Removed item: " + dc.getDocument().getData());
                                    deleteInventoryItem(dc.getDocument().getString("name"));
                                    break;
                            }
                        }
                    }
                });
    }

    private void updateInventoryList(QueryDocumentSnapshot document) {
        String name = document.getString("name");

        // 가격을 double로 가져와서 int로 변환
        Double priceDouble = document.getDouble("price");
        int price = (priceDouble != null) ? priceDouble.intValue() : 0;

        Long stockLong = document.getLong("stock");
        int stock = (stockLong != null) ? stockLong.intValue() : 0;

        InventoryItem updatedItem = new InventoryItem(name, price, stock);

        boolean isNewItem = true;
        for (int i = 0; i < inventoryList.size(); i++) {
            if (inventoryList.get(i).getName().equals(name)) {
                inventoryList.set(i, updatedItem);
                inventoryAdapter.notifyItemChanged(i);
                isNewItem = false;
                break;
            }
        }

        if (isNewItem) {
            inventoryList.add(updatedItem);
            inventoryAdapter.notifyItemInserted(inventoryList.size() - 1); // 마지막 위치에 아이템 추가
        }
    }

    private void deleteInventoryItem(String name) {
        for (int i = 0; i < inventoryList.size(); i++) {
            if (inventoryList.get(i).getName().equals(name)) {
                inventoryList.remove(i);
                inventoryAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }
}




