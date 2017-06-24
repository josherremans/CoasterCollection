package josh.android.coastercollection.adapters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import josh.android.coastercollection.R;
import josh.android.coastercollection.activities.AddCollectorActivity;
import josh.android.coastercollection.activities.GalleryActivity;
import josh.android.coastercollection.bo.Collector;
import josh.android.coastercollection.comparators.CollectorComparator;
import josh.android.coastercollection.enums.IIntentExtras;

/**
 * Created by Jos on 2/02/2017.
 */
public class CollectorAdapter extends ArrayAdapter<Collector> {

    private ArrayList<Collector> lstCollectors;

    private LayoutInflater layoutInflater;

    private int listViewTypeId;

    public CollectorAdapter(Context context, ArrayList<Collector> lstCollectors) {
        super(context, R.layout.item_collector_list, lstCollectors);

        listViewTypeId = R.layout.item_collector_list;

        this.lstCollectors = lstCollectors;

        layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (lstCollectors == null) {
            return 0;
        } else {
            return lstCollectors.size();
        }
    }

    @Override
    public Collector getItem(int position) {
        return lstCollectors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lstCollectors.get(position).getCollectorID();
    }

    @Override
    public void notifyDataSetChanged() {
        this.setNotifyOnChange(false); // To prevent looping!

        sort(new CollectorComparator());
        // Remark: this will automatically call super.notifyDataSetChanged() !!!
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        final Collector collector = getItem(position);

        // reuse views
        if (rowView == null) {
            rowView = layoutInflater.inflate(this.listViewTypeId, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder(rowView);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.collectorFullName.setText(collector.getFullName());

        if ((collector.getInitials() != null) && (collector.getInitials().length() > 0)) {
            holder.collectorInitials.setText("(" + collector.getInitials() + ")");
        }

        if ((collector.getAlias() != null) && (collector.getAlias().length() > 0)) {
            holder.collectorAlias.setText(collector.getAlias());
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(getContext(), GalleryActivity.class);

                galleryIntent.putExtra(IIntentExtras.EXTRA_COLLECTORID, collector.getCollectorID());
                galleryIntent.putExtra(IIntentExtras.EXTRA_GALLERY_SUBTITLE, collector.getDisplayName());

                getContext().startActivity(galleryIntent);
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent alterCollectorIntent = new Intent(getContext(), AddCollectorActivity.class);

                alterCollectorIntent.putExtra(IIntentExtras.EXTRA_COLLECTORID, collector.getCollectorID());

                getContext().startActivity(alterCollectorIntent);

                return true;
            }
        });

        return rowView;
    }

    private class ViewHolder {
        public TextView collectorFullName;
        public TextView collectorInitials;
        public TextView collectorAlias;

        public ViewHolder(View parent) {
            collectorFullName = (TextView) parent.findViewById(R.id.txtCollectorFullName);
            collectorInitials = (TextView) parent.findViewById(R.id.txtCollectorInitials);
            collectorAlias = (TextView) parent.findViewById(R.id.txtCollectorAlias);
        }
    }
}
