package com.urielhuerta.criterio.data.local

import androidx.room.TypeConverter
import com.urielhuerta.criterio.domain.model.EvidenceLevel
import com.urielhuerta.criterio.domain.model.ModuleCategory

class Converters {
    @TypeConverter
    fun fromEvidenceLevel(level: EvidenceLevel?): String? = level?.name

    @TypeConverter
    fun toEvidenceLevel(value: String?): EvidenceLevel? =
        value?.let { enumValueOf<EvidenceLevel>(it) } ?: EvidenceLevel.MODERATE_EVIDENCE

    @TypeConverter
    fun fromModuleCategory(category: ModuleCategory?): String? = category?.name

    @TypeConverter
    fun toModuleCategory(value: String?): ModuleCategory? =
        value?.let { enumValueOf<ModuleCategory>(it) } ?: ModuleCategory.FUNDAMENTOS
}
