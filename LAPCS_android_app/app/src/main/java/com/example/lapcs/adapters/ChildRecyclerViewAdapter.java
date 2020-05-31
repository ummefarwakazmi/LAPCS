package com.example.lapcs.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapcs.R;
import com.example.lapcs.Utils.ServiceUtils;
import com.example.lapcs.models.Child;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.example.lapcs.AppConsts.TAG;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.ChildViewHolder> {

    private Context mContext;
    private List<Child> MyChildList;

    public ChildRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public ChildRecyclerViewAdapter(Context mContext, List<Child> myChildList) {
        this.mContext = mContext;
        MyChildList = myChildList;
    }

    @NonNull
    @Override
    public ChildRecyclerViewAdapter.ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_card, parent, false);

        return new ChildRecyclerViewAdapter.ChildViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildRecyclerViewAdapter.ChildViewHolder holder, int position) {

        Child Child = MyChildList.get(position);
        holder.ChildName.setText(Child.getChildName());
        holder.ChildIMEI.setText(Child.getChildDevice());
        holder.childImage.setImageResource(R.drawable.ic_phonelink_setup_black_24dp);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return MyChildList.size();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);


        Toast.makeText(mContext, "Just Clicked on  showPopupMenu" + " and Position is " + position, Toast.LENGTH_SHORT).show();

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_children, popup.getMenu());
        MenuCompat.setGroupDividerEnabled(popup.getMenu(), true);
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {

        public TextView ChildName;
        public TextView ChildIMEI;
        public ImageView childImage, overflow;
        private CardView mCardViewMain;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            ChildName = (TextView) itemView.findViewById(R.id.tv_childName);
            ChildIMEI = (TextView) itemView.findViewById(R.id.tv_childIMEI);
            childImage = (ImageView) itemView.findViewById(R.id.childImage);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            mCardViewMain = (CardView) itemView.findViewById(R.id.card_view);
            mCardViewMain.setRadius(30);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    TextView ChildName;
                    TextView ChildIMEI;

                    ChildName = (TextView) v.findViewById(R.id.tv_childName);
                    String selectedName = (String) ChildName.getText();

                    ChildIMEI = (TextView) v.findViewById(R.id.tv_childIMEI);
                    String childIMEI = (String) ChildIMEI.getText();

                    Toast.makeText(mContext, "Long Clicked on Item " + " and Position is " + getAdapterPosition() + " and selectedName is =" + selectedName, Toast.LENGTH_SHORT).show();

                    LayoutInflater li = LayoutInflater.from(mContext);
                    View promptsView = li.inflate(R.layout.dialog_rename_child, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(promptsView);

                    TextView title = new TextView(mContext);
                    title.setText("RENAME YOUR DEVICE");
                    title.setBackgroundColor(Color.DKGRAY);
                    title.setPadding(10, 10, 10, 10);
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);


                    //builder.setCustomTitle(title);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    EditText editTextReassign = (EditText) promptsView.findViewById(R.id.editTextReassign);
                    Button BtnReassign = (Button) promptsView.findViewById(R.id.btn_reassign_device);

                    editTextReassign.setText(selectedName);
                    BtnReassign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "Reaasign Button Pressed! ", Toast.LENGTH_SHORT).show();

                            String newName = editTextReassign.getText().toString();
                            if (!selectedName.equals(newName)) {
                                int selectedItemId = -1;
                                for (int i = 0; i < MyChildList.size(); i++) {
                                    if (selectedName.equals(MyChildList.get(i).childName)) {
                                        MyChildList.get(i).setChildName(newName);
                                        notifyDataSetChanged();
                                        ServiceUtils.updateMobileDataInFireBase(mContext, childIMEI, newName);
                                        selectedItemId = i;
                                        break;
                                    }
                                }
                                MyChildList.remove(selectedItemId);
                                notifyItemRemoved(selectedItemId);
                            } else {
                                Toast.makeText(mContext, "Device Name not Changed! ", Toast.LENGTH_SHORT).show();
                            }

                            alertDialog.dismiss();

                        }
                    });

                    alertDialog.show();


                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Just Clicked on Item " + " and Position is " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        Child child=null;

        public MyMenuItemClickListener(int position) {
            child = MyChildList.get(position);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.action_remove_device:
                    if (child!=null) {

                        int selectedItemId = -1;
                        for (int i = 0; i < MyChildList.size(); i++) {
                            if (child.getChildDevice().equals(MyChildList.get(i).childDevice)) {
                                ServiceUtils.updateTriggerInFireBase(mContext, child.getChildDevice(), child.getChildName());
                                selectedItemId = i;
                                break;
                            }
                        }

                        Log.d(TAG, "MyMenuItemClickListener: onMenuItemClick:  Child to Remove=" + new Gson().toJson(child));
                        Toast.makeText(mContext, "Child To Remove=" + child.getChildName(), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        MyChildList.remove(selectedItemId);
                        notifyItemRemoved(selectedItemId);
                        child = null;
                        Log.d(TAG, "MyMenuItemClickListener: onMenuItemClick:  MyChildList after Removal=" + new Gson().toJson(MyChildList));
                    }

                    return true;
                case R.id.action_update_avatar:
                    Toast.makeText(mContext, "Update Mobile Icon", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_show_details:
                    Toast.makeText(mContext, "Show More Details", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }

    }


}
