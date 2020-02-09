import com.androidapp.entity.models.Category
import com.androidapp.entity.models.Locations
import com.google.gson.annotations.SerializedName



data class CategoryResponse (

	@SerializedName("message") val message : String,
	@SerializedName("categories") val categories : List<Category>
)

