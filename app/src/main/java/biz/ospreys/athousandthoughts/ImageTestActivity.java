package biz.ospreys.athousandthoughts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageTestActivity extends AppCompatActivity {

    private static final ExecutorService DATABASE_EXECUTOR =
            Executors.newSingleThreadExecutor();

    private ImageView photoImage;
    private TextView photoStatus;

    private final ActivityResultLauncher<String[]> photoPicker =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    this::handleSelectedPhoto
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_test);

        View root = findViewById(R.id.image_test_root);
        int initialLeft = root.getPaddingLeft();
        int initialTop = root.getPaddingTop();
        int initialRight = root.getPaddingRight();
        int initialBottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );
            view.setPadding(
                    initialLeft + systemBars.left,
                    initialTop + systemBars.top,
                    initialRight + systemBars.right,
                    initialBottom + systemBars.bottom
            );
            return windowInsets;
        });

        photoImage = findViewById(R.id.photo_image);
        photoStatus = findViewById(R.id.photo_status);
        Button backButton = findViewById(R.id.back_button);
        Button pickPhotoButton = findViewById(R.id.pick_photo_button);

        backButton.setOnClickListener(view -> finish());
        pickPhotoButton.setOnClickListener(view -> {
            photoStatus.setText(R.string.opening_photo_picker);
            photoPicker.launch(new String[]{"image/*"});
        });
    }

    private void handleSelectedPhoto(Uri uri) {
        if (uri == null) {
            return;
        }

        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException exception) {
            photoStatus.setText(R.string.photo_permission_error);
            return;
        }

        photoImage.setImageURI(uri);
        photoStatus.setText(R.string.saving_photo);

        String uriString = uri.toString();
        GalleryDatabase database =
                GalleryDatabase.getDatabase(getApplicationContext());
        DATABASE_EXECUTOR.execute(() -> {
            database.photoDao().insertPhoto(new Photo(uriString));
            runOnUiThread(() -> photoStatus.setText(R.string.photo_saved));
        });
    }
}
