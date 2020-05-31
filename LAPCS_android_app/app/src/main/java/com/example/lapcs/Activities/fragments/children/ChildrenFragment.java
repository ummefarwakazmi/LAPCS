package com.example.lapcs.Activities.fragments.children;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.adapters.ChildRecyclerViewAdapter;
import com.example.lapcs.models.Child;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.lapcs.AppConsts.TAG;


public class ChildrenFragment extends Fragment {


    /** Adapter to deal with the list of childs */
    private ChildRecyclerViewAdapter mChildListAdapter;

    private RecyclerView mChildListRecyclerView;
    private List<Child> MyChildList;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabaseUsers;
    private DatabaseReference mFirebaseDatabaseMobiles;

    SharedPreferences sharedPreferences;
    private LinearLayoutManager layoutManager;

    View.OnLongClickListener ChildListItemOnLongClickListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_children, container, false);

        mChildListRecyclerView = (RecyclerView) root.findViewById(R.id.ChildListRecyclerView);

        //ChildListItemOnLongClickListener = new MyChildListItemOnLongClickListener(getActivity());

        MyChildList = new ArrayList<>();
        mChildListAdapter = new ChildRecyclerViewAdapter(getActivity(), MyChildList);

        layoutManager = new LinearLayoutManager(getActivity());
        mChildListRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        mChildListRecyclerView.setItemAnimator(itemAnimator);
        mChildListRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        mChildListRecyclerView.setHasFixedSize(true);
        mChildListRecyclerView.setAdapter(mChildListAdapter);



        mFirebaseInstance = FirebaseDatabase.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String userID = sharedPreferences.getString("UserID", "");
        Log.d(TAG, this.getClass().getName()+": "+" UserID = "+ userID);

        // get reference to 'user/uid' node
        mFirebaseDatabaseUsers = mFirebaseInstance.getReference(AppConsts.Users_Node_Ref+"/"+userID);

        // read childs
        mFirebaseDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    String childIMEI = childSnapshot.getKey();
                    Log.d(TAG, this.getClass().getName()+": "+" UsersSnapshot Key = "+ childSnapshot.getKey());
                    Log.d(TAG, this.getClass().getName()+": "+" UsersSnapshot Value= "+ childSnapshot.getValue().toString());

                    mFirebaseDatabaseMobiles = mFirebaseInstance.getReference(AppConsts.Mobiles_Node_Ref+"/"+childIMEI);

                    mFirebaseDatabaseMobiles.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() != null)
                            {
                                Log.d(TAG, this.getClass().getName()+": "+" MobilesSnapshot Key= "+ dataSnapshot.getKey());
                                Log.d(TAG, this.getClass().getName()+": "+" MobilesSnapshot Value= "+ dataSnapshot.getValue().toString());

                                Child child = new Child();
                                child.setChildDevice(dataSnapshot.getKey());                //imei
                                child.setChildName(dataSnapshot.getValue().toString());     //device name

                                Log.d(TAG, "ChildFragment: onDataChange:  MyChildList Before Adding Child=" + new Gson().toJson(MyChildList));

                                if(!MyChildList.equals(child))
                                {
                                    MyChildList.add(child);
                                    mChildListAdapter.notifyDataSetChanged();
                                    Log.d(TAG, this.getClass().getName()+": "+" Child comparison with List = Child "+ new Gson().toJson(child)+" Added because child not found");
                                    Log.d(TAG, "ChildFragment: onDataChange:  MyChildList After Adding Child=" + new Gson().toJson(MyChildList));
                                }
                                else
                                {
                                    Log.d(TAG, this.getClass().getName()+": "+" Child comparison with List = Child "+ new Gson().toJson(child)+" Not Added");
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "The read failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG,this.getClass().getName()+": "+"The read failed: " + databaseError.getMessage());
            }

        });


        final TextView textView = root.findViewById(R.id.text_tools);

        return root;
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}