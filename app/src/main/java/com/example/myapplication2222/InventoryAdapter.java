package com.example.myapplication2222;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> inventoryList;

    public InventoryAdapter(List<InventoryItem> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public void updateInventoryList(List<InventoryItem> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new InventoryDiffCallback(inventoryList, newList));
        inventoryList.clear();
        inventoryList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {

        // UI 요소 선언 및 초기화

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI 요소 초기화
        }

        public void bind(InventoryItem item) {
            // UI 요소에 데이터 바인딩
        }
    }

    static class InventoryDiffCallback extends DiffUtil.Callback {

        private final List<InventoryItem> oldList;
        private final List<InventoryItem> newList;

        public InventoryDiffCallback(List<InventoryItem> oldList, List<InventoryItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // 아이템의 고유성을 판단할 수 있는 기준이 있다면 사용
            return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}

