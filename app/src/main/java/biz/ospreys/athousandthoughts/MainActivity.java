package biz.ospreys.athousandthoughts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button imageTestButton = findViewById(R.id.image_test_button);
        imageTestButton.setOnClickListener(view ->
                startActivity(new Intent(this, ImageTestActivity.class))
        );

        Button tagTestButton = findViewById(R.id.tag_test_button);
        tagTestButton.setOnClickListener(view ->
                startActivity(new Intent(this, TagTestActivity.class))
        );
    }
}