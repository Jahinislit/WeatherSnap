package com.weathersnap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedCityDao {

    @Query("SELECT * FROM cached_cities WHERE `query` = :query AND timestamp > :minTimestamp LIMIT 1")
    suspend fun getCachedResult(query: String, minTimestamp: Long): CachedCityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(entity: CachedCityEntity)

    @Query("DELETE FROM cached_cities WHERE timestamp < :olderThan")
    suspend fun clearOldCache(olderThan: Long)
}
