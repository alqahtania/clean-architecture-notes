package com.abdull.cleannotes.framework.datasource.cache.mappers

import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.util.DateUtil
import com.abdull.cleannotes.business.domain.util.EntityMapper
import com.abdull.cleannotes.framework.datasource.cache.model.NoteCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/17/2020.
 */

@Singleton
class CacheMapper
@Inject
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<NoteCacheEntity, Note>{

    fun entityListToNoteList(entities : List<NoteCacheEntity>) : List<Note>{
        val noteList = ArrayList<Note>()
        for(entity in entities){
            noteList.add(mapFromEntity(entity))
        }

        return noteList
    }
    fun noteListToEntityList(notes : List<Note>) : List<NoteCacheEntity>{
        val entityList = ArrayList<NoteCacheEntity>()
        for(note in notes){
            entityList.add(mapToEntity(note))
        }

        return entityList
    }


    override fun mapFromEntity(entity: NoteCacheEntity): Note {
        return Note(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            created_at = entity.created_at,
            updated_at = entity.updated_at
        )
    }

    override fun mapToEntity(domainModel: Note): NoteCacheEntity {

        return NoteCacheEntity(
            id = domainModel.id,
            title = domainModel.title,
            body = domainModel.body,
            created_at = domainModel.created_at,
            updated_at = domainModel.updated_at
        )
    }
}