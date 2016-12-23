package miyagi389.android.apps.tr.presentation.ui.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IconWithItemAdapter extends ArrayAdapter<IconWithItemAdapter.Item> {

    public static class Item {

        @SuppressWarnings("WeakerAccess")
        @DrawableRes
        public int iconResId;

        public String text;

        @SuppressWarnings("unused")
        public Item() {
        }

        public Item(
            final int iconResId,
            @NonNull final String text
        ) {
            this.iconResId = iconResId;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final Context context;

    public IconWithItemAdapter(
        @NonNull final Context context,
        @NonNull final Item[] items
    ) {
        super(
            context,
            android.R.layout.select_dialog_item,
            android.R.id.text1,
            items
        );
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(
        final int position,
        final View convertView,
        @NonNull final ViewGroup parent
    ) {
        //User super class to create the View
        final View v = super.getView(position, convertView, parent);
        final TextView tv = (TextView) v.findViewById(android.R.id.text1);

        final Item item = getItem(position);
        if (item == null) {
            return v;
        }

        //Put the image on the TextView
        tv.setCompoundDrawablesWithIntrinsicBounds(item.iconResId, 0, 0, 0);

        //Add margin between image and text (support various screen densities)
        final int padding = (int) (16 * context.getResources().getDisplayMetrics().density + 0.5f);
        tv.setCompoundDrawablePadding(padding);

        return v;
    }
}
