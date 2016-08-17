package com.gc.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.contact.R;
import com.gc.contact.entity.Contact;
import com.gc.contact.widget.ColorGenerator;
import com.gc.contact.widget.TextDrawable;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    private static ColorGenerator colorGenerator;
    protected Context context;
    protected List<Contact> list;
    protected LayoutInflater inflater;
    protected OnRecyclerItemClickListener listener;

    public ContactAdapter(Context context, List<Contact> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        colorGenerator = ColorGenerator.MATERIAL;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactHolder(inflater.inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContactHolder holder, int position) {
        String displayName = list.get(position).getDisplayName();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)  // 字体颜色
                .fontSize(48)  // 字体大小
                .useFont(Typeface.DEFAULT)  // 字体样式
                .width(96)  // 宽度
                .height(96)  // 高度
                .endConfig()
                .buildRect(displayName.charAt(0) + "", colorGenerator.getColor(displayName));
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  // 此属性必须设置，否则图片不显示
            holder.name.setCompoundDrawables(drawable, null, null, null);  // 图片设置在左侧
        }
        holder.name.setText(displayName);
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    listener.onClick(v, pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    return listener.onLongClick(v, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setOnItemListener(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnRecyclerItemClickListener {
        void onClick(View view, int position);

        boolean onLongClick(View view, int position);
    }


    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = list.get(i).getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    protected class ContactHolder extends RecyclerView.ViewHolder {

        TextView name;

        public ContactHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.id_contact_name);
        }
    }

}
