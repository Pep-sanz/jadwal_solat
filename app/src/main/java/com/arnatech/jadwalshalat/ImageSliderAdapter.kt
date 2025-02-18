import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.arnatech.jadwalshalat.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ImageSliderAdapter(private val imageList: List<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.BackgroundImageViewHolder>() {

    inner class BackgroundImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slide, parent, false)
        return BackgroundImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackgroundImageViewHolder, position: Int) {
//        holder.imageView.setImageResource(imageList[position])
        val image = imageList[position]
        Glide.with(holder.itemView.context)
            .load(image)
            .apply(
                RequestOptions
                    .placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
            )
            .into(holder.imageView)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1000
        fadeIn.startOffset = 0
        holder.imageView.startAnimation(fadeIn)
    }

    override fun getItemCount(): Int = imageList.size
}
