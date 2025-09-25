package com.example.carengineoil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "oils")
public class Oil {

    @PrimaryKey(autoGenerate = true)
    private int id;

    public String name;       // Название масла
    public boolean isGood;    // Годно или нет
    public String parameters; // Параметры масла (можно хранить как строку)

    public Oil(String name, boolean isGood, String parameters) {
        this.name = name;
        this.isGood = isGood;
        this.parameters = parameters;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isGood() { return isGood; }
    public void setGood(boolean good) { isGood = good; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
}
