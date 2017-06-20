package com.sobot.chat.widget;
 
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.sobot.chat.utils.ResourceUtils;

public class ThankDialog extends Dialog {
 
    public ThankDialog(Context context, int theme) {
        super(context, theme);
    }
 
    public ThankDialog(Context context) {
        super(context);
    }

    public static class Builder {
 
        private Context context;
        private String message;
 
        public Builder(Context context) {
            this.context = context;
        }
 
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }
 
        @SuppressWarnings("deprecation")
		public ThankDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ThankDialog dialog = new ThankDialog(context, ResourceUtils.getIdByName(context, "style", "sobot_Dialog"));
            dialog.setCanceledOnTouchOutside(false);
            View layout = inflater.inflate(ResourceUtils.getIdByName(context, "layout", "sobot_thank_dialog_layout"), null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            
            if (message != null) {
                ((TextView) layout.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_message"))).setText(message);
            } 
            dialog.setContentView(layout);
            return dialog;
        }
    }
}