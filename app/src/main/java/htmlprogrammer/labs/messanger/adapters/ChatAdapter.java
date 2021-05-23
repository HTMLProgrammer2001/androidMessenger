package htmlprogrammer.labs.messanger.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.TreeSet;

import htmlprogrammer.labs.messanger.R;
import htmlprogrammer.labs.messanger.adapters.messagesVH.AudioMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.DocumentMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.ImageMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.ResendMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.SpecialMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.TextMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.UnknownMessageViewHolder;
import htmlprogrammer.labs.messanger.adapters.messagesVH.VideoMessageViewHolder;
import htmlprogrammer.labs.messanger.interfaces.MessageViewHolder;
import htmlprogrammer.labs.messanger.models.Message;

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private ArrayList<Message> data = new ArrayList<>();
    private ArrayList<String> selectedIds = new ArrayList<>();
    private FragmentManager manager;
    private Activity activity;
    private boolean selectable;

    private final int EMPTY_TYPE = -1;

    public ChatAdapter(FragmentManager manager, Activity activity){
        this(manager, activity, true);
    }

    public ChatAdapter(FragmentManager manager, Activity activity, boolean selectable){
        this.manager = manager;
        this.activity = activity;
        this.selectable = selectable;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MessageViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if(data.size() == 0)
            //show no messages label
            holder = new TextMessageViewHolder(inflater.inflate(R.layout.empty_adapter, viewGroup, false));
        else{
            Message message = data.get(i);

            //show messages by type
            switch (message.getType()){
                case TEXT:
                    holder = new TextMessageViewHolder(inflater.inflate(R.layout.text_message_layout, viewGroup, false));
                    break;

                case IMAGE:
                    holder = new ImageMessageViewHolder(inflater.inflate(R.layout.image_message_layout, viewGroup, false));
                    break;

                case AUDIO:
                    holder = new AudioMessageViewHolder(inflater.inflate(R.layout.audio_message_layout, viewGroup, false));
                    break;

                case VIDEO:
                    holder = new VideoMessageViewHolder(inflater.inflate(R.layout.video_message_layout, viewGroup, false));
                    break;

                case DOCUMENT:
                    holder = new DocumentMessageViewHolder(inflater.inflate(R.layout.document_message_layout, viewGroup, false), activity);
                    break;

                case SPECIAL:
                    holder = new SpecialMessageViewHolder(inflater.inflate(R.layout.special_message_layout, viewGroup, false));
                    break;

                case RESEND:
                    holder = new ResendMessageViewHolder(inflater.inflate(R.layout.resend_message_layout, viewGroup, false), activity);
                    break;

                default:
                    holder = new UnknownMessageViewHolder(inflater.inflate(R.layout.unknown_message_layout, viewGroup, false));
            }
        }

        holder.setSelectable(selectable);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder textMessageViewHolder, int i) {
        if(data.size() == 0)
            return;

        Message message = data.get(i);
        textMessageViewHolder.updateUI(message, manager);
        textMessageViewHolder.setIsRecyclable(false);

        if(selectedIds.contains(message.getId()))
            textMessageViewHolder.select();

        //show date if needed
        if(i == data.size() - 1) {
            textMessageViewHolder.setDate(data.get(i).getDateString());
            return;
        }

        String prevDate = data.get(i + 1).getDateString();
        String curDate = data.get(i).getDateString();

        if(prevDate.equals(curDate))
            textMessageViewHolder.hideDate();
        else
            textMessageViewHolder.setDate(curDate);
    }

    @Override
    public int getItemViewType(int position) {
        return data.size() == 0 ? EMPTY_TYPE : position;
    }

    @Override
    public int getItemCount() {
        return Math.max(data.size(), 1);
    }

    public void setData(TreeSet<Message> data){
        Object[] arr = data.toArray();
        ArrayList<Message> messages = new ArrayList<>();

        for(Object dialog : arr){
            messages.add((Message) dialog);
        }

        this.data = messages;
        this.notifyDataSetChanged();
    }

    public void setSelectedIds(ArrayList<String> ids){
        this.selectedIds = ids;
        this.notifyDataSetChanged();
    }
}
