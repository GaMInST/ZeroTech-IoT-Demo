package com.zerotechiot.eg.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.AutomationModel;

import java.util.ArrayList;
import java.util.List;

public class AutomationAdapter extends RecyclerView.Adapter<AutomationAdapter.AutomationViewHolder> {

    private List<AutomationModel> automations = new ArrayList<>();
    private Context context;
    private OnAutomationClickListener onAutomationClickListener;

    public AutomationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AutomationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_automation_card, parent, false);
        return new AutomationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutomationViewHolder holder, int position) {
        AutomationModel automation = automations.get(position);
        holder.bind(automation);
    }

    @Override
    public int getItemCount() {
        return automations.size();
    }

    public void setAutomations(List<AutomationModel> automations) {
        this.automations = automations;
        notifyDataSetChanged();
    }

    public void setOnAutomationClickListener(OnAutomationClickListener listener) {
        this.onAutomationClickListener = listener;
    }

    public interface OnAutomationClickListener {
        void onAutomationClick(AutomationModel automation);

        void onAutomationLongClick(AutomationModel automation);
    }

    class AutomationViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private ImageView automationIcon;
        private TextView automationName;
        private TextView automationDescription;
        private View activeIndicator;

        public AutomationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.automation_card);
            automationIcon = itemView.findViewById(R.id.automation_icon);
            automationName = itemView.findViewById(R.id.automation_name);
            automationDescription = itemView.findViewById(R.id.automation_description);
            activeIndicator = itemView.findViewById(R.id.active_indicator);

            cardView.setOnClickListener(v -> {
                if (onAutomationClickListener != null) {
                    onAutomationClickListener.onAutomationClick(automations.get(getAdapterPosition()));
                }
            });

            cardView.setOnLongClickListener(v -> {
                if (onAutomationClickListener != null) {
                    onAutomationClickListener.onAutomationLongClick(automations.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(AutomationModel automation) {
            automationName.setText(automation.getName());
            automationDescription.setText(automation.getDescription());

            // Set icon based on automation type
            switch (automation.getIcon()) {
                case "sunset":
                    automationIcon.setImageResource(R.drawable.ic_automation);
                    break;
                case "motion":
                    automationIcon.setImageResource(R.drawable.ic_automation);
                    break;
                case "temperature":
                    automationIcon.setImageResource(R.drawable.ic_automation);
                    break;
                case "away":
                    automationIcon.setImageResource(R.drawable.ic_automation);
                    break;
                case "energy":
                    automationIcon.setImageResource(R.drawable.ic_automation);
                    break;
                default:
                    automationIcon.setImageResource(R.drawable.ic_automation);
                    break;
            }

            // Show active indicator
            activeIndicator.setVisibility(automation.isActive() ? View.VISIBLE : View.GONE);
        }
    }
}