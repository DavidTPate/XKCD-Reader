package com.davidtpate.xkcdviewer.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.davidtpate.xkcdviewer.R;
import com.davidtpate.xkcdviewer.util.Strings;

public class JumpToDialogFragment extends DialogFragment implements
    TextView.OnEditorActionListener {
    @InjectView(R.id.tv_comic_number_label) TextView comidNumberLabel;
    @InjectView(R.id.et_comic_number) EditText comicNumber;

    public interface JumpToDialogListener {
        void onJumpTo(int jumpToValue);
    }

    public static JumpToDialogFragment newInstance() {
        return new JumpToDialogFragment();
    }

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(LayoutInflater, android.view.ViewGroup, android.os.Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     *
     * <p>This method will be called after {@link #onCreate(android.os.Bundle)} and
     * before {@link #onCreateView(LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.  The
     * default implementation simply instantiates and returns a {@link android.app.Dialog}
     * class.
     *
     * <p><em>Note: DialogFragment own the {@link android.app.Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link android.app.Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
     * To find out about these events, override {@link #onCancel(DialogInterface)}
     * and {@link #onDismiss(DialogInterface)}.</p>
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        View dialogView = lf.inflate(R.layout.jump_dialog_fragment, null);
        ButterKnife.inject(this, dialogView);
        comicNumber.setOnEditorActionListener(this);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
            .setTitle("Jump To...")
            .setPositiveButton("Jump", null)
            .setNegativeButton("Cancel", null)
            .create();

        // We have to override setOnShowListener here (min API level 8) in order to validate
        // the inputs before closing the dialog. Just overriding setPositiveButton closes the
        // automatically when the button is pressed
        dialog.setOnShowListener(getDialogOnShowListener());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    /**
     * We have to override setOnShowListener here (min API level 8) in order to validate
     * the inputs before closing the dialog. Just overriding setPositiveButton closes the
     * automatically when the button is pressed
     * @return The onShowListener for the AlertDialog
     */
    private DialogInterface.OnShowListener getDialogOnShowListener() {
        return new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button jumpTo = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                jumpTo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        doGo();
                    }
                });

            }
        };
    }

    /**
     * Perform the login if the user presses the IME key from the password field
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doGo();
            return true;
        }
        return false;
    }

    public void doGo() {
        if (comicNumber != null) {
            String value = comicNumber.getText().toString();
            if (!Strings.isEmpty(value) && Strings.isValidComicNumber(value)) {
                ((JumpToDialogListener) getActivity()).onJumpTo(Integer.valueOf(value));
                this.dismiss();
            } else {
                comicNumber.setError("You must enter a positive integer.");
            }
        }
    }
}
