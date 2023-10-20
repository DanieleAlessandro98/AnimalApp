package it.uniba.dib.sms222334.Views.RecycleViews.Expences.RequestReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.R;

public class RequestReportAdapter extends RecyclerView.Adapter<RequestReportViewHolder>{

    ArrayList<Document> requestReportList;
    Context context;

    public RequestReportAdapter(ArrayList<Document> mModel, Context context){
        this.requestReportList = mModel;
        this.context=context;
    }

    @NonNull
    @Override
    public RequestReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(viewType==0?R.layout.request_list_item:R.layout.report_list_item,parent,false);

        return new RequestReportViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestReportViewHolder holder, int position) {
        if(getItemViewType(position)==0){
            holder.bind((Request) requestReportList.get(position));
        }
        else{
            holder.bind((Report) requestReportList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.requestReportList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(requestReportList.get(position) instanceof Request){
            return 0;
        }
        else {
            return 1;
        }


    }
}
