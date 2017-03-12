package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import miyagi389.android.apps.tr.domain.model.Calendars;

@SuppressWarnings("WeakerAccess")
class CalendarsChoiceAdapter
    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements CalendarsChoiceAdapterItemViewHolder.Listener {

    public interface Listener {

        void onItemClick(
            @NonNull CalendarsChoiceAdapter adapter,
            int position
        );
    }

    public static final int UNSELECTED_ITEM_POSITION = -1;

    private final CalendarsChoiceAdapter self = this;

    private List<Calendars> items = Collections.emptyList();
    private final Listener listener;
    private final LayoutInflater layoutInflater;

    private int selectedItemPosition = UNSELECTED_ITEM_POSITION;

    CalendarsChoiceAdapter(
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
        return new CalendarsChoiceAdapterItemViewHolder(parent, self.layoutInflater, this);
    }

    @Override
    public void onBindViewHolder(
        final RecyclerView.ViewHolder holder,
        final int position
    ) {
        ((CalendarsChoiceAdapterItemViewHolder) holder).onBindViewHolder(self, position);
    }

    @Override
    public long getItemId(final int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return self.items.size();
    }

    Calendars getItem(final int position) {
        return self.items.get(position);
    }

    @NonNull
    List<Calendars> getItems() {
        return self.items;
    }

    void setItems(@NonNull final List<Calendars> items) {
        self.items = items;
    }

    public void setItemChecked(int position) {
        if (selectedItemPosition != position) {
            selectedItemPosition = position;
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public int getItemChecked() {
        return selectedItemPosition;
    }

    public void clearChoices() {
        selectedItemPosition = UNSELECTED_ITEM_POSITION;
        notifyItemRangeChanged(0, getItemCount());
    }

    public int getPositionAtId(final long id) {
        for (int position = 0; position < getItemCount(); position++) {
            final Calendars o = getItem(position);
            if (o == null) {
                continue;
            }
            if (id == o.getId()) {
                return position;
            }
        }
        return -1;
    }

    /**
     * {@link CalendarsChoiceAdapterItemViewHolder.Listener#onClick(CalendarsChoiceAdapterItemViewHolder, int)}
     */
    @Override
    public void onClick(
        @NonNull final CalendarsChoiceAdapterItemViewHolder viewHolder,
        final int position
    ) {
        setItemChecked(position);
        if (this.listener != null) {
            this.listener.onItemClick(this, position);
        }
    }
}
