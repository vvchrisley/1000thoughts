package biz.ospreys.athousandthoughts;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "photo_tags",
        primaryKeys = {"photoUri", "tagId"},
        indices = {@Index(value = "tagId")},
        foreignKeys = {
                @ForeignKey(
                        entity = Photo.class,
                        parentColumns = "uri",
                        childColumns = "photoUri",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Tag.class,
                        parentColumns = "id",
                        childColumns = "tagId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class PhotoTag {
    @NonNull
    private String photoUri;
    private long tagId;

    public PhotoTag(@NonNull String photoUri, long tagId) {
        this.photoUri = photoUri;
        this.tagId = tagId;
    }

    @NonNull
    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(@NonNull String photoUri) { this.photoUri = photoUri; }

    public long getTagId() { return tagId; }
    public void setTagId(long tagId) { this.tagId = tagId; }
}
