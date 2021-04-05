package htmlprogrammer.labs.messanger.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.models.Dialog;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogViewHolder> {
    private List<Dialog> data;
    private LayoutInflater inflater;

    private static int EMPTY_TYPE = 1;
    private static int LIST_TYPE = 2;

    public DialogAdapter(Context ctx, List<Dialog> data){
        this.data = data;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.dialog_item, viewGroup, false);
        return new DialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder dialogViewHolder, int i) {
        Dialog dialog = data.get(i);

        dialogViewHolder.showName(dialog.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DialogViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView message;
        private TextView time;

        DialogViewHolder(View view){
            super(view);

            this.name = view.findViewById(R.id.name);
            this.message = view.findViewById(R.id.message);
            this.time = view.findViewById(R.id.time);
        }

        void showName(String name){
            this.name.setText(name);
        }

        void showMessage(String message){
            this.message.setText(message);
        }

        void showTime(String time){
            this.time.setText(time);
        }
    }
}
