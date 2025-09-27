package com.example.carengineoil;
import androidx.room.Dao;


import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OilDao {

    @Insert
    void insert(Oil oil);

    @Update
    void update(Oil oil);

    @Delete
    void delete(Oil oil);

    @Query("SELECT * FROM oils")
    List<Oil> getAllOils();

    @Query("DELETE FROM oils")
    void deleteAll();
    @Query("SELECT * FROM oils WHERE name = :name LIMIT 1")
    Oil getOilByName(String name);

}