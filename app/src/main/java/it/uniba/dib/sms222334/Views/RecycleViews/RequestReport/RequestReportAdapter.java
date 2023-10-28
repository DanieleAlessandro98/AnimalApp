package it.uniba.dib.sms222334.Views.RecycleViews.RequestReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
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
        holder.requestReport = requestReportList.get(position);

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

    public void sortByDistance() {
        ArrayList<Document> userDocuments = new ArrayList<>();
        ArrayList<Document> otherDocuments = new ArrayList<>();

        String loggedUserID = SessionManager.getInstance().isLogged() ? SessionManager.getInstance().getCurrentUser().getFirebaseID() : null;

        for (Document document : requestReportList) {
            User documentUser = (document instanceof Report) ? ((Report) document).getUser() : ((Request) document).getUser();

            if (documentUser != null && documentUser.getFirebaseID().equals(loggedUserID))
                userDocuments.add(document);
            else
                otherDocuments.add(document);
        }

        Comparator<Document> distanceComparator = new Comparator<Document>() {
            @Override
            public int compare(Document document1, Document document2) {
                float distance1 = (document1 instanceof Report) ? ((Report) document1).getDistance() : ((Request) document1).getDistance();
                float distance2 = (document2 instanceof Report) ? ((Report) document2).getDistance() : ((Request) document2).getDistance();

                return Float.compare(distance1, distance2);
            }
        };

        Collections.sort(userDocuments, distanceComparator);
        Collections.sort(otherDocuments, distanceComparator);

        requestReportList.clear();
        requestReportList.addAll(userDocuments);
        requestReportList.addAll(otherDocuments);
        notifyDataSetChanged();
    }

}
