package biz.ospreys.athousandthoughts;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos")
public class Photo {
    @PrimaryKey
    @NonNull
    private String uri;

    public Photo(@NonNull String uri) {
        this.uri = uri;
    }

    @NonNull
    public String getUri() { return uri; }

    public void setUri(@NonNull String uri) { this.uri = uri; }
}