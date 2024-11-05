import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProfileManagementActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView profileRecyclerView;
    private ProfileAdapter profileAdapter;
    private List<Profile> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);

        db = FirebaseFirestore.getInstance();
        profileRecyclerView = findViewById(R.id.profileRecyclerView);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(profileList);
        profileRecyclerView.setAdapter(profileAdapter);

        loadProfiles();
    }

    private void loadProfiles() {
        db.collection("profiles").get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Profile profile = document.toObject(Profile.class);
                        profileList.add(profile);
                    }
                    profileAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ProfileManagement", "Error getting profiles", task.getException());
                }
            });
    }
}
