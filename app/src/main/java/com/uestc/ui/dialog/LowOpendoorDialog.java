package com.uestc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.uestc.ui.activity.R;

/**
 * Created by Ryon on 2015/11/14.
 * email:shadycola@gmail.com
 * 这是低版本选择开园区，车闸，楼栋的对话框
 */
public class LowOpendoorDialog extends Dialog{



    public LowOpendoorDialog(Context context) {
        super(context);
    }

    public LowOpendoorDialog(Context context, int theme) {
        super(context,theme);
    }

    public static class Builder {
        private Context context;
        private View contentView;
        private Button gardenButton,buildingButton,carlockButton;

        private DialogInterface.OnClickListener gardenListener,buildingListener,carlockListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setGardenButton(DialogInterface.OnClickListener listener) {
            this.gardenListener = listener;
            return this;
        }

        public Builder setBuildingButton(DialogInterface.OnClickListener listener) {
            this.buildingListener = listener;
            return this;
        }

        public Builder setCarlockButton(DialogInterface.OnClickListener listener) {
            this.carlockListener = listener;
            return this;
        }

        public LowOpendoorDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final LowOpendoorDialog dialog = new LowOpendoorDialog(context);
            View layout = inflater.inflate(R.layout.low_opendoor_way, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the confirm button
            if (gardenButton != null) {
                if (gardenListener != null) {
                    ((Button) layout.findViewById(R.id.garden_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    gardenListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.garden_btn).setVisibility(
                        View.GONE);
            }

            if (buildingButton != null) {
                if (buildingListener != null) {
                    ((Button) layout.findViewById(R.id.building_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    buildingListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.building_btn).setVisibility(
                        View.GONE);
            }

            if (carlockButton != null) {
                if (carlockListener != null) {
                    ((Button) layout.findViewById(R.id.car_lock_btn))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    carlockListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.car_lock_btn).setVisibility(
                        View.GONE);
            }

            dialog.setContentView(layout);
            return dialog;
        }

}

//    private void setCustomDialog() {
//        View mView = LayoutInflater.from(getContext()).inflate(R.layout.low_opendoor_way,null);
//        gardenButton = (Button) findViewById(R.id.garden_btn);
//        gardenButton.setOnClickListener(this);
//        buildingButton = (Button) findViewById(R.id.building_btn);
//        buildingButton.setOnClickListener(this);
//        carlockButton = (Button) findViewById(R.id.car_lock_btn);
//        carlockButton.setOnClickListener(this);
//        super.setContentView(mView);
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent;
//        switch (v.getId()) {
//            case R.id.garden_btn:
//                intent = new Intent(getContext(),LowOpenActivity.class);
//                intent.putExtra("category", GARDEN);
//                getContext().startActivity(intent);
//                break;
//            case R.id.building_btn:
//                intent = new Intent(getContext(),LowOpenActivity.class);
//                intent.putExtra("category",BUILDING);
//                getContext().startActivity(intent);
//                break;
//            case R.id.car_lock_btn:
//                intent = new Intent(getContext(),LowOpenActivity.class);
//                intent.putExtra("category",CARLOCK);
//                getContext().startActivity(intent);
//                break;
//        }
//    }
}
