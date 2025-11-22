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

    // Новые поля для ввода характеристик автомобиля
    private EditText carBrandEditText, carModelEditText, carYearEditText, engineTypeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Инициализация EditText для автомобиля — проверьте, что в layout есть соответствующие id
        carBrandEditText = findViewById(R.id.editTextCarBrand);
        carModelEditText = findViewById(R.id.editTextCarModel);
        carYearEditText = findViewById(R.id.editTextCarYear);
        engineTypeEditText = findViewById(R.id.editTextEngineType);

        Button buttonSelectOil = findViewById(R.id.button7); // Кнопка для запуска подбора масла
        Button buttonBack = findViewById(R.id.button);       // Кнопка "Назад"
        Button btnClear = findViewById(R.id.button4);         // Кнопка очистки
        Button buttonGoToInfo = findViewById(R.id.button5);  // Кнопка перехода к информационной странице

        // Переход к инфо активности
        buttonGoToInfo.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity2.this, InfoActivity4.class));
        });

        // Обработка кнопки подбора масла
        buttonSelectOil.setOnClickListener(v -> {
            // Получаем данные пользователя
            String brand = carBrandEditText.getText().toString().trim();
            String model = carModelEditText.getText().toString().trim();
            String yearStr = carYearEditText.getText().toString().trim();
            String engineType = engineTypeEditText.getText().toString().trim();

            if (brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() || engineType.isEmpty()) {
                showAlertDialog("Ошибка", "Пожалуйста, заполните все поля.");
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearStr);
                if (year < 1900 || year > 2100) {
                    showAlertDialog("Ошибка", "Введите корректный год выпуска.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlertDialog("Ошибка", "Год выпуска должен быть числом.");
                return;
            }

            // Здесь добавьте вызов функции подбора масла по введённым данным,
            // например, передайте данные в базу или покажите рекомендации.
            performOilSelection(brand, model, year, engineType);
        });

        buttonBack.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this, MainActivity.class));
            finish();
        });

        btnClear.setOnClickListener(v -> {
            carBrandEditText.setText("");
            carModelEditText.setText("");
            carYearEditText.setText("");
            engineTypeEditText.setText("");
            Toast.makeText(this, "Поля очищены", Toast.LENGTH_SHORT).show();
        });
    }

    private void performOilSelection(String brand, String model, int year, String engineType) {
        // Заглушка: Здесь реализуйте логику подбора масла по параметрам
        // Можно вызвать другую активити с результатами, например:
        // Intent intent = new Intent(this, OilResultsActivity.class);
        // intent.putExtra("brand", brand);
        // intent.putExtra("model", model);
        // intent.putExtra("year", year);
        // intent.putExtra("engineType", engineType);
        // startActivity(intent);

        // Пока что просто выводим сообщение
        String message = "Подбор масла для " + brand + " " + model + ", " + year + " г.в., двигатель: " + engineType;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ОК", null)
                .setCancelable(false)
                .show();
    }
}
