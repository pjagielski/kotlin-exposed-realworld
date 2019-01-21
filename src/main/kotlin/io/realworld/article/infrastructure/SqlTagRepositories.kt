package io.realworld.article.infrastructure

import io.realworld.article.domain.Tag
import io.realworld.article.domain.TagReadRepository
import io.realworld.article.domain.TagWriteRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component

object TagTable : Table("tags") {
    val id = tagId("id").primaryKey().autoIncrement()
    val name = text("name")
}

@Component
class SqlTagReadRepository : TagReadRepository {

    override fun findByNames(names: List<String>) =
            TagTable.select { TagTable.name inList names }
                    .map { it.toTag() }
}

@Component
class SqlTagWriteRepository : TagWriteRepository {

    override fun save(tag: Tag): Tag {
        return TagTable.insert { it[TagTable.name] = tag.name }[TagTable.id]!!
                .let { tag.copy(id = it) }
    }
}

fun ResultRow.toTag() = Tag(
        id = this[TagTable.id],
        name = this[TagTable.name]
)