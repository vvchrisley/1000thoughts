package biz.ospreys.athousandthoughts;

import android.content.Context;

import androidx.room.Database;
import androidx.room.migration.Migration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {Photo.class, Tag.class, PhotoTag.class},
        version = 2,
        exportSchema = false
)
public abstract class GalleryDatabase extends RoomDatabase {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_photo_tags_tagId` ON `photo_tags` (`tagId`)"
            );
        }
    };

    private static volatile GalleryDatabase INSTANCE;

    public abstract PhotoDao photoDao();

    public abstract TagDao tagDao();

    public static GalleryDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (GalleryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            GalleryDatabase.class,
                            "gallery_database"
                    ).addMigrations(MIGRATION_1_2).build();
                }
            }
        }
        return INSTANCE;
    }
}
