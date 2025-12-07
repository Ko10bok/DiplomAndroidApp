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

    private Button btnBack, btnOpenMode, btnDeleteMode;
    private boolean isOpenMode = false;
    private boolean isDeleteMode = false;

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
        btnOpenMode = findViewById(R.id.button8);   // "Открыть"
        btnDeleteMode = findViewById(R.id.button9); // "Удалить"

        btnBack.setOnClickListener(v -> finish());

        // При нажатии на кнопку "Отмена" просто вызывайте exitSelectionMode(), а не finish().
        btnDeleteMode.setOnClickListener(v -> {
            if (isDeleteMode) {
                exitSelectionMode();
            } else if (!isOpenMode && !isDeleteMode) {
                enterDeleteMode();
            } else if (isOpenMode) {
                exitSelectionMode();
            }
        });
        btnOpenMode.setOnClickListener(v -> {
            if (!isOpenMode && !isDeleteMode) {
                enterOpenMode();
            } else if (isOpenMode) {
                openSelectedOil();
            } else if (isDeleteMode) {
                exitSelectionMode();
            }
        });



        loadOils();
    }

    private void enterOpenMode() {
        isOpenMode = true;
        if (oilAdapter != null) {
            oilAdapter.setSelectionMode(true);
            oilAdapter.notifyDataSetChanged();
        }
        btnOpenMode.setText("Применить");
        btnDeleteMode.setText("Отмена");
    }

    private void enterDeleteMode() {
        isDeleteMode = true;
        if (oilAdapter != null) {
            oilAdapter.setSelectionMode(true);
            oilAdapter.notifyDataSetChanged();
        }
        btnOpenMode.setText("Отмена");
        btnDeleteMode.setText("Удалить");
    }

    private void openSelectedOil() {
        if (oilAdapter == null) {
            finish();  // ← ДОБАВИТЬ
            return;
        }

        List<Oil> selectedOils = oilAdapter.getSelectedOils();
        if (!selectedOils.isEmpty()) {
            Oil oil = selectedOils.get(0);
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
        }
        finish();  // ← ДОБАВИТЬ: возврат на MainActivity3 после открытия
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
                    oilAdapter.updateData(oils);       // Обновляем данные
                    oilAdapter.clearSelection();       // Очищаем выделения
                    oilAdapter.notifyDataSetChanged(); // Обновляем RecyclerView
                });
            });
        }
        exitSelectionMode();
    }



    private void exitSelectionMode() {
        isOpenMode = false;
        isDeleteMode = false;
        if (oilAdapter != null) {
            oilAdapter.setSelectionMode(false);
            oilAdapter.clearSelection();
            oilAdapter.notifyDataSetChanged();
        }
        // Возврат к исходным кнопкам
        btnOpenMode.setText("Открыть");
        btnDeleteMode.setText("Удалить");
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
                        if (isOpenMode || isDeleteMode) {
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
