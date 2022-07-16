package io.ikws4.weiju.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.divider.MaterialDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.ikws4.weiju.R;

public class SearchListDialog extends Dialog {
    private OnItemClickListener mOnItemClickListener;
    private final List<Item> mSourceItems = new ArrayList<>();
    private List<Item> mDisplayItems;
    private final Adapter mAdapter;

    private final EditText vInput;
    private final ImageButton vSearch;
    private final MaterialDivider vDivider;

    public SearchListDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.search_list_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // bluring
        getWindow().setDimAmount(0.3f);
        //  48% height
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.85);
        lp.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.48);
        getWindow().setAttributes(lp);

        RecyclerView rv = findViewById(R.id.rv_item_list);
        mAdapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

        vInput = findViewById(R.id.et_input);
        vSearch = findViewById(R.id.btn_search);
        vDivider = findViewById(R.id.divider);

        vInput.setOnEditorActionListener((v, a, e) -> true);
        vInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s);

                if (mDisplayItems.size() == 0) {
                    vDivider.setVisibility(View.GONE);
                } else {
                    vDivider.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void filter(CharSequence s) {
        mDisplayItems = mSourceItems.stream().filter((item) -> isSubsequence(s, item.title)).collect(Collectors.toList());
        mAdapter.notifyDataSetChanged();
    }

    // check a if the subsequence of b
    private boolean isSubsequence(CharSequence a, CharSequence b) {
        int i = 0, j = 0;
        while (i < a.length()) {
            while (j < b.length() && Character.toLowerCase(b.charAt(j)) != Character.toLowerCase(a.charAt(i))) j++;
            if (j == b.length()) break;
            i++;
        }
        return i == a.length();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setItems(List<Item> items) {
        mSourceItems.clear();
        mSourceItems.addAll(items);
        filter("");
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mDisplayItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mDisplayItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView vIcon;
            TextView vTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vIcon = itemView.findViewById(R.id.img_icon);
                vTitle = itemView.findViewById(R.id.tv_title);
            }

            public void bind(Item item) {
                vTitle.setText(item.title);
                Glide.with(getContext())
                    .load(item.iconUri)
                    .into(vIcon);
                itemView.setOnClickListener((v) -> {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(item);
                    }
                });
            }
        }

    }

    public static class Item {
        public final CharSequence title;
        public final String iconUri;

        public Item(CharSequence title, String iconUri) {
            this.title = title;
            this.iconUri = iconUri;
        }
    }

    public interface OnItemClickListener {
        void onClick(Item item);
    }
}
