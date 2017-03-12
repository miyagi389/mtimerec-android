package miyagi389.android.apps.tr.presentation.ui.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

/**
 * {@link DialogFragment} like {@link AlertDialog.Builder}
 */
@SuppressWarnings("Convert2Lambda")
public class AlertDialogFragment extends DialogFragment {

    @SuppressWarnings("WeakerAccess")
    public interface OnItemClickListener {
        void onItemClick(
            @NonNull AlertDialogFragment dialog,
            int which
        );
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnMultiItemClickListener {
        void onItemClick(
            @NonNull AlertDialogFragment dialog,
            int which,
            boolean isChecked
        );
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnClickPositiveListener {
        void onClickPositive(@NonNull AlertDialogFragment dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnClickNeutralListener {
        void onClickNeutral(@NonNull AlertDialogFragment dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnClickNegativeListener {
        void onClickNegative(@NonNull AlertDialogFragment dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnClickListener extends OnClickPositiveListener, OnClickNeutralListener, OnClickNegativeListener {
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnCancelListener {
        void onCancel(@NonNull AlertDialogFragment dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnDismissListener {
        void onDismiss(@NonNull AlertDialogFragment dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnKeyListener {
        boolean onKey(
            @NonNull AlertDialogFragment dialog,
            int keyCode,
            @NonNull KeyEvent event
        );
    }

    @SuppressWarnings("WeakerAccess")
    public interface ViewDelegate {
        @NonNull
        View getView(@NonNull AlertDialogFragment dialog);
    }

    @SuppressWarnings("WeakerAccess")
    public interface ListAdapterDelegate {
        @NonNull
        ListAdapter getListAdapter(@NonNull AlertDialogFragment dialog);
    }

    private static final String ARG_THEME = "theme";
    private static final String ARG_ICON = "icon";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private static final String ARG_ITEMS = "items";
    private static final String ARG_ITEMS_LISTENER = "itemsListener";

    private static final String ARG_ADAPTER = "adapter";
    private static final String ARG_ADAPTER_LISTENER = "adapterListener";

    private static final String ARG_CHECKED_ITEMS = "checkedItems";
    private static final String ARG_MULTI_CHOICE_ITEMS = "multiChoiceItems";
    private static final String ARG_MULTI_CHOICE_LISTENER = "multiChoiceListener";

    private static final String ARG_CHECKED_ITEM = "checkedItem";
    private static final String ARG_SINGLE_CHOICE_ITEMS = "singleChoiceItems";
    private static final String ARG_SINGLE_CHOICE_ADAPTER = "singleChoiceAdapter";
    private static final String ARG_SINGLE_CHOICE_LISTENER = "singleChoiceListener";

    private static final String ARG_NEGATIVE_BUTTON = "negative";
    private static final String ARG_NEGATIVE_BUTTON_LISTENER = "negativeListener";

    private static final String ARG_NEUTRAL_BUTTON = "neutral";
    private static final String ARG_NEUTRAL_BUTTON_LISTENER = "neutralListener";

    private static final String ARG_POSITIVE_BUTTON = "positive";
    private static final String ARG_POSITIVE_BUTTON_LISTENER = "positiveListener";

    private static final String ARG_CANCEL_LISTENER = "cancelListener";
    private static final String ARG_DISMISS_LISTENER = "dismissListener";
    private static final String ARG_KEY_LISTENER = "keyListener";

    private static final String ARG_CUSTOM_VIEW = "customView";


    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_FRAGMENT = "fragment:";

    private static final int VALUE_NULL = 0;
    private static final int VALUE_TRUE = 1;
    private static final int VALUE_FALSE = 2;

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class Builder {

        private final Context context;
        private final Bundle arguments = new Bundle();
        private int cancelable = VALUE_NULL;

        public Builder(@NonNull final Context context) {
            this(context, VALUE_NULL);
        }

        public Builder(
            @NonNull final Context context,
            final int theme
        ) {
            this.context = context.getApplicationContext();
            this.arguments.putInt(ARG_THEME, theme);
        }

        @NonNull
        public AlertDialogFragment create() {
            final AlertDialogFragment f = new AlertDialogFragment();
            f.setArguments(arguments);
            if (cancelable != VALUE_NULL) {
                f.setCancelable(cancelable == VALUE_TRUE);
            }
            return f;
        }

        public void show(
            @NonNull final FragmentManager manager,
            @Nullable final String tag
        ) {
            create().show(manager, tag);
        }

        public void show(
            @NonNull final FragmentTransaction transaction,
            @NonNull final String tag
        ) {
            create().show(transaction, tag);
        }

        @NonNull
        public Builder setCancelable(final boolean cancelable) {
            this.cancelable = cancelable ? VALUE_TRUE : VALUE_FALSE;
            return this;
        }

        @NonNull
        public Builder setIcon(@DrawableRes final int iconId) {
            arguments.putInt(ARG_ICON, iconId);
            return this;
        }

        //
        // Title
        //

        @NonNull
        public Builder setTitle(@Nullable final CharSequence title) {
            arguments.putCharSequence(ARG_TITLE, title);
            return this;
        }

        @NonNull
        public Builder setTitle(@StringRes final int resId) {
            arguments.putCharSequence(ARG_TITLE, context.getText(resId));
            return this;
        }


        //
        // Message
        //

        public Builder setMessage(final CharSequence message) {
            arguments.putCharSequence(ARG_MESSAGE, message);
            return this;
        }

        public Builder setMessage(final int resId) {
            return setMessage(context.getText(resId));
        }


        //
        // CustomView
        //

        public <T extends Fragment & ViewDelegate> Builder setView(final T delegate) {
            putArgument(ARG_CUSTOM_VIEW, delegate);
            return this;
        }

        public <T extends Activity & ViewDelegate> Builder setView(final T delegate) {
            putArgument(ARG_CUSTOM_VIEW, delegate);
            return this;
        }


        //
        // List
        //

        public <T extends Fragment & OnItemClickListener> Builder setItems(
            final CharSequence[] items,
            final T listener
        ) {
            arguments.putCharSequenceArray(ARG_ITEMS, items);
            putArgument(ARG_ITEMS_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnItemClickListener> Builder setItems(
            final int itemResId,
            final T listener
        ) {
            return setItems(context.getResources().getTextArray(itemResId), listener);
        }

        public <T extends Activity & OnItemClickListener> Builder setItems(
            final CharSequence[] items,
            final T listener
        ) {
            arguments.putCharSequenceArray(ARG_ITEMS, items);
            putArgument(ARG_ITEMS_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnItemClickListener> Builder setItems(
            final int itemResId,
            final T listener
        ) {
            return setItems(context.getResources().getTextArray(itemResId), listener);
        }


        //
        // Adapter
        //

        public <TAdapter extends Fragment & ListAdapterDelegate,
            TListener extends Fragment & OnItemClickListener> Builder setAdapter(
            final TAdapter adapter,
            final TListener listener
        ) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
            TListener extends Activity & OnItemClickListener> Builder setAdapter(
            final TAdapter adapter,
            final TListener listener
        ) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Fragment & ListAdapterDelegate,
            TListener extends Activity & OnItemClickListener> Builder setAdapter(
            final TAdapter adapter,
            final TListener listener
        ) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
            TListener extends Fragment & OnItemClickListener> Builder setAdapter(
            final TAdapter adapter,
            final TListener listener
        ) {
            putArgument(ARG_ADAPTER, adapter);
            putArgument(ARG_ADAPTER_LISTENER, listener);
            return this;
        }


        //
        // MultiChoiceItems
        //

        public <T extends Fragment & OnMultiItemClickListener> Builder setMultiChoiceItems(
            final CharSequence[] items,
            final boolean[] checkedItems,
            final T listener
        ) {
            arguments.putCharSequenceArray(ARG_MULTI_CHOICE_ITEMS, items);
            arguments.putBooleanArray(ARG_CHECKED_ITEMS, checkedItems);
            putArgument(ARG_MULTI_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnMultiItemClickListener> Builder setMultiChoiceItems(
            final int itemResId,
            final boolean[] checkedItems,
            final T listener
        ) {
            return setMultiChoiceItems(context.getResources().getTextArray(itemResId), checkedItems, listener);
        }

        public <T extends Activity & OnMultiItemClickListener> Builder setMultiChoiceItems(
            final CharSequence[] items,
            final boolean[] checkedItems,
            final T listener
        ) {
            arguments.putCharSequenceArray(ARG_MULTI_CHOICE_ITEMS, items);
            arguments.putBooleanArray(ARG_CHECKED_ITEMS, checkedItems);
            putArgument(ARG_MULTI_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnMultiItemClickListener> Builder setMultiChoiceItems(
            final int itemResId,
            final boolean[] checkedItems,
            final T listener
        ) {
            return setMultiChoiceItems(context.getResources().getTextArray(itemResId), checkedItems, listener);
        }


        //
        // SingleChoiceItems
        //

        public <T extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
            final CharSequence[] items,
            final int checkedItem,
            final T listener
        ) {
            arguments.putCharSequenceArray(ARG_SINGLE_CHOICE_ITEMS, items);
            arguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
            final int itemResId,
            final int checkedItem,
            final T listener
        ) {
            return setSingleChoiceItems(context.getResources().getTextArray(itemResId), checkedItem, listener);
        }

        public <T extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
            final CharSequence[] items,
            final int checkedItem,
            final T listener
        ) {
            arguments.putCharSequenceArray(ARG_SINGLE_CHOICE_ITEMS, items);
            arguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
            final int itemResId,
            final int checkedItem,
            final T listener
        ) {
            return setSingleChoiceItems(context.getResources().getTextArray(itemResId), checkedItem, listener);
        }

        public <TAdapter extends Fragment & ListAdapterDelegate,
            TListener extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
            final TAdapter adapter,
            final int checkedItem,
            final TListener listener
        ) {
            arguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
            TListener extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
            final TAdapter adapter,
            final int checkedItem,
            final TListener listener
        ) {
            arguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Fragment & ListAdapterDelegate,
            TListener extends Activity & OnItemClickListener> Builder setSingleChoiceItems(
            final TAdapter adapter,
            final int checkedItem,
            final TListener listener
        ) {
            arguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }

        public <TAdapter extends Activity & ListAdapterDelegate,
            TListener extends Fragment & OnItemClickListener> Builder setSingleChoiceItems(
            final TAdapter adapter,
            final int checkedItem,
            final TListener listener
        ) {
            arguments.putInt(ARG_CHECKED_ITEM, checkedItem);
            putArgument(ARG_SINGLE_CHOICE_ADAPTER, adapter);
            putArgument(ARG_SINGLE_CHOICE_LISTENER, listener);
            return this;
        }


        //
        // NegativeButton
        //

        public <T extends Fragment & OnClickNegativeListener> Builder setNegativeButton(
            final CharSequence text,
            final T listener
        ) {
            arguments.putCharSequence(ARG_NEGATIVE_BUTTON, text);
            putArgument(ARG_NEGATIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnClickNegativeListener> Builder setNegativeButton(
            final int resId,
            final T listener
        ) {
            return setNegativeButton(context.getText(resId), listener);
        }

        public <T extends Activity & OnClickNegativeListener> Builder setNegativeButton(
            final CharSequence text,
            final T listener
        ) {
            arguments.putCharSequence(ARG_NEGATIVE_BUTTON, text);
            putArgument(ARG_NEGATIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnClickNegativeListener> Builder setNegativeButton(
            final int resId,
            final T listener
        ) {
            return setNegativeButton(context.getText(resId), listener);
        }

        public Builder setNegativeButton(final CharSequence text) {
            arguments.putCharSequence(ARG_NEGATIVE_BUTTON, text);
            return this;
        }

        public Builder setNegativeButton(final int resId) {
            return setNegativeButton(context.getText(resId));
        }


        //
        // NeutralButton
        //

        public <T extends Fragment & OnClickNeutralListener> Builder setNeutralButton(
            final CharSequence text,
            final T listener
        ) {
            arguments.putCharSequence(ARG_NEUTRAL_BUTTON, text);
            putArgument(ARG_NEUTRAL_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnClickNeutralListener> Builder setNeutralButton(
            final int resId,
            final T listener
        ) {
            return setNeutralButton(context.getText(resId), listener);
        }

        public <T extends Activity & OnClickNeutralListener> Builder setNeutralButton(
            final CharSequence text,
            final T listener
        ) {
            arguments.putCharSequence(ARG_NEUTRAL_BUTTON, text);
            putArgument(ARG_NEUTRAL_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnClickNeutralListener> Builder setNeutralButton(
            final int resId,
            final T listener
        ) {
            return setNeutralButton(context.getText(resId), listener);
        }

        public Builder setNeutralButton(final CharSequence text) {
            arguments.putCharSequence(ARG_NEUTRAL_BUTTON, text);
            return this;
        }

        public Builder setNeutralButton(final int resId) {
            return setNeutralButton(context.getText(resId));
        }


        //
        // PositiveButton
        //

        public <T extends Fragment & OnClickPositiveListener> Builder setPositiveButton(
            final CharSequence text,
            final T listener
        ) {
            arguments.putCharSequence(ARG_POSITIVE_BUTTON, text);
            putArgument(ARG_POSITIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Fragment & OnClickPositiveListener> Builder setPositiveButton(
            final int resId,
            final T listener
        ) {
            return setPositiveButton(context.getText(resId), listener);
        }

        public <T extends Activity & OnClickPositiveListener> Builder setPositiveButton(
            final CharSequence text,
            final T listener
        ) {
            arguments.putCharSequence(ARG_POSITIVE_BUTTON, text);
            putArgument(ARG_POSITIVE_BUTTON_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnClickPositiveListener> Builder setPositiveButton(
            final int resId,
            final T listener
        ) {
            return setPositiveButton(context.getText(resId), listener);
        }

        public Builder setPositiveButton(final CharSequence text) {
            arguments.putCharSequence(ARG_POSITIVE_BUTTON, text);
            return this;
        }

        public Builder setPositiveButton(final int resId) {
            return setPositiveButton(context.getText(resId));
        }


        //
        // CancelListener
        //

        public <T extends Fragment & OnCancelListener> Builder setOnCancelListener(final T listener) {
            putArgument(ARG_CANCEL_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnCancelListener> Builder setOnCancelListener(final T listener) {
            putArgument(ARG_CANCEL_LISTENER, listener);
            return this;
        }


        //
        // DismissListener
        //

        public <T extends Fragment & OnDismissListener> Builder setOnDismissListener(final T listener) {
            putArgument(ARG_DISMISS_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnDismissListener> Builder setOnDismissListener(final T listener) {
            putArgument(ARG_DISMISS_LISTENER, listener);
            return this;
        }


        //
        // KeyListener
        //

        public <T extends Fragment & OnKeyListener> Builder setOnKeyListener(final T listener) {
            putArgument(ARG_KEY_LISTENER, listener);
            return this;
        }

        public <T extends Activity & OnKeyListener> Builder setOnKeyListener(final T listener) {
            putArgument(ARG_KEY_LISTENER, listener);
            return this;
        }


        //
        // Helper
        //

        private void putArgument(
            final String key,
            final Fragment fragment
        ) {
            if (fragment != null && fragment.getTag() != null)
                arguments.putString(key, TAG_FRAGMENT + fragment.getTag());
        }

        private void putArgument(
            final String key,
            final Activity activity
        ) {
            if (activity != null)
                arguments.putString(key, TAG_ACTIVITY);
        }
    }


    public AlertDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Bundle args = getArguments();
        final int theme = args.getInt(ARG_THEME);

        final AlertDialog.Builder builder;
        if (theme == VALUE_NULL || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(getActivity());
        } else {
            builder = newDialogBuilder(theme);
        }

        final CharSequence title = args.getCharSequence(ARG_TITLE);
        if (title != null)
            builder.setTitle(title);

        final CharSequence message = args.getCharSequence(ARG_MESSAGE);
        if (message != null)
            builder.setMessage(message);

        final int iconId = args.getInt(ARG_ICON, VALUE_NULL);
        if (iconId != VALUE_NULL) {
            builder.setIcon(iconId);
        }

        // View
        setCustomView(builder);

        // List
        setItems(builder);
        setAdapter(builder);
        setMultiChoiceItems(builder);
        setSingleChoiceItems(builder);

        // Buttons
        setPositiveButton(builder);
        setNegativeButton(builder);
        setNeutralButton(builder);

        return builder.create();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final OnKeyListener listener = findListenerByTag(OnKeyListener.class, ARG_KEY_LISTENER);
        if (listener != null) {
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(
                    final DialogInterface dialog,
                    final int keyCode,
                    final KeyEvent event
                ) {
                    return listener.onKey(AlertDialogFragment.this, keyCode, event);
                }
            });
        }
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        super.onCancel(dialog);

        final OnCancelListener listener = findListenerByTag(
            OnCancelListener.class, ARG_CANCEL_LISTENER);
        if (listener != null)
            listener.onCancel(this);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        final OnDismissListener listener = findListenerByTag(
            OnDismissListener.class, ARG_DISMISS_LISTENER);
        if (listener != null)
            listener.onDismiss(this);
    }

    private void setCustomView(final AlertDialog.Builder builder) {
        final ViewDelegate delegate = findListenerByTag(
            ViewDelegate.class, ARG_CUSTOM_VIEW);
        if (delegate == null)
            return;
        builder.setView(delegate.getView(this));
    }

    private void setAdapter(final AlertDialog.Builder builder) {
        final ListAdapterDelegate delegate = findListenerByTag(ListAdapterDelegate.class, ARG_ADAPTER);
        if (delegate == null)
            return;

        builder.setAdapter(delegate.getListAdapter(this), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                final DialogInterface dialog,
                final int which
            ) {
                final OnItemClickListener listener = findListenerByTag(
                    OnItemClickListener.class, ARG_ADAPTER_LISTENER);
                if (listener != null) {
                    listener.onItemClick(AlertDialogFragment.this, which);
                }
            }
        });
    }

    private void setSingleChoiceItems(final AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence[] items = args.getCharSequenceArray(ARG_SINGLE_CHOICE_ITEMS);
        final int checkedItem = args.getInt(ARG_CHECKED_ITEM);
        if (items != null) {
            builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(
                    final DialogInterface dialog,
                    final int which
                ) {
                    final OnItemClickListener listener = findListenerByTag(
                        OnItemClickListener.class, ARG_SINGLE_CHOICE_LISTENER);
                    if (listener != null) {
                        listener.onItemClick(AlertDialogFragment.this, which);
                    }
                }
            });
            return;
        }

        final ListAdapterDelegate delegate = findListenerByTag(
            ListAdapterDelegate.class, ARG_SINGLE_CHOICE_ADAPTER);
        if (delegate == null)
            return;

        builder.setSingleChoiceItems(delegate.getListAdapter(this),
            checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(
                    final DialogInterface dialog,
                    final int which
                ) {
                    final OnItemClickListener listener = findListenerByTag(
                        OnItemClickListener.class, ARG_SINGLE_CHOICE_LISTENER);
                    if (listener != null) {
                        listener.onItemClick(AlertDialogFragment.this, which);
                    }
                }
            }
        );
    }

    private void setPositiveButton(final AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence positiveButtonText = args.getCharSequence(ARG_POSITIVE_BUTTON);
        if (positiveButtonText == null)
            return;

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                final DialogInterface dialog,
                final int which
            ) {
                final OnClickPositiveListener listener = findListenerByTag(
                    OnClickPositiveListener.class,
                    ARG_POSITIVE_BUTTON_LISTENER
                );
                if (listener != null) {
                    listener.onClickPositive(AlertDialogFragment.this);
                }
            }
        });
    }

    private void setNeutralButton(final AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence naturalButtonText = args.getCharSequence(ARG_NEUTRAL_BUTTON);
        if (naturalButtonText == null)
            return;

        builder.setNeutralButton(naturalButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                final DialogInterface dialog,
                final int which
            ) {
                final OnClickNeutralListener listener = findListenerByTag(
                    OnClickNeutralListener.class,
                    ARG_NEUTRAL_BUTTON_LISTENER
                );
                if (listener != null) {
                    listener.onClickNeutral(AlertDialogFragment.this);
                }
            }
        });
    }

    private void setNegativeButton(final AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence negativeButtonText = args.getCharSequence(ARG_NEGATIVE_BUTTON);
        if (negativeButtonText == null)
            return;

        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                final DialogInterface dialog,
                final int which
            ) {
                final OnClickNegativeListener listener = findListenerByTag(
                    OnClickNegativeListener.class,
                    ARG_NEGATIVE_BUTTON_LISTENER
                );
                if (listener != null) {
                    listener.onClickNegative(AlertDialogFragment.this);
                }
            }
        });
    }

    private void setItems(final AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence[] items = args.getCharSequenceArray(ARG_ITEMS);
        if (items == null)
            return;

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(
                final DialogInterface dialog,
                final int which
            ) {
                final OnItemClickListener listener = findListenerByTag(
                    OnItemClickListener.class,
                    ARG_ITEMS_LISTENER
                );
                if (listener != null) {
                    listener.onItemClick(AlertDialogFragment.this, which);
                }
            }
        });
    }

    private void setMultiChoiceItems(final AlertDialog.Builder builder) {
        final Bundle args = getArguments();
        final CharSequence[] items = args.getCharSequenceArray(ARG_MULTI_CHOICE_ITEMS);
        final boolean[] checked = args.getBooleanArray(ARG_CHECKED_ITEMS);
        if (items == null || checked == null || items.length != checked.length)
            return;

        builder.setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(
                final DialogInterface dialog,
                final int which,
                final boolean isChecked
            ) {
                final OnMultiItemClickListener listener = findListenerByTag(
                    OnMultiItemClickListener.class,
                    ARG_MULTI_CHOICE_LISTENER
                );
                if (listener != null) {
                    listener.onItemClick(AlertDialogFragment.this, which, isChecked);
                }
            }
        });
    }

    @TargetApi(11)
    private AlertDialog.Builder newDialogBuilder(final int theme) {
        return new AlertDialog.Builder(getActivity(), theme);
    }

    private <T> T findListenerByTag(
        final Class<T> clazz,
        final String argName
    ) {
        final String target = getArguments().getString(argName);
        if (target == null) {
            return null;
        } else if (TAG_ACTIVITY.equals(target)) {
            return findListener(clazz, getActivity());
        } else if (target.startsWith(TAG_FRAGMENT)) {
            return findListener(clazz, getFragmentManager().findFragmentByTag(
                target.substring(TAG_FRAGMENT.length())));
        } else {
            return null;
        }
    }

    private <T> T findListener(
        final Class<T> clazz,
        final Object object
    ) {
        if (object != null && clazz.isInstance(object)) {
            //noinspection unchecked
            return (T) object;
        }
        return null;
    }
}
