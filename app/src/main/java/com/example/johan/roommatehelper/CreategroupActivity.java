package com.example.johan.roommatehelper;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class CreategroupActivity extends AppCompatActivity implements PostgroupHelper.CallbackPost, GroupsRequest.Callback, PutuserHelper.CallbackPut {

    private DrawerLayout Drawerlayout;

//    Declare variables that can be used throughout this activity.
    User user;
    Group group;
    String groupName;
    String groupPassword;
    ArrayList<String> members;
    ArrayList<Group> groups;

//    Set toolbar with title and drawerlayout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Create Group");

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("loggedInUser");
        String userName = user.getUser_name();
        members = new ArrayList<String>();
        members.add(userName);
    }

//    Send user back to JoingroupActivity when back button is pressed
    public void onBackPressed() {
        startActivity(new Intent(CreategroupActivity.this, JoingroupActivity.class));
    }

//    Send input user to PostgroupHelper to create new Group object if passwords are the same.
    public void group_created(View view) {
        groupName = ((EditText)findViewById(R.id.createGroupName)).getText().toString();
        groupPassword = ((EditText)findViewById(R.id.createGroupPassword)).getText().toString();
        String groupPassword2 = ((EditText)findViewById(R.id.recreateGroupPassword)).getText().toString();
        if (groupName.length() == 0 || groupPassword.length() == 0)  {
            Toast.makeText(this, "Fill in all boxes", Toast.LENGTH_SHORT).show();
        }   else if (!groupPassword.equals(groupPassword2))  {
            Toast.makeText(this, "passwords must be the same", Toast.LENGTH_SHORT).show();
        }   else    {
            PostgroupHelper helper = new PostgroupHelper(groupName, groupPassword, members, getApplicationContext(), CreategroupActivity.this);
        }
    }

//    When new groups are loaded, the group id of the just created group is copied and entered to the current user.
    @Override
    public void gotGroups(ArrayList<Group> groupsList) {
        Log.d("hierhebbenwe", "CreateActivity groups zijn opgevraagd");
        groups = new ArrayList<Group>();
        groups = groupsList;
        Toast.makeText(this, "groups loaded", Toast.LENGTH_SHORT).show();
        Log.d("hierhebbenwe", "CreateActivity groups zijn opgevraagd" + groups.size());
        for(int i = 0; i < groups.size(); i++)  {
            group = groups.get(i);
            String currentName = group.getGroupName();
            String currentPassword = group.getGroupPassword();
            if(groupName.equals(currentName) && groupPassword.equals(currentPassword))  {
                int groupId = group.getGroupId();
                user.setGroup_id(groupId);
                Log.d("hierhebbenwe", "Create Activity nu hebben we de juiste groep om de id uit te pakken" + groupId + "  " + user.getGroup_id());
                PutuserHelper helper = new PutuserHelper(user, getApplicationContext(), CreategroupActivity.this);
            }
        }
    }

//    Notice user when an error in loading the groups occurs.
    @Override
    public void gotGroupsError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

//    Notice user when group is uploaded and request new list of all groups.
    @Override
    public void gotHelper(String message) {
        Toast.makeText(this, "Group created", Toast.LENGTH_SHORT).show();
        Log.d("hierhebbenwe2", "CreateActivity groep is gepost, nu worden groepen opgevraagd");
        GroupsRequest groupRequest = new GroupsRequest(this);
        groupRequest.getGroups(this);
    }

//    Notice user if something went wrong in uploading the new group object.
    @Override
    public void gotHelperError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
//    Notify the user that the newly created group is added to its account and send user back to OverviewActivity.
    @Override
    public void gotputHelper(String message) {
        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
        Log.d("hierhebbenwe", "CreateActivity nu is de user geupdate en gaan we terug naar overviewactivity");
        Intent intent = new Intent(CreategroupActivity.this, OverviewActivity.class);
        intent.putExtra("loggedInUser", user);
        startActivity(intent);
    }
//    Notify user if something went wrong with assigning the group to its account.
    @Override
    public void gotputHelperError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
