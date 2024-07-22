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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Executors.newSingleThreadExecutor().execute(() -> {
            db.collection("inventory")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<InventoryItem> newInventoryList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                InventoryItem item = document.toObject(InventoryItem.class);
                                newInventoryList.add(item);
                            }
                            runOnUiThread(() -> {
                                inventoryList.clear();
                                inventoryList.addAll(newInventoryList);
                                inventoryAdapter.notifyDataSetChanged();
                            });
                        } else {
                            Log.e("InventoryActivity", "Error fetching data: " + task.getException());
                            runOnUiThread(() -> Toast.makeText(context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show());
                        }
                    });
        });
    }

    private void setupFirestoreListener() {
        db.collection("inventory")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("InventoryActivity", "Listen failed.", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            InventoryItem updatedItem = dc.getDocument().toObject(InventoryItem.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    addInventoryItem(updatedItem);
                                    break;
                                case MODIFIED:
                                    updateInventoryItem(updatedItem);
                                    break;
                                case REMOVED:
                                    removeInventoryItem(updatedItem.getName());
                                    break;
                            }
                        }
                    }
                });
    }

    private void addInventoryItem(InventoryItem item) {
        if (findInventoryIndexByName(item.getName()) == -1) {
            inventoryList.add(item);
            inventoryAdapter.notifyItemInserted(inventoryList.size() - 1);
        }
    }

    private void updateInventoryItem(InventoryItem item) {
        int index = findInventoryIndexByName(item.getName());
        if (index != -1) {
            inventoryList.set(index, item);
            inventoryAdapter.notifyItemChanged(index);
        }
    }

    private void removeInventoryItem(String name) {
        int index = findInventoryIndexByName(name);
        if (index != -1) {
            inventoryList.remove(index);
            inventoryAdapter.notifyItemRemoved(index);
        }
    }

    private int findInventoryIndexByName(String name) {
        for (int i = 0; i < inventoryList.size(); i++) {
            if (inventoryList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
