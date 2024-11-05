import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ImageManagementActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_management);

        db = FirebaseFirestore.getInstance();
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageUrlList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageUrlList);
        imageRecyclerView.setAdapter(imageAdapter);

        loadImages();
    }

    private void loadImages() {
        db.collection("images").get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String imageUrl = document.getString("url");  // Assumes each document has a field "url"
                        imageUrlList.add(imageUrl);
                    }
                    imageAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ImageManagement", "Error getting images", task.getException());
                }
            });
    }
}
