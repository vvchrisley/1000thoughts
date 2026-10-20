package biz.ospreys.athousandthoughts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private PhotoAdapter adapter;
    private ChipGroup tagChips;
    private View tagScroll;
    private TextView emptyMessage, countText;
    private final Set<String> selectedTags = new LinkedHashSet<>();
    private String tagQuery = "";

    private final ActivityResultLauncher<String[]> pickPhotos = registerForActivityResult(
            new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris == null || uris.isEmpty()) return;
                List<String> chosen = new ArrayList<>();
                for (Uri u : uris) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                u, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        chosen.add(u.toString());
                    } catch (SecurityException ignored) { }
                }
                PhotoStore.addPhotos(chosen);   // TODO: Temporary photo storage. Switch to Room once added
                refreshAll();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        countText = findViewById(R.id.countText);
        emptyMessage = findViewById(R.id.emptyMessage);
        tagChips = findViewById(R.id.tagChips);
        tagScroll = findViewById(R.id.tagScroll);

        RecyclerView recycler = findViewById(R.id.recycler);
        adapter = new PhotoAdapter(this::openDetail);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        recycler.setAdapter(adapter);

        findViewById(R.id.addFab).setOnClickListener(
                v -> pickPhotos.launch(new String[]{"image/*"}));

        ((EditText) findViewById(R.id.searchInput)).addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                tagQuery = s.toString().trim().toLowerCase(Locale.ROOT);
                rebuildChips();
            }
        });
    }

    @Override
    protected void onResume() {            // pick up tags added on the detail screen
        super.onResume();
        refreshAll();
    }

    private void refreshAll() {
        selectedTags.retainAll(PhotoStore.getAllTags());  // drop filters whose tag no longer exists
        rebuildChips();
        refreshGrid();
    }

    private void refreshGrid() {
        List<String> uris = PhotoStore.getUris(selectedTags);
        adapter.submitList(uris);
        countText.setText(uris.size() + (uris.size() == 1 ? " photo" : " photos"));

        if (PhotoStore.count() == 0) {
            emptyMessage.setText("No photos yet. Tap \"Add photos\" to choose some.");
        } else if (uris.isEmpty()) {
            emptyMessage.setText("No photos have all of the selected tags.");
        }
        emptyMessage.setVisibility(uris.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void rebuildChips() {
        tagChips.removeAllViews();
        for (String tag : PhotoStore.getAllTags()) {
            boolean selected = selectedTags.contains(tag);
            if (!selected && !tag.contains(tagQuery)) continue;   // search narrows the list
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChecked(selected);
            chip.setOnCheckedChangeListener((b, checked) -> {
                if (checked) selectedTags.add(tag); else selectedTags.remove(tag);
                refreshGrid();
            });
            tagChips.addView(chip);
        }
        tagScroll.setVisibility(tagChips.getChildCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private void openDetail(String uri) {
        Intent i = new Intent(this, PhotoDetailActivity.class);
        i.putExtra(PhotoDetailActivity.EXTRA_URI, uri);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_manage_tags) {
            Toast.makeText(this, "Tag management coming soon", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}