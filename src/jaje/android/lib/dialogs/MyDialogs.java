package jaje.android.lib.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class MyDialogs {

	public static class Input {
		public static EditText input;

		public static String getInput() {
			return input.getText().toString();
		}

		public static void show(Context context, String title, String body,
				int okTextID,
				DialogInterface.OnClickListener positiveButtonOnClickListener,
				int canceltextID,
				DialogInterface.OnClickListener negativeButtonOnClickListener) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			alert.setTitle(title);
			alert.setMessage(body);

			input = new EditText(context);
			alert.setView(input);
			alert.setPositiveButton(okTextID, positiveButtonOnClickListener);
			alert.setNegativeButton(canceltextID, negativeButtonOnClickListener);
			alert.show();
		}
	}

	public static class Confirm {
		public static void show(Context context, String title, String body,
				int yesTextID,
				DialogInterface.OnClickListener positiveButtonOnClickListener,
				int noTextID,
				DialogInterface.OnClickListener negativeButtonOnClickListener) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			alert.setTitle(title);
			alert.setMessage(body);

			alert.setPositiveButton(yesTextID, positiveButtonOnClickListener);

			alert.setNegativeButton(noTextID, negativeButtonOnClickListener);

			alert.show();
		}
	}

	public static class Info {
		public static void show(Context context, String title, String body,
				int okTextID) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			alert.setTitle(title);
			alert.setMessage(body);

			alert.show();
		}
	}

	public static class SingleChoice {
		public static void show(Context context, String title,
				CharSequence[] options,
				DialogInterface.OnClickListener onSelectListener) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle(title);
			int selected = -1;
			alert.setSingleChoiceItems(options, selected, onSelectListener);
			alert.show();
		}
	}
}
