package com.urielhuerta.criterio.data.local.dao

import androidx.room.*
import com.urielhuerta.criterio.data.local.entities.*
import kotlinx.coroutines.flow.Flow

data class ModuleWithLessons(
    @Embedded val module: ModuleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "moduleId"
    )
    val lessons: List<LessonEntity>
)

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules ORDER BY levelIndex ASC")
    fun getAllModules(): Flow<List<ModuleEntity>>

    @Transaction
    @Query("SELECT * FROM modules ORDER BY levelIndex ASC")
    fun getModulesWithLessons(): Flow<List<ModuleWithLessons>>

    @Transaction
    @Query("SELECT * FROM modules WHERE id = :moduleId")
    suspend fun getModuleWithLessonsById(moduleId: String): ModuleWithLessons?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    @Update
    suspend fun updateModule(module: ModuleEntity)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE moduleId = :moduleId ORDER BY orderIndex ASC")
    fun getLessonsByModule(moduleId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :completed WHERE id = :lessonId")
    suspend fun setLessonCompleted(lessonId: String, completed: Boolean)

    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    fun getCompletedLessonsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM lessons")
    fun getTotalLessonsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getLessonsCount(): Int
}

@Dao
interface SpacedRepetitionDao {
    @Query("SELECT * FROM spaced_repetition_cards WHERE nextReviewTimestamp <= :now ORDER BY nextReviewTimestamp ASC")
    fun getCardsDueForReview(now: Long = System.currentTimeMillis()): Flow<List<SpacedRepetitionCardEntity>>

    @Query("SELECT COUNT(*) FROM spaced_repetition_cards WHERE nextReviewTimestamp <= :now")
    fun getCardsDueCount(now: Long = System.currentTimeMillis()): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCards(cards: List<SpacedRepetitionCardEntity>)

    @Update
    suspend fun updateCard(card: SpacedRepetitionCardEntity)

    @Query("SELECT * FROM spaced_repetition_cards")
    fun getAllCards(): Flow<List<SpacedRepetitionCardEntity>>
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntity): Long

    @Delete
    suspend fun deleteEntry(entry: JournalEntity)

    @Query("SELECT COUNT(*) FROM journal_entries")
    fun getJournalCount(): Flow<Int>
}

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions ORDER BY timestamp DESC")
    fun getAllPredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE actualOutcome IS NOT NULL ORDER BY resolvedTimestamp DESC")
    fun getResolvedPredictions(): Flow<List<PredictionEntity>>

    @Query("SELECT * FROM predictions WHERE actualOutcome IS NULL ORDER BY timestamp DESC")
    fun getPendingPredictions(): Flow<List<PredictionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionEntity): Long

    @Update
    suspend fun updatePrediction(prediction: PredictionEntity)

    @Query("SELECT * FROM predictions WHERE id = :id")
    suspend fun getPredictionById(id: Long): PredictionEntity?
}

@Dao
interface SimulationDao {
    @Query("SELECT * FROM simulation_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<SimulationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: SimulationRecordEntity): Long

    @Query("SELECT AVG(totalScore) FROM simulation_records")
    fun getAverageScore(): Flow<Double?>
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAtTimestamp = :timestamp WHERE id = :id AND isUnlocked = 0")
    suspend fun unlockAchievement(id: String, timestamp: Long = System.currentTimeMillis())
}
