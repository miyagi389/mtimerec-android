package miyagi389.android.apps.tr.presentation.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import miyagi389.android.apps.tr.presentation.R;

class TemplateListAdapterFooterViewHolder extends RecyclerView.ViewHolder {

    TemplateListAdapterFooterViewHolder(
        @NonNull final ViewGroup parent,
        @NonNull final LayoutInflater layoutInflater
    ) {
        super(layoutInflater.inflate(R.layout.template_list_fragment_footer, parent, false));
    }
}
