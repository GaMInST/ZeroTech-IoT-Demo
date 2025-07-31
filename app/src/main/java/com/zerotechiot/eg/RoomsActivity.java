package com.zerotechiot.eg;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerotechiot.eg.ui.adapters.RoomAdapter;
import com.zerotechiot.eg.ui.models.RoomModel;
import com.zerotechiot.eg.ui.models.DeviceModel;
import com.zerotechiot.eg.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class RoomsActivity extends AppCompatActivity implements RoomAdapter.OnRoomClickListener {

    private RecyclerView roomsRecyclerView;
    private RoomAdapter roomAdapter;
    private List<RoomModel> rooms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        initializeViews();
        setupToolbar();
        loadRoomsData();
    }

    private void initializeViews() {
        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Rooms");
        }

        // Initialize RecyclerView
        roomsRecyclerView = findViewById(R.id.rooms_recycler_view);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomAdapter = new RoomAdapter(this);
        roomsRecyclerView.setAdapter(roomAdapter);

        // Setup floating action button
        FloatingActionButton fabAddRoom = findViewById(R.id.fab_add_room);
        fabAddRoom.setOnClickListener(v -> {
            NotificationHelper.showSmartNotification(this, "Add room feature coming soon!");
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Rooms");
        }
    }

    private void loadRoomsData() {
        // Add sample rooms
        rooms.add(new RoomModel("1", "Living Room", "living"));
        rooms.add(new RoomModel("2", "Kitchen", "kitchen"));
        rooms.add(new RoomModel("3", "Bedroom", "bedroom"));
        rooms.add(new RoomModel("4", "Bathroom", "bathroom"));
        rooms.add(new RoomModel("5", "Office", "office"));
        rooms.add(new RoomModel("6", "Garage", "garage"));

        // Add sample devices to rooms
        rooms.get(0).addDevice(new DeviceModel("1", "Smart Light", "light", "Living Room", "1", null));
        rooms.get(0).addDevice(new DeviceModel("2", "Smart TV", "tv", "Living Room", "1", null));
        rooms.get(1).addDevice(new DeviceModel("3", "Smart Fridge", "fridge", "Kitchen", "2", null));
        rooms.get(2).addDevice(new DeviceModel("4", "Bedside Lamp", "light", "Bedroom", "3", null));

        roomAdapter.setRooms(rooms);
    }

    @Override
    public void onRoomClick(RoomModel room) {
        NotificationHelper.showSmartNotification(this, "Selected room: " + room.getName());
        // In a real app, this would navigate to room details
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}