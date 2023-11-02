package it.uniba.dib.sms222334.Views.RecycleViews.RequestReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.R;

public class RequestReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int SHOW_REQUEST_MENU = 1;
    private final int HIDE_REQUEST_MENU = 3;

    private final int SHOW_REPORT_MENU = 2;
    private final int HIDE_REPORT_MENU = 4;

    private List<Document> list;
    private HomeFragment fragment;

    public RequestReportAdapter(HomeFragment fragment, List<Document> articlesList) {
        this.list = articlesList;
        this.fragment = fragment;
    }

    @Override
    public int getItemViewType(int position) {
        Document document = list.get(position);
        if(document instanceof Request) {
            if (document.isShowMenu())
                return SHOW_REQUEST_MENU;
            else
                return HIDE_REQUEST_MENU;
        } else {
            if (document.isShowMenu())
                return SHOW_REPORT_MENU;
            else
                return HIDE_REPORT_MENU;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case SHOW_REQUEST_MENU:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_request_menu, parent, false);
                return new MenuViewHolder(v, fragment);

            case HIDE_REQUEST_MENU:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_item, parent, false);
                return new RequestViewHolder(v, fragment.getContext());

            case SHOW_REPORT_MENU:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_report_menu, parent, false);
                return new MenuViewHolder(v, fragment);

            case HIDE_REPORT_MENU:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_item, parent, false);
                return new ReportViewHolder(v, fragment.getContext());
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Document entity = list.get(holder.getAdapterPosition());
        if (holder instanceof RequestViewHolder)
            ((RequestViewHolder) holder).bind((Request) entity);
        else if (holder instanceof ReportViewHolder)
            ((ReportViewHolder) holder).bind((Report) entity);
        else if (holder instanceof MenuViewHolder) {
            if (entity instanceof Request)
                ((MenuViewHolder) holder).bind((Request) entity);
            else
                ((MenuViewHolder) holder).bind((Report) entity);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void showMenu(int position) {
        for (int i = 0; i < list.size(); i++) {
            if (i != position && isMenuShown(i)) {
                list.get(i).setShowMenu(false);
                notifyItemChanged(i);
            }
        }

        list.get(position).setShowMenu(true);
        notifyItemChanged(position);
    }

    public void closeMenu() {
        for (int i = 0; i < list.size(); i++) {
            if (isMenuShown(i)) {
                list.get(i).setShowMenu(false);
                notifyItemChanged(i);
            }
        }
    }

    public boolean isMenuShown() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isShowMenu())
                return true;
        }

        return false;
    }

    public boolean isMenuShown(int position) {
        if (position >= 0 && position < list.size())
            return list.get(position).isShowMenu();

        return false;
    }

    public void sortByDistance() {
        ArrayList<Document> userDocuments = new ArrayList<>();
        ArrayList<Document> otherDocuments = new ArrayList<>();

        String loggedUserID = SessionManager.getInstance().isLogged() ? SessionManager.getInstance().getCurrentUser().getFirebaseID() : null;

        for (Document document : list) {
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

        list.clear();
        list.addAll(userDocuments);
        list.addAll(otherDocuments);
        notifyDataSetChanged();
    }

    public void removeDocument(Document requestReport) {
        this.list.remove(requestReport);
        notifyDataSetChanged();
    }

    public void updateDocument(Document requestReport) {
        int position = list.indexOf(requestReport);
        if (position != -1) {
            list.set(position, requestReport);
            notifyItemChanged(position);
        }
    }
}
