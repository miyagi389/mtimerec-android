package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.List;

import miyagi389.android.apps.tr.domain.model.Events;

class EventsListAdapterDiffUtilCallback extends DiffUtil.Callback {

    private final List<Events> oldList;
    private final List<Events> newList;

    EventsListAdapterDiffUtilCallback(
        @NonNull final List<Events> oldList,
        @NonNull final List<Events> newList
    ) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(
        final int oldItemPosition,
        final int newItemPosition
    ) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(
        final int oldItemPosition,
        final int newItemPosition
    ) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
