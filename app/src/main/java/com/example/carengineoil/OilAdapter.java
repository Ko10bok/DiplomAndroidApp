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

import java.util.List;

public class OilAdapter extends RecyclerView.Adapter<OilAdapter.OilViewHolder> {

    private List<Oil> oilList;
    private OnOilClickListener listener;

    public interface OnOilClickListener {
        void onOilClick(Oil oil);
    }

    public OilAdapter(List<Oil> oils) {
        this.oilList = oils;
    }

    public void updateData(List<Oil> newOilList) {
        this.oilList = newOilList;
        notifyDataSetChanged();
    }

    public void setOnOilClickListener(OnOilClickListener listener) {
        this.listener = listener;
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

                // Проверяем, начинается ли параметр с одного из failedParams
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
                    // Выделяем красным цветом только проблемный параметр
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (i != paramPairs.length - 1) {
                    spannable.append(", ");
                }
            }

            holder.textParams.setText(spannable);
        }

        // Обработка клика по элементу списка
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOilClick(oil);
            }
        });
    }

    @Override
    public int getItemCount() {
        return oilList.size();
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