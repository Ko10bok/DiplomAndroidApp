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

import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private OilDB db;
    private RecyclerView recyclerView;
    private OilAdapter oilAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Включаем Edge-to-Edge режим (убираем отступы системы)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main3);

        // Обрабатываем отступы для view с id main
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация базы данных
        db = OilDB.getInstance(this);

        // Настройка RecyclerView для отображения списка масел
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Получаем данные из базы
        List<Oil> oils = db.oilDao().getAllOils();

        // Создаём и устанавливаем адаптер
        oilAdapter = new OilAdapter(oils);
        recyclerView.setAdapter(oilAdapter);

        // Кнопка назад
        Button btnBack = findViewById(R.id.button2);
        btnBack.setOnClickListener(v -> finish());
    }
}
