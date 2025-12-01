package com.example.carengineoil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity3 extends AppCompatActivity {

    private OilDB db;
    private RecyclerView recyclerView;
    private OilAdapter oilAdapter;

    private Button btnBack, btnSelectMode, btnDelete;
    private boolean isSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main3);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = OilDB.getInstance(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.button2);
        btnSelectMode = findViewById(R.id.button8);
        btnDelete = findViewById(R.id.button9);

        btnBack.setOnClickListener(v -> finish());

        btnSelectMode.setOnClickListener(v -> enterSelectionMode());

        btnDelete.setOnClickListener(v -> {
            if (oilAdapter == null) return;
            List<Oil> selectedOils = oilAdapter.getSelectedOils();
            if (!selectedOils.isEmpty()) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    for (Oil oil : selectedOils) {
                        db.oilDao().delete(oil);
                    }
                    List<Oil> oils = db.oilDao().getAllOils();
                    runOnUiThread(() -> {
                        oilAdapter.updateData(oils);
                        // Очищаем выделение после удаления
                        oilAdapter.clearSelection();
                    });
                });
            }
        });

        loadOils();
    }

    private void enterSelectionMode() {
        isSelectionMode = true;
        if (oilAdapter != null) {
            oilAdapter.setSelectionMode(true);
        }
        // Меняем текст кнопки на "Применить"
        btnSelectMode.setText("Применить");
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void exitSelectionMode() {
        isSelectionMode = false;
        if (oilAdapter != null) {
            oilAdapter.setSelectionMode(false);
            oilAdapter.clearSelection(); // Очищаем выделение
        }
        // Возвращаем исходный текст кнопки
        btnSelectMode.setText("Открыть"); // или "Выбрать" - какой был изначально
        btnDelete.setVisibility(View.GONE);
    }

    private void loadOils() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Oil> oils = db.oilDao().getAllOils();
            runOnUiThread(() -> {
                if (oilAdapter == null) {
                    oilAdapter = new OilAdapter(oils);
                    recyclerView.setAdapter(oilAdapter);

                    oilAdapter.setOnOilClickListener(oil -> {
                        if (!isSelectionMode) {
                            Intent intent;
                            if ("MainActivity".equals(oil.getSourceActivity())) {
                                intent = new Intent(MainActivity3.this, MainActivity.class);
                            } else if ("MainActivity2".equals(oil.getSourceActivity())) {
                                intent = new Intent(MainActivity3.this, MainActivity2.class);
                            } else {
                                intent = new Intent(MainActivity3.this, MainActivity.class);
                            }
                            intent.putExtra("oilName", oil.getName());
                            intent.putExtra("parameters", oil.getParameters());
                            startActivity(intent);
                        } else {
                            int pos = oilAdapter.getOilList().indexOf(oil);
                            if (pos != -1) {
                                oilAdapter.toggleSelection(pos);
                            }
                        }
                    });

                } else {
                    oilAdapter.updateData(oils);
                }
            });
        });
    }
}
