package com.example.lapcs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.InitApp.App;
import com.example.lapcs.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    //defining firebaseauth object
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }

        setContentView(R.layout.activity_home);

        //initializing firebase authentication object
        mAuth = FirebaseAuth.getInstance();
        //getting current user
        FirebaseUser user = mAuth.getCurrentUser();

        //if the user is not logged in
        //that means current user will return null
        if(user == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginParentActivity.class));
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Toast.makeText(HomeActivity.this,"onAuthStateChanged Called",Toast.LENGTH_SHORT).show();
            }
        };

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "You will Receive Push Notification Shortly!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                PushNotificationHelper.SendPushNotification(HomeActivity.this, App.DeviceToken,Server_API_key,ContentType,"Test Notification!","LAPCS Alert!");
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        TextView mUserNameTextView = navigationView.getHeaderView(0).findViewById(R.id.tv_username_nav_header);
        ImageView mProfileImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView_profile_nav_header);
        TextView mEmailTextView = navigationView.getHeaderView(0).findViewById(R.id.tv_email_nav_header);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_children,
                R.id.nav_faq, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (user != null) {
            try{

                String userEmail = user.getEmail();
                int index = userEmail.indexOf('@');
                String splittedEmail = userEmail.substring(0,index);

                mUserNameTextView.setText(splittedEmail.toUpperCase());
                mEmailTextView.setText(user.getEmail());

            }
            catch (Exception ex)
            {
                //Toast.makeText(HomeActivity.this,"Exception Occurred"+ex.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }

        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuCompat.setGroupDividerEnabled(menu, true);
        getMenuInflater().inflate(R.menu.home, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(HomeActivity.this, "Settings Option Clicked!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
//            case R.id.action_rate:
//                Toast.makeText(HomeActivity.this, "Rate Us Option Clicked!", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.action_about:
//                Toast.makeText(HomeActivity.this, "About Us Option Clicked!", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
