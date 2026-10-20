package biz.ospreys.athousandthoughts;

import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class PhotoAdapter extends ListAdapter<String, PhotoAdapter.VH> {

    public interface OnPhotoClick { void onClick(String uri); }
    private final OnPhotoClick listener;

    public PhotoAdapter(OnPhotoClick listener) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override public boolean areItemsTheSame(@NonNull String a, @NonNull String b) {
                return a.equals(b);
            }
            @Override public boolean areContentsTheSame(@NonNull String a, @NonNull String b) {
                return a.equals(b);
            }
        });
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        final SquareImageView image;
        VH(SquareImageView v) { super(v); image = v; }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SquareImageView v = new SquareImageView(parent.getContext());
        v.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String uri = getItem(position);
        Glide.with(holder.image)
                .load(Uri.parse(uri))
                .override(300, 300)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(holder.image);
        holder.image.setOnClickListener(v -> listener.onClick(uri));
    }
}
