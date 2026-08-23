package com.urielhuerta.criterio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.urielhuerta.criterio.data.local.dao.*
import com.urielhuerta.criterio.data.local.entities.*

@Database(
    entities = [
        ModuleEntity::class,
        LessonEntity::class,
        SpacedRepetitionCardEntity::class,
        JournalEntity::class,
        PredictionEntity::class,
        SimulationRecordEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CriterioDatabase : RoomDatabase() {
    abstract fun moduleDao(): ModuleDao
    abstract fun lessonDao(): LessonDao
    abstract fun spacedRepetitionDao(): SpacedRepetitionDao
    abstract fun journalDao(): JournalDao
    abstract fun predictionDao(): PredictionDao
    abstract fun simulationDao(): SimulationDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: CriterioDatabase? = null

        fun getInstance(context: Context): CriterioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CriterioDatabase::class.java,
                    "criterio_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
