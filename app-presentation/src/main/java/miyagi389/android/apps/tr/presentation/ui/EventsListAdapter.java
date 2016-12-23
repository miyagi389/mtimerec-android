package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.presentation.R;
import miyagi389.android.apps.tr.presentation.ui.widget.PopupWindowCompat;

class EventsListAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements EventsListAdapterItemViewHolder.Listener {

    public interface Listener {

        void onItemClick(
            @NonNull EventsListAdapter adapter,
            int position
        );

        void onMenuEditClick(
            @NonNull EventsListAdapter adapter,
            int position
        );

        void onMenuDeleteClick(
            @NonNull EventsListAdapter adapter,
            int position
        );
    }

    private final EventsListAdapter self = this;

    private final List<Events> items = new ArrayList<>();
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return self.items.size();
    }

    Events getItem(final int position) {
        return self.items.get(position);
    }

    void addAll(@NonNull final List<Events> items) {
        self.items.addAll(items);
    }

    public void add(
        final int position,
        @NonNull final Events item
    ) {
        self.items.add(position, item);
    }

    public void clear() {
        self.items.clear();
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

        final PopupMenu popup = new PopupMenu(iconButton.getContext(), iconButton);
        popup.getMenuInflater().inflate(R.menu.events_list_fragment_item, popup.getMenu());
        PopupWindowCompat.setForceShowIcon(popup);
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    self.listener.onMenuEditClick(self, position);
                    break;
                case R.id.menu_delete:
                    self.listener.onMenuDeleteClick(self, position);
                    break;
                default:
                    break;
            }
            return true;
        });

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
