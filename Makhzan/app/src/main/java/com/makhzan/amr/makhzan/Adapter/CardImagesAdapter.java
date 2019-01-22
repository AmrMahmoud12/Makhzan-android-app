package com.makhzan.amr.makhzan.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makhzan.amr.makhzan.R;

import java.util.List;

public class CardImagesAdapter extends RecyclerView.Adapter<CardImagesAdapter.ViewHolder>{

     public List<String> fileNameList;
     public List<String> fileDoneList;
   public CardImagesAdapter(List<String> fileNameList, List<String> fileDoneList){
        this.fileNameList = fileNameList;
        this.fileDoneList = fileDoneList;
   }
     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	 View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
	 return new ViewHolder(v);
   }

     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
	 String fileName = fileNameList.get(position);
	 holder.fileNameView.setText(fileName);

	 String fileDone = fileDoneList.get(position);
	 if ( fileDone.equals("Uploading") ) {
	      holder.fileDoneView.setImageResource(R.drawable.notcompleted);
	 } else {
	      holder.fileDoneView.setImageResource(R.drawable.completed);
	 }
     }

     @Override
     public int getItemCount() {
	 return fileNameList.size();
     }

     public class ViewHolder extends RecyclerView.ViewHolder {
	 View mView;
	 public TextView fileNameView;
	 public ImageView fileDoneView;

	 public ViewHolder(View itemView) {
	      super(itemView);
	      mView = itemView;
	      fileNameView = mView.findViewById(R.id.picName);
	      fileDoneView = mView.findViewById(R.id.completedPic);
	 }
     }
}
