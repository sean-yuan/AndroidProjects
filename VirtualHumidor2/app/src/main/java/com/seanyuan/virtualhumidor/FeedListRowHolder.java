package com.seanyuan.virtualhumidor;


        import android.content.Intent;
        import android.media.Rating;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.RatingBar;
        import android.widget.TextView;
        import android.widget.Toast;

public class FeedListRowHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail;
    protected TextView title;
    protected TextView type;
    protected TextView price;
    protected RatingBar rating;
    protected TextView quantity;

    public FeedListRowHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
        this.type = (TextView) view.findViewById(R.id.type);
        this.price = (TextView) view.findViewById(R.id.price);
        this.rating = (RatingBar) view.findViewById(R.id.ratingBar);
        this.quantity = (TextView) view.findViewById(R.id.quantity);
    }

}
