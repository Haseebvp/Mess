package mess.bangalore.com.mess.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mess.bangalore.com.mess.R;
import mess.bangalore.com.mess.Utilities.AppUtils;
import mess.bangalore.com.mess.Utilities.ItemAnimation;
import mess.bangalore.com.mess.Utilities.SessionHandler;
import mess.bangalore.com.mess.models.ExpenseItem;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private Context mContext;
    private List<ExpenseItem> data;

    private int lastPosition = -1;
    private boolean on_attach = true;

    public ExpenseAdapter(Context context, List<ExpenseItem> data) {
        this.mContext = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expensive, parent, false);
        return new ExpenseAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        ExpenseItem item = data.get(position);
        if (item != null) {
            if (holder instanceof ExpenseAdapter.ViewHolder) {
                holder.tagtext.setText(item.getTag());
                holder.amount.setText(mContext.getString(R.string.amount, String.valueOf(item.getAmount())));
                holder.name.setText(item.getName());
                holder.time.setText(AppUtils.getTime(Long.parseLong(item.getTime())));
                setImage(item.getTag(), holder.tag);
                String paidby = "";
                if (item.getUserId().equalsIgnoreCase(SessionHandler.getInstance(mContext).getUserId())) {
                    paidby = "paid by " + "<b>" + "You" + "</b> ";
                } else {
                    paidby = "paid by " + "<b>" + item.getUsername() + "</b> ";
                }
                holder.user.setText(Html.fromHtml(paidby));
                setAnimation(holder.itemView, position);
            }
        }
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, ItemAnimation.FADE_IN);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setImage(String image, ImageView tag) {
        switch (image) {
            case "Bill":
                tag.setImageDrawable(mContext.getDrawable(R.drawable.ic_tag_bill_symbol));
                break;

            case "Food and Stationaries":
                tag.setImageDrawable(mContext.getDrawable(R.drawable.ic_tag_food));
                break;

            case "Furniture and Electronics":
                tag.setImageDrawable(mContext.getDrawable(R.drawable.ic_tag_furniture));
                break;

            case "Personal":
                tag.setImageDrawable(mContext.getDrawable(R.drawable.ic_tag_personal));
                break;

            case "Others":
                tag.setImageDrawable(mContext.getDrawable(R.drawable.ic_tag_others));
                break;
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tag;
        TextView name, amount, tagtext, time, user;

        public ViewHolder(View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
            tagtext = itemView.findViewById(R.id.tag);
            time = itemView.findViewById(R.id.time);
            time = itemView.findViewById(R.id.time);
            user = itemView.findViewById(R.id.user);
        }
    }
}
