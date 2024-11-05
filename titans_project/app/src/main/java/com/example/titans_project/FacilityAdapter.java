import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {
    private List<Facility> facilityList;
    private FirebaseFirestore db;
    private Context context;

    public FacilityAdapter(List<Facility> facilityList, FirebaseFirestore db, Context context) {
        this.facilityList = facilityList;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.facilityName.setText(facility.getName());
        holder.deleteButton.setOnClickListener(view -> deleteFacility(facility, position));
    }

    private void deleteFacility(Facility facility, int position) {
        db.collection("facilities").document(facility.getId())
            .delete()
            .addOnSuccessListener(aVoid -> {
                facilityList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Facility removed", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> 
                Toast.makeText(context, "Failed to remove facility", Toast.LENGTH_SHORT).show()
            );
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName;
        Button deleteButton;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.facilityName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
