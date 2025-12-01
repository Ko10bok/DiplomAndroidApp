package com.example.carengineoil;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText[] params = new EditText[9]; // 4 параметра из MainActivity + 4 из MainActivity2

    private double[][] ranges = {
            // Параметры из MainActivity (текущие)
            {4.0, 12.0},     // Щелочное число
            {12.5, 16.3},    // Вязкость кинематическая
            {10.0, 15.0},    // Испаряемость
            {190.0, 250.0},  // Температура вспышки
            // Параметры из MainActivity2
            {0.5, 3.0},      // Кислотное число
            {1.0, 6.0},      // Дисперсионно стабилизирующие свойства
            {12.5, 16.3},    // Плотность
            {0.14, 1.0},      // Содержание нерастворимых примесей
            {0, 200}
    };

    private String[] paramNames = {
            "Щелочное число",
            "Вязкость кинематическая",
            "Испаряемость",
            "Температура вспышки",
            "Кислотное число",
            "Дисперсионно стабилизирующие свойства",
            "Плотность",
            "Содержание нерастворимых примесей",
            "Температура двигателя"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация EditText — обязательно обновите id в разметке activity_main, чтобы добавить новые поля
        params[0] = findViewById(R.id.editTextText3);   // Щелочное число
        params[1] = findViewById(R.id.editTextText23);  // Вязкость кинематическая
        params[2] = findViewById(R.id.editTextText24);  // Испаряемость
        params[3] = findViewById(R.id.editTextText25);  // Температура вспышки

        params[4] = findViewById(R.id.editTextText);  // Кислотное число (новое, создайте в layout, например editTextTextAcidValue)
        params[5] = findViewById(R.id.editTextText2);   // Дисперсионно стабилизирующие свойства (новое)
        params[6] = findViewById(R.id.editTextText4);    // Плотность (новое)
        params[7] = findViewById(R.id.editTextText5); // Содержание нерастворимых примесей (новое)
        params[8] = findViewById(R.id.editText10);
        // Подстановка параметров из Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("parameters")) {
            String parameters = intent.getStringExtra("parameters");
            if (parameters != null) {
                String[] pairs = parameters.split(",\\s*");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();
                        for (int i = 0; i < paramNames.length; i++) {
                            if (paramNames[i].equals(key)) {
                                params[i].setText(value);
                                break;
                            }
                        }
                    }
                }
            }
        }


        Button btnCheck = findViewById(R.id.button7);
        Button btnClear = findViewById(R.id.button4);
        Button btnGoToSecond = findViewById(R.id.button3);
        Button buttonGoTo4 = findViewById(R.id.button5);

        // Обработчик проверки с учётом всех параметров
        btnCheck.setOnClickListener(v -> {
            StringBuilder errors = new StringBuilder();
            boolean hasValues = false;

            for (int i = 0; i < params.length; i++) {
                String input = params[i].getText().toString().trim();
                if (input.isEmpty()) continue;
                hasValues = true;
                double value;
                try {
                    value = Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    errors.append("Некорректное число в ").append(paramNames[i]).append("\n");
                    continue;
                }
                if (value < ranges[i][0] || value > ranges[i][1]) {
                    errors.append(paramNames[i]).append(", ");
                }
            }

            if (!hasValues) {
                showAlertDialog("Внимание", "Пожалуйста, заполните хотя бы одно поле.");
                return;
            }

            boolean isGood = errors.length() == 0;
            String failedParams = isGood ? "" : errors.toString();

            String message = isGood ? "Масло удовлетворяет требованиям" :
                    "Масло не удовлетворяет требованиям по параметрам:\n" + failedParams + "\nСохранить с пометкой 'Непригодно'?";

            showSaveDialog("Проверка параметров", message, isGood, failedParams);
        });

        // Переход во вторую активити (MainActivity2)
        btnGoToSecond.setOnClickListener(v -> {
            Intent intentToSecond = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intentToSecond);
        });

        // Переход в четвёртую активити (InfoActivity4)
        buttonGoTo4.setOnClickListener(v -> {
            Intent intentToInfo = new Intent(MainActivity.this, InfoActivity4.class);
            startActivity(intentToInfo);
        });

        btnClear.setOnClickListener(v -> {
            Intent intentToList = new Intent(MainActivity.this, MainActivity3.class);
            startActivity(intentToList);
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

    private void showSaveDialog(String title, String message, boolean isGood, String failedParams) {
        final EditText input = new EditText(this);
        input.setHint("Введите название масла");

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setView(input)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String oilName = input.getText().toString().trim();
                    if (oilName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Название не может быть пустым", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveOilData(oilName, isGood, failedParams, "MainActivity");
                    Toast.makeText(MainActivity.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .setCancelable(false)
                .show();
    }

    private void saveOilData(String oilName, boolean isGood, String failedParams, String sourceActivity) {
        String fullName = oilName + (isGood ? "(Пригодно)" : "(Непригодно)");

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            OilDB db = OilDB.getInstance(getApplicationContext());
            Oil existingOil = db.oilDao().getOilByName(fullName);

            if (existingOil != null) {
                runOnUiThread(() -> showOverwriteDialog(fullName, isGood, failedParams, sourceActivity));
            } else {
                insertOil(fullName, isGood, failedParams, sourceActivity);
            }
        });
    }

    private void insertOil(String fullName, boolean isGood, String failedParams, String sourceActivity) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            sb.append(paramNames[i]).append("=").append(params[i].getText().toString().trim());
            if (i != params.length - 1) sb.append(", ");
        }
        String parameters = sb.toString();

        Oil oil = new Oil(fullName, isGood, parameters, failedParams, sourceActivity);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            OilDB.getInstance(getApplicationContext()).oilDao().insert(oil);
        });
    }

    private void showOverwriteDialog(String oilName, boolean isGood, String failedParams, String sourceActivity) {
        new AlertDialog.Builder(this)
                .setTitle("Имя занято")
                .setMessage("Масло с таким названием уже существует. Перезаписать?")
                .setPositiveButton("Перезаписать", (dialog, which) -> {
                    // Удаляем старое масло и сохраняем новое
                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        OilDB db = OilDB.getInstance(getApplicationContext());
                        Oil oldOil = db.oilDao().getOilByName(oilName);
                        if (oldOil != null) {
                            db.oilDao().delete(oldOil);
                        }
                        insertOil(oilName, isGood, failedParams, sourceActivity);
                    });
                    Toast.makeText(this, "Данные перезаписаны", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    Toast.makeText(this, "Сохранение отменено", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
    }


}