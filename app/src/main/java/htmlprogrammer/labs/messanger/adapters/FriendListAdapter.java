package htmlprogrammer.labs.messanger.adapters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.interfaces.IFriendListContext;
import htmlprogrammer.labs.messanger.models.User;

public class FriendListAdapter extends RecyclerView.Adapter<SearchViewHolder> {
    private ArrayList<User> data = new ArrayList<>();
    private ArrayList<String> selected = new ArrayList<>();
    private LayoutInflater inflater;
    private AppCompatActivity ctx;
    private IFriendListContext dialogCtx;

    private static int EMPTY_TYPE = 0;
    private static int LIST_TYPE = 1;

    public FriendListAdapter(AppCompatActivity ctx, IFriendListContext dialogCtx){
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.dialogCtx = dialogCtx;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;

        if(data.size() == 0) {
            view = inflater.inflate(R.layout.empty_adapter, viewGroup, false);
        }
        else {
            view = inflater.inflate(R.layout.dialog_item, viewGroup, false);
        }

        return new SearchViewHolder(ctx, view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder friendListVH, int i) {
        if(data.size() > 0 && data.size() != i){
            User user = data.get(i);
            friendListVH.showName(user.getFullName());
            friendListVH.showMessage("@" + user.getNick());
            //friendListVH.showAvatar(user.getFullName(), user.getAvatar());

            friendListVH.showTime("");
            friendListVH.setOnClick(v -> dialogCtx.toggleSelect(user.getId()));
            friendListVH.setSelected(false);

            if(selected.contains(data.get(i).getId()))
                friendListVH.setSelected(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.size() == 0 ? EMPTY_TYPE : LIST_TYPE;
    }

    @Override
    public long getItemId(int position) {
        return data.size() == 0 ? 1 : data.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return Math.max(data.size(), 1);
    }

    public void setUsers(TreeSet<User> data){
        Object[] arr = data.toArray();
        ArrayList<User> users = new ArrayList<>();

        for(Object user : arr){
            users.add((User) user);
        }

        this.data = users;
        this.notifyDataSetChanged();
    }

    public void setSelected(ArrayList<String> selected){
        this.selected = selected;
        this.notifyDataSetChanged();
    }
}
