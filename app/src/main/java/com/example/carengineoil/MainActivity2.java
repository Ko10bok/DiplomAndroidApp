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

    // Поля для ввода характеристик автомобиля
    private EditText carBrandEditText, carModelEditText, carYearEditText, engineTypeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Инициализация полей — убедитесь, что в layout activity_main2.xml есть такие id
        carBrandEditText = findViewById(R.id.editTextText6);
        carModelEditText = findViewById(R.id.editTextText7);
        carYearEditText = findViewById(R.id.editTextText8);
        engineTypeEditText = findViewById(R.id.editTextText9);

        Button buttonSelectOil = findViewById(R.id.button7); // кнопка запуска подбора масла
        Button btnClear = findViewById(R.id.button4);        // кнопка очистки полей
        Button buttonGoToInfo = findViewById(R.id.button5);  // переход к информационной странице

        // Переход на информационный экран
        buttonGoToInfo.setOnClickListener(view ->
                startActivity(new Intent(MainActivity2.this, InfoActivity4.class)));

        // Обработка клика по кнопке подбора масла
        buttonSelectOil.setOnClickListener(v -> {
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

            performOilSelection(brand, model, year, engineType);
        });

        // Очистка полей
        btnClear.setOnClickListener(v -> {
            carBrandEditText.setText("");
            carModelEditText.setText("");
            carYearEditText.setText("");
            engineTypeEditText.setText("");
            Toast.makeText(this, "Поля очищены", Toast.LENGTH_SHORT).show();
        });
    }

    // Метод подбора масла — здесь реализовать вашу логику
    private void performOilSelection(String brand, String model, int year, String engineType) {
        String message = "Подбор масла для " + brand + " " + model + ", " + year + " г.в., двигатель: " + engineType;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Пример: можно запустить новую активность с результатами подбора
        // Intent intent = new Intent(this, OilResultsActivity.class);
        // intent.putExtra("brand", brand);
        // intent.putExtra("model", model);
        // intent.putExtra("year", year);
        // intent.putExtra("engineType", engineType);
        // startActivity(intent);
    }

    // Всплывающее окно с сообщением об ошибке
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ОК", null)
                .setCancelable(false)
                .show();
    }
}
