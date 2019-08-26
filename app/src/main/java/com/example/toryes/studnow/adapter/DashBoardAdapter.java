package com.example.toryes.studnow.adapter;

/**
 * Created by TORYES on 8/10/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toryes.studnow.R;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.ViewHolder> {


private LayoutInflater mInflater;
private ItemClickListener mClickListener;
        String str[];
    Integer img[];
        Context context;
// data is passed into the constructor
public DashBoardAdapter(Context context,   String str[],Integer img[]) {
        this.mInflater = LayoutInflater.from(context);
        this.img = img;
    this.str=str;
        this.context=context;


        }

// inflates the cell layout from xml when needed
@Override
public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.dashnoard_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
        }

// binds the data to the textview in each cell
@Override
public void onBindViewHolder(ViewHolder holder, int position) {

        holder.myTextView.setText(str[position]);

       holder.imageView.setImageResource(img[position]);
        }

// total number of cells
@Override
public int getItemCount() {
        return str.length;
        }


// stores and recycles views as they are scrolled off screen
public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView myTextView;
    public ImageView imageView;
    public ViewHolder(View itemView) {
        super(itemView);
        myTextView = (TextView) itemView.findViewById(R.id.textview);
        imageView= (ImageView) itemView.findViewById(R.id.imageview);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
    }
}

    // convenience method for getting data at click position
//    public String getItem(int id) {
//        return mData[id];
//    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

// parent activity will implement this method to respond to click events
public interface ItemClickListener {
    void onItemClick(View view, int position);
}
}
