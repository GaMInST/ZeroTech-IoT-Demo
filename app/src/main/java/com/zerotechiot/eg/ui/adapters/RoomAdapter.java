package com.zerotechiot.eg.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.RoomModel;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<RoomModel> rooms = new ArrayList<>();
    private OnRoomClickListener listener;

    public interface OnRoomClickListener {
        void onRoomClick(RoomModel room);
    }

    public RoomAdapter(OnRoomClickListener listener) {
        this.listener = listener;
    }

    public void setRooms(List<RoomModel> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    public void addRoom(RoomModel room) {
        rooms.add(room);
        notifyItemInserted(rooms.size() - 1);
    }

    public void updateRoom(RoomModel room) {
        int position = findRoomPosition(room.getId());
        if (position != -1) {
            rooms.set(position, room);
            notifyItemChanged(position);
        }
    }

    private int findRoomPosition(String roomId) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId().equals(roomId)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_card, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomModel room = rooms.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView roomIcon;
        private TextView roomName;
        private TextView roomDeviceCount;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIcon = itemView.findViewById(R.id.room_icon);
            roomName = itemView.findViewById(R.id.room_name);
            roomDeviceCount = itemView.findViewById(R.id.room_device_count);

            // Set click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRoomClick(rooms.get(position));
                }
            });
        }

        public void bind(RoomModel room) {
            roomName.setText(room.getName());
            roomDeviceCount.setText(room.getDeviceCount() + " devices");

            // Update room icon based on room type
            updateRoomIcon(room.getType());

            // Add subtle animation
            itemView.setAlpha(0f);
            itemView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setStartDelay(getAdapterPosition() * 100L)
                    .start();
        }

        private void updateRoomIcon(String roomType) {
            // Set appropriate icon based on room type
            // This can be expanded based on available room icons
            roomIcon.setImageResource(R.drawable.ic_device_default);
        }
    }
}
