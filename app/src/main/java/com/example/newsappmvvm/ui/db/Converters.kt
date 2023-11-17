package com.example.newsappmvvm.ui.db

import androidx.room.TypeConverter
import com.example.newsappmvvm.ui.models.Source  // Update this import to point to your Source class

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name?:"N/A"
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}
