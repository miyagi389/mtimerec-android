package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Collections;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Events;

class EventsListAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements EventsListAdapterItemViewHolder.Listener {

    public interface Listener {

        void onItemClick(
            @NonNull EventsListAdapter adapter,
            int position
        );

        void onMenuInfoClick(
            @NonNull EventsListAdapter adapter,
            int position
        );
    }

    private final EventsListAdapter self = this;

    private List<Events> items = Collections.emptyList();
    private final Listener listener;
    private final LayoutInflater layoutInflater;

    EventsListAdapter(
        @NonNull final Context context,
        @Nullable final Listener listener
    ) {
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
        final ViewGroup parent,
        final int viewType
    ) {
        return new EventsListAdapterItemViewHolder(parent, self.layoutInflater, self);
    }

    @Override
    public void onBindViewHolder(
        final RecyclerView.ViewHolder holder,
        final int position
    ) {
        ((EventsListAdapterItemViewHolder) holder).onBindViewHolder(self, position);
    }

    @Override
    public long getItemId(final int position) {
        final Events item = getItem(position);
        if (item == null) {
            return RecyclerView.NO_ID;
        }
        return item.getId();
    }

    @Override
    public int getItemCount() {
        return self.items.size();
    }

    Events getItem(final int position) {
        return self.items.get(position);
    }

    @NonNull
    List<Events> getItems() {
        return self.items;
    }

    void setItems(@NonNull final List<Events> items) {
        self.items = items;
    }

    /**
     * {@link EventsListAdapterItemViewHolder.Listener#onIconButtonClick(ImageButton, int)}
     */
    @Override
    public void onIconButtonClick(
        @NonNull final ImageButton iconButton,
        final int position
    ) {
        if (self.listener == null) {
            return;
        }

        self.listener.onItemClick(self, position);
    }

    /**
     * {@link EventsListAdapterItemViewHolder.Listener#onClick(EventsListAdapterItemViewHolder, int)}
     */
    @Override
    public void onClick(
        @NonNull final EventsListAdapterItemViewHolder viewHolder,
        final int position
    ) {
        if (self.listener == null) {
            return;
        }

        self.listener.onItemClick(self, position);
    }
}
