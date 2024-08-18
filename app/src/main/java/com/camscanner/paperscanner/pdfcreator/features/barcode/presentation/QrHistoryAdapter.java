package com.camscanner.paperscanner.pdfcreator.features.barcode.presentation;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import com.camscanner.paperscanner.pdfcreator.R;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.ParsedResultAdapter;
import com.camscanner.paperscanner.pdfcreator.features.barcode.model.QrResult;

public class QrHistoryAdapter extends RecyclerView.Adapter<QrHistoryAdapter.QrViewHolder> {
    private List<QrResult> history;
    private QrListListener listener;

    public interface QrListListener {
        void onQrClicked(QrResult qrResult);
    }



    static class QrViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView name;
        View root;

        public QrViewHolder(@NonNull View itemView) {
            super(itemView);

            date = (TextView)itemView.findViewById(R.id.date);
            name = (TextView)itemView.findViewById(R.id.name);
            root = (View)itemView.findViewById(R.id.root);

        }
    }

    public QrHistoryAdapter(List<QrResult> list, QrListListener listener2) {
        this.history = new ArrayList(list);
        this.listener = listener2;
    }

    @NonNull
    public QrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QrViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_history, parent, false));
    }

    public void onBindViewHolder(@NonNull QrViewHolder viewHolder, int position) {
        final QrResult result = this.history.get(position);
        viewHolder.name.setText(result.getName());
        viewHolder.date.setText(ParsedResultAdapter.getDateDisplay(result.getDate()));
        viewHolder.root.setOnClickListener(new View.OnClickListener() {


            public final void onClick(View view) {
                QrHistoryAdapter.this.lambda$onBindViewHolder$0$QrHistoryAdapter(result, view);
            }
        });
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$QrHistoryAdapter(QrResult result, View v) {
        QrListListener qrListListener = this.listener;
        if (qrListListener != null) {
            qrListListener.onQrClicked(result);
        }
    }

    public int getItemCount() {
        return this.history.size();
    }

    public void update(List<QrResult> qrResults) {
        this.history = new ArrayList(qrResults);
        notifyDataSetChanged();
    }
}
