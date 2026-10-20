package biz.ospreys.athousandthoughts;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PhotoDetailActivity extends AppCompatActivity {
    public static final String EXTRA_URI = "extra_photo_uri";

    private String uri;
    private ChipGroup tagGroup;
    private AutoCompleteTextView tagInput;
    private TextView noTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        uri = getIntent().getStringExtra(EXTRA_URI);
        if (uri == null) { finish(); return; }

        Glide.with(this).load(Uri.parse(uri)).into((ImageView) findViewById(R.id.photo));

        tagGroup = findViewById(R.id.tagGroup);
        tagInput = findViewById(R.id.tagInput);
        noTags = findViewById(R.id.noTags);

        tagInput.setThreshold(1);
        tagInput.setOnItemClickListener((p, v, pos, id) -> addTagFromInput());
        tagInput.setOnEditorActionListener((v, actionId, e) -> { addTagFromInput(); return true; });
        findViewById(R.id.addTagButton).setOnClickListener(v -> addTagFromInput());

        renderTags();
    }

    private void addTagFromInput() {
        String tag = tagInput.getText().toString().trim().toLowerCase(Locale.ROOT);
        if (tag.isEmpty()) return;
        PhotoStore.addTag(uri, tag);
        tagInput.setText("");
        tagInput.dismissDropDown();
        renderTags();
    }

    private void renderTags() {
        tagGroup.removeAllViews();
        Set<String> tags = PhotoStore.getTags(uri);
        noTags.setVisibility(tags.isEmpty() ? View.VISIBLE : View.GONE);

        for (String t : tags) {
            Chip chip = new Chip(this);
            chip.setText(t);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                PhotoStore.removeTag(uri, t);
                renderTags();
            });
            tagGroup.addView(chip);
        }

        List<String> suggestions = new ArrayList<>(PhotoStore.getAllTags());
        suggestions.removeAll(tags);
        tagInput.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, suggestions));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}