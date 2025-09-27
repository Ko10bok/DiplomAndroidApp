package com.example.carengineoil;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "oils")
public class Oil {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public String name;              // Название + (Годно)/(Не годно)
    public boolean isGood;           // Годно или нет
    public String parameters;        // Все параметры масла (строка)
    public String failedParameters;  // Параметры, не соответствующие норме (строка)
    public String sourceActivity;    // Источник сохранения: "MainActivity" или "MainActivity2"

    public Oil(String name, boolean isGood, String parameters, String failedParameters, String sourceActivity) {
        this.name = name;
        this.isGood = isGood;
        this.parameters = parameters;
        this.failedParameters = failedParameters;
        this.sourceActivity = sourceActivity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isGood() { return isGood; }
    public void setGood(boolean good) { isGood = good; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public String getFailedParameters() { return failedParameters; }
    public void setFailedParameters(String failedParameters) { this.failedParameters = failedParameters; }

    public String getSourceActivity() { return sourceActivity; }
    public void setSourceActivity(String sourceActivity) { this.sourceActivity = sourceActivity; }
}