package com.example.carengineoil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.content.Intent;


import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private OilDB db;
    private RecyclerView recyclerView;
    private OilAdapter oilAdapter;

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

        loadOils();

        Button btnBack = findViewById(R.id.button2);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOils() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Oil> oils = db.oilDao().getAllOils();
            runOnUiThread(() -> {
                if (oilAdapter == null) {
                    oilAdapter = new OilAdapter(oils);
                    recyclerView.setAdapter(oilAdapter);
                    // Устанавливаем слушатель кликов на элементы списка
                    oilAdapter.setOnOilClickListener(oil -> {
                        Intent intent;
                        if ("MainActivity".equals(oil.getSourceActivity())) {
                            intent = new Intent(MainActivity3.this, MainActivity.class);
                        } else if ("MainActivity2".equals(oil.getSourceActivity())) {
                            intent = new Intent(MainActivity3.this, MainActivity2.class);
                        } else {
                            // По умолчанию можно открывать MainActivity
                            intent = new Intent(MainActivity3.this, MainActivity.class);
                        }
                        // Передаем параметры масла в активность
                        intent.putExtra("oilName", oil.getName());
                        intent.putExtra("parameters", oil.getParameters());
                        startActivity(intent);
                    });
                } else {
                    oilAdapter.updateData(oils);
                }
            });
        });
    }
}