package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import miyagi389.android.apps.tr.domain.model.Calendars;
import miyagi389.android.apps.tr.presentation.R;

class CalendarsChoiceAdapterItemViewHolder extends RecyclerView.ViewHolder {

    public interface Listener {

        void onClick(
            @NonNull CalendarsChoiceAdapterItemViewHolder viewHolder,
            int position
        );
    }

    private final RadioButton checked;
    private final TextView text1;
    private final TextView text2;

    CalendarsChoiceAdapterItemViewHolder(
        @NonNull final ViewGroup parent,
        @NonNull final LayoutInflater layoutInflater,
        @NonNull final Listener listener
    ) {
        super(layoutInflater.inflate(R.layout.calendars_choice_item, parent, false));
        this.checked = (RadioButton) itemView.findViewById(android.R.id.button1);
        this.text1 = (TextView) itemView.findViewById(android.R.id.text1);
        this.text2 = (TextView) itemView.findViewById(android.R.id.text2);
        super.itemView.setOnClickListener(v -> listener.onClick(this, getAdapterPosition()));
    }

    void onBindViewHolder(
        @NonNull final CalendarsChoiceAdapter adapter,
        final int position
    ) {
        final Calendars item = adapter.getItem(position);
        checked.setChecked(position == adapter.getItemChecked());
        text1.setText(item.getCalendarDisplayName());
        text2.setText(item.getAccountName());
    }
}
