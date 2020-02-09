import com.androidapp.entity.models.Locations
import com.google.gson.annotations.SerializedName

data class LocationResponse (
	@SerializedName("message") val message : String,
	@SerializedName("location") val location : Locations
)
