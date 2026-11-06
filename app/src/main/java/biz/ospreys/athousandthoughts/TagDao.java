package biz.ospreys.athousandthoughts;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TagDao {

    @Insert
    long insertTag(Tag tag);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertTagIfAbsent(Tag tag);

    @Update
    void updateTag(Tag tag);

    @Delete
    void deleteTag(Tag tag);

    @Query("SELECT * FROM tags ORDER BY name ASC")
    LiveData<List<Tag>> getAllTags();

    @Query("SELECT * FROM tags WHERE id = :id LIMIT 1")
    LiveData<Tag> getTagById(long id);

    @Query("SELECT id FROM tags WHERE name = :name LIMIT 1")
    Long findTagIdByName(String name);
}