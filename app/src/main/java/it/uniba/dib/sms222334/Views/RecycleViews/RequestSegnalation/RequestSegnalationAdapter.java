package it.uniba.dib.sms222334.Views.RecycleViews.RequestSegnalation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Segnalation;
import it.uniba.dib.sms222334.R;

public class RequestSegnalationAdapter extends RecyclerView.Adapter<RequestSegnalationViewHolder>{

    ArrayList<Document> requestSegnalationList;
    Context context;

    public RequestSegnalationAdapter(ArrayList<Document> mModel,Context context){
        this.requestSegnalationList = mModel;
        this.context=context;
    }

    @NonNull
    @Override
    public RequestSegnalationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(viewType==0?R.layout.request_list_item:R.layout.segnalation_list_item,parent,false);

        return new RequestSegnalationViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestSegnalationViewHolder holder, int position) {
        if(getItemViewType(position)==0){
            holder.bind((Request) requestSegnalationList.get(position));
        }
        else{
            holder.bind((Segnalation) requestSegnalationList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return this.requestSegnalationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(requestSegnalationList.get(position) instanceof Request){
            return 0;
        }
        else {
            return 1;
        }


    }
}
