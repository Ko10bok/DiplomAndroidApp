package com.example.carengineoil;

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

        btnSelectMode.setOnClickListener(v -> {
            if (!isSelectionMode) {
                // Вход в режим выделения
                enterSelectionMode();
            } else {
                // Удаление выбранных элементов по кнопке "Применить"
                deleteSelectedOils();
            }
        });

        btnDelete.setOnClickListener(v -> {
            // Кнопка отмены - выход из режима выделения
            exitSelectionMode();
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
        btnDelete.setText("Отмена");
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void deleteSelectedOils() {
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
                    // НЕ вызываем exitSelectionMode(), остаёмся в режиме выделения
                    oilAdapter.clearSelection(); // очищаем выделение после удаления
                    // Кнопки остаются с текстом "Применить" и "Отмена"
                    btnSelectMode.setText("Применить");
                    btnDelete.setText("Отмена");
                    btnDelete.setVisibility(View.VISIBLE);
                });
            });
        } else {
            exitSelectionMode();
        }
    }


    private void exitSelectionMode() {
        isSelectionMode = false;
        if (oilAdapter != null) {
            oilAdapter.setSelectionMode(false);
            oilAdapter.clearSelection();
        }
        btnSelectMode.setText("Открыть");  // Возвращаем "Открыть"
        btnDelete.setText("Удалить");       // Возвращаем "Удалить"
        btnDelete.setVisibility(View.VISIBLE); // Делаем кнопку "Удалить" видимой
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

