package com.aqube.ram.data.repository

import com.aqube.ram.data.models.CharacterEntity
import kotlinx.coroutines.flow.Flow

interface CharacterCache {
    suspend fun getCharacters(): Flow<List<CharacterEntity>>
    suspend fun getCharacter(characterId: Int): Flow<CharacterEntity>
    suspend fun saveCharacters(listCharacters: List<CharacterEntity>)
    fun getBookMarkedCharacters(): Flow<List<CharacterEntity>>
    fun setCharacterBookmarked(characterId: Int)
    fun setCharacterUnBookMarked(characterId: Int)
    fun isCached(): Flow<Boolean>
    fun setLastCacheTime(lastCache: Long)
    fun isExpired(): Boolean
}
