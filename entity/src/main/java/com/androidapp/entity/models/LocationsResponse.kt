import com.androidapp.entity.models.Locations
import com.google.gson.annotations.SerializedName

data class LocationsResponse (
	@SerializedName("message") val message : String,
	@SerializedName("locations") val locations : List<Locations>
)
