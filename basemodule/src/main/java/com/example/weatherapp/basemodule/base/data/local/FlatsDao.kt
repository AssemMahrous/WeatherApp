package com.example.weatherapp.basemodule.base.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.basemodule.base.data.model.FlatDbEntity

@Dao
interface FlatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<FlatDbEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(flatDbEntity: FlatDbEntity)

    @Query(
        """SELECT * FROM flats 
        WHERE (:numberOfBeds IS NULL OR bedrooms LIKE :numberOfBeds) 
        AND (:startDate NOT BETWEEN startDate AND endDate) 
        AND ( :endDate NOT BETWEEN startDate AND endDate)
        ORDER BY distance ASC"""
    )
    fun flatsByQuery(
        numberOfBeds: Int? = null,
        startDate: Long? = null,
        endDate: Long? = null
    ): PagingSource<Int, FlatDbEntity>

    @Query("UPDATE flats SET startDate = :startDate,endDate=:endDate  WHERE id= :id")
    fun updateFlat(
        startDate: Long,
        endDate: Long,
        id: String
    )

    @Query("SELECT * from flats WHERE id= :id")
    fun getItemById(id: String): List<FlatDbEntity>

    @Query("DELETE FROM flats")
    suspend fun clearFlats()
}
