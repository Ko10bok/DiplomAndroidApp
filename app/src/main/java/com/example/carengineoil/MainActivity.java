package com.example.carengineoil;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;



import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText[] params = new EditText[7];
    private double[][] ranges = {
            {4.0, 12.0},     // Щелочное число
            {12.5, 16.3},    // Вязкость кинематическая
            {10.0, 15.0},    // Испаряемость
            {190.0, 250.0},  // Температура вспышки
            {0.5, 2.0},      // Зольность сульфатная
            {200.0, 800.0},  // Содержание кальция
            {800.0, 1500.0}  // Содержание цинка
    };

    private String[] paramNames = {
            "Щелочное число",
            "Вязкость кинематическая",
            "Испаряемость",
            "Температура вспышки",
            "Зольность сульфатная",
            "Содержание кальция",
            "Содержание цинка"
    };

    // Для примера — список сохранённых масел
    private ArrayList<String> savedOils = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        params[0] = findViewById(R.id.editTextText3);
        params[1] = findViewById(R.id.editTextText24);
        params[2] = findViewById(R.id.editTextText23);
        params[3] = findViewById(R.id.editTextText25);



        Button btnCheck = findViewById(R.id.button7);
        Button btnClear = findViewById(R.id.button4);
        Button btnGoToSecond = findViewById(R.id.button3);

        btnGoToSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });


        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder errors = new StringBuilder();
                boolean hasValues = false; // Проверяем, есть ли хоть одно заполненное поле

                for (int i = 0; i < params.length; i++) {
                    String input = params[i].getText().toString().trim();
                    if (input.isEmpty()) {
                        // Игнорируем пустые поля
                        continue;
                    }
                    hasValues = true;
                    double value;
                    try {
                        value = Double.parseDouble(input);
                    } catch (NumberFormatException e) {
                        errors.append("Некорректное число в ").append(paramNames[i]).append("\n");
                        continue;
                    }

                    if (value < ranges[i][0] || value > ranges[i][1]) {
                        errors.append("Параметр ").append(paramNames[i])
                                .append(" вне диапазона (").append(ranges[i][0])
                                .append(" - ").append(ranges[i][1]).append(")\n");
                    }
                }

                if (!hasValues) {
                    // Если все поля пустые — просто ничего не показываем или можно подсказать заполнить
                    showAlertDialog("Внимание", "Пожалуйста, заполните хотя бы одно поле.");
                    return;
                }

                if (errors.length() > 0) {
                    // Есть ошибки — показываем их все в одном диалоговом окне
                    showAlertDialog("Ошибки в параметрах", errors.toString());
                } else {
                    // Все заполненные поля в норме — показываем диалог с сохранением
                    showSaveDialog("Проверка пройдена", "Масло удовлетворяет требованиям");
                }
            }
        });


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Только открываем третью активность, без сброса данных
                Intent intent = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(intent);
            }
        });
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ОК", null)
                .setCancelable(false)
                .show();
    }

    private void showSaveDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    saveOilData();
                    Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("ОК", null)
                .setCancelable(false)
                .show();
    }

    private void saveOilData() {
        // Для примера сохраняем строку с параметрами масла
        StringBuilder sb = new StringBuilder();
        sb.append("Масло: ");
        for (int i = 0; i < params.length; i++) {
            sb.append(paramNames[i]).append("=").append(params[i].getText().toString().trim());
            if (i != params.length - 1) sb.append(", ");
        }
        savedOils.add(sb.toString());
        // В реальном приложении здесь лучше сохранять в базу данных или файл
    }
}
