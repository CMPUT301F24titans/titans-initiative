import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FacilityManagementActivity extends AppCompatActivity {
    private RecyclerView facilityRecyclerView;
    private FacilityAdapter facilityAdapter;
    private FirebaseFirestore db;
    private List<Facility> facilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_management);

        db = FirebaseFirestore.getInstance();
        facilityRecyclerView = findViewById(R.id.facilityRecyclerView);
        facilityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        facilityList = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(facilityList, db, this);
        facilityRecyclerView.setAdapter(facilityAdapter);

        loadFacilities();
    }

    private void loadFacilities() {
        db.collection("facilities").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                facilityList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Facility facility = document.toObject(Facility.class);
                    facility.setId(document.getId());  // Save document ID for deletion
                    facilityList.add(facility);
                }
                facilityAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error loading facilities", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
