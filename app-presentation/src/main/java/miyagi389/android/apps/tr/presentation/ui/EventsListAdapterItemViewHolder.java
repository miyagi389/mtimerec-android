package miyagi389.android.apps.tr.presentation.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import miyagi389.android.apps.tr.domain.model.Events;
import miyagi389.android.apps.tr.presentation.R;

class EventsListAdapterItemViewHolder extends RecyclerView.ViewHolder {

    public interface Listener {

        void onIconButtonClick(
            @NonNull ImageButton iconButton,
            int position
        );

        void onClick(
            @NonNull EventsListAdapterItemViewHolder viewHolder,
            int position
        );
    }

    private static final int DATE_FORMAT = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME;

    private final TextView summaryText;
    private final ImageButton iconButton;

    EventsListAdapterItemViewHolder(
        @NonNull final ViewGroup parent,
        @NonNull final LayoutInflater layoutInflater,
        @NonNull final Listener listener
    ) {
        super(layoutInflater.inflate(R.layout.events_list_fragment_item, parent, false));
        this.summaryText = (TextView) itemView.findViewById(android.R.id.text1);
        this.iconButton = (ImageButton) itemView.findViewById(android.R.id.icon);
        this.iconButton.setOnClickListener(v -> listener.onIconButtonClick(this.iconButton, getAdapterPosition()));
        super.itemView.setOnClickListener(v -> listener.onClick(this, getAdapterPosition()));
    }

    void onBindViewHolder(
        @NonNull final EventsListAdapter adapter,
        final int position
    ) {
        final Events item = adapter.getItem(position);
        summaryText.setText(formatDtStartAndDtEnd(itemView.getContext(), item));
    }

    @NonNull
    private static String formatDtStartAndDtEnd(
        @NonNull final Context context,
        @NonNull final Events item
    ) {
        if (item.getDtStart() != null && item.getDtEnd() != null) {
            return DateUtils.formatDateRange(
                context,
                item.getDtStart().getTime(),
                item.getDtEnd().getTime(),
                DATE_FORMAT
            );
        } else {
            return "";
        }
    }
}
