package com.example.carengineoil;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

public class MainActivity2 extends AppCompatActivity {

    private EditText[] params = new EditText[7];
    private double[][] ranges = {
            {0.5, 3.0},      // Кислотное число
            {12.5, 16.3},    // Вязкость кинематическая
            {2.0, 5.0},      // Содержание сажи
            {856.0, 875.0},  // Плотность
            {20.0, 100.0},   // Содержание железа
            {0.14, 1.0},     // Содержание нерастворимых примесей
            {0.01, 0.08}     // Содержание воды
    };

    private String[] paramNames = {
            "Кислотное число",
            "Вязкость кинематическая",
            "Содержание сажи",
            "Плотность",
            "Содержание железа",
            "Содержание нерастворимых примесей",
            "Содержание воды"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        params[0] = findViewById(R.id.editTextText3);
        params[1] = findViewById(R.id.editTextText24);
        params[3] = findViewById(R.id.editTextText25);
        params[5] = findViewById(R.id.editTextText27);

        Button buttonCheck = findViewById(R.id.button7);
        Button buttonBack = findViewById(R.id.button);
        Button btnClear = findViewById(R.id.button4);

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder errors = new StringBuilder();
                boolean hasValues = false;

                for (int i = 0; i < params.length; i++) {
                    String input = params[i].getText().toString().trim();
                    if (input.isEmpty()) {
                        // Пустые поля игнорируем
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
                    showAlertDialog("Внимание", "Пожалуйста, заполните хотя бы одно поле.");
                    return;
                }

                if (errors.length() > 0) {
                    showAlertDialog("Ошибки в параметрах", errors.toString());
                } else {
                    showSaveDialog("Проверка пройдена", "Масло удовлетворяет требованиям");
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход обратно в MainActivity
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText param : params) {
                    param.setText("");
                }
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
                    // Здесь можно добавить логику сохранения
                    Toast.makeText(MainActivity2.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("ОК", null)
                .setCancelable(false)
                .show();
    }
}