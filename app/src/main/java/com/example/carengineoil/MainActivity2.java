package com.example.carengineoil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
public class MainActivity2 extends AppCompatActivity {

    private EditText[] params = new EditText[4];
    private double[][] ranges = {
            {0.5, 3.0},      // Кислотное число
            {1.0, 6.0},      // Дисперсионно стабилизирующие свойства (баллы)
            {12.5, 16.3},    // Плотность
            {0.14, 1.0}      // Содержание нерастворимых примесей
    };

    private String[] paramNames = {
            "Кислотное число",
            "Дисперсионно стабилизирующие свойства",
            "Плотность",
            "Содержание нерастворимых примесей"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Инициал  // Содержание нерастворимых примесей

        // --- Добавляем код для подстановки параметров из Intent ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("parameters")) {
            String parameters = intent.getStringExtra("parameters");
            if (parameters != null && !parameters.isEmpty()) {
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

        Button buttonCheck = findViewById(R.id.button7);
        Button btnClear = findViewById(R.id.button4);
        Button buttonGoTo4 = findViewById(R.id.button5);

        buttonGoTo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity2.this, InfoActivity4.class);
                startActivity(intent);
            }
        });


        buttonCheck.setOnClickListener(v -> {
            StringBuilder errors = new StringBuilder();
            boolean hasValues = false;

            for (int i = 0; i < params.length; i++) {
                String input = params[i].getText().toString().trim();
                if (input.isEmpty()) {
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
                    "Масло не удовлетворяет требованиям по параметрам:\n" + failedParams + "\nСохранить с пометкой 'Не годно'?";

            showSaveDialog("Проверка параметров", message, isGood, failedParams);
        });



        btnClear.setOnClickListener(v -> {
            Intent intentClear = new Intent(MainActivity2.this, MainActivity3.class);
            startActivity(intentClear);
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
                        Toast.makeText(MainActivity2.this, "Название не может быть пустым", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveOilData(oilName, isGood, failedParams, "MainActivity2");
                    Toast.makeText(MainActivity2.this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .setCancelable(false)
                .show();
    }

    private void saveOilData(String oilName, boolean isGood, String failedParams, String sourceActivity) {
        String fullName = oilName + (isGood ? "(Годно)" : "(Не годно)");

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