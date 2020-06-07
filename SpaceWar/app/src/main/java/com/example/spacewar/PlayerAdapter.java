package com.example.spacewar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> players;

    public PlayerAdapter(List<Player> players) {
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_cell,parent,false);
        PlayerViewHolder playerViewHolder = new PlayerViewHolder(view);
        return playerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.name.setText(player.get_Name());
        holder.score.setText(player.get_Score()+"");
        holder.medal.setImageResource(player.get_MedalResId());
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView score;
        ImageView medal;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Player_name_text_view);
            score = itemView.findViewById(R.id.Player_score_text_view);
            medal = itemView.findViewById(R.id.medal_icon);
        }


    }
}
