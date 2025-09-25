package com.example.carengineoil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OilAdapter extends RecyclerView.Adapter<OilAdapter.OilViewHolder> {

    private List<Oil> oilList;

    public OilAdapter(List<Oil> oils) {
        this.oilList = oils;
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
        holder.textParams.setText(oil.getParameters());
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
