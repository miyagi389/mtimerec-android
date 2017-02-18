package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Template;

class TemplateListAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements TemplateListAdapterItemViewHolder.Listener {

    public interface Listener {

        void onItemClick(
            @NonNull TemplateListAdapter adapter,
            int position
        );

        void onMenuInfoClick(
            @NonNull TemplateListAdapter adapter,
            int position
        );
    }

    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_FOOTER = 1;

    private final TemplateListAdapter self = this;

    private final List<Template> items = new ArrayList<>();
    private final Listener listener;
    private final LayoutInflater layoutInflater;

    TemplateListAdapter(
        @NonNull final Context context,
        @Nullable final Listener listener
    ) {
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(final int position) {
        // Get the footer position (the last item in the list)
        final int footerPosition = getItemCount() - 1;

        if (position == footerPosition) {
            return TYPE_FOOTER;
        } else {
            return TYPE_CONTENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
        final ViewGroup parent,
        final int viewType
    ) {
        switch (viewType) {
            case TYPE_CONTENT:
                return new TemplateListAdapterItemViewHolder(parent, self.layoutInflater, self);
            case TYPE_FOOTER:
                return new TemplateListAdapterFooterViewHolder(parent, self.layoutInflater);
            default:
                throw new RuntimeException("illegal viewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(
        final RecyclerView.ViewHolder holder,
        final int position
    ) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                ((TemplateListAdapterItemViewHolder) holder).onBindViewHolder(self, position);
                break;
            case TYPE_FOOTER:
                break;
            default:
                throw new RuntimeException("illegal position: " + position);
        }
    }

    @Override
    public long getItemId(final int position) {
        return super.getItemId(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int itemCount = self.items.size();
        if (itemCount > 0) {
            itemCount += 1; // add footer item
        }
        return itemCount;
    }

    Template getItem(final int position) {
        return self.items.get(position);
    }

    void addAll(@NonNull final List<Template> items) {
        self.items.addAll(items);
    }

    public void add(
        final int position,
        @NonNull final Template item
    ) {
        self.items.add(position, item);
    }

    public void clear() {
        self.items.clear();
    }

    /**
     * {@link TemplateListAdapterItemViewHolder.Listener#onIconButtonClick(ImageButton, int)}
     */
    @Override
    public void onIconButtonClick(
        @NonNull final ImageButton iconButton,
        final int position
    ) {
        if (self.listener == null) {
            return;
        }

        self.listener.onMenuInfoClick(self, position);
    }

    /**
     * {@link TemplateListAdapterItemViewHolder.Listener#onClick(TemplateListAdapterItemViewHolder, int)}
     */
    @Override
    public void onClick(
        @NonNull final TemplateListAdapterItemViewHolder viewHolder,
        final int position
    ) {
        if (self.listener == null) {
            return;
        }

        self.listener.onItemClick(self, position);
    }
}
