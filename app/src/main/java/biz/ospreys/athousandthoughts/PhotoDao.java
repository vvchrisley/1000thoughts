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
public interface PhotoDao {

    @Insert
    void insertPhoto(Photo photo);

    @Update
    void updatePhoto(Photo photo);

    @Delete
    void deletePhoto(Photo photo);

    @Query("SELECT * FROM photos ORDER BY uri ASC")
    LiveData<List<Photo>> getAllPhotos();

    @Query("SELECT COUNT(*) FROM photos")
    int getPhotoCount();

    @Query("SELECT * FROM photos WHERE uri = :uri LIMIT 1")
    LiveData<Photo> getPhotoByUri(String uri);

    @Query("SELECT DISTINCT photos.* FROM photos "
            + "INNER JOIN photo_tags ON photos.uri = photo_tags.photoUri "
            + "INNER JOIN tags ON photo_tags.tagId = tags.id "
            + "WHERE tags.name = :tagName ORDER BY photos.uri ASC")
    LiveData<List<Photo>> getPhotosByTag(String tagName);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addTagToPhoto(PhotoTag photoTag);

    @Query("DELETE FROM photo_tags WHERE photoUri = :photoUri AND tagId = :tagId")
    void removeTagFromPhoto(String photoUri, long tagId);

    @Query("SELECT tags.* FROM tags "
            + "INNER JOIN photo_tags ON tags.id = photo_tags.tagId "
            + "WHERE photo_tags.photoUri = :photoUri ORDER BY tags.name ASC")
    LiveData<List<Tag>> getTagsForPhoto(String photoUri);
}