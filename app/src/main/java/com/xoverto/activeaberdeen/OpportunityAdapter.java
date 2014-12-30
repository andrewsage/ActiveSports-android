package com.xoverto.activeaberdeen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by andrew on 30/12/14.
 */
public class OpportunityAdapter extends ArrayAdapter<Opportunity> {

    private static final String TAG = "OpportunityAdapter";
    private Context ctx;

    public OpportunityAdapter(Context context, List<Opportunity> opportunities) {
        super(context, 0, opportunities);
        this.ctx = context;
    }

    public View getView(int pos, View convertView, ViewGroup parent)
    {
        // Get the current object
        final Opportunity offer = getItem(pos);

        OpportunityViewHolder viewHolder = null;

        if (convertView == null)
        {

            convertView = View.inflate(getContext(), R.layout.grid_item_opportunity, null);

            viewHolder = new OpportunityViewHolder(getContext());

            /*
            viewHolder = new OfferViewHolder(getContext(),
                    (ImageView) convertView.findViewById(R.id.offer_icon_img),
                    (TextView) convertView.findViewById(R.id.offer_business_name_txt),
                    (TextView) convertView.findViewById(R.id.offer_created_via_txt),
                    (TextView) convertView.findViewById(R.id.offer_availability_txt),
                    (TextView) convertView.findViewById(R.id.offer_title_txt),
                    (BannerImageView) convertView.findViewById(R.id.offer_main_image_img),
                    (ImageButton) convertView.findViewById(R.id.offer_share_btn),
                    (ImageView) convertView.findViewById(R.id.offer_starred_img),
                    convertView.findViewById(R.id.offer_top_container),
                    convertView.findViewById(R.id.offer_bottom_container),
                    convertView.findViewById(R.id.offer_video_videoview_play_overlay_img));

*/
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (OpportunityViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class OpportunityViewHolder
    {
        Context ctx;


        public OpportunityViewHolder(Context ctx)
        {
            this.ctx = ctx;
        }
    }
}
