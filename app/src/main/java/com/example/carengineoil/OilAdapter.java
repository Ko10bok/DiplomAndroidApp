package com.example.carengineoil;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OilAdapter extends RecyclerView.Adapter<OilAdapter.OilViewHolder> {

    private List<Oil> oilList;
    private OnOilClickListener listener;
    private Set<Integer> selectedPositions = new HashSet<>();
    private boolean isSelectionMode = false;

    public interface OnOilClickListener {
        void onOilClick(Oil oil);
    }

    public OilAdapter(List<Oil> oils) {
        this.oilList = oils;
    }

    public void updateData(List<Oil> newOilList) {
        this.oilList = newOilList;
        clearSelection();
        notifyDataSetChanged();
    }

    public void setOnOilClickListener(OnOilClickListener listener) {
        this.listener = listener;
    }

    public List<Oil> getOilList() {
        return oilList;
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if (!selectionMode) {
            clearSelection();
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return isSelectionMode;
    }

    public void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        selectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedPositions.size();
    }

    public List<Oil> getSelectedOils() {
        List<Oil> selected = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            selected.add(oilList.get(pos));
        }
        return selected;
    }

    @NonNull
    @Override
    public OilViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.oil_item, parent, false);
        return new OilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OilViewHolder holder, int position) {
        Oil oil = oilList.get(position);
        holder.textName.setText(oil.getName());

        String parameters = oil.getParameters();
        String failedParamsStr = oil.getFailedParameters();


        if (failedParamsStr == null || failedParamsStr.isEmpty()) {
            holder.textParams.setTextColor(Color.BLACK);
            holder.textParams.setText(parameters);
        } else {
            String[] paramPairs = parameters.split(",\\s*");
            String[] failedParams = failedParamsStr.split(",\\s*");

            SpannableStringBuilder spannable = new SpannableStringBuilder();

            for (int i = 0; i < paramPairs.length; i++) {
                String paramPair = paramPairs[i];

                boolean isFailed = false;
                for (String failedParam : failedParams) {
                    if (paramPair.startsWith(failedParam)) {
                        isFailed = true;
                        break;
                    }
                }

                int start = spannable.length();
                spannable.append(paramPair);
                int end = spannable.length();

                if (isFailed) {
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (i != paramPairs.length - 1) {
                    spannable.append(", ");
                }
            }

            holder.textParams.setText(spannable);
        }

        // Подсветка выбранных элементов
        holder.itemView.setBackgroundColor(selectedPositions.contains(position) ? Color.LTGRAY : Color.WHITE);

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleSelection(position);
            } else {
                if (listener != null) {
                    listener.onOilClick(oil);
                }
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                setSelectionMode(true);
                toggleSelection(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return oilList != null ? oilList.size() : 0;
    }

    static class OilViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textParams;

        public OilViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textParams = itemView.findViewById(R.id.textParams);
        }
    }
}
