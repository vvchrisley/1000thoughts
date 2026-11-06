package biz.ospreys.athousandthoughts;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagTestActivity extends AppCompatActivity {

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();
    private final List<Photo> photos = new ArrayList<>();
    private final List<String> photoLabels = new ArrayList<>();

    private GalleryDatabase database;
    private EditText tagNameInput;
    private Button addTagButton;
    private TextView photoTags;
    private String selectedPhotoUri;
    private LiveData<List<Tag>> observedTags;
    private Observer<List<Tag>> tagsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_test);

        View root = findViewById(R.id.tag_test_root);
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

        Spinner photoSpinner = findViewById(R.id.photo_spinner);
        Button backButton = findViewById(R.id.back_button);
        tagNameInput = findViewById(R.id.tag_name_input);
        addTagButton = findViewById(R.id.add_tag_button);
        photoTags = findViewById(R.id.photo_tags);
        database = GalleryDatabase.getDatabase(getApplicationContext());
        backButton.setOnClickListener(view -> finish());

        ArrayAdapter<String> photoAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                photoLabels
        );
        photoAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        photoSpinner.setAdapter(photoAdapter);
        addTagButton.setEnabled(false);

        photoSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        if (position < 0 || position >= photos.size()) {
                            return;
                        }
                        selectedPhotoUri = photos.get(position).getUri();
                        observeSelectedPhotoTags(selectedPhotoUri);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedPhotoUri = null;
                        addTagButton.setEnabled(false);
                    }
                }
        );

        database.photoDao().getAllPhotos().observe(this, savedPhotos -> {
            String previousSelection = selectedPhotoUri;
            photos.clear();
            photos.addAll(savedPhotos);
            photoLabels.clear();
            for (int i = 0; i < savedPhotos.size(); i++) {
                photoLabels.add(getString(R.string.photo_number, i + 1));
            }
            photoAdapter.notifyDataSetChanged();
            addTagButton.setEnabled(!photos.isEmpty());

            if (photos.isEmpty()) {
                selectedPhotoUri = null;
                photoTags.setText(R.string.no_saved_photos);
                removeTagsObserver();
                return;
            }

            int selection = findPhotoPosition(previousSelection);
            photoSpinner.setSelection(selection);
            selectedPhotoUri = photos.get(selection).getUri();
            observeSelectedPhotoTags(selectedPhotoUri);
        });

        addTagButton.setOnClickListener(view -> addTagToSelectedPhoto());
    }

    private int findPhotoPosition(String photoUri) {
        if (photoUri != null) {
            for (int i = 0; i < photos.size(); i++) {
                if (photoUri.equals(photos.get(i).getUri())) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void observeSelectedPhotoTags(String photoUri) {
        removeTagsObserver();
        observedTags = database.photoDao().getTagsForPhoto(photoUri);
        tagsObserver = tags -> {
            if (tags.isEmpty()) {
                photoTags.setText(R.string.no_tags_yet);
                return;
            }

            StringBuilder result = new StringBuilder();
            for (Tag tag : tags) {
                if (result.length() > 0) {
                    result.append('\n');
                }
                result.append(tag.getName());
            }
            photoTags.setText(result.toString());
        };
        observedTags.observe(this, tagsObserver);
    }

    private void removeTagsObserver() {
        if (observedTags != null && tagsObserver != null) {
            observedTags.removeObserver(tagsObserver);
        }
        observedTags = null;
        tagsObserver = null;
    }

    private void addTagToSelectedPhoto() {
        String tagName = tagNameInput.getText().toString().trim();
        if (selectedPhotoUri == null) {
            photoTags.setText(R.string.no_saved_photos);
            return;
        }
        if (tagName.isEmpty()) {
            tagNameInput.setError(getString(R.string.tag_name_required));
            return;
        }

        String photoUri = selectedPhotoUri;
        addTagButton.setEnabled(false);
        databaseExecutor.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    TagDao tagDao = database.tagDao();
                    tagDao.insertTagIfAbsent(new Tag(tagName));
                    Long tagId = tagDao.findTagIdByName(tagName);
                    if (tagId == null) {
                        throw new IllegalStateException(
                                "Tag insert completed but the tag could not be found."
                        );
                    }
                    database.photoDao().addTagToPhoto(
                            new PhotoTag(photoUri, tagId)
                    );
                });
                runOnUiThread(() -> {
                    tagNameInput.setText("");
                    photoTags.setText(R.string.tag_added);
                    addTagButton.setEnabled(!photos.isEmpty());
                });
            } catch (SQLiteException | IllegalStateException exception) {
                runOnUiThread(() -> {
                    photoTags.setText(getString(
                            R.string.tag_save_failed,
                            exception.getLocalizedMessage()
                    ));
                    addTagButton.setEnabled(!photos.isEmpty());
                });
            }
        });
    }
}
