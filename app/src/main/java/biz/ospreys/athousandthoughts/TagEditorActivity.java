package biz.ospreys.athousandthoughts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagEditorActivity extends AppCompatActivity {



    private EditText tagNameInput;
    private EditText tagNameOutput;

    private TextView tagList;

    private GalleryDatabase database;
    private TagDao tagDao;
    private LiveData<List<Tag>> observedTags;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tag_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tag_setting_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tagNameInput = findViewById(R.id.tagText);
        tagNameOutput = findViewById(R.id.newTagText);
        tagList = findViewById(R.id.tagList);
        Button addTagButton = findViewById(R.id.createTagButton);
        Button editTagButton = findViewById(R.id.editTagButton);
        Button removeTagButton = findViewById(R.id.removeTagButton);

        database = GalleryDatabase.getDatabase(this);
        tagDao = database.tagDao();
        observedTags = tagDao.getAllTags();


        //observedTags.observe(this, tags -> updateTagView(tags));
        observedTags.observe(this, this::updateTagView);

        addTagButton.setOnClickListener(view -> {

            String potTag = tagNameInput.getText().toString().toLowerCase();
            if (potTag.isEmpty()) {
                tagNameInput.setText(R.string.tag_name_hint);
                return;
            }

            List<Tag> currentTags;
            currentTags = (observedTags != null) ? observedTags.getValue() : null;
            if (currentTags != null) {;
                for (Tag tag : currentTags) {
                    if (tag.getName().equals(potTag)) {
                        tagNameInput.setText(R.string.dup_tag);
                        return;
                    }
                }
            }

            Tag temp = new Tag(potTag);
            executor.execute(() -> tagDao.insertTag(temp));

            tagNameInput.setText("");
            observedTags.observe(this, this::updateTagView);
        });

        editTagButton.setOnClickListener(view -> {

            String potTag = tagNameInput.getText().toString().toLowerCase();
            String newPotTag = tagNameOutput.getText().toString().toLowerCase();
            if (potTag.isEmpty()) {
                tagNameInput.setText(R.string.tag_name_hint);
                return;
            } else if (newPotTag.isEmpty()) {
                tagNameOutput.setText(R.string.tag_name_hint);
                return;
            }


            List<Tag> currentTags = (observedTags != null) ? observedTags.getValue() : null;
            if (currentTags == null || currentTags.isEmpty()) {
                tagList.setText(R.string.no_tags_yet);
                return;
            }

            for (Tag tag : currentTags) {
                if (tag.getName().equals(potTag)) {
                    tag.setName(newPotTag);
                    executor.execute(() -> tagDao.updateTag(tag));
                }
            }

            tagNameInput.setText("");
            tagNameOutput.setText("");

        });




        removeTagButton.setOnClickListener(view -> {
            String potTag = tagNameInput.getText().toString().toLowerCase();

            if (potTag.isEmpty()) {
                tagNameInput.setText(R.string.tag_name_hint);
                return;
            }

            List<Tag> currentTags = (observedTags != null) ? observedTags.getValue() : null;
            if (currentTags == null || currentTags.isEmpty()) {
                tagList.setText(R.string.no_tags_yet);
                return;
            }

            for (Tag tag : currentTags) {
                if (tag.getName().equals(potTag)) {
                    executor.execute(() -> tagDao.deleteTag(tag));
                }
            }

            tagNameInput.setText("");

        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view ->
                startActivity(new Intent(this, MainActivity.class))
        );

    }//end onCreate


    void updateTagView(List<Tag> tags){

        if (tags == null || tags.isEmpty()) {
            tagList.setText(R.string.no_tags_yet);
            return;
        }

        StringBuilder result = new StringBuilder();
        for (Tag tag : tags) {
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(tag.getName());
        }
        tagList.setText(result.toString());

    }



}
